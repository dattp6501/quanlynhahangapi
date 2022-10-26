package model.entity;

public class MemberGroup {
    private int id;
    private ThanhVien member;
    private Group group;
    public MemberGroup() {
        super();
    }
    public MemberGroup(int id, ThanhVien member, Group group) {
        this.id = id;
        this.member = member;
        this.group = group;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public ThanhVien getMember() {
        return member;
    }
    public void setMember(ThanhVien member) {
        this.member = member;
    }
    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }

}
