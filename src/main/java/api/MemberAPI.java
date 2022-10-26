package api;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import api.filters.FCheckSession;
import controller.user.MemberDAO;
import global.InitVariable;
import model.entity.Group;
import model.entity.Permission;
import model.entity.ThanhVien;
import model.entity.UserLogin;
import utils.JsonCustom;


@WebServlet(urlPatterns = {"/profile/member"})
public class MemberAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String url = req.getRequestURI();
        String host = "/quanlynhahangapi";
        if(url.equals(host+"/profile/member")){
            getProfile(req,resp);
        }
    }

    private void getProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject resp1 = new JSONObject();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        try {
            // int mem_id = objReq.getInt("mem_id");
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                resp1.put("code", 700);
                resp1.put("description", "Người dùng chưa đăng nhập");
            }else{
                MemberDAO memdao = new MemberDAO();
                if(!memdao.connect()){
                    resp1.put("code",500);
                    resp1.put("description","Không kết nối được CSDL");
                }else{
                    //lay thong tin thanh vien trong danh sach user dang dang nhap tren server
                    ThanhVien member = new ThanhVien();
                    UserLogin user = new UserLogin(session,null);
                    int index = InitVariable.ListUserLogin.indexOf(user);
                    UserLogin userLogin = InitVariable.ListUserLogin.get(index);
                    member.setId(userLogin.getKh().getId());
                    // lay  nhom cua user
                    memdao.checkGroup(member);
                    // tra ket qua cho client
                    resp1.put("code",200);
                    resp1.put("description","Thành công");
                    JSONObject result = new JSONObject();
                    result.put("mem_id", member.getId());
                    result.put("mem_fullname", userLogin.getKh().getTendaydu());
                    result.put("mem_username", userLogin.getKh().getTendangnhap());
                    for(Group g : member.getGroups()){
                        JSONObject jsonGroup = new JSONObject();
                        for(Permission p: g.getPermissions()){
                            jsonGroup.put("is_"+p.getName().toLowerCase(), true);
                        }
                        result.put(g.getName().toLowerCase(), jsonGroup);
                    }
                    resp1.put("result", result);
                    memdao.close();
                }
            }
        } catch (Exception e) {
            resp1.put("code",300);
            resp1.put("description",e.getMessage());
        }
        writer.println(resp1.toString());
        writer.close();
    }
}