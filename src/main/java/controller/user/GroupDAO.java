package controller.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import controller.DAO;
import model.entity.Group;

public class GroupDAO extends DAO{
    public GroupDAO(){
        super();
    }

    public boolean check(Group group){
        boolean ok = false;
        try {
            String select = "SELECT * FROM nhom where id = ?";
            PreparedStatement ps = connection.prepareStatement(select);
            ps.setInt(1, group.getId());
            ResultSet resg = ps.executeQuery();
            if(resg.next()){
                group.setName(resg.getString(2));
                group.setDescription(resg.getString(3));
                PermissionGroupDAO permissionGroupDAO = new PermissionGroupDAO();
                permissionGroupDAO.setConnection(connection);
                if(!permissionGroupDAO.check(group)){
                    ok = false;
                }else{
                    ok = true;
                }
            }
        } catch (SQLException e) {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }
}