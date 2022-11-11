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
import api.filters.FsessionAdmin;
import global.InitVariable;
import utils.JsonCustom;

@WebServlet(urlPatterns = {"/admin/remove_all_user"})
public class AdminAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String url = req.getRequestURI();
        String host = InitVariable.HOST;
        if(url.equals(host+"/admin/remove_all_user")){
            removeAllUser(req,resp);
        }
    }
    private void removeAllUser(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject jsonResp = new JSONObject();
        try {
            JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
            System.out.println("REQUEST DATA: " + objReq.toString());
            String session = objReq.getString("session");
            int ok = FCheckSession.SessionFilter(session);
            if(ok==0){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            if(ok==2){
                jsonResp.put("code",700);
                jsonResp.put("description","Hết phiên đăng nhập");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            if(!FsessionAdmin.FCheckSessionAdmin(session)){
                jsonResp.put("code",500);
                jsonResp.put("description","không dủ quyền");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            //
            
            jsonResp.put("code",200);
            jsonResp.put("description","Thành công");
            
        } catch (Exception e) {
            jsonResp.put("code",300);
            jsonResp.put("description",e.getMessage());
        }
        writer.print(jsonResp);
        writer.close();
    }
}
