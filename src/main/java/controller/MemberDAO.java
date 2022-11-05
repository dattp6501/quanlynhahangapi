package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.entity.Member;

public class MemberDAO extends DAO{
    public MemberDAO() {
        super();
    }

    public boolean checkLogin(Member member) throws SQLException{
        boolean ok = false;
        String sqlcheckcustomer = "select * "
            + "from thanhvien "
            + "where thanhvien.tendangnhap = ? and thanhvien.matkhau = ?";
        PreparedStatement ps = connection.prepareStatement(sqlcheckcustomer);
        ps.setString(1, member.getTendangnhap());
        ps.setString(2, member.getMatkhau());
        ResultSet res = ps.executeQuery();
        if(res.next()){
            member.setId(res.getInt(1));
            member.setTendaydu(res.getString(2));
            ok = true;
        }
        res.close();
        return ok;
    }
    
    public boolean add(Member member) throws SQLException{
        boolean ok = false;
        String sql = "insert into thanhvien(tendaydu,tendangnhap,matkhau) values(?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,member.getTendaydu());
        ps.setString(2,member.getTendangnhap());
        ps.setString(3,member.getMatkhau());
        ps.executeUpdate();
        ResultSet res = ps.getGeneratedKeys();
        if(res.next()){
            member.setId(res.getInt(1));
            ok = true;
        }
        res.close();
        return ok;
    }
    public boolean checkGroup(Member member) throws SQLException{
        MemberGroupDAO mgDAO = new MemberGroupDAO();
        mgDAO.setConnection(connection);
        return mgDAO.check(member);
    }
}