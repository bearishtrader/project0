package util;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class H2Util {
    public static String url = "jdbc:h2:./h2/db";
    public static String username = "sa";
    public static String password = "sa";
    static Logger logger = Logger.getLogger(H2Util.class);

    public static void createClientsTable(){
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql = "CREATE TABLE clients(\n" +
                    "\tclient_id serial PRIMARY KEY,\n" +
                    "\tfirst_name varchar(50),\n" +
                    "\tlast_name varchar(50));";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            conn.close();
        }catch(SQLException e) {
            logger.error(e);
        }
    }

    public static void createAccountsTable(){
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql = "CREATE TABLE accounts(\n" +
                    "\taccount_id integer,\n" +
                    "\tclient_id integer REFERENCES clients(client_id) ON DELETE CASCADE ON UPDATE NO ACTION,\n" +
                    "\tbalance double PRECISION DEFAULT 0.0,\n" +
                    "\tPRIMARY KEY (client_id, account_id)\n" +
                    "\t);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            conn.close();
        }catch(SQLException e) {
            logger.error(e);
        }
    }

    public static void dropClientsTable(){
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql = "DROP TABLE clients;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            conn.close();
        }catch(SQLException e) {
            logger.error(e);
        }
    }

    public static void dropAccountsTable(){
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql = "DROP TABLE accounts;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            conn.close();
        }catch(SQLException e) {
            logger.error(e);
        }
    }
}
