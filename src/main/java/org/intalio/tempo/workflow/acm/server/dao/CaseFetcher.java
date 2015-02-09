package org.intalio.tempo.workflow.acm.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.intalio.tempo.acm.server.CaseType;
import org.intalio.tempo.workflow.task.PIPATaskOutput;
import org.intalio.tempo.workflow.task.Task;
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
	private Session session;
	private Query selectAllTypes;
	private Query selectCaseById;

	private static String DATABASE_PRODUCT_NAME = "";

	public CaseFetcher(EntityManager em) {
		this.session = em.unwrap(Session.class);
		this.selectAllTypes = session.getNamedQuery(CaseType.SelectAll);
		this.selectCaseById = session.getNamedQuery(CaseType.SelectCaseById);

		/*
		 * Fix for CON-794 Getting database name from JPA Entity Manager. For
		 * using Group By clause for MYSQL and DISTINCT for other databases
		 */
		if ("".equals(DATABASE_PRODUCT_NAME)) {
			Connection conn = null;
			try {
				conn = session.connection();
				java.sql.DatabaseMetaData dbmd = conn.getMetaData();
				DATABASE_PRODUCT_NAME = dbmd.getDatabaseProductName();
			} catch (Exception e) {
				_logger.error("Error while getting Database name ", e);
			} finally {
				if (conn != null)
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
	public List<CaseType> selectAll() {

		Query q = selectAllTypes;
		List<CaseType> resultList = q.list();

		return resultList;

	}

	public CaseList selectCases(String caseType)
			throws UnavailableTaskException {
		PreparedStatement ps;
		try {
			ps = session.connection().prepareStatement(
					"select * from " + caseType);

			ResultSet h = ps.executeQuery();
			ResultSetMetaData metadata = h.getMetaData();

			List<DBColumn> columns = new ArrayList<DBColumn>();
			for (int i = 0; i < metadata.getColumnCount(); i++) {
				DBColumn column = new DBColumn(metadata.getColumnType(i + 1),
						metadata.getColumnName(i + 1));
				columns.add(column);
			}

			List<List> cases = new ArrayList<List>();
			while (h.next()) {

				List<Object> caseData = new ArrayList<Object>();
				for (int i = 0; i < metadata.getColumnCount(); i++) {

					caseData.add(h.getObject(i + 1));
				}
				cases.add(caseData);
			}
			CaseList caseList = new CaseList(caseType, columns, cases);

			return caseList;

			// List header=h.getMetaData();
			// return resultList;
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnavailableTaskException();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnavailableTaskException();
		}
	}

	// public List<Task> fetchTaskIfExistsfrominstanceID(String instanceid)
	// throws UnavailableTaskException {
	// try {
	// Query q = session.getNamedQuery(PATask.FIND_BY_INSTANCEID);
	// q.setParameter(0, instanceid);
	// List<Task> resultList = q.list();
	// if (resultList.size() < 1)
	// throw new UnavailableTaskException("Task does not exist with InstanceID"
	// + instanceid);
	// return resultList;
	// } catch (NoResultException nre) {
	// throw new UnavailableTaskException("Task does not exist" + instanceid);
	// }
	// }

	public void beginTransaction() {
		this.session.beginTransaction();
	}

	public void commitTransaction() {
		if (this.session.getTransaction().isActive()) {
			this.session.getTransaction().commit();
		}
	}

	public CaseHistory selectCaseHistory(String caseType, String caseId)
			throws UnavailableTaskException {
		PreparedStatement ps;
		try {
			ps = session.connection().prepareStatement(
					"select * from " + caseType + "_history where caseId='"
							+ caseId + "'");

			ResultSet h = ps.executeQuery();
			ResultSetMetaData metadata = h.getMetaData();

			List<DBColumn> columns = new ArrayList<DBColumn>();
			for (int i = 0; i < metadata.getColumnCount(); i++) {
				DBColumn column = new DBColumn(metadata.getColumnType(i + 1),
						metadata.getColumnName(i + 1));
				columns.add(column);
			}

			List<List> caseHistory = new ArrayList<List>();
			while (h.next()) {

				List<Object> caseHistoryEntry = new ArrayList<Object>();
				for (int i = 0; i < metadata.getColumnCount(); i++) {

					caseHistoryEntry.add(h.getObject(i + 1));
				}
				caseHistory.add(caseHistoryEntry);
			}
			CaseHistory caseList = new CaseHistory(caseType, columns,
					caseHistory, caseId);
			return caseList;
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnavailableTaskException();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnavailableTaskException();
		}
	}

	public Case selectCaseData(String caseType, String caseId)
			throws UnavailableTaskException {
		PreparedStatement ps;
		try {
			ps = session.connection().prepareStatement(
					"select * from " + caseType + " where id='" + caseId + "'");

			ResultSet h = ps.executeQuery();
			ResultSetMetaData metadata = h.getMetaData();

			List<DBColumn> columns = new ArrayList<DBColumn>();
			for (int i = 0; i < metadata.getColumnCount(); i++) {
				DBColumn column = new DBColumn(metadata.getColumnType(i + 1),
						metadata.getColumnName(i + 1));
				columns.add(column);
			}

			List caseData = new ArrayList();
			while (h.next()) {

				for (int i = 0; i < metadata.getColumnCount(); i++) {

					caseData.add(h.getObject(i + 1));
				}

			}

			return new Case(caseType, columns, caseData, caseId);
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnavailableTaskException();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnavailableTaskException();
		}
	}
	

	public CaseType selectCaseById(String caseTypeId) {
		Query q = selectCaseById;
		selectCaseById.setParameter(0, caseTypeId);
		CaseType resultList = (CaseType) q.uniqueResult();
	
		return resultList;
	
	}
}
