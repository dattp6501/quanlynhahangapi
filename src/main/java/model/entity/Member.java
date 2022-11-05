package model.entity;

import java.util.ArrayList;

public class Member {
    private String tendangnhap="",matkhau="",tendaydu="";
    private int id=-1;
    private ArrayList<Group> groups;
    public Member(){
        super();
    }
    public Member(String tendangnhap, String matkhau, String tendaydu, int id, ArrayList<Group> groups) {
        this.tendangnhap = tendangnhap;
        this.matkhau = matkhau;
        this.tendaydu = tendaydu;
        this.id = id;
        this.groups = groups;
    }

    public String getTendangnhap() {
        return tendangnhap;
    }

    public void setTendangnhap(String tendangnhap) {
        this.tendangnhap = tendangnhap;
    }

    public String getMatkhau() {
        return matkhau;
    }

    public void setMatkhau(String matkhau) {
        this.matkhau = matkhau;
    }

    public String getTendaydu() {
        return tendaydu;
    }

    public void setTendaydu(String tendaydu) {
        this.tendaydu = tendaydu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public ArrayList<Group> getGroups() {
        return groups;
    }
    public Group getGroup(){
        return groups.get(0);
    }
    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }
    public void setGroup(Group group){
        this.groups.get(0).setId(group.getId());
        this.groups.get(0).setName(group.getName());
        this.groups.get(0).setDescription(group.getDescription());
        this.groups.get(0).setPermissions(group.getPermissions());
    }
}
