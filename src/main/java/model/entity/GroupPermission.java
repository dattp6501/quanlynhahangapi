package model.entity;

public class GroupPermission {
    private int id;
    private Permission permission;
    private Group group;
    public GroupPermission() {
    }
    public GroupPermission(int id, Permission permission, Group group) {
        this.id = id;
        this.permission = permission;
        this.group = group;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Permission getPermission() {
        return permission;
    }
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
}
