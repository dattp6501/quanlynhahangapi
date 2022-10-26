package model.entity;

import java.util.ArrayList;

public class Group {
    private int id;
    private String name,description;
    private ArrayList<Permission> permissions;
    public Group() {
        super();
    }
    public Group(int id, String name, String description, ArrayList<Permission> permissions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public ArrayList<Permission> getPermissions() {
        return permissions;
    }
    public void setPermissions(ArrayList<Permission> permissions) {
        this.permissions = permissions;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Group))
            return false;
        Group other = (Group) obj;
        if(id == other.id){
            return true;
        }
        return name.toLowerCase().equals(other.name.toLowerCase());
    }
}
