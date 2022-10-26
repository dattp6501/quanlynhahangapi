package controller.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import controller.DAO;
import model.entity.ThanhVien;

public class MemberDAO extends DAO{
    public MemberDAO() {
        super();
    }

    public boolean checkLogin(ThanhVien member){
        boolean ok = false;
        String sqlcheckcustomer = "select * "
            + "from thanhvien "
            + "where thanhvien.tendangnhap = ? and thanhvien.matkhau = ?";
        try {
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
            // ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok;
    }
    
    public boolean add(ThanhVien member){
        boolean ok = false;
        try {
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
            // ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ok;
    }


    public boolean checkGroup(ThanhVien member){
        MemberGroupDAO mgDAO = new MemberGroupDAO();
        mgDAO.setConnection(connection);
        return mgDAO.check(member);
    }
}