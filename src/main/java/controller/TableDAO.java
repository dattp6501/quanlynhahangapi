package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.entity.Table;
import model.entity.FreeTime;

public class TableDAO extends DAO{
    public TableDAO(){
        super();
    }

    private boolean addTable(Table ban) throws SQLException{
        boolean ok = false;
        String sqlinsertPermission = "insert into ban(ten,gia,songuoi,mota) values(?,?,?,?)";
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
        return ok;
    }

    public boolean addTable(ArrayList<Table> listTable) throws SQLException{
        boolean ok = false;
        connection.setAutoCommit(false);
        for(Table table : listTable){
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
        return ok;
    }
    public boolean getTableByID(Table table) throws SQLException{
        boolean ok = false;
        String sql = "select * from ban where id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,table.getId());
        ResultSet res = ps.executeQuery();
        if(res.next()){
            table.setId(res.getInt("id"));
            table.setTen(res.getString("ten"));
            table.setGiathue(res.getFloat("gia"));
            table.setSucchua(res.getInt("songuoi"));
            table.setMota(res.getString("mota"));
            ok = true;
        }
        return ok;
    }

    public ArrayList<Table> getAllTable(String name, int limit) throws SQLException{
        ArrayList<Table> list = new ArrayList<>();
        String sql = "select * from ban where ten=? ";
        if(limit>0){
            sql += "limit ? ";
        }
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, "%"+name+"%");
        if(limit>0){
            ps.setInt(2, limit);
        }
        ResultSet res = ps.executeQuery();
        while(res.next()){
            Table table = new Table();
            table.setId(res.getInt("id"));
            table.setTen(res.getString("ten"));
            table.setGiathue(res.getFloat("gia"));
            table.setSucchua(res.getInt("songuoi"));
            table.setMota(res.getString("mota"));
            // free time
            FreeTimeDAO freeTimeDAO = new FreeTimeDAO();
            freeTimeDAO.setConnection(connection);
            table.setFreeTime(freeTimeDAO.getFreeTimes(table));
            list.add(table);
        }
        res.close();
        return list;
    }

    public ArrayList<Table> getFreeTable(FreeTime ft) throws SQLException{
        FreeTimeDAO freeTimeDAO = new FreeTimeDAO();
        freeTimeDAO.setConnection(connection);
        ArrayList<Table> tables = freeTimeDAO.getListTable(ft);
        for(Table table: tables){
            getTableByID(table);
            table.setFreeTime(freeTimeDAO.getFreeTimes(table));
        }
        return tables;
    }
}
