package controller.schedule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import controller.DAO;
import controller.table.TableBookingDAO;
import model.entity.BookingSchedule;
import model.entity.KhachHang;
import model.entity.TableBooking;

public class BookingScheduleDAO extends DAO{
    public BookingScheduleDAO(){
        super();
    }
    public boolean add(BookingSchedule bs){
        boolean ok = false;
            try {
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
                    for(TableBooking t : bs.getTableBooking()){
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
            } catch (SQLException e) {
                ok = false;
                e.printStackTrace();
            }
        return ok;
    }

    public ArrayList<BookingSchedule> get(KhachHang customer, Date startTime, Date endTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<BookingSchedule> list = new ArrayList<>();
        try {
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
                
                TableBookingDAO TBDAO = new TableBookingDAO();
                TBDAO.setConnection(connection);
                TBDAO.check(bs);
                list.add(bs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}