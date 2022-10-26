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
import controller.schedule.BookingScheduleDAO;
import global.InitVariable;
import model.entity.Ban;
import model.entity.BookingDish;
import model.entity.BookingSchedule;
import model.entity.KhachHang;
import model.entity.TableBooking;
import model.entity.UserLogin;
import utils.JsonCustom;

@WebServlet(urlPatterns = {"/service/add_booking_schedule","/service/get_booking_schedule"})
public class BookingScheduleAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String url = req.getRequestURI();
        String host = "/quanlynhahangapi";
        if(url.equals(host+"/service/get_booking_schedule")){
            getBookingSchedule(req,resp);
        }else if(url.equals(host+"/service/add_booking_schedule")){
            addBookingSchedule(req,resp);
        }
    }
    //-----------------------------------add booking schedule-----------------
    private void addBookingSchedule(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
            }else{
                //booking schedule
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = new Date();
                float depositMoney = objReq.getFloat("deposit_money");
                String note = objReq.getString("booking_schedule_note");
                // customer
                // int customerId = objReq.getInt("customer_id");
                // KhachHang customer = new KhachHang("", "", "", customerId, "", "", null);
                KhachHang customer = checkCustomer(session);
                //booking table
                ArrayList<TableBooking> tableBookings = new ArrayList<>();
                JSONArray listBT = objReq.getJSONArray("list_booking_table");
                if(listBT.length()<=0){
                    jsonResp.put("code",300);
                    jsonResp.put("description","không có bàn nào được đặt");
                }else{
                    for(Object bt : listBT){
                        JSONObject JsonBT = (JSONObject)bt;
                        float tablePrice = JsonBT.getFloat("booking_table_price");
                        Date startTime = format.parse(JsonBT.getString("booking_start_time"));
                        int amountTime = JsonBT.getInt("booking_amount_time");
                        String tableNote = JsonBT.getString("booking_table_note");
                        int tableID = JsonBT.getInt("table_id");
                        Ban table = new Ban(-1, tableID, -1, null, "", "");
                        // booking dish
                        ArrayList<BookingDish> bookingDishs = new ArrayList<>();
                        // JSONArray listBD = JsonBT.getJSONArray("list_booking_dish");
                        // for(Object bd : listBD){
                        //     JSONObject jsonBD = (JSONObject) bd;
                        // }
                        TableBooking tb = new TableBooking(-1, amountTime, tablePrice, tableNote, table, startTime, bookingDishs);
                        tableBookings.add(tb);
                    }
                    
    
                    BookingSchedule bs = new BookingSchedule(-1, date, depositMoney, note, customer, tableBookings);
                    BookingScheduleDAO bsDAO = new BookingScheduleDAO();
                    if(!bsDAO.connect()){
                        jsonResp.put("code",300);
                        jsonResp.put("description","không kết nối được CSDL");
                    }else{
                        if(!bsDAO.add(bs)){
                            jsonResp.put("code",300);
                            jsonResp.put("description","không tạo được lịch đặt bàn");
                        }else{
                            jsonResp.put("code",200);
                            jsonResp.put("description","thành công");
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
    //--------------------------------------------get booking schedule---------------------
    private void getBookingSchedule(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            if(!FCheckSession.checkSession(session)){
                jsonResp.put("code",300);
                jsonResp.put("description","chưa đăng nhập");
            }else{
                KhachHang customer = checkCustomer(session);
                BookingScheduleDAO BSDAO = new BookingScheduleDAO();
                if(!BSDAO.connect()){
                    jsonResp.put("code",300);
                    jsonResp.put("description","không tạo được lịch đặt bàn");
                }else{
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    jsonResp.put("code",200);
                    jsonResp.put("description","thành công");
                    JSONArray jsonList = new JSONArray();
                    ArrayList<BookingSchedule> list = BSDAO.get(customer, null, null);
                    for(BookingSchedule bs : list){
                        JSONObject jsonBS = new JSONObject();
                        jsonBS.put("booking_schedule_id", bs.getId());
                        jsonBS.put("booking_schedule_date", format.format(bs.getDate()));
                        jsonBS.put("booking_schedule_deposit_money", bs.getDepositMoney());
                        jsonBS.put("booking_schedule_note", bs.getNote());
                        //booking table
                        JSONArray jsonBTs = new JSONArray();
                        for(TableBooking tb : bs.getTableBooking()){
                            JSONObject jsonTB = new JSONObject();
                            jsonTB.put("table_id", tb.getTable().getId());
                            jsonTB.put("table_name", tb.getTable().getTen());
                            jsonTB.put("table_note", tb.getTable().getMota());
                            jsonTB.put("table_amount_people", tb.getTable().getSucchua());
                            jsonTB.put("booking_table_id", tb.getId());
                            jsonTB.put("booking_table_price", tb.getPrice());
                            jsonTB.put("booking_table_start_time", format.format(tb.getStartTime()));
                            jsonTB.put("booking_table_time", tb.getTime());
                            jsonTB.put("booking_table_note", tb.getNote());
                            // booking dish
                            

                            jsonBTs.put(jsonTB);
                        }
                        jsonBS.put("list_booking_table", jsonBTs);
                        jsonList.put(jsonBS);
                    }
                    jsonResp.put("list_booking_schedule", jsonList);
                }
            }
        } catch (Exception e) {
            jsonResp.put("code",300);
            jsonResp.put("description",e.getMessage());
        }
        writer.print(jsonResp);
        writer.close();
    }
    private KhachHang checkCustomer(String session){
        UserLogin u = new UserLogin(session, new KhachHang());
        int index = InitVariable.ListUserLogin.indexOf(u);
        u = InitVariable.ListUserLogin.get(index);
        return u.getKh();
    }
}