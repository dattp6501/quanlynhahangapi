package controller.table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import controller.DAO;
import controller.dish.BookingDishDAO;
import model.entity.Ban;
import model.entity.BookingDish;
import model.entity.BookingSchedule;
import model.entity.TableBooking;

public class TableBookingDAO extends DAO{
    public TableBookingDAO(){
        super();
    }
    public boolean add(TableBooking tb, BookingSchedule bs){
        boolean ok = false;
        try {
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
            }else{// add booking table success
                tb.setId(res.getInt(1));
                FreeTimeDAO ftDAO = new FreeTimeDAO();
                ftDAO.setConnection(connection);
                if(!ftDAO.updateFreeTime(tb)){
                    connection.rollback();
                    ok = false;
                }else{// update freetime success
                    ok =true;
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean check(BookingSchedule bs){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean ok = false;
        String select = "SELECT * FROM bandat where lichdatid = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(select);
            ps.setInt(1, bs.getId());
            ResultSet res = ps.executeQuery();
            while(res.next()){
                int id = res.getInt(1);
                float price = res.getFloat(2);
                Date startTime = null;
                try {
                    startTime = format.parse(res.getString(3));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int time = res.getInt(4);
                String note = res.getString(5);
                //table
                int tableId = res.getInt(6);
                Ban table = new Ban();
                table.setId(tableId);
                TableDAO TDAO = new TableDAO();
                TDAO.setConnection(connection);
                TDAO.checkTableByID(table);
                //
                TableBooking tb = new TableBooking(id, time, price, note, table, startTime, new ArrayList<BookingDish>());
                bs.getTableBooking().add(tb);
            }
        } catch (SQLException e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }
}
