package api;


import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import api.filters.FCheckSession;
import api.filters.FSessionManager;
import controller.TableDAO;
import model.entity.Table;
import model.entity.FreeTime;
import utils.JsonCustom;


@WebServlet(urlPatterns = {"/service/get_free_table","/service/add_tables","/service/get_all_table"})
public class TableAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String url = req.getRequestURI();
        String host = "/quanlynhahangapi";
        if(url.equals(host+"/service/get_free_table")){
            getFreeTable(req,resp);
        }else if(url.equals(host+"/service/add_tables")){
            addTables(req,resp);
        }else if(url.equals(host+"/service/get_all_table")){
            getAllTable(req,resp);
        }
    }
    //--------------------------------get free table------------------------------------
    private void getFreeTable(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            int ok = FCheckSession.SessionFilter(session);
            if(ok == 0){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            if(ok == 2){
                jsonResp.put("code",700);
                jsonResp.put("description","Hết phiên đăng nhập");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            TableDAO tableDAO = new TableDAO();
            if(!tableDAO.connect()){
                jsonResp.put("code",300);
                jsonResp.put("description","không kết nối được CSDL");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date startime = format.parse(objReq.getString("start_time"));
            Date endtime = format.parse(objReq.getString("end_time"));
            if(startime.compareTo(endtime)>=0){
                jsonResp.put("code",300);
                jsonResp.put("description","Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            JSONObject res = new JSONObject();
            ArrayList<Table> list = tableDAO.getFreeTable(new FreeTime(-1, startime, endtime, null));
            ArrayList<JSONObject> listJson = new ArrayList<>();
            for(Table t : list){
                JSONObject tableJson = new JSONObject();
                tableJson.put("table_id", t.getId());
                tableJson.put("table_name", t.getTen());
                tableJson.put("table_price", t.getGiathue());
                tableJson.put("table_amount_people", t.getSucchua());
                tableJson.put("table_note", t.getMota());
                ArrayList<JSONObject> listFreeTime = new ArrayList<>();
                for(FreeTime ft: t.getFreeTime()){
                    JSONObject freeTimeJson = new JSONObject();
                    freeTimeJson.put("free_time_id", ft.getId());
                    freeTimeJson.put("free_time_start_time", format.format(ft.getStarttime()));
                    freeTimeJson.put("free_time_end_time", format.format(ft.getEndtime()));
                    freeTimeJson.put("free_time_note", ft.getMota());
                    listFreeTime.add(freeTimeJson);
                }
                tableJson.put("free_times", listFreeTime);
                listJson.add(tableJson);
            }
            jsonResp.put("code",200);
            jsonResp.put("description","Thành công");
            res.put("table_number", list.size());
            res.put("list", listJson);
            jsonResp.put("result", res);
        } catch (Exception e) {
            jsonResp.put("code",300);
            jsonResp.put("description",e.getMessage());
        }
        writer.print(jsonResp);
        writer.close();
    }

    //--------------------------------get all table------------------------------------
    private void getAllTable(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            int ok = FCheckSession.SessionFilter(session);
            if(ok == 0){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            if(ok == 2){
                jsonResp.put("code",700);
                jsonResp.put("description","Hết phiên đăng nhập");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            TableDAO tableDAO = new TableDAO();
            if(!tableDAO.connect()){
                jsonResp.put("code",300);
                jsonResp.put("description","không kết nối được CSDL");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String name = objReq.getString("name");
            int limit = objReq.getInt("limit");
            JSONObject res = new JSONObject();
            ArrayList<Table> list = tableDAO.getAllTable(name, limit);
            ArrayList<JSONObject> listJson = new ArrayList<>();
            for(Table t : list){
                JSONObject tableJson = new JSONObject();
                tableJson.put("table_id", t.getId());
                tableJson.put("table_name", t.getTen());
                tableJson.put("table_price", t.getGiathue());
                tableJson.put("table_amount_people", t.getSucchua());
                tableJson.put("table_note", t.getMota());
                ArrayList<JSONObject> listFreeTime = new ArrayList<>();
                for(FreeTime ft: t.getFreeTime()){
                    JSONObject freeTimeJson = new JSONObject();
                    freeTimeJson.put("free_time_id", ft.getId());
                    freeTimeJson.put("free_time_start_time", format.format(ft.getStarttime()));
                    freeTimeJson.put("free_time_end_time", format.format(ft.getEndtime()));
                    freeTimeJson.put("free_time_note", ft.getMota());
                    listFreeTime.add(freeTimeJson);
                }
                tableJson.put("free_times", listFreeTime);
                listJson.add(tableJson);
            }
            jsonResp.put("code",200);
            jsonResp.put("description","Thành công");
            res.put("table_number", list.size());
            res.put("list", listJson);
            jsonResp.put("result", res);
        } catch (Exception e) {
            jsonResp.put("code",300);
            jsonResp.put("description",e.getMessage());
        }
        writer.print(jsonResp);
        writer.close();
    }





    //=========================   ADMIN    ======================
    private void addTables(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            int ok = FCheckSession.SessionFilter(session);
            if(ok == 0){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            if(ok == 2){
                jsonResp.put("code",700);
                jsonResp.put("description","Hết phiên đăng nhập");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            if(!FSessionManager.FCheckSessionManager(session)){
                jsonResp.put("code",500);
                jsonResp.put("description","không dủ quyền");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            // get list table from request
            ArrayList<Table> list = new ArrayList<>();
            JSONArray listTable = objReq.getJSONArray("list_table");
            for(Object table : listTable){
                JSONObject jsonT = (JSONObject)table;
                String tableName = jsonT.getString("table_name");
                float tablePrice = jsonT.getFloat("table_price");
                int amountPeople = jsonT.getInt("table_amount_people");
                String tableNote = jsonT.getString("table_note");
                //list free time
                ArrayList<FreeTime> fts = new ArrayList<>();
                JSONArray listFreeTime = jsonT.getJSONArray("free_times");
                for(Object ft : listFreeTime){
                    JSONObject jsonFT = (JSONObject)ft;
                    String startTime = jsonFT.getString("start_time");
                    String endTime = jsonFT.getString("end_time");
                    String freeTimeNote = jsonFT.getString("free_time_note");
                    FreeTime newFT = new FreeTime(-1, startTime, endTime, null);
                    newFT.setMota(freeTimeNote);
                    fts.add(newFT);
                }
                Table newTable = new Table(amountPeople, -1, tablePrice, fts, tableNote, tableName);
                list.add(newTable);
            }
            // add list table to database
            TableDAO tDAO = new TableDAO();
            if(!tDAO.connect()){
                jsonResp.put("code",300);
                jsonResp.put("description","không kết nối được CSDL");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            if(!tDAO.addTable(list)){
                jsonResp.put("code",300);
                jsonResp.put("description","không thêm được bàn mới");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            // response
            // SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            // JSONArray result = new JSONArray();
            // for(Table table: list){
            //     // table
            //     JSONObject jsonTable = new JSONObject();
            //     jsonTable.put("table_id", table.getId());
            //     jsonTable.put("table_name", table.getTen());
            //     jsonTable.put("table_price", table.getGiathue());
            //     jsonTable.put("table_amount_people", table.getSucchua());
            //     jsonTable.put("table_note", table.getMota());
            //     // list free time
            //     JSONArray listFT = new JSONArray();
            //     for(FreeTime ft: table.getFreeTime()){
            //         JSONObject jsonFT = new JSONObject();
            //         jsonFT.put("free_time_id", ft.getId());
            //         jsonFT.put("start_time", format.format(ft.getStarttime()));
            //         jsonFT.put("end_time", format.format(ft.getEndtime()));
            //         jsonFT.put("free_time_note", ft.getMota());
            //         listFT.put(jsonFT);
            //     }
            //     jsonTable.put("free_times", listFT);
            //     result.put(jsonTable);
            // }
            jsonResp.put("code",200);
            jsonResp.put("description","thành công");
            // jsonResp.put("result", result);
        } catch (Exception e) {
            jsonResp.put("code",300);
            jsonResp.put("description",e.getMessage());
        }
        writer.print(jsonResp);
        writer.close();
    }
}