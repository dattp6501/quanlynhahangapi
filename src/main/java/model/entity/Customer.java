package model.entity;

import java.util.ArrayList;

public class Customer extends Member{
    private String mobile="",address="";
    public Customer(){
        super();
    }
    public Customer(String username, String password, String fullname, int id, String mobile, String address, ArrayList<Group> groups) {
        super(username, password, fullname, id, groups);
        this.mobile = mobile;
        this.address = address;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}