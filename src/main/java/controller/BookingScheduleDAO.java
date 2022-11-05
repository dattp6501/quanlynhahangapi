package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.entity.BookingSchedule;
import model.entity.Customer;
import model.entity.TableBooking;

public class BookingScheduleDAO extends DAO{
    public BookingScheduleDAO(){
        super();
    }

    public ArrayList<BookingSchedule> get(Customer customer, Date startTime, Date endTime) throws SQLException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<BookingSchedule> list = new ArrayList<>();
        String select = "SELECT * FROM lichdat where khachhangid = ?";
        PreparedStatement ps = connection.prepareStatement(select);
        ps.setInt(1, customer.getId());
        ResultSet res = ps.executeQuery();
        while(res.next()){
            int id = res.getInt(1);
            Date date = null;
            try {
                date = format.parse(res.getString(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            float depositMoney = res.getFloat(3);
            String note = res.getString(4);
            BookingSchedule bs = new BookingSchedule(id, date, depositMoney, note, customer, new ArrayList<TableBooking>());
            // get table booking
            TableBookingDAO TBDAO = new TableBookingDAO();
            TBDAO.setConnection(connection);
            TBDAO.getTableBooking(bs);
            list.add(bs);
        }
        return list;
    }

    public BookingSchedule getByID(Customer customer, int BSId, Date startTime, Date endTime) throws SQLException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BookingSchedule bs = null;
        String select = "SELECT * FROM lichdat where khachhangid=? and id=?";
        PreparedStatement ps = connection.prepareStatement(select);
        ps.setInt(1, customer.getId());
        ps.setInt(2, BSId);
        ResultSet res = ps.executeQuery();
        if(res.next()){
            int id = res.getInt("id");
            Date date = null;
            try {
                date = format.parse(res.getString("ngay"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            float depositMoney = res.getFloat(3);
            String note = res.getString(4);
            // get table booking
            bs = new BookingSchedule(id, date, depositMoney, note, customer, new ArrayList<TableBooking>());
            TableBookingDAO TBDAO = new TableBookingDAO();
            TBDAO.setConnection(connection);
            TBDAO.getTableBooking(bs);
        }
        res.close();
        return bs;
    }

    public boolean add(BookingSchedule bs) throws SQLException{
        boolean ok = false;
        connection.setAutoCommit(false);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String insert = "insert into lichdat(ngay,tiencoc,mota,khachhangid) values(?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,format.format(bs.getDate()));
        ps.setFloat(2,bs.getDepositMoney());
        ps.setString(3,bs.getNote());
        ps.setInt(4,bs.getCustomer().getId());
        ps.executeUpdate();
        ResultSet res = ps.getGeneratedKeys();
        if(!res.next()){
            connection.rollback();
            ok = false;
        }else{// add schedule success
            bs.setId(res.getInt(1));
            for(TableBooking t : bs.getTableBooking()){// add list table booking
                TableBookingDAO tbDAO = new TableBookingDAO();
                tbDAO.setConnection(connection);
                if(!tbDAO.add(t,bs)){
                    connection.rollback();
                    ok = false;
                    break;
                }
                // add table success
                ok = true;
            }
        }
        if(ok){
            connection.setAutoCommit(true);
        }
        return ok;
    }

    public boolean update(BookingSchedule BS) throws SQLException{
        connection.setAutoCommit(false);
        boolean ok = false;
        //update booking table
        TableBookingDAO TBDAO = new TableBookingDAO(); TBDAO.setConnection(connection);
        if(!TBDAO.update(BS.getTableBooking())){
            connection.rollback();
            ok = false;
            return ok;
        }
        connection.setAutoCommit(true);
        ok = true;
        return ok;
    }
}