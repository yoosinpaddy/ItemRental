package yoosin.paddy.itemrental.messaging.model;

public class Person_m {
    String name;
    String email;
    String id;
    String photoUrl;
    String userType;
    Status status=new Status();

    public Person_m(String name, String email, String id, String photoUrl, String userType, String userName) {
        this.name = name;
        this.id = id;
        this.photoUrl = photoUrl;
        this.userType = userType;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Person_m(){
        Status status;
        String name;
        String email;
        String id;
        String photoUrl;
        String userType;
    }
}
