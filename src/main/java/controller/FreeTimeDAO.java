package controller;

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

import model.entity.Table;
import model.entity.TableBooking;
import model.entity.FreeTime;

public class FreeTimeDAO extends DAO{
    public FreeTimeDAO(){
        super();
    }

    public boolean addFreeTimeNewTable(Table table) throws SQLException{
        boolean ok = false;
        for(FreeTime ft : table.getFreeTime()){
            if(!add(ft, table)){
                ok = false;
                break;
            }else{
                ok = true;
            }
        }
        return ok;
    }

    public ArrayList<Table> getListTable(FreeTime freeTime) throws SQLException{
        ArrayList<Table> list = null;
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
            Table ban = new Table();
            ban.setId(res.getInt("banid"));
            list.add(ban);
        }
        return list;
    }

    public ArrayList<FreeTime> getFreeTimes(Table table) throws SQLException{// get list free time by table id
        ArrayList<FreeTime> list = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select * from thoigiantrong where banid = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,table.getId());
        ResultSet res = ps.executeQuery();
        while(res.next()){
            FreeTime ft = new FreeTime();
            ft.setId(res.getInt("id"));
            String starttime = res.getString("batdau");
            String endtime = res.getString("ketthuc");
            try {
                ft.setStarttime(format.parse(starttime));
                ft.setEndtime(format.parse(endtime));
            } catch (ParseException e) {
                e.printStackTrace();
                return list;
            }
            ft.setMota(res.getString("mota"));
            list.add(ft);
        }
        res.close();
        return list;
    }

    public boolean add(FreeTime ft, Table table) throws SQLException{// add free time for table
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        boolean ok = false;
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
        return ok;
    }

    public boolean updateFreeTime(TableBooking tb) throws SQLException{
        boolean ok = false;
        ArrayList<FreeTime> listFt = getFreeTimes(tb.getTable());// get list free time of table by table id
        Collections.sort(listFt);// sap xep thoi gian trong tang dan theo start time
        Date endtime = new Date();endtime.setTime(tb.getStartTime().getTime()+tb.getTime()*60*1000);
        FreeTime bookingTime = new FreeTime(-1,tb.getStartTime(), endtime, null);
        int index = listFt.indexOf(bookingTime);// tim thoi gian trong ma thoi gian dat ban thuoc vao
        FreeTime curentFt = null;
        if(index==-1){
            ok = false;
            return ok;
        }
        //exist free time booked
        curentFt = listFt.get(index);
        long disBefore = (bookingTime.getStarttime().getTime()-curentFt.getStarttime().getTime())/(60*1000)+1;
        long disAfter = (curentFt.getEndtime().getTime()-bookingTime.getEndtime().getTime())/(60*1000)+1;
        String delete = "delete from thoigiantrong where id = ?";
        PreparedStatement ps = connection.prepareStatement(delete);
        ps.setInt(1, curentFt.getId());
        if(ps.executeUpdate()<=0){
            ok = false;
            return ok;
        }
        ok = true;
        if(disBefore>=30){//only add time>=30 minute
            FreeTime ftBefore = new FreeTime(-1, curentFt.getStarttime(), bookingTime.getStarttime(), tb.getTable());
            if(!add(ftBefore, tb.getTable())){// add new free time error
                connection.rollback();
                ok = false;
            }
        }
        if(disAfter>=30){//only add time>=30 minute 
            FreeTime ftAfter = new FreeTime(-1, bookingTime.getEndtime(), curentFt.getEndtime(), tb.getTable());
            if(!add(ftAfter, tb.getTable())){//add new free time error
                connection.rollback();
                ok = false;
            }
        }
        return ok;
    }

    public boolean reset(ArrayList<Table> tables) throws SQLException{
        boolean ok = false;
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
            for(Table t : tables){
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
        return ok;
    }
    public static void main(String[] args) throws SQLException {
        // reset
        FreeTimeDAO freeTimeDAO = new FreeTimeDAO();
        if(!freeTimeDAO.connect()){
            System.out.println("khong the ket noi CSDL");
        }else{
            TableDAO tDAO = new TableDAO();
            tDAO.setConnection(freeTimeDAO.getConnection());
            ArrayList<Table> tables = tDAO.getAllTable("",-1);
            if(freeTimeDAO.reset(tables)){
                System.out.println("thanh cong");
            }else{
                System.out.println("reset error");
            }
        }
    }
}
