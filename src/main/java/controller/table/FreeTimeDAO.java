package controller.table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import controller.DAO;
import model.entity.Ban;
import model.entity.TableBooking;
import model.entity.ThoiGianTrong;

public class FreeTimeDAO extends DAO{
    public FreeTimeDAO(){
        super();
    }

    public boolean addFreeTimeNewTable(Ban table){
        boolean ok = false;
        for(ThoiGianTrong ft : table.getFreeTime()){
            if(!add(ft, table)){
                ok = false;
                break;
            }else{
                ok = true;
            }
        }
        return ok;
    }

    public ArrayList<Ban> getListTable(ThoiGianTrong freeTime){
        ArrayList<Ban> list = null;
        try {
            String sql = "select banid,id,batdau,ketthuc from thoigiantrong where "
            + "batdau<=? and ?<=ketthuc "
            +"group by banid ";
            PreparedStatement ps = connection.prepareStatement(sql);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ps.setString(1,format.format(freeTime.getStarttime()));
            ps.setString(2,format.format(freeTime.getEndtime()));
            ResultSet res = ps.executeQuery();
            list = new ArrayList<>();
            while(res.next()){
                Ban ban = new Ban();
                ban.setId(res.getInt(1));
                list.add(ban);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<ThoiGianTrong> getFreeTimes(Ban table){
        ArrayList<ThoiGianTrong> list = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String sql = "select * from thoigiantrong where banid = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,table.getId());
            ResultSet res = ps.executeQuery();
            while(res.next()){
                ThoiGianTrong ft = new ThoiGianTrong();
                ft.setId(res.getInt(1));
                String starttime = res.getString(2);
                String endtime = res.getString(3);
                try {
                    ft.setStarttime(format.parse(starttime));
                    ft.setEndtime(format.parse(endtime));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return list;
                }
                ft.setMota(res.getString(4));
                list.add(ft);
            }
            res.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean add(ThoiGianTrong ft, Ban table){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        boolean ok = false;
        try {
            String insert = "insert into thoigiantrong(banid,batdau,ketthuc,mota) values(?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, table.getId());
            ps.setString(2, format.format(ft.getStarttime()));
            ps.setString(3, format.format(ft.getEndtime()));
            ps.setString(4, ft.getMota());
            ps.executeUpdate();
            ResultSet res = ps.getGeneratedKeys();
            if(res.next()){
                ft.setId(res.getInt(1));
                ok = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean updateFreeTime(TableBooking tb){
        boolean ok = false;
        try {
            ArrayList<ThoiGianTrong> listFt = getFreeTimes(tb.getTable());// get list free time of table by tableid
            Collections.sort(listFt);// sort
            Date endtime =new Date();endtime.setTime(tb.getStartTime().getTime()+tb.getTime()*60*1000);
            ThoiGianTrong bookingTime = new ThoiGianTrong(-1,tb.getStartTime(), endtime, null);
            int index = listFt.indexOf(bookingTime);
            ThoiGianTrong curentFt = null;
            if(index==-1){
                ok = false;
            }else{// not exist free time booked
                curentFt = listFt.get(index);
                long disBefore = (bookingTime.getStarttime().getTime()-curentFt.getStarttime().getTime())/(60*1000)+1;
                long disAfter = (curentFt.getEndtime().getTime()-bookingTime.getEndtime().getTime())/(60*1000)+1;
                String delete = "delete from thoigiantrong where id = ?";
                PreparedStatement ps = connection.prepareStatement(delete);
                ps.setInt(1, curentFt.getId());
                if(ps.executeUpdate()<=0){
                    ok = false;
                }else{
                    ok = true;
                    if(disBefore>=30){//only add time>=30 minute
                        ThoiGianTrong ftBefore = new ThoiGianTrong(-1, curentFt.getStarttime(), bookingTime.getStarttime(), tb.getTable());
                        if(!add(ftBefore, tb.getTable())){// add new free time error
                            connection.rollback();
                            ok = false;
                        }
                    }
                    if(disAfter>=30){//only add time>=30 minute 
                        ThoiGianTrong ftAfter = new ThoiGianTrong(-1, bookingTime.getEndtime(), curentFt.getEndtime(), tb.getTable());
                        if(!add(ftAfter, tb.getTable())){//add new free time error
                            connection.rollback();
                            ok = false;
                        }
                    }
                }
            } 
        } catch (SQLException e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }

    public boolean reset(ArrayList<Ban> tables){
        boolean ok = false;
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int date = calendar.get(Calendar.DATE);
            String datestr = String.format("%04d-%02d-%02d",year,month,date);
            String startTime = datestr+" 00:00";
            String endTime = datestr+" 23:59";
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement("TRUNCATE thoigiantrong");
            if(ps.executeUpdate()>=0){
                ps = connection.prepareStatement("alter table thoigiantrong auto_increment = 1");
                ps.executeUpdate();
                for(Ban t : tables){
                    int banId = t.getId();
                    ps = connection.prepareStatement("insert into thoigiantrong(banid,batdau,ketthuc,mota) values(?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, banId);
                    ps.setString(2, startTime);
                    ps.setString(3, endTime);
                    ps.setString(4, "init");
                    ps.executeUpdate();
                    ResultSet res1 = ps.getGeneratedKeys();
                    if(!res1.next()){
                        ok = false;
                        break;
                    }else{
                        ok = true;
                    }
                }
            }
            if(ok){
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok;
    }
    public static void main(String[] args) {
        // reset
        FreeTimeDAO freeTimeDAO = new FreeTimeDAO();
        if(!freeTimeDAO.connect()){
            System.out.println("khong the ket noi CSDL");
        }else{
            TableDAO tDAO = new TableDAO();
            tDAO.setConnection(freeTimeDAO.getConnection());
            ArrayList<Ban> tables = tDAO.getAllTable();
            if(freeTimeDAO.reset(tables)){
                System.out.println("thanh cong");
            }else{
                System.out.println("reset error");
            }
        }
    }
}
