package api.admin;

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
import controller.PermissionGroupDAO;
import model.entity.Group;
import model.entity.GroupPermission;
import model.entity.Permission;
import utils.JsonCustom;


@WebServlet(urlPatterns = "/admin/add_permissions_group")
public class AddPermissionForGroupAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
            }else{
                ArrayList<GroupPermission> listGP = new ArrayList<>();
                JSONArray jsonListGP = objReq.getJSONArray("list");
                for(Object jsonGP : jsonListGP){
                    int groupId = ((JSONObject)jsonGP).getInt("group_id");
                    int permissionId = ((JSONObject)jsonGP).getInt("permission_id");
                    Group g = new Group(); g.setId(groupId);
                    Permission p = new Permission(); p.setId(permissionId);
                    GroupPermission gp = new GroupPermission(-1, p, g);
                    listGP.add(gp);
                }
                PermissionGroupDAO PGDAO = new PermissionGroupDAO();
                if(!PGDAO.connect()){
                    jsonResp.put("code",300);
                    jsonResp.put("description","không kết nối được CSDL");
                }else{
                    if(PGDAO.add(listGP)){
                        jsonResp.put("code",200);
                        jsonResp.put("description","thành công");
                    }else{
                        jsonResp.put("code",300);
                        jsonResp.put("description","thêm quyền thất bại");
                    }
                }
            }
        } catch (Exception e) {
            jsonResp.put("code",300);
            jsonResp.put("description",e.getMessage());
        }
        writer.print(jsonResp);
        writer.close();
    }
}
