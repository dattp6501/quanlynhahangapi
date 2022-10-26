package model.entity;

public class Permission {
    private int id;
    private String name,description;
    public Permission() {
        super();
    }
    public Permission(int id, String name, String description) {
        this.id = id;
        name = formatInput(name);
        this.name = name.toUpperCase();
        this.description = description;
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
        this.name = formatInput(name.toUpperCase());
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    private String formatInput(String name){
        return name.replaceAll("\\s+", "_");
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Permission))
            return false;
        Permission other = (Permission) obj;
        if (id == other.id)
            return true;
        return name.toLowerCase().equals(other.name.toLowerCase());
    }
}
