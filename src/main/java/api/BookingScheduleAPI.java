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
import controller.BookingScheduleDAO;
import global.InitVariable;
import model.entity.Table;
import model.entity.BookingDish;
import model.entity.BookingSchedule;
import model.entity.Customer;
import model.entity.Dish;
import model.entity.TableBooking;
import model.entity.UserLogin;
import utils.JsonCustom;

@WebServlet(urlPatterns = {"/service/add_booking_schedule","/service/update_booking_schedule","/service/get_booking_schedule"})
public class BookingScheduleAPI extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        String url = req.getRequestURI();
        String host = InitVariable.HOST;
        if(url.equals(host+"/service/get_booking_schedule")){
            getBookingSchedule(req,resp);
        }else if(url.equals(host+"/service/add_booking_schedule")){
            addBookingSchedule(req,resp);
        }else if(url.equals(host+"/service/update_booking_schedule")){
            updateBookingSchedule(req,resp);
        }
    }
    //-----------------------------------add booking schedule-----------------
    private void addBookingSchedule(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            int ok = FCheckSession.SessionFilter(session);
            if(ok==0){
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
            //booking schedule
            objReq = objReq.getJSONObject("booking_schedule");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = new Date();
            float depositMoney = objReq.getFloat("deposit_money");
            String note = objReq.getString("booking_schedule_note");
            // customer
            Customer customer = checkCustomer(session);
            JSONArray listBT = objReq.getJSONArray("list_booking_table");
            if(listBT.length()<=0){
                jsonResp.put("code",300);
                jsonResp.put("description","không có bàn nào được đặt");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            //booking table
            ArrayList<TableBooking> tableBookings = new ArrayList<>();
            for(Object bt : listBT){
                JSONObject JsonBT = (JSONObject)bt;
                float tablePrice = JsonBT.getFloat("booking_table_price");
                Date startTime = format.parse(JsonBT.getString("booking_table_start_time"));
                int amountTime = JsonBT.getInt("booking_table_amount_time");
                String tableNote = JsonBT.getString("booking_table_note");
                int tableID = JsonBT.getInt("table_id");
                Table table = new Table(-1, tableID, -1, null, "", "");
                // booking dish
                ArrayList<BookingDish> bookingDishs = new ArrayList<>();
                JSONArray listBD = JsonBT.getJSONArray("list_booking_dish");
                for(Object bd : listBD){
                    JSONObject jsonBD = (JSONObject) bd;
                    int number = jsonBD.getInt("dish_number");
                    float price = jsonBD.getFloat("dish_price");
                    String size = jsonBD.getString("dish_size");
                    String dishNote = jsonBD.getString("dish_note");
                    int id = jsonBD.getInt("dish_id");
                    Dish dish = new Dish("", "", "", -1, "", id);
                    BookingDish BD = new BookingDish(-1, number, price, size, dishNote, dish);
                    bookingDishs.add(BD);
                }
                //end dish
                TableBooking tb = new TableBooking(-1, amountTime, tablePrice, tableNote, table, startTime, bookingDishs);
                tableBookings.add(tb);
            }
            BookingSchedule bs = new BookingSchedule(-1, date, depositMoney, note, customer, tableBookings);
            BookingScheduleDAO bsDAO = new BookingScheduleDAO();
            if(!bsDAO.connect()){
                jsonResp.put("code",300);
                jsonResp.put("description","không kết nối được CSDL");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            if(!bsDAO.add(bs)){
                jsonResp.put("code",300);
                jsonResp.put("description","không tạo được lịch đặt bàn");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            jsonResp.put("code",200);
            jsonResp.put("description","thành công");
        } catch (Exception e) {
            jsonResp.put("code",300);
            jsonResp.put("description",e.getMessage());
        }
        writer.print(jsonResp);
        writer.close();
    }


    private void updateBookingSchedule(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        JSONObject objReq = JsonCustom.toJsonObject(req.getReader());
        JSONObject jsonResp = new JSONObject();
        try {
            String session = objReq.getString("session");
            int ok = FCheckSession.SessionFilter(session);
            if(ok==0){
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
            //---------       dong goi du lieu               -------------------
            //booking schedule
            objReq = objReq.getJSONObject("booking_schedule");
            int bookingScheduleID = objReq.getInt("booking_schedule_id");
            //list table booking
            ArrayList<TableBooking> tableBookings = new ArrayList<>();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            JSONArray jsonListBookingTable = objReq.getJSONArray("list_booking_table");
            for(Object obj : jsonListBookingTable){
                // booking table
                JSONObject jsonBT = (JSONObject)obj;
                int BTID = jsonBT.getInt("booking_table_id");
                float tablePrice = jsonBT.getFloat("booking_table_price");
                Date startTime = format.parse(jsonBT.getString("booking_table_start_time"));
                int amountTime = jsonBT.getInt("booking_table_amount_time");
                String tableNote = jsonBT.getString("booking_table_note");
                //table
                int tableID = jsonBT.getInt("table_id");
                Table table = new Table(-1, tableID, -1, null, "", "");
                //end table
                //booking dish
                ArrayList<BookingDish> bookingDishs = new ArrayList<>();
                JSONArray listBD = jsonBT.getJSONArray("list_booking_dish");
                for(Object bd : listBD){
                    JSONObject jsonBD = (JSONObject) bd;
                    int number = jsonBD.getInt("dish_number");
                    float price = jsonBD.getFloat("dish_price");
                    String size = jsonBD.getString("dish_size");
                    String dishNote = jsonBD.getString("dish_note");
                    //dish
                    int id = jsonBD.getInt("dish_id");
                    Dish dish = new Dish("", "", "", -1, "", id);
                    //end dish
                    BookingDish BD = new BookingDish(-1, number, price, size, dishNote, dish);
                    bookingDishs.add(BD);
                }
                // end booking dish
                TableBooking tb = new TableBooking(BTID, amountTime, tablePrice, tableNote, table, startTime, bookingDishs);
                tableBookings.add(tb);
                //end booking table
            }
            BookingSchedule BS = new BookingSchedule(bookingScheduleID, null, -1, "", checkCustomer(session), tableBookings);
            // end booking schedule
            //update booking schedule database
            BookingScheduleDAO BSDAO = new BookingScheduleDAO();
            //check input
            if(!BSDAO.connect()){
                jsonResp.put("code",300);
                jsonResp.put("description","không kết nối được CSDL");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            if(!BSDAO.update(BS)){
                jsonResp.put("code",500);
                jsonResp.put("description","không cập nhật được lịch đặt");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            jsonResp.put("code",200);
            jsonResp.put("description","thành công");
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
            int ok = FCheckSession.SessionFilter(session);
            if(ok==0){
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
            Customer customer = checkCustomer(session);
            BookingScheduleDAO BSDAO = new BookingScheduleDAO();
            if(!BSDAO.connect()){
                jsonResp.put("code",300);
                jsonResp.put("description","không kết nối được CSDL");
                writer.print(jsonResp);
                writer.close();
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
                    jsonTB.put("booking_table_amount_time", tb.getTime());
                    jsonTB.put("booking_table_note", tb.getNote());
                    // booking dish
                    JSONArray jsonBDs = new JSONArray();
                    for(BookingDish BD: tb.getDishs()){
                        JSONObject jsonD = new JSONObject();
                        jsonD.put("booking_dish_id", BD.getId());
                        jsonD.put("booking_disn_number", BD.getNumber());
                        jsonD.put("booking_disn_price", BD.getPrice());
                        jsonD.put("booking_disn_size", BD.getSize());
                        jsonD.put("booking_disn_note", BD.getNote());
                        jsonD.put("disn_id", BD.getDish().getId());
                        jsonD.put("dish_name", BD.getDish().getTen());
                        jsonD.put("dish_image", BD.getDish().getAnh());
                        jsonBDs.put(jsonD);
                    }
                    jsonTB.put("list_booking_dish", jsonBDs);
                    //end booking dish
                    jsonBTs.put(jsonTB);
                }
                jsonBS.put("list_booking_table", jsonBTs);
                jsonList.put(jsonBS);
            }
            jsonResp.put("code",200);
            jsonResp.put("description","thành công");
            jsonResp.put("list_booking_schedule", jsonList);
            
        } catch (Exception e) {
            jsonResp.put("code",300);
            jsonResp.put("description",e.getMessage());
        }
        writer.print(jsonResp);
        writer.close();
    }
    private Customer checkCustomer(String session){
        UserLogin u = new UserLogin(session, new Customer());
        int index = InitVariable.LIST_USER_LOGIN.indexOf(u);
        u = InitVariable.LIST_USER_LOGIN.get(index);
        return u.getKh();
    }
}