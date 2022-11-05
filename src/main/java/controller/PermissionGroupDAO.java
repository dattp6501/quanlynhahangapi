package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.entity.Group;
import model.entity.GroupPermission;
import model.entity.Permission;

public class PermissionGroupDAO extends DAO{
    public PermissionGroupDAO(){
        super();
    }

    private boolean add(GroupPermission gp) throws SQLException{
        boolean ok = false;
        String insert = "insert into quyennhom(nhomid,quyenid) values(?,?)";
            PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, gp.getGroup().getId());
        ps.setInt(2, gp.getPermission().getId());
        ps.executeUpdate();
        ResultSet res = ps.getGeneratedKeys();
        if(res.next()){
            gp.setId(res.getInt(1));
            ok = true;
        }
        return ok;
    }

    public boolean add(ArrayList<GroupPermission> list) throws SQLException{
        boolean ok = false;
        connection.setAutoCommit(false);
        for(GroupPermission gp : list){
            if(!add(gp)){
                connection.rollback();
                ok = false;
                break;
            }
            ok = true;
        }
        if(ok){
            connection.setAutoCommit(true);
        }
        return ok;
    }
    //remove
    public boolean remove(Group group) throws SQLException{
        boolean ok = false;
        connection.setAutoCommit(false);
        String delete = "delete from quyennhom where nhomid=? and quyenid=?";
        PreparedStatement ps = connection.prepareStatement(delete);
        ps.setInt(1, group.getId());
        for(Permission per : group.getPermissions()){
            ps.setInt(2, per.getId());
            if(ps.executeUpdate()<=0){
                connection.rollback();
                ok = false;
                break;
            }
            ok = true;
        }
        if(ok){
            connection.setAutoCommit(true);
            group.getPermissions().clear();
        }
        return ok;
    }
    
    public boolean check(Group group) throws SQLException{
        boolean ok = false;
        String select = "SELECT * FROM quyennhom where nhomid = ?";
        PreparedStatement ps = connection.prepareStatement(select);
        ps.setInt(1, group.getId());
        ResultSet res = ps.executeQuery();
        ArrayList<Permission> listP = new ArrayList<>();
        PermissionDAO permissionDAO = new PermissionDAO();
        permissionDAO.setConnection(connection);
        while(res.next()){
            Permission p = permissionDAO.getPermission(res.getInt(3));
            listP.add(p);
            ok = true;
        }
        group.setPermissions(listP);
        return ok;
    }
}