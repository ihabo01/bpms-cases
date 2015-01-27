package org.intalio.tempo.workflow.acm.server.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.CustomColumn;
import org.intalio.tempo.workflow.task.Notification;
import org.intalio.tempo.workflow.task.PATask;
import org.intalio.tempo.workflow.task.PIPATask;
import org.intalio.tempo.workflow.task.PIPATaskOutput;
import org.intalio.tempo.workflow.task.Task;
import org.intalio.tempo.workflow.task.TaskState;
import org.intalio.tempo.workflow.tms.UnavailableTaskException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The code to retrieve task is decomposed into two calls to the persistence:
 * <ul>
 * <li>One call to retrieve the tasks ids. The query cannot be expressed through
 * JPQL AND load the proper classes so we are using simple SQL</li>
 * <li>The second call uses JPQL to load the tasks, and load the proper
 * inheritance tree</li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public class CaseFetcher {
	public static final String FETCH_MAX = ":max";
	public static final String FETCH_FIRST = ":first";
	public static final String FETCH_SUB_QUERY = ":subQuery";
	public static final String FETCH_CLASS = ":class";
	public static final String FETCH_CLASS_NAME = ":classname";
	public static final String FETCH_USER = ":user";
	public static final String FETCH_COUNT = ":count";
	public static final String FILTER = ":filter";
	public static final String FILTER_STATE = ":taskStates";
	public static final String FILTER_PRIORITY = ":prioritys";
	public static final String FILTER_USER_OWNERS = ":userOwners";
	public static final String FILTER_ROLE_OWNERS = ":roleOwners";
	public static final String FILTER_DEADLINE = ":deadline";

	final static Logger _logger = LoggerFactory.getLogger(CaseFetcher.class);
	private EntityManager _entityManager;
	private Session session;
	private Query find_by_id;
	private Query find_pipa_task_output_by_task_id;
	private final String QUERY_PENDING_NON_ERROR_TASKS = "select taskID from tempo_pending_task where status = 0";
	private final String QUERY_GENERIC1 = "select T from ";
	private final String QUERY_GENERIC_DISTINCT = "select DISTINCT T from ";
	private final String QUERY_GENERIC_DISTINCT_CUSTOM_COLUMN_SEARCH = "select DISTINCT T, C from ";
	private final String QUERY_GENERIC_GROUPBY = " GROUP BY T._id ";
	private final String QUERY_GENERIC_COUNT = "select COUNT(DISTINCT T) from ";
//	private final String QUERY_GENERIC2 = " T where (T._userOwners = (?1) or T._roleOwners = (?2)) ";
	private final String QUERY_GENERIC2_FOR_ADMIN = " T ";
	private final String QUERY_GENERIC2_FOR_CUSTOM_COLUMN_SEARCH = " T LEFT JOIN T._customMetadata C ";
	private final String QUERY_GENERIC3_FOR_ADMIN = " T._id not in (:tasks)";
	private final String QUERY_GENERIC2 = " LEFT JOIN T._userOwners UO LEFT JOIN T._roleOwners RO where (UO in (:users) or RO in (:roles) or RO in ('*')) and (T._id not in (:tasks))";
	private final String QUERY_FILTER_TASK_STATE = " T._state =  org.intalio.tempo.workflow.task.TaskState.";
	private final String QUERY_FILTER_PRIORITY_CRITICAL = " T._priority >= 51 ";
	private final String QUERY_FILTER_PRIORITY_IMPORTANT = " ( T._priority >= 31 AND T._priority <= 50 ) ";
	private final String QUERY_FILTER_PRIORITY_NORMAL = " ( T._priority >= 11 AND T._priority <= 30 ) ";
	private final String QUERY_FILTER_PRIORITY_LOW = " T._priority <= 10 ";
	private final String QUERY_FILTER_TASK_DEADLINE = " AND ( T._deadline <= (:deadline)) ";
	
	// private final String DELETE_TASKS =
	// "delete from Task m where m._userOwners in (?1) or m._roleOwners in (?2) "
	// ;
	private final String DELETE_ALL_TASK_WITH_ID = "delete from Task m where m._id = (?) ";

	private static String DATABASE_PRODUCT_NAME = "";
	private static String DATABASE_MYSQL = "MySQL";
	private static final String TASK_STATE = "TaskState";
	private static final String PIPA_TASK_STATE = "PIPATaskState";
	private static final String PACKAGE_SEPERATOR = ".";
	private static final String PACKAGE_PATH = "org.intalio.tempo.workflow.task";
	private static final String EMPTY_ELEMENT = "";

	public CaseFetcher(EntityManager em) {
		this._entityManager = em;
		this.session = em.unwrap(Session.class);
		this.find_by_id = session.getNamedQuery(Task.FIND_BY_ID);
		this.find_pipa_task_output_by_task_id = session.getNamedQuery(PIPATaskOutput.FIND_BY_TASK_ID_AND_USER);
		/*
		 * Fix for CON-794
		 * Getting database name from JPA Entity Manager.
		 * For using Group By clause for MYSQL and DISTINCT for other databases
		 */
		if("".equals(DATABASE_PRODUCT_NAME)) {
		    Connection conn = null;
		    try {
		        conn = session.connection();
		        java.sql.DatabaseMetaData dbmd = conn.getMetaData();
		        DATABASE_PRODUCT_NAME = dbmd.getDatabaseProductName();
		    }catch (Exception e) {
		        _logger.error("Error while getting Database name ", e);
		    }finally{
		        if(conn != null)
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        _logger.error("Error while closing connection ", e);
                    }
		    }
		}
	}

	/**
	 * Retrieve and load a task if it exists
	 * 
	 * @throws UnavailableTaskException
	 */
	public Task fetchTaskIfExists(String taskID)
			throws UnavailableTaskException {
		try {
			Query q = find_by_id.setParameter(0, taskID);
			List resultList = q.list();
			if (resultList.size() < 1)
				throw new UnavailableTaskException("Task does not exist"
						+ taskID);
			return (Task) resultList.get(0);
		} catch (NoResultException nre) {
			throw new UnavailableTaskException("Task does not exist" + taskID);
		}
	}
	
	   public List<Task> fetchTaskIfExistsfrominstanceID(String instanceid)
       throws UnavailableTaskException {
	       try {
       Query q = session.getNamedQuery(PATask.FIND_BY_INSTANCEID);
       q.setParameter(0, instanceid);
       List<Task> resultList = q.list();
       if (resultList.size() < 1)
           throw new UnavailableTaskException("Task does not exist with InstanceID"
                   + instanceid);
       return  resultList;
	       } catch (NoResultException nre) {
       throw new UnavailableTaskException("Task does not exist" + instanceid);
   }
}

	public int deleteTasksWithID(String taskID) {
		Query q = session.createQuery(DELETE_ALL_TASK_WITH_ID);
		q.setParameter(0, taskID);
		return q.executeUpdate();
	}

	/**
	 * Fetch a PIPA task from its URL
	 * 
	 * @throws UnavailableTaskException
	 */
	public PIPATask fetchPipaFromUrl(String formUrl)
			throws UnavailableTaskException {
        if(formUrl == null) throw new UnavailableTaskException();
		Query q = session.getNamedQuery(PIPATask.FIND_BY_URL)
				.setParameter(0, formUrl);
		try {
			return (PIPATask) q.uniqueResult();
		} catch (NoResultException nre) {
			throw new UnavailableTaskException(
					"Task with following endpoint does not exist:" + formUrl);
		}
	}
	
	/**
	 * Core method. retrieve all the tasks for the given <code>UserRoles</code>
	 */
	public Task[] fetchAllAvailableTasks(UserRoles user) {
		ArrayList userIdList = new ArrayList();
		userIdList.add(user.getUserID());
		Query q = session.getNamedQuery(Task.FIND_BY_ROLE_USER)
				.setParameterList("users", userIdList).setParameterList("roles",
				                user.getAssignedRoles());
		List result = q.list();
		return (Task[]) result.toArray(new Task[result.size()]);
	}

        /**
         * Core method. retrieve all the tasks for the given <code>Users</code>
         * @param users List<String>
         * @return tasks List<Task>
         */
        public final List<Task> fetchAllAvailableTasks(
                                final List<String> users) {
            Query q = session.getNamedQuery(Task.FIND_BY_USER)
                    .setParameterList("users", users);
            List<Task> result = q.list();
            return result;
        }

	/**
	 * Core method. retrieve all the tasks for the given <code>UserRoles</code>
	 * and task state, task type
	 */
	public Task[] fetchAvailableTasks(UserRoles user, Class taskClass,
			String subQuery) {
		HashMap params = new HashMap(3);
		params.put(FETCH_USER, user);
		params.put(FETCH_CLASS, taskClass);
		params.put(FETCH_SUB_QUERY, subQuery);
		return fetchAvailableTasks(params);
	}

	public Long countTasks(Map parameters) {
//	    Task[] tasks = fetchAvailableTasks(parameters);
//	    Long count = (long) tasks.length;
//		return count;
	    parameters.put(CaseFetcher.FETCH_COUNT, StringUtils.EMPTY);
	    Query q = buildQuery(parameters);
	    return (Long) q.uniqueResult();
	}

	private String buildFilterQuery(Map filter) {

		String stateFilterQuery = "";
		if(filter.get(FILTER_STATE) != null && !((List)filter.get(FILTER_STATE)).isEmpty()) {
			stateFilterQuery = " AND (";
			ArrayList<String> statesList = (ArrayList<String>)filter.get(FILTER_STATE);
			Set<String> taskStates = new HashSet<String>(statesList);
			for(String state: taskStates){
				if(stateFilterQuery.length() > 6) {
					stateFilterQuery += " OR ";
				}
				stateFilterQuery += QUERY_FILTER_TASK_STATE + state.toUpperCase()+ " ";
			}
			stateFilterQuery += " )";
		}
		String priorityFilterQuery = "";
		if(filter.get(FILTER_PRIORITY) != null && !((List)filter.get(FILTER_PRIORITY)).isEmpty()) {
			priorityFilterQuery = " AND (";
			ArrayList<String> prioritiesList = (ArrayList<String>)filter.get(FILTER_PRIORITY);
			Set<String> taskPriorities = new HashSet<String>(prioritiesList);
			for(String priority: taskPriorities){
				if(priorityFilterQuery.length() > 6) {
					priorityFilterQuery += " OR ";
				}
				if(priority.toUpperCase().equals("LOW")) {
					priorityFilterQuery += QUERY_FILTER_PRIORITY_LOW;
				}else if (priority.toUpperCase().equals("NORMAL")) {
					priorityFilterQuery += QUERY_FILTER_PRIORITY_NORMAL;
				}else if (priority.toUpperCase().equals("IMPORTANT")) {
					priorityFilterQuery += QUERY_FILTER_PRIORITY_IMPORTANT;
				}else if (priority.toUpperCase().equals("CRITICAL")) {
					priorityFilterQuery += QUERY_FILTER_PRIORITY_CRITICAL;
				}
			}
			priorityFilterQuery += " )";
		}
        String deadlineFilterQuery = "";
        if(filter.get(FILTER_DEADLINE) != null && !((List)filter.get(FILTER_DEADLINE)).isEmpty()) {
            deadlineFilterQuery += QUERY_FILTER_TASK_DEADLINE;
        }
		return stateFilterQuery + priorityFilterQuery + deadlineFilterQuery;
	}

	private List<String> getAllNonErrorPendingTaskIDs() {
        Query query = session
                .createSQLQuery(QUERY_PENDING_NON_ERROR_TASKS);
        List<String> taskIDs = query.list();
        if(taskIDs.isEmpty()) {
            taskIDs.add("NA");
        }

        return taskIDs;
    }

	private Query buildQuery(Map parameters) {
		// _entityManager.clear();
		UserRoles user = (UserRoles) parameters.get(FETCH_USER);
		Class taskClass = (Class) parameters.get(FETCH_CLASS);		
		String subQuery = MapUtils.getString(parameters, FETCH_SUB_QUERY, "");
		if (taskClass.equals(PATask.class) || taskClass.equals(Notification.class)) {
		    subQuery = this.replaceTaskStatePath(TASK_STATE, subQuery);
		} else if (taskClass.equals(PIPATask.class)) {
		    subQuery = this.replaceTaskStatePath(PIPA_TASK_STATE, subQuery);
		}
		ArrayList<String> userIdList = new ArrayList<String>();
		userIdList.add(user.getUserID());
		userIdList.addAll(user.getVacationUsers());
		ArrayList<String> rolesList = new ArrayList<String>();
		rolesList.addAll(user.getAssignedRoles());
		Map<String, List<String>> filter = (Map<String, List<String>>)parameters.get(FILTER);
		String filterQuery = "";
		boolean isFilterOff = true;
		if(filter != null) {
			ArrayList<String> filterUsers = (ArrayList<String>)filter.get(FILTER_USER_OWNERS);
			ArrayList<String> filterRoles = (ArrayList<String>)filter.get(FILTER_ROLE_OWNERS);
			if(filterUsers != null || filterRoles != null) {
				rolesList.clear();
				rolesList.add(EMPTY_ELEMENT);
				userIdList.clear();
				userIdList.add(EMPTY_ELEMENT);
				userIdList = filterUsers != null?filterUsers:userIdList;
				rolesList = filterRoles != null?filterRoles:rolesList;
			}
			filterQuery = buildFilterQuery(filter);
			if(!StringUtils.isBlank(filterQuery) || filterUsers != null || filterRoles != null){
				isFilterOff = false;
			}
		}
		String baseQuery = null;
		boolean isGroupByRequired = false;
		boolean isCustomColumnSearch = subQuery.toLowerCase().indexOf("index(c)") >= 0 ?true:false;
		if(parameters.containsKey(FETCH_COUNT)){
		    baseQuery =  QUERY_GENERIC_COUNT;
		}else if(isCustomColumnSearch){
			baseQuery = QUERY_GENERIC_DISTINCT_CUSTOM_COLUMN_SEARCH;
		}else if(DATABASE_MYSQL.equalsIgnoreCase(DATABASE_PRODUCT_NAME)) {
		    //we are not using DISTINCT for MYSQL database to improve performance
		    baseQuery = QUERY_GENERIC1;
		    isGroupByRequired = true;
		}else {
		    baseQuery = QUERY_GENERIC_DISTINCT;
		}
		Query q;
		boolean isWorkflowAdmin=user.isWorkflowAdmin();

		List<String> pendingTaskIDs = getAllNonErrorPendingTaskIDs();
		if (StringUtils.isEmpty(subQuery)) {
			if(isWorkflowAdmin && isFilterOff) {
				q = session.createQuery(
					baseQuery + taskClass.getSimpleName() + QUERY_GENERIC2_FOR_ADMIN + " where "
                            + QUERY_GENERIC3_FOR_ADMIN).setParameterList("tasks",
                                    pendingTaskIDs);
			}else{
				q = session.createQuery(
						baseQuery + taskClass.getSimpleName() + QUERY_GENERIC2_FOR_ADMIN + QUERY_GENERIC2 + filterQuery).setParameterList("users", userIdList).setParameterList("roles", rolesList).setParameterList("tasks", pendingTaskIDs);
				
			}
				
		} else {
				StringBuffer buffer = new StringBuffer();
				
				if(isWorkflowAdmin && isFilterOff){
					buffer.append(baseQuery).append(taskClass.getSimpleName());
					if (isCustomColumnSearch) {
					    buffer.append(QUERY_GENERIC2_FOR_CUSTOM_COLUMN_SEARCH);
					} else {
					    buffer.append(QUERY_GENERIC2_FOR_ADMIN);
					}
					buffer.append(" where " + QUERY_GENERIC3_FOR_ADMIN);
					String trim = subQuery.toLowerCase().trim();
					int orderIndex = trim.indexOf("order by");
					if (orderIndex == -1) {
						buffer.append(" and ").append(subQuery);
					} else {
						if (!trim.startsWith("order by")){
							buffer.append(" and ").append(subQuery);
						}
						else {
							buffer.append(subQuery);
						}
				}
			}else{
				buffer.append(baseQuery).append(taskClass.getSimpleName());
				if (isCustomColumnSearch) {
				    buffer.append(QUERY_GENERIC2_FOR_CUSTOM_COLUMN_SEARCH);
				} else {
				    buffer.append(QUERY_GENERIC2_FOR_ADMIN);
				}
				buffer.append(QUERY_GENERIC2);
				buffer.append(filterQuery);
				String trim = subQuery.toLowerCase().trim();
                int orderIndex = trim.indexOf("order by");
                int groupIndex = trim.indexOf("group by");
                if (orderIndex == -1 && groupIndex == -1) {
                    buffer.append(" and ").append(" ( ").append(subQuery)
                            .append(" ) ");
                    if(isGroupByRequired) {
                        //Apply Group By clause only for MySQL database
                        buffer.append(QUERY_GENERIC_GROUPBY);
                    }
                } else if (groupIndex == -1) {
                    if (!trim.startsWith("order by")) {
                        buffer.append(" and (")
                                .append(subQuery.substring(0, orderIndex))
                                .append(") ");
                        if(isGroupByRequired) {
                            buffer.append(QUERY_GENERIC_GROUPBY);
                        }
                        buffer.append(subQuery.substring(orderIndex));
                    }
                    else {
                        if(isGroupByRequired) {
                            buffer.append(QUERY_GENERIC_GROUPBY);
                        }
                        buffer.append(subQuery);
                    }
                } else if (orderIndex == -1) {
                    if (!trim.startsWith("group by"))
                        buffer.append(" and (")
                                .append(subQuery.substring(0, groupIndex))
                                .append(") ")
                                .append(subQuery.substring(groupIndex));
                    else {
                        buffer.append(subQuery);
                    }
                } else {
                    int index = (groupIndex < orderIndex) ? groupIndex
                            : orderIndex;
                    if (!(trim.startsWith("group by") || trim.startsWith("order by")))
                        buffer.append(" and (")
                                .append(subQuery.substring(0, index))
                                .append(") ").append(subQuery.substring(index));
                    else {
                        buffer.append(subQuery);
                    }
                }
			}	
			
			if (_logger.isDebugEnabled()){
				_logger.debug(buffer.toString());
				_logger.debug("Parameter 1:" + userIdList);				
				_logger.debug("Parameter 2:" + rolesList);
				_logger.debug("isWorkflowAdmin" + isWorkflowAdmin);
			}			
		
			if(isWorkflowAdmin && isFilterOff) {
				q = session.createQuery(buffer.toString()).setParameterList("tasks", pendingTaskIDs);
			} else {
				q = session.createQuery(buffer.toString()).setParameterList("users",userIdList).setParameterList("roles", rolesList).setParameterList("tasks", pendingTaskIDs);
			}
		}

        if (filter != null && filter.get(FILTER_DEADLINE) != null
                && !((List) filter.get(FILTER_DEADLINE)).isEmpty()) {
            String deadline = ((ArrayList<String>) filter.get(FILTER_DEADLINE))
                    .get(0);
            Timestamp timestamp = new Timestamp(DatatypeConverter
                    .parseDateTime(deadline).getTimeInMillis());
            q.setTimestamp("deadline", timestamp);
        }

		return q;
	}
	
