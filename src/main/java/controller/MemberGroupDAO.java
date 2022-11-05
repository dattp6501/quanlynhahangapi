package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.entity.Group;
import model.entity.MemberGroup;
import model.entity.Member;

public class MemberGroupDAO extends DAO{
    public MemberGroupDAO(){
        super();
    }

    public boolean add(MemberGroup mg) throws SQLException{
        boolean ok = false;
        String sql = "insert into thanhviennhom(thanhvienid,nhomid) values(?,?)";
        PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1,mg.getMember().getId());
        ps.setInt(2,mg.getGroup().getId());
        ps.executeUpdate();
        ResultSet res = ps.getGeneratedKeys();
        if(res.next()){
            ok = true;
            mg.setId(res.getInt(1));
        }
        res.close();
        ps.close();
        return ok;
    }

    public boolean check(Member member) throws SQLException{
        boolean ok = false;
        String sql = "SELECT * FROM thanhviennhom where thanhvienid = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1,member.getId());
        ResultSet res = ps.executeQuery();
        if(!res.next()){
            ok = false;
        }else{
            Group g = new Group();
            g.setId(res.getInt(3));
            member.setGroups(new ArrayList<Group>());
            member.getGroups().add(g);
            GroupDAO gDAO = new GroupDAO();
            gDAO.setConnection(connection);
            if(!gDAO.check(g)){
                ok = false;
            }else{
                ok =true;
            }
        }
        return ok;
    }

}