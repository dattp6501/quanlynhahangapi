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
import controller.table.TableDAO;
import model.entity.Ban;
import model.entity.ThoiGianTrong;
import utils.JsonCustom;


@WebServlet(urlPatterns = {"/service/get_free_table","/service/add_tables"})
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
        }
    }
    //--------------------------------get free table------------------------------------
    private void getFreeTable(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
            }else{
                TableDAO tableDAO = new TableDAO();
                if(!tableDAO.connect()){
                    jsonResp.put("code",300);
                    jsonResp.put("description","không kết nối được CSDL");
                }else{
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date startime = format.parse(objReq.getString("start_time"));
                    Date endtime = format.parse(objReq.getString("end_time"));
                    if(startime.compareTo(endtime)>=0){
                        jsonResp.put("code",300);
                        jsonResp.put("description","Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc");
                    }
                    jsonResp.put("code",200);
                    jsonResp.put("description","Thành công");
                    JSONObject res = new JSONObject();
                    ArrayList<Ban> list = tableDAO.getFreeTable(new ThoiGianTrong(-1, startime, endtime, null));
                    ArrayList<JSONObject> listJson = new ArrayList<>();
                    for(Ban t : list){
                        JSONObject tableJson = new JSONObject();
                        tableJson.put("table_id", t.getId());
                        tableJson.put("table_name", t.getTen());
                        tableJson.put("table_price", t.getGiathue());
                        tableJson.put("table_amount_people", t.getSucchua());
                        tableJson.put("table_note", t.getMota());
                        ArrayList<JSONObject> listFreeTime = new ArrayList<>();
                        for(ThoiGianTrong ft: t.getFreeTime()){
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
                    res.put("table_number", list.size());
                    res.put("list", listJson);
                    jsonResp.put("result", res);
                }
            }
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
            if(!FCheckSession.checkSession(session)){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
            }else{
                if(!FCheckSession.FCheckSessionManager(session)){
                    jsonResp.put("code",500);
                    jsonResp.put("description","không dủ quyền");
                }else{
                    ArrayList<Ban> list = new ArrayList<>();
                    JSONArray listTable = objReq.getJSONArray("list_table");
                    for(Object table : listTable){
                        JSONObject jsonT = (JSONObject)table;
                        String tableName = jsonT.getString("table_name");
                        float tablePrice = jsonT.getFloat("table_price");
                        int amountPeople = jsonT.getInt("table_amount_people");
                        String tableNote = jsonT.getString("table_note");
                        //list free time
                        ArrayList<ThoiGianTrong> fts = new ArrayList<>();
                        JSONArray listFreeTime = jsonT.getJSONArray("free_times");
                        for(Object ft : listFreeTime){
                            JSONObject jsonFT = (JSONObject)ft;
                            String startTime = jsonFT.getString("start_time");
                            String endTime = jsonFT.getString("end_time");
                            String freeTimeNote = jsonFT.getString("free_time_note");
                            ThoiGianTrong newFT = new ThoiGianTrong(-1, startTime, endTime, null);
                            newFT.setMota(freeTimeNote);
                            fts.add(newFT);
                        }
                        Ban newTable = new Ban(amountPeople, -1, tablePrice, fts, tableNote, tableName);
                        list.add(newTable);
                    }
                    // add list table to database
                    TableDAO tDAO = new TableDAO();
                    if(!tDAO.connect()){
                        jsonResp.put("code",300);
                        jsonResp.put("description","không kết nối được CSDL");
                    }else{
                        if(!tDAO.addTable(list)){
                            jsonResp.put("code",300);
                            jsonResp.put("description","không thêm được bàn mới");
                        }else{
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            jsonResp.put("code",200);
                            jsonResp.put("description","thành công");
                            JSONArray result = new JSONArray();
                            for(Ban table: list){
                                // table
                                JSONObject jsonTable = new JSONObject();
                                jsonTable.put("table_id", table.getId());
                                jsonTable.put("table_name", table.getTen());
                                jsonTable.put("table_price", table.getGiathue());
                                jsonTable.put("table_amount_people", table.getSucchua());
                                jsonTable.put("table_note", table.getMota());
                                // list free time
                                JSONArray listFT = new JSONArray();
                                for(ThoiGianTrong ft: table.getFreeTime()){
                                    JSONObject jsonFT = new JSONObject();
                                    jsonFT.put("free_time_id", ft.getId());
                                    jsonFT.put("start_time", format.format(ft.getStarttime()));
                                    jsonFT.put("end_time", format.format(ft.getEndtime()));
                                    jsonFT.put("free_time_note", ft.getMota());
                                    listFT.put(jsonFT);
                                }
                                jsonTable.put("free_times", listFT);
                                result.put(jsonTable);
                            }
                            jsonResp.put("result", result);
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