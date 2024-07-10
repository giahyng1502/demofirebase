package fpt.hunghvph46928.demofirebase;

public class User {
    private String ID;
    private String name;
    private String old;
    private String avatar;


    public User(String ID, String name, String old, String avatar) {
        this.ID = ID;
        this.name = name;
        this.old = old;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public User() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }
}
