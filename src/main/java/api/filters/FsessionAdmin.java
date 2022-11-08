package api.filters;

import java.util.ArrayList;

import global.InitVariable;
import model.entity.Permission;
import model.entity.UserLogin;

public class FsessionAdmin {
    public static boolean FCheckSessionAdmin(String session){
        int index = InitVariable.LIST_USER_LOGIN.indexOf(new UserLogin(session, null));
        UserLogin userLogin = InitVariable.LIST_USER_LOGIN.get(index);
        ArrayList<Permission> permissions = userLogin.getKh().getGroup().getPermissions();
        return permissions.contains(new Permission(8, "ADMIN", ""));
    }
}