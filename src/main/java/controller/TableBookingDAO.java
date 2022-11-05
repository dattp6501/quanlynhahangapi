package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.entity.Table;
import model.entity.BookingDish;
import model.entity.BookingSchedule;
import model.entity.TableBooking;

public class TableBookingDAO extends DAO{
    public TableBookingDAO(){
        super();
    }
    public boolean add(TableBooking tb, BookingSchedule bs) throws SQLException{
        boolean ok = false;
        connection.setAutoCommit(false);
        String insert = "insert into bandat(gia,batdau,thoiluong,mota,banid,lichdatid) values(?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ps.setFloat(1, tb.getPrice());
        ps.setString(2, format.format(tb.getStartTime()));
        ps.setInt(3, tb.getTime());
        ps.setString(4, tb.getNote());
        ps.setInt(5, tb.getTable().getId());
        ps.setInt(6, bs.getId());
        ps.executeUpdate();
        ResultSet res = ps.getGeneratedKeys();
        if(!res.next()){
            connection.rollback();
            ok = false;
            return ok;
        }
        // add booking table success
        tb.setId(res.getInt(1));
        // update freetime
        FreeTimeDAO ftDAO = new FreeTimeDAO();
        ftDAO.setConnection(connection);
        if(!ftDAO.updateFreeTime(tb)){
            connection.rollback();
            ok = false;
            return ok;
        }
        ok = true;
        // update dish
        if(tb.getDishs().size()>0){
            BookingDishDAO bdDAO = new BookingDishDAO();
            bdDAO.setConnection(connection);
            for(BookingDish bd : tb.getDishs()){
                if(!bdDAO.add(bd,tb)){
                    connection.rollback();
                    ok = false;
                    break;
                }
                // add booking dish success
                ok = true;
            }
        }
        return ok;
    }

    public boolean getTableBooking(BookingSchedule bs) throws SQLException{
        boolean ok = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String select = "SELECT * FROM bandat where lichdatid = ?";
        PreparedStatement ps = connection.prepareStatement(select);
        ps.setInt(1, bs.getId());
        ResultSet res = ps.executeQuery();
        while(res.next()){
            // get table booking
            int id = res.getInt("id");
            float price = res.getFloat("gia");
            Date startTime = null;
            try {
                startTime = format.parse(res.getString("batdau"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int time = res.getInt("thoiluong");
            String note = res.getString("mota");
            int tableId = res.getInt("banid");
            // get table
            Table table = new Table(); table.setId(tableId);
            TableDAO TDAO = new TableDAO(); TDAO.setConnection(connection);
            TDAO.getTableByID(table);
            //
            TableBooking tb = new TableBooking(id, time, price, note, table, startTime, new ArrayList<BookingDish>());
            bs.getTableBooking().add(tb);
        }
        return ok;
    }
}
