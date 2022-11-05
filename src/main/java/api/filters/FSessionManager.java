package api.filters;

import java.util.ArrayList;

import global.InitVariable;
import model.entity.Permission;
import model.entity.UserLogin;

public class FSessionManager {
    public static boolean FCheckSessionManager(String session){
        int index = InitVariable.ListUserLogin.indexOf(new UserLogin(session, null));
        UserLogin userLogin = InitVariable.ListUserLogin.get(index);
        ArrayList<Permission> permissions = userLogin.getKh().getGroup().getPermissions();
        return permissions.contains(new Permission(2, "MANAGER_TABLE", ""))
        &&permissions.contains(new Permission(3, "MANAGER_DISH", ""));
    }
}