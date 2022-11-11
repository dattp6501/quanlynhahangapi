package global;

import java.util.ArrayList;

import model.entity.UserLogin;

/**
 * InitVariable
 */
public class InitVariable {
    // session
    public static final ArrayList<UserLogin> LIST_USER_LOGIN = new ArrayList<>();
    public static final long TIME = 2*60*60*1000;
    public static final long USER_LOGIN_NUMBER = 4;
    public static final String HOST = "";
    public static final String JDBC ="jdbc:mysql://sql12.freesqldatabase.com:3306/sql12546504";
    public static final String USER_NAME_DB = "sql12546504";
    public static final String  PASS_WORD_DB = "P1fwzuH3gC";

    // public static final String HOST = "/quanlynhahangapi";
    // public static final String JDBC ="jdbc:mysql://localhost:3306/quanlynhahang";
    // public static final String USER_NAME_DB = "dattp";
    // public static final String  PASS_WORD_DB = "dattp";
}