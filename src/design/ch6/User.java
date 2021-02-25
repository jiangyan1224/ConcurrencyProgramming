package design.ch6;

public class User {
    private String name;
    private int id;
    public User(int id, String name){
        System.out.println("create User:"+ id + name);
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
