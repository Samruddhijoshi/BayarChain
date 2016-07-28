package Model;

/**
 * Created by Adi_711 on 29-05-2016.
 */
public class ID {
    public String address;
    public String username;
    public String name;
    public String noti_id;

    public String getAddress() {
        return address;
    }

    public String getUsername() {
        return username;
    }

    public String getName(){ return name;}

    public String getNoti_id() { return noti_id; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) { this.name = name; }

    public void setNoti_id (String noti_id) { this.noti_id = noti_id;   }
}
