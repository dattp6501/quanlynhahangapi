package api.admin.groups;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import api.filters.FCheckSession;
import controller.user.PermissionGroupDAO;
import model.entity.Group;
import model.entity.Permission;
import utils.JsonCustom;

@WebServlet(urlPatterns = "/admin/remove_permissions_group")
public class RemovePermissionGroupAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        JSONObject resp1 = new JSONObject();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        try {
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                resp1.put("code", 700);
                resp1.put("description", "Người dùng chưa đăng nhập");
            }else{
                if(!FCheckSession.FCheckSessionAdmin(session)){
                    resp1.put("code", 500);
                    resp1.put("description", "Không đủ quyền");
                }else{
                    int groupid = objReq.getInt("group_id");
                    ArrayList<Permission> permissions = new ArrayList<>();
                    JSONArray jsonListPermission = objReq.getJSONArray("permissions");
                    for(Object per : jsonListPermission){
                        int permissionId = ((JSONObject)per).getInt("permission_id");
                        Permission permission = new Permission(permissionId, "", "");
                        permissions.add(permission);
                    }
                    Group g = new Group(groupid, "", "", permissions);
                    PermissionGroupDAO PGDAO = new PermissionGroupDAO();
                    if(!PGDAO.connect()){
                        resp1.put("code",500);
                        resp1.put("description","Không kết nối được CSDL");
                    }else{
                        if(!PGDAO.remove(g)){
                            resp1.put("code",300);
                            resp1.put("description","không cập nhật được quyền");
                        }else{
                            resp1.put("code",200);
                            resp1.put("description","thành công");
                        }
                    }
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