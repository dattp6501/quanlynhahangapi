package controller;

import java.sql.SQLException;

import model.entity.Admin;

public class AdminDAO extends DAO{
    public AdminDAO(){super();}
    public boolean check(Admin admin) throws SQLException{
        MemberDAO memberDAO = new MemberDAO();
        memberDAO.setConnection(connection);
        return memberDAO.checkLogin(admin);
    }
}
