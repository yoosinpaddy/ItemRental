package yoosin.paddy.itemrental.messaging.model;



public class User {
    public String name;
    public String email;
    public String avata;
    public Status status=new Status();
    public Message message;

    public User(String name, String email, String avata, boolean status, Message message) {
        this.name = name;
        this.email = email;
        this.avata = avata;
        this.status.isOnline = status;
        this.message = message;
    }

    public User(){
        status = new Status();
        message = new Message();
        status.isOnline = false;
        status.timestamp = 0;
        message.idReceiver = "0";
        message.idSender = "0";
        message.text = "";
        message.timestamp = 0;
    }
}
