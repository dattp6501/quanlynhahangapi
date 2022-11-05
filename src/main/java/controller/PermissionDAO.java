package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.entity.Permission;

public class PermissionDAO extends DAO{
    
    public PermissionDAO(){
        super();
    }

    private boolean add(Permission permission) throws SQLException{
        boolean ok = false;
        connection.setAutoCommit(false);
        String sqlinsertPermission = "insert into quyen(ten,mota) values(?,?)";
        PreparedStatement ps = connection.prepareStatement(sqlinsertPermission, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,permission.getName());
        ps.setString(2,permission.getDescription());
        ps.executeUpdate();
        ResultSet res = ps.getGeneratedKeys();
        if(!res.next()){
            connection.rollback();
            ok = false;
        }else{
            permission.setId(res.getInt(1));
            connection.setAutoCommit(true);
            ok = true;
        }
        return ok;
    }

    public boolean add(ArrayList<Permission> list) throws SQLException{
        boolean ok = false;
        connection.setAutoCommit(false);
        for(Permission p : list){
            if(!add(p)){
                connection.rollback();
                ok = false;
                break;
            }
            ok = true;
        }
        return ok;
    }

    public Permission getPermission(int idPer) throws SQLException{
        Permission permission = null;
        connection.setAutoCommit(false);
        String sql = "SELECT * FROM quyen WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idPer);
        ResultSet res = ps.executeQuery();
        if(res.next()){
            permission = new Permission(res.getInt(1),res.getString(2),res.getString(3));
        }
        res.close();
        return permission;
    }
}
