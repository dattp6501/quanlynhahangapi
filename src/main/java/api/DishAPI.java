package api;

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
import api.filters.FSessionManager;
import controller.DishDAO;
import model.entity.Dish;
import utils.JsonCustom;


@WebServlet(urlPatterns = {"/service/get_list_dish","/service/add_dishs","/service/update_dishs"})
public class DishAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String url = req.getRequestURI();
        String host = "/quanlynhahangapi";
        if(url.equals(host+"/service/get_list_dish")){
            getListDish(req,resp);
        }else if(url.equals(host+"/service/add_dishs")){
            addDishs(req, resp);
        }else if(url.equals(host+"/service/update_dishs")){
            updateDishs(req, resp);
        }
    }
    //---------------------------get list dish------------------
    private void getListDish(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
            }else{
                String name = objReq.getString("name");
                DishDAO dDAO = new DishDAO();
                if(!dDAO.connect()){
                    jsonResp.put("code",300);
                    jsonResp.put("description","không kết nối được CSDL");
                }else{
                    JSONObject jsonResult = new JSONObject();
                    ArrayList<Object> listD = new ArrayList<>();
                    for(Dish d : dDAO.get(name)){
                        JSONObject jsonD = new JSONObject();
                        jsonD.put("dish_id", d.getId());
                        jsonD.put("dish_name", d.getTen());
                        jsonD.put("dish_price", d.getGia());
                        jsonD.put("dish_sizes", d.getSize());
                        jsonD.put("dish_note",d.getMota());
                        if(d.getAnh()!=null){
                            jsonD.put("dish_image", d.getAnh());
                        }
                        listD.add(jsonD);
                    }
                    jsonResult.put("list",listD);
                    jsonResp.put("result", jsonResult);
                    jsonResp.put("code",200);
                    jsonResp.put("description","Thành công");
                }
            }
        } catch (Exception e) {
            jsonResp.put("code",300);
            jsonResp.put("description",e.getMessage());
        }
        writer.print(jsonResp);
        writer.close();
    }

    //=============================     ADMIN        ========================
    private void addDishs(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
            }else{
                if(!FSessionManager.FCheckSessionManager(session)){
                    jsonResp.put("code",500);
                    jsonResp.put("description","không dủ quyền");
                }else{
                    // get list dish
                    ArrayList<Dish> listD = new ArrayList<>();
                    JSONArray jsonListDish = objReq.getJSONArray("list_dish");
                    for(Object jsonD : jsonListDish){
                        String name = ((JSONObject) jsonD).getString("name_dish");
                        float price = ((JSONObject) jsonD).getFloat("price_dish");
                        String sizes = ((JSONObject) jsonD).getString("sizes_dish");
                        String anhBase64 = null;
                        try {
                            anhBase64 = ((JSONObject) jsonD).getString("image_dish");
                        } catch (Exception e) {
                        }
                        String note = ((JSONObject) jsonD).getString("note");
                        Dish dish = new Dish(name, note, sizes, price, anhBase64, -1);
                        listD.add(dish);
                    }
                    //add list dish
                    DishDAO dDAO = new DishDAO();
                    if(!dDAO.connect()){
                        jsonResp.put("code",300);
                        jsonResp.put("description","không kết nối được CSDL");
                    }else{
                        if(!dDAO.add(listD)){
                            jsonResp.put("code",300);
                            jsonResp.put("description","không thêm được món mới");
                        }else{
                            jsonResp.put("code",200);
                            jsonResp.put("description","Thành công");
                        }
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
    private void updateDishs(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
            }else{
                if(!FSessionManager.FCheckSessionManager(session)){
                    jsonResp.put("code",500);
                    jsonResp.put("description","không dủ quyền");
                }else{
                    // get list dish
                    ArrayList<Dish> listD = new ArrayList<>();
                    JSONArray jsonListDish = objReq.getJSONArray("list_dish");
                    for(Object jsonD : jsonListDish){
                        String name = ((JSONObject) jsonD).getString("name_dish");
                        float price = ((JSONObject) jsonD).getFloat("price_dish");
                        String sizes = ((JSONObject) jsonD).getString("sizes_dish");
                        String anhBase64 = null;
                        try {
                            anhBase64 = ((JSONObject) jsonD).getString("image_dish");
                        } catch (Exception e) {
                        }
                        String note = ((JSONObject) jsonD).getString("note");
                        int id = ((JSONObject) jsonD).getInt("id_dish");
                        Dish dish = new Dish(name, note, sizes, price, anhBase64, id);
                        listD.add(dish);
                    }
                    //add list dish
                    DishDAO dDAO = new DishDAO();
                    if(!dDAO.connect()){
                        jsonResp.put("code",300);
                        jsonResp.put("description","không kết nối được CSDL");
                    }else{
                        if(!dDAO.updateDishs(listD)){
                            jsonResp.put("code",300);
                            jsonResp.put("description","không thêm được món mới");
                        }else{
                            jsonResp.put("code",200);
                            jsonResp.put("description","Thành công");
                        }
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