//	private Query buildQueryForSingleRole(Map parameters, String role){
//	       UserRoles user = (UserRoles) parameters.get(FETCH_USER);
//	        Class taskClass = (Class) parameters.get(FETCH_CLASS);
//	        String subQuery = MapUtils.getString(parameters, FETCH_SUB_QUERY, "");
//	        String baseQuery = parameters.containsKey(FETCH_COUNT) ? QUERY_GENERIC_COUNT
//	                : QUERY_GENERIC1;
//	        Query q;
//	        if (StringUtils.isEmpty(subQuery)) {
//	            q = _entityManager.createQuery(
//	                    baseQuery + taskClass.getSimpleName() + QUERY_GENERIC2)
//	                    .setParameter(1, user.getUserID()).setParameter(2,
//	                            role);
//	        } else {
//	            StringBuffer buffer = new StringBuffer();
//	            buffer.append(baseQuery).append(taskClass.getSimpleName()).append(
//	                    QUERY_GENERIC2);
//
//	            String trim = subQuery.toLowerCase().trim();
//	            int orderIndex = trim.indexOf("order");
//	            if (orderIndex == -1) {
//	                buffer.append(" and ").append(" ( ").append(subQuery).append(
//	                        " ) ");
//	            } else {
//	                if (!trim.startsWith("order"))
//	                    buffer.append(" and (").append(
//	                            subQuery.substring(0, orderIndex)).append(") ")
//	                            .append(subQuery.substring(orderIndex));
//	                else {
//	                    buffer.append(subQuery);
//	                }
//	            }
//	            if (_logger.isDebugEnabled()){
//	                _logger.debug(buffer.toString());
//	                _logger.debug("Parameter 1:" + user.getUserID());
//	                _logger.debug("Parameter 2:" + role);
//	            }
//	            q = _entityManager.createQuery(buffer.toString()).setParameter(1,
//	                    user.getUserID()).setParameter(2, role);
//	        }
//	        return q;
//	}

	public Task[] fetchAvailableTasks(Map parameters) {
//	    UserRoles user = (UserRoles) parameters.get(FETCH_USER);
//	    Set<String> roles = user.getAssignedRoles();
//	    Set result = new HashSet();
//	    for(String role : roles){
//	        Query q = buildQueryForSingleRole(parameters, role);
//	        int first = MapUtils.getIntValue(parameters, FETCH_FIRST, -1);
//	        int max = MapUtils.getIntValue(parameters, FETCH_MAX, -1);
//
//	        // WARNING: there is a bug in OpenJPA 2.0. You need to call setMax #before# setFirst
//	        if (max > 0)
//	            q.setMaxResults(max);
//	        if (first >= 0)
//	            q.setFirstResult(first);
//	        List singleRoleResult = q.getResultList();
//	        for( Object object : singleRoleResult){
//	            result.add(object);
//	        }
//	    }
		Query q = buildQuery(parameters);
		int first = MapUtils.getIntValue(parameters, FETCH_FIRST, -1);
		int max = MapUtils.getIntValue(parameters, FETCH_MAX, -1);

		// WARNING: there is a bug in OpenJPA 2.0. You need to call setMax #before# setFirst
		if (max > 0)
			q.setMaxResults(max);
		if (first >= 0)
			q.setFirstResult(first);
		if(_logger.isDebugEnabled())
            _logger.debug("Executing query: "+q.toString());
		List result = q.list();
		List<Task> resultSet = new ArrayList<Task>();
		String subQuery = MapUtils.getString(parameters, FETCH_SUB_QUERY, "");
		if (subQuery.toLowerCase().indexOf("index(c)") >= 0) {
	        Iterator<Object> iterator = result.iterator();
	        while(iterator.hasNext())
	        {
	            Object[] row = (Object[]) iterator.next();
	            resultSet.add((Task)row[0]);
	        }
		}else {
			resultSet = (List<Task>)result;
		}
		if(_logger.isDebugEnabled())
            _logger.debug("Returning the resultSetList");
		return (Task[]) resultSet.toArray(new Task[resultSet.size()]);
	}

	/**
	 * Fetch the tasks for a given user
	 */
	public Task[] fetchTasksForUser(String user) {
		List<String> params = new ArrayList<String>();
		params.add(user);
		Query q = session.getNamedQuery(Task.FIND_BY_USER)
				.setParameterList("users", params);
		List result = q.list();
		return (Task[]) result.toArray(new Task[result.size()]);
	}

	/**
	 * Fetch the tasks for a given role
	 */
	public Object[] fetchTasksForRole(String role) {
		List<String> params = new ArrayList<String>();
		params.add(role);
		Query q = session.getNamedQuery(Task.FIND_BY_ROLE)
				.setParameterList("roles", params);
		List result = q.list();
		return (Task[]) result.toArray(new Task[result.size()]);
	}

	/**
	 * Fetch Custom Columns from its Process Name.
	 * 
	 * @throws UnavailableTaskException
	 */
	public List<CustomColumn> fetchCustomColumnIfExistsfromprocessname(
			String processName) throws UnavailableTaskException {
       try {
           Query q = session.getNamedQuery(CustomColumn.FIND_BY_PROCESS_NAME);
           q.setParameter(0, processName);
           List<CustomColumn> resultList = q.list();
//           if (resultList.size() < 1)
//               throw new UnavailableTaskException("Custom Column does not exist with ProcessName"
//                       + processName);
           return  resultList;
    	       } catch (NoResultException nre) {
           throw new UnavailableTaskException("Custom Column does not exist " + processName + nre);
       }
	}
	
	public List<CustomColumn> fetchCustomColumns(){
	        Query q = session.getNamedQuery( CustomColumn.FIND_ALL_CUSTOM_COLUMNS);
	        List<CustomColumn> resultList = q.list();
	        return resultList;
	}
	
	public PIPATaskOutput fetchPIPATaskOutput(String taskId, String userOwner) {
		   Query query = find_pipa_task_output_by_task_id.setParameter(0, taskId).setParameter(1, userOwner);
		   List resultList = query.list();
		   PIPATaskOutput pipaTaskOutput=null;
		   if(resultList!= null && !resultList.isEmpty()){
			   pipaTaskOutput = (PIPATaskOutput) resultList.get(0);
			   
		   }
		   return pipaTaskOutput;
	}
	/**
     * Fetch the pending and claimed task count for all users.
     */
    public List<Object> fetchTaskCountForUsers() {
        Query q = session.getNamedQuery(PATask.GET_PENDING_CLAIMED_TASK_COUNT_FOR_ALL_USERS);
        List result = q.list();
        List <Object>resultSet = new ArrayList<Object>();
        
        Iterator<Object> iterator = result.iterator();
        while(iterator.hasNext())
        {
            Object[] row = (Object[]) iterator.next();
            HashMap <String,Object>rowData = new HashMap<String,Object>();
            rowData.put("Count",row[0]);
            rowData.put("State",((TaskState)row[1]).getName());
            rowData.put("User",row[2]);
            resultSet.add(rowData);
        }
        
        return resultSet;
    }

    /**
     * Fetch the pending and claimed task count for all users.
     */
    public List<Object> fetchTaskDistributionByUserStatusAndTime(Date since, Date until,
            List<String> users, List<String> statusList) {
        List<TaskState> taskStatusList = new ArrayList<TaskState>();

        if (statusList == null) {
            taskStatusList.add(TaskState.READY);
            taskStatusList.add(TaskState.CLAIMED);
            taskStatusList.add(TaskState.COMPLETED);
        } else {
            for (String status : statusList) {
                taskStatusList.add(TaskState.valueOf(status));
            }
        }

        Query q = null;

        if (users != null) {
            q = session.getNamedQuery(PATask.GET_TASK_DISTRIBUTION_FOR_USERS_BASED_ON_TIME);
            q.setParameter("since", since).setParameter("until", until).setParameterList("userOwners", users)
                    .setParameterList("states", taskStatusList);
        } else {
            q = session.getNamedQuery(PATask.GET_TASK_DISTRIBUTION_BY_USERS_BASED_ON_TIME);
            q.setParameter("since", since).setParameter("until", until).setParameterList("states",
                    taskStatusList);
        }

        List result = q.list();
        List<Object> resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            HashMap<String, Object> rowData = new HashMap<String, Object>();
            rowData.put("Count", row[0]);
            rowData.put("State", ((TaskState) row[1]).getName());
            rowData.put("User", row[2]);
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the pending and claimed task count for all users.
     */
    public List<Object> fetchTaskDistributionByRoleStatusAndTime(Date since, Date until,
            List<String> roles, List<String> statusList) {
        List<TaskState> taskStatusList = new ArrayList<TaskState>();

        if (statusList == null) {
            taskStatusList.add(TaskState.READY);
            taskStatusList.add(TaskState.CLAIMED);
            taskStatusList.add(TaskState.COMPLETED);
        } else {
            for (String status : statusList) {
                taskStatusList.add(TaskState.valueOf(status));
            }
        }

        Query q = null;

        if (roles != null) {
            q = session.getNamedQuery(PATask.GET_TASK_DISTRIBUTION_FOR_ROLES_BASED_ON_TIME);
            q.setParameter("since", since).setParameter("until", until).setParameterList("roleOwners", roles)
                    .setParameterList("states", taskStatusList);
        } else {
            q = session.getNamedQuery(PATask.GET_TASK_DISTRIBUTION_BY_ROLES_BASED_ON_TIME);
            q.setParameter("since", since).setParameter("until", until).setParameterList("states",
                    taskStatusList);
        }

        List result = q.list();
        List<Object> resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            HashMap<String, Object> rowData = new HashMap<String, Object>();
            rowData.put("Count", row[0]);
            rowData.put("State", ((TaskState) row[1]).getName());
            rowData.put("Role", row[2]);
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the average completion time for tasks assigned to given users.
     */
    public List<Object> fetchAverageTaskCompletionTime(Date since, Date until,
            List<String> users) {

        Query q = null;

        q = session.getNamedQuery(PATask.GET_AVERAGE_COMPLETION_TIME_BY_USER);
        q.setParameter("since", since).setParameter("until", until)
                .setParameterList("userOwners", users);

        List result = q.list();
        List<Object> resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            HashMap<String, Object> rowData = new HashMap<String, Object>();
            rowData.put("EndDate", row[0]);
            rowData.put("StartDate", row[1]);
            rowData.put("User", row[2]);
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the task count for all states.
     */
    public List<Object> fetchTaskCountByStatus(Date since, Date until) {
        Query q = session.getNamedQuery(PATask.GET_TASK_COUNT_BY_STATUS);
        q.setParameter("since", since);
        q.setParameter("until", until);

        List result = q.list();
        List <Object>resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while(iterator.hasNext())
        {
            Object[] row = (Object[]) iterator.next();
            HashMap <String,Object>rowData = new HashMap<String,Object>();
            rowData.put("Count",row[0]);
            rowData.put("State",((TaskState)row[1]).getName());
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the task count for all priorities.
     */
    public List<Object> fetchTaskCountByPriority(Date since, Date until) {
        Query q = session.getNamedQuery(PATask.GET_TASK_COUNT_BY_PRIORITY);
        q.setParameter("since", since);
        q.setParameter("until", until);

        List result = q.list();
        List <Object>resultSet = new ArrayList<Object>();

        Iterator<Object> iterator = result.iterator();
        while(iterator.hasNext())
        {
            Object[] row = (Object[]) iterator.next();
            HashMap <String,Object>rowData = new HashMap<String,Object>();
            rowData.put("Count",row[0]);
            rowData.put("Priority",row[1]);
            resultSet.add(rowData);
        }

        return resultSet;
    }

    /**
     * Fetch the task count by creation date.
     */
    public Map<String, Date> fetchTaskCountByCreationDate(Date since, Date until) {
        Query q = session.getNamedQuery(PATask.GET_TASK_COUNT_BY_CREATION_DATE);
        q.setParameter("since", since);
        q.setParameter("until", until);

        Map<String, Date> taskCreationMap = new HashMap<String, Date>();

        List result = q.list();
        Iterator<Object> iterator = result.iterator();

        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            String taskId = (String) row[0];
            Date taskCreationDate = (Date) row[1];
            taskCreationMap.put(taskId, taskCreationDate);
        }

        return taskCreationMap;
    }

    public Map<String, Long> getMaxTaskCompletionForUsers(Date since,
            Date until) {

        Map<String, Long> taskSummary = new HashMap<String, Long>();
        Query q = null;

        q = session.getNamedQuery(PATask.GET_COMPLETED_TASK_COUNT_FOR_USERS);
        q.setParameter("since", since).setParameter("until", until);

        List result = q.list();

        Iterator<Object> iterator = result.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            taskSummary.put((String) row[1], (Long)row[0]);
        }

        return taskSummary;
    }

    public void beginTransaction() {
        this.session.beginTransaction();
    }

    public void commitTransaction() {
        if(this.session.getTransaction().isActive()) {
            this.session.getTransaction().commit();
        }
    }

    /**
     * This method is used to replace TaskState and PIPATaskState with
     * fully qualified class name.
     * @param taskState task state type
     * @param subQuery sub query to replace task state
     * @return string subQuery
     */
    private String replaceTaskStatePath(String taskState, String subQuery) {
        if (subQuery.indexOf(taskState + PACKAGE_SEPERATOR) >= 0
                && subQuery.indexOf(PACKAGE_SEPERATOR + taskState
                        + PACKAGE_SEPERATOR) == -1) {
            subQuery = subQuery.replaceAll(taskState + PACKAGE_SEPERATOR,
                    PACKAGE_PATH + PACKAGE_SEPERATOR + taskState
                            + PACKAGE_SEPERATOR);
        }
        return subQuery;
    }
}
