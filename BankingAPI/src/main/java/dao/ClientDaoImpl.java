package dao;

import model.Client;
import org.apache.log4j.Logger;
import util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDaoImpl implements ClientDao{
    static Logger logger = Logger.getLogger(ClientDaoImpl.class);

    @Override
    public Boolean createClient(Client client) {
        boolean success = true;
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            String sql = (client.getClientId()==null) ? "INSERT INTO clients VALUES (DEFAULT, ?, ?);" : "INSERT INTO clients VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            if (client.getClientId()==null) {   // DEFAULT in prepared SQL is auto-increment field
                ps.setString(1, client.getFirstName());
                ps.setString(2, client.getLastName());
            } else {
                ps.setInt(1, client.getClientId()); // we didn't pass in null as clientId so for whatever reason we want ot directly set it
                ps.setString(2, client.getFirstName());
                ps.setString(3, client.getLastName());
            }
            if (ps.executeUpdate()==0) success = false;
        }
        catch (SQLException e) {
            logger.error(e);
            success = false;
        }
        return success;
    }

    @Override
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            String sql = "SELECT * FROM clients ORDER BY client_id;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                clients.add(new Client(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        }
        catch (SQLException e) {
            logger.error(e);
        }
        return clients;
    }

    @Override
    public Client getOneClient(Integer clientId) {
        Client client = null;
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            String sql = "SELECT * FROM clients WHERE client_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                client = new Client(rs.getInt(1), rs.getString(2), rs.getString(3));
            }
        }
        catch (SQLException e) {
            logger.error(e);
        }
        return client;
    }

    @Override
    public Boolean updateClient(Client client) {
        boolean success = true;
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            String sql = "UPDATE clients SET first_name = ?, last_name=? WHERE client_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,client.getFirstName());
            ps.setString(2,client.getLastName());
            ps.setInt(3, client.getClientId());
            if (ps.executeUpdate()==0) success = false;
        }
        catch (SQLException e) {
            logger.error(e);
            success = false;
        }
        return success;
    }

    @Override
    public Boolean deleteClient(Integer clientId) {
        boolean success = true;
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            String sql = "DELETE FROM clients WHERE client_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clientId);
            if (ps.executeUpdate()==0) success = false;
        }
        catch (SQLException e) {
            logger.error(e);
            success = false;
        }
        //System.out.println("ClientDaoImpl::deleteClient("+clientId+") success="+success);
        return success;
    }
}
