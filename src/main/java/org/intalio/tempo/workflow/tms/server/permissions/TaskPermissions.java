package org.intalio.tempo.workflow.tms.server.permissions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.intalio.tempo.workflow.auth.UserRoles;
import org.intalio.tempo.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskPermissions {

    public static final Logger LOG = LoggerFactory.getLogger(TaskPermissions.class);
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_READ = "read";

    public static final String ACTION_UPDATE_DUE_DATE = "updateDueDate";

    private static String TASK_TAB = "task";

    private Map<String, Map<String, Set<String>>> _toolbarIconSets;
    private Map<String, Set<String>> _bindIconSetToRole;

    public TaskPermissions(Map<String, Map<String, Set<String>>> toolbarIconSets,
            Map<String, Set<String>> bindIconSetToRole) {
        _toolbarIconSets = toolbarIconSets;
        _bindIconSetToRole = bindIconSetToRole;
    }

    /**
     * check if the action is possible on the task with the given credentials
     */
    public boolean isAuthorized(String action, Task t, UserRoles credentials) {

        Set<String> roles = credentials.getAssignedRoles();
        roles.add(credentials.getUserID());

        HashSet<String> mappedRoleSet = getKeysFromValues(_bindIconSetToRole, roles);
        for (String mappedRole : mappedRoleSet) {
            Map<String, Set<String>> iconSetByRole = _toolbarIconSets
                    .get(mappedRole);
            if(iconSetByRole.containsKey(TASK_TAB)) {
                Set<String> icons = iconSetByRole.get(TASK_TAB);
                if (icons.contains(action)) {
                    return true;
                }
            }

        }

        return false;
    }
    
    public boolean isAuthroized(String action,UserRoles credentials){
        Set<String> roles = credentials.getAssignedRoles();
        roles.add(credentials.getUserID());

        HashSet<String> mappedRoleSet = getKeysFromValues(_bindIconSetToRole, roles);
        for (String mappedRole : mappedRoleSet) {
            Map<String, Set<String>> iconSetByRole = _toolbarIconSets
                    .get(mappedRole);
            if(iconSetByRole.containsKey(TASK_TAB)) {
                Set<String> icons = iconSetByRole.get(TASK_TAB);
                if (icons.contains(action)) {
                    return true;
                }
            }

        }

        return false;
    }

    private HashSet<String> getKeysFromValues(Map<String, Set<String>> hm, Set<String> values) {
        HashSet<String> list = new HashSet<String>();
        for (String value : values) {
            for (String key : hm.keySet()) {
                for(String role:hm.get(key)) {
                    if (role.equalsIgnoreCase(value)) {
                        list.add(key);
                    }
                }
            }
        }

        return list;
    }
}
