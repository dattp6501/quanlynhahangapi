package controller.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import controller.DAO;
import model.entity.GroupPermission;
public class GroupPermissionDAO extends DAO{
    public GroupPermissionDAO(){
        super();
    }

    public boolean add(GroupPermission gp){// them quyen cho nhom
        boolean ok = false;
        try {
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
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok;
    }
}