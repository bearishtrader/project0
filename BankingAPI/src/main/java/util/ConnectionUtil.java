package util;

import frontcontroller.ClientController;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
// read in our configuration file from
public class ConnectionUtil {
    public static String jdbcConnectionURL;
    public static String databaseUsername;
    public static String databasePassword;
    private static final String propertiesFile="src/main/resources/connection.properties";
    static Logger logger = Logger.getLogger(ConnectionUtil.class);

    public static void getConnectionParams() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(propertiesFile) ) {
            properties.load(inputStream);
        } catch (IOException e)  {
            logger.error(e);
        }
        ConnectionUtil.jdbcConnectionURL = properties.getProperty("jdbc_connection_url");
        ConnectionUtil.databaseUsername = properties.getProperty("database_username");
        ConnectionUtil.databasePassword = properties.getProperty("database_password");
    }

    public static void setConnectionParams(String jdbcConnectionURL, String databaseUsername, String databasePassword) {
        ConnectionUtil.jdbcConnectionURL = jdbcConnectionURL;
        ConnectionUtil.databaseUsername = databaseUsername;
        ConnectionUtil.databasePassword = databasePassword;
    }
}
