package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.entity.GroupPermission;
public class GroupPermissionDAO extends DAO{
    public GroupPermissionDAO(){
        super();
    }

    public boolean add(GroupPermission gp) throws SQLException{// them quyen cho nhom
        boolean ok = false;
        String sqlinsert = "insert into quyennhom(nhomid,quyenid) values(?,?)";
        PreparedStatement ps = connection.prepareStatement(sqlinsert,Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1,gp.getGroup().getId());
        ps.setInt(2,gp.getPermission().getId());
        ps.executeUpdate();
        ResultSet res = ps.getGeneratedKeys();
        if(!res.next()){
            ok = false;
        }else{
            ok = true;
        }
        res.close();
        return ok;
    }
}