package model.entity;

import java.util.Date;

import global.InitVariable;

public class AdminLogin {
    private String session;
    private long time = new Date().getTime() + InitVariable.TIME;
    private Admin admin;
    public AdminLogin(String session, long time, Admin admin) {
        this.session = session;
        this.time = time;
        this.admin = admin;
    }
    public AdminLogin() {
    }
    public String getSession() {
        return session;
    }
    public void setSession(String session) {
        this.session = session;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public Admin getAdmin() {
        return admin;
    }
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((session == null) ? 0 : session.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof AdminLogin))
            return false;
        AdminLogin other = (AdminLogin) obj;
        if (session == null) {
            if (other.session != null)
                return false;
        } else if (!session.equals(other.session))
            return false;
        return true;
    }
}