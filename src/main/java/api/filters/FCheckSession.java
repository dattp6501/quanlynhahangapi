package api.filters;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
// import javax.servlet.annotation.WebFilter;
import org.json.JSONObject;
import global.InitVariable;
import model.entity.Permission;
import model.entity.UserLogin;
import utils.JsonCustom;


// @WebFilter(urlPatterns = {"/service/*","/profile/*","/logout/*"})
public class FCheckSession implements Filter{
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)throws IOException, ServletException {
        resp.setContentType("application/json;charset=UTF-8");
        JSONObject reqJson;
        BufferedReader reader = req.getReader();
        reader.mark(1);
        reqJson = JsonCustom.toJsonObject(reader);
        reader.reset();

        JSONObject resp1 = new JSONObject();
        try {
            String session = reqJson.getString("session");
            int ok = SessionFilter(session);
            if(ok == 0){// da dang nhap hop le
                resp1.put("code", 700);
                resp1.put("description", "Hết phiên đăng nhập");
                resp.getWriter().println(resp1.toString());
            }else if(ok==1){
                chain.doFilter(req, resp);
            }else{
                resp1.put("code", 700);
                resp1.put("description", "Người dùng chưa đăng nhập");
                resp.getWriter().println(resp1.toString());
            }
        } catch (Exception e) {
            resp1.put("code",300);
            resp1.put("description",e.getMessage());
            resp.getWriter().println(resp1.toString());
        }
    }

    public static boolean checkSession(String session){
        UserLogin user = new UserLogin(session,null);
        return InitVariable.ListUserLogin.contains(user);
    }

    public static int SessionFilter(String session){
        //0 chua dang nhap
        //1 thanh cong
        //2 session het thoi gian
        UserLogin user = new UserLogin(session,null);
        int index = InitVariable.ListUserLogin.indexOf(user);
        if(index < 0){
            return 0;
        }
        user = InitVariable.ListUserLogin.get(index);
        long time_current = new Date().getTime();
        if(time_current<user.getTime()){
            return 1;
        }
        InitVariable.ListUserLogin.remove(user);
        return 2;
    }

    public static boolean FCheckSessionAdmin(String session){
        int index = InitVariable.ListUserLogin.indexOf(new UserLogin(session, null));
        UserLogin userLogin = InitVariable.ListUserLogin.get(index);
        ArrayList<Permission> permissions = userLogin.getKh().getGroup().getPermissions();
        return permissions.contains(new Permission(6, "MANAGER_ALL_GROUP", ""));
    }

    public static void main(String[] args) {
        ArrayList<UserLogin> list = new ArrayList<>();
        for(int i=0; i<10; i++){
            list.add(new UserLogin(""+String.format("%d",i), null));
        }
        System.out.println(list.indexOf(new UserLogin("5", null)));
    }
}
