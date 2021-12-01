package util;
// This class is used as an easy way to create a JSON formatted HTTP response for Javalin endpoint Handler's context.json(object) method
public class StatusObj {
    // these members are public so that Javalin's built in JSON object serializer/deserializer can recognize the data, private will not work without getter and setter methods which bloats the class
    public int status=404;
    public String message="No client(s) found or returned.";

    public StatusObj() {}
    public StatusObj(int status, String msg) {
        this.status = status;
        this.message = msg;
    }

    @Override
    public String toString() {
        return "StatusObj{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}