import frontcontroller.FrontController;
import io.javalin.Javalin;
import util.ConnectionUtil;

public class Main {
    public static void main(String[] args) {
        ConnectionUtil.getConnectionParams();   // load JDBC/database connection parameters from connection.properties
        Javalin javalin = Javalin.create().start(9000);
        new FrontController(javalin);
    }
}
