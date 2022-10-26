package controller.user;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import controller.DAO;
import model.entity.Group;
import model.entity.KhachHang;
import model.entity.MemberGroup;
import model.entity.ThanhVien;

public class CustomerDAO extends DAO{
    public CustomerDAO() {
        super();
    }
    public boolean kiemTraDangNhap(KhachHang customer){
        boolean ok = false;
        MemberDAO memberDAO = new MemberDAO();
        memberDAO.setConnection(connection);
        if(memberDAO.checkLogin(customer)){
            ok = true;
        }
        return ok;
    }

    public boolean themKhachhang(KhachHang kh){
        boolean ok = false;
        try {
            super.connection.setAutoCommit(false);
            ThanhVien member = new ThanhVien(kh.getTendangnhap(),kh.getMatkhau(),kh.getTendaydu(),-1,null);
            MemberDAO memberDAO = new MemberDAO();
            memberDAO.setConnection(connection);
            if(!memberDAO.add(member)){
                super.connection.rollback();
                ok = false;
            }else{
                kh.setId(member.getId());
                Group group = new Group(1,"CUSTOMER","",null);
                MemberGroup memberGroup = new MemberGroup(-1,member,group);
                MemberGroupDAO memberGroupDAO = new MemberGroupDAO();
                memberGroupDAO.setConnection(connection);
                if(!memberGroupDAO.add(memberGroup)){
                    super.connection.rollback();
                    ok = false;
                }else{
                    String sqlinsertcustomer = "insert into khachhang(id,sdt,diachi) values(?,?,?)";
                    PreparedStatement ps = connection.prepareStatement(sqlinsertcustomer);
                    ps.setInt(1, kh.getId());
                    ps.setString(2, kh.getSdt());
                    ps.setString(3, kh.getDiachi());
                    if(ps.executeUpdate()>=1){
                        connection.setAutoCommit(true);
                        ok = true;
                    }else{
                        connection.rollback();
                    }
                    ps.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ok = false;
        }
        return ok;
    }
}
