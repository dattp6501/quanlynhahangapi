package api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
import controller.user.CustomerDAO;
import controller.user.MemberDAO;
import global.InitVariable;
import model.entity.KhachHang;
import model.entity.UserLogin;
import utils.JsonCustom;


@WebServlet(urlPatterns = {"/login/customer","/register/customer","/logout/customer"})
public class CustomerAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String url = req.getRequestURI();
        String host = "/quanlynhahangapi";
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
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject resp1 = new JSONObject();
        try {
            String tendangnhap = objReq.getString("tendangnhap");
            String matkhau = objReq.getString("matkhau");
            KhachHang customer = new KhachHang(tendangnhap,matkhau,null,-1,null,null,null);
            CustomerDAO cusdao = new CustomerDAO();
            if(cusdao.connect()){
                if(!cusdao.kiemTraDangNhap(customer)) {// tai khoan hoac mat khau sai
                    resp1.put("code", 401);
                    resp1.put("description","Sai tên đăng nhập hoặc mật khẩu");
                } else {// tai khoan mat khau dung
                    UserLogin newUser = new UserLogin(null,customer);
                    if(!addUserToListUserLogin(newUser)){
                        resp1.put("code",503);
                        resp1.put("description","Hệ thống đang quá tải! vui lòng vào lại sau ít phút :)");
                    }else{
                        if(checkPermission(newUser, cusdao.getConnection())){
                            newUser.setSession(generateSession(InitVariable.ListUserLogin));
                            writeUserLogIn(newUser);
                            resp1.put("code",200);
                            resp1.put("description", "Đăng nhập thành công");
                            resp1.put("session", newUser.getSession());
                        }else{
                            resp1.put("code",500);
                            resp1.put("description", "Không có quyền đăng nhập");
                        }
                    }
                }
                cusdao.close();
            }else{
                resp1.put("code",500);
                resp1.put("description","Không kết nối được CSDL");
            }
        } catch (Exception e) {
            resp1.put("code",300);
            resp1.put("description",e.getMessage());
        }
        writer.println(resp1.toString());
        writer.close();
    }
    private boolean checkPermission(UserLogin userLogin, Connection connection){
        MemberDAO memDAO = new MemberDAO();
        memDAO.setConnection(connection);
        if(!memDAO.checkGroup(userLogin.getKh())){
            return false;
        }
        return true;
    }
    private boolean addUserToListUserLogin(UserLogin newUser){
        if(InitVariable.ListUserLogin.size() >= InitVariable.lengthListUserLogin){// qua gioi hang bo nho he thong
            return false;
        }
        InitVariable.ListUserLogin.add(newUser);
        return true;
    }
    private String generateSession(ArrayList<UserLogin> listUserLogin){
        UUID session = UUID.randomUUID();
        return session.toString();
    }
    private boolean writeUserLogIn(Object user){
        try {
            KhachHang customer = (KhachHang) user;
            System.out.println(customer);
        } catch (Exception e) {
        }
        return true;
    }
    //---------------------------------------register----------------------------
    private void customerRegister(HttpServletRequest req, HttpServletResponse resp) throws JSONException, IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq =new JSONObject(JsonCustom.JsonToString(req.getReader()).toString());
        JSONObject resp1 = new JSONObject();
        try {
            String tendaydu = objReq.getString("tendaydu");
            String tendangnhap = objReq.getString("tendangnhap");
            String matkhau = objReq.getString("matkhau");
            String diachi = objReq.getString("diachi");
            String sdt = objReq.getString("sdt");
            KhachHang customerReq = new KhachHang(tendangnhap, matkhau, tendaydu,-1, sdt, diachi, null);
            CustomerDAO cusdao = new CustomerDAO();
            if(cusdao.connect()){// connect database success
                if(!cusdao.themKhachhang(customerReq)){
                    resp1.put("code",300);
                    resp1.put("description","Không đăng kí được");
                }else{
                    resp1.put("code",200);
                    resp1.put("description","Đăng kí thành công");
                }
                cusdao.close();
            }else{// connect DB error
                resp1.put("code",500);
                resp1.put("description","Không kết nối được CSDL");
            }
            cusdao.close();
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
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject resp1 = new JSONObject();
        try {
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                resp1.put("code", 700);
                resp1.put("description", "Người dùng chưa đăng nhập");
            }else{
                if(logout(session)){
                    resp1.put("code",200);
                    resp1.put("description","Đăng xuất thành công");
                }else{
                    resp1.put("code",300);
                    resp1.put("description","Đăng xuất thất bại");
                }
            }
        }catch (Exception e) {
            resp1.put("code",300);
            resp1.put("description",e.getMessage());
        }
        writer.println(resp1.toString());
        writer.close();
    }
    private boolean logout(String session){
        return InitVariable.ListUserLogin.remove(new UserLogin(session,null));
    }
}