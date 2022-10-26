package controller.table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import controller.DAO;
import model.entity.Ban;
import model.entity.ThoiGianTrong;

public class TableDAO extends DAO{
    public TableDAO(){
        super();
    }

    private boolean addTable(Ban ban){
        boolean ok = false;
        try {
            String sqlinsertPermission = "insert into ban(ten,gia,songuoi,mota) values(?,?,?,?);";
            PreparedStatement ps = connection.prepareStatement(sqlinsertPermission, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ban.getTen());
            ps.setFloat(2, (float)ban.getGiathue());
            ps.setInt(3, ban.getSucchua());
            ps.setString(4, ban.getMota());
            ps.executeUpdate();
            ResultSet res = ps.getGeneratedKeys();
            if(!res.next()){
                ok = false;
            }else{// them ban thanh cong
                ban.setId(res.getInt(1));
                FreeTimeDAO freeTimeDAO = new FreeTimeDAO();
                freeTimeDAO.setConnection(connection);
                if(!freeTimeDAO.addFreeTimeNewTable(ban)){
                    ok = false;
                }else{// them thoi gian trong thanh cong
                    ok = true;
                }
            }
            ps.close();
        } catch (SQLException e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }

    public boolean addTable(ArrayList<Ban> listTable){
        boolean ok = false;
        try {
            connection.setAutoCommit(false);
            for(Ban table : listTable){
                if(!addTable(table)){
                    connection.rollback();
                    ok = false;
                    break;
                }else{
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
    public boolean checkTableByID(Ban table){
        boolean ok = false;
        try {
            String sql = "select * from ban where id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,table.getId());
            ResultSet res = ps.executeQuery();
            if(res.next()){
                table.setId(res.getInt(1));
                table.setTen(res.getString(2));
                table.setGiathue(res.getFloat(3));
                table.setSucchua(res.getInt(4));
                table.setMota(res.getString(5));
                ok = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok;
    }

    public ArrayList<Ban> getAllTable(){
        ArrayList<Ban> list = new ArrayList<>();
        try {
            String sql = "select * from ban";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet res = ps.executeQuery();
            while(res.next()){
                Ban table = new Ban();
                table.setId(res.getInt(1));
                table.setTen(res.getString(2));
                table.setGiathue(res.getFloat(3));
                table.setSucchua(res.getInt(4));
                table.setMota(res.getString(5));
                list.add(table);
            }
            res.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Ban> getFreeTable(ThoiGianTrong ft){
        FreeTimeDAO freeTimeDAO = new FreeTimeDAO();
        freeTimeDAO.setConnection(connection);
        ArrayList<Ban> tables = freeTimeDAO.getListTable(ft);
        for(Ban table: tables){
            checkTableByID(table);
            table.setFreeTime(freeTimeDAO.getFreeTimes(table));
        }
        return tables;
    }
    public static void main(String[] args) {
        // ArrayList<ThoiGianTrong> freeTime = new ArrayList<>();
        // Date starttime = new Date();
        // starttime.setHours(0);
        // starttime.setMinutes(0);
        // starttime.setSeconds(0);
        // Date endtime = new Date();
        // endtime.setHours(23);
        // endtime.setMinutes(59);
        // endtime.setSeconds(59);
        // ThoiGianTrong t = new ThoiGianTrong(-1, starttime, endtime, null);
        // freeTime.add(t);
        // Ban b = new Ban(10, -1, 100000, freeTime, "test", "ABC14");

        // Date starttime = new Date();
        // Date endtime = new Date();
        // TableDAO tableDAO = new TableDAO();
        // if(!tableDAO.connect()){
        //     System.out.println("khong ket noi duoc CSDL");
        // }else{
        //     for(Ban b : tableDAO.getFreeTable(new ThoiGianTrong(-1, starttime, endtime, null))){
        //         System.out.println(b);
        //     }
        // }

        // add new table
        ArrayList<ThoiGianTrong> fts = new ArrayList<>();
        fts.add(new ThoiGianTrong(-1, "16/10/2022 00:00", "16/10/2022 23:59", null));
        Ban table = new Ban(6, -1, 100000, fts, "test", "ABC115");
        TableDAO tDAO = new TableDAO();
        if(!tDAO.connect()){
            System.out.println("khong ket noi duoc CSDL");
        }else{
            if(!tDAO.addTable(table)){
                System.out.println("khong them duoc");
            }else{
                System.out.println(table);
            }
        }
    }
}
