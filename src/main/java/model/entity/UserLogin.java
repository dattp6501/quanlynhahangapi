package model.entity;

import java.util.Date;

import global.InitVariable;

public class UserLogin {
    private String session;
    private long time = new Date().getTime() + InitVariable.TIME;
    private Customer kh;
    public UserLogin(String session, Customer kh) {
        this.session = session;
        this.kh = kh;
    }


    public UserLogin() {
        super();
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
    public Customer getKh() {
        return kh;
    }
    public void setKh(Customer kh) {
        this.kh = kh;
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
        if (!(obj instanceof UserLogin))
            return false;
        UserLogin other = (UserLogin) obj;
        if (session == null) {
            if (other.session != null)
                return false;
        } 
        return session.equals(other.getSession());
    }
}
