package api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import api.filters.FCheckSession;
import controller.CustomerDAO;
import controller.MemberDAO;
import global.InitVariable;
import model.entity.Customer;
import model.entity.UserLogin;
import utils.JsonCustom;


@WebServlet(urlPatterns = {"/login/customer","/register/customer","/logout/customer"})
public class CustomerAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String url = req.getRequestURI();
        String host = InitVariable.HOST;
        if(url.equals(host+"/login/customer")){
            customerLogin(req,resp);
        }else if(url.equals(host+"/register/customer")){
            customerRegister(req,resp);
        }else if(url.equals(host+"/logout/customer")){
            customerLogout(req,resp);
        }
    }
    //-----------------------------------login-------------------------
    private void customerLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject resp1 = new JSONObject();
        try {
            JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
            System.out.println("REQUEST DATA: " + objReq.toString());
            String usenname = objReq.getString("tendangnhap");
            String password = objReq.getString("matkhau");
            Customer customer = new Customer(usenname,password,null,-1,null,null,null);
            CustomerDAO cusdao = new CustomerDAO();
            if(!cusdao.connect()){
                resp1.put("code",500);
                resp1.put("description","Không kết nối được CSDL");
                writer.println(resp1.toString());
                writer.close();
                return;
            }
            if(!cusdao.kiemTraDangNhap(customer)) {// tai khoan hoac mat khau sai
                resp1.put("code", 401);
                resp1.put("description","Sai tên đăng nhập hoặc mật khẩu");
                writer.println(resp1.toString());
                writer.close();
                return;
            }
            // tai khoan mat khau dung
            UserLogin newUser = new UserLogin(null,customer);
            if(!addUserToListUserLogin(newUser)){
                resp1.put("code",503);
                resp1.put("description","Hệ thống đang quá tải! vui lòng vào lại sau ít phút :)");
                writer.println(resp1.toString());
                writer.close();
                return;
            }
            if(!checkPermission(newUser, cusdao.getConnection())){
                resp1.put("code",500);
                resp1.put("description", "Không có quyền đăng nhập");
                writer.println(resp1.toString());
                writer.close();
                return;
            }
            newUser.setSession(generateSession(InitVariable.LIST_USER_LOGIN));
            writeUserLogIn(newUser);
            resp1.put("code",200);
            resp1.put("description", "Đăng nhập thành công");
            resp1.put("session", newUser.getSession());
            cusdao.close();
        } catch (Exception e) {
            resp1.put("code",300);
            resp1.put("description",e.getMessage());
        }
        writer.println(resp1.toString());
        writer.close();
    }
    private boolean checkPermission(UserLogin userLogin, Connection connection) throws SQLException{
        MemberDAO memDAO = new MemberDAO();
        memDAO.setConnection(connection);
        if(!memDAO.checkGroup(userLogin.getKh())){
            return false;
        }
        return true;
    }
    private boolean addUserToListUserLogin(UserLogin newUser){
        if(InitVariable.LIST_USER_LOGIN.size() >= InitVariable.USER_LOGIN_NUMBER){// qua gioi hang bo nho he thong
            return false;
        }
        InitVariable.LIST_USER_LOGIN.add(newUser);
        return true;
    }
    private String generateSession(ArrayList<UserLogin> listUserLogin){
        UUID session = UUID.randomUUID();
        return session.toString();
    }
    private boolean writeUserLogIn(Object user){
        try {
            Customer customer = (Customer) user;
            System.out.println(customer);
        } catch (Exception e) {
        }
        return true;
    }
    //---------------------------------------register----------------------------
    private void customerRegister(HttpServletRequest req, HttpServletResponse resp) throws JSONException, IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject resp1 = new JSONObject();
        try {
            JSONObject objReq = new JSONObject(JsonCustom.JsonToString(req.getReader()).toString());
            System.out.println("REQUEST DATA: " + objReq.toString());
            String fullname = objReq.getString("tendaydu");
            String username = objReq.getString("tendangnhap");
            String password = objReq.getString("matkhau");
            String address = objReq.getString("diachi");
            String mobile = objReq.getString("sdt");
            Customer customerReq = new Customer(username, password, fullname,-1, mobile, address, null);
            CustomerDAO cusdao = new CustomerDAO();
            if(!cusdao.connect()){// connect DB error
                resp1.put("code",500);
                resp1.put("description","Không kết nối được CSDL");
                writer.println(resp1.toString());
                writer.close();
                return;
            }
            // connect database success
            if(!cusdao.add(customerReq)){
                resp1.put("code",300);
                resp1.put("description","Không đăng kí được");
                writer.println(resp1.toString());
                writer.close();
                return;
            }
            cusdao.close();
            resp1.put("code",200);
            resp1.put("description","Đăng kí thành công");
        } catch (Exception e) {
            resp1.put("code",300);
            resp1.put("description",e.getMessage());
        }
        writer.println(resp1.toString());
        writer.close();
    }
    //-----------------------------------------logout----------------------------------
    private void customerLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject resp1 = new JSONObject();
        try {
            JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
            System.out.println("REQUEST DATA: " + objReq.toString());
            String session = objReq.getString("session");
            int ok = FCheckSession.SessionFilter(session);
            if(ok==0){
                resp1.put("code", 700);
                resp1.put("description", "Người dùng chưa đăng nhập");
                writer.println(resp1.toString());
                writer.close();
                return;
            }
            if(ok == 2){
                resp1.put("code",200);
                resp1.put("description","Đăng xuất thành công");
                writer.println(resp1.toString());
                writer.close();
                return;
            }
            if(!logout(session)){
                resp1.put("code",300);
                resp1.put("description","Đăng xuất thất bại");
                writer.println(resp1.toString());
                writer.close();
                return;
            }
            resp1.put("code",200);
            resp1.put("description","Đăng xuất thành công");
        }catch (Exception e) {
            resp1.put("code",300);
            resp1.put("description",e.getMessage());
        }
        writer.println(resp1.toString());
        writer.close();
    }
    private boolean logout(String session){
        return InitVariable.LIST_USER_LOGIN.remove(new UserLogin(session,null));
    }
}