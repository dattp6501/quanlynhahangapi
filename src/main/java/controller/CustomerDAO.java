package controller;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.entity.Group;
import model.entity.Customer;
import model.entity.MemberGroup;
import model.entity.Member;

public class CustomerDAO extends DAO{
    public CustomerDAO() {
        super();
    }
    public boolean kiemTraDangNhap(Customer customer) throws SQLException{
        MemberDAO memberDAO = new MemberDAO();
        memberDAO.setConnection(connection);
        return memberDAO.checkLogin(customer);
    }

    public boolean add(Customer kh) throws SQLException{
        boolean ok = false;
        super.connection.setAutoCommit(false);
        Member member = new Member(kh.getTendangnhap(),kh.getMatkhau(),kh.getTendaydu(),-1,null);
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
                ps.setString(2, kh.getMobile());
                ps.setString(3, kh.getAddress());
                if(ps.executeUpdate()>=1){
                    connection.setAutoCommit(true);
                    ok = true;
                }else{
                    connection.rollback();
                }
                ps.close();
            }
        }
        return ok;
    }
}
