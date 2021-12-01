package dao;

import model.Account;
import org.apache.log4j.Logger;
import util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDaoImpl implements AccountDao{
    static Logger logger = Logger.getLogger(ClientDaoImpl.class);
    @Override
    public Boolean createAccount(Account account) {
        boolean success = true;
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            if (account.getAccountId()==null) {
                // This is done because account_id is not an auto-increment field, we are always starting at account_id 1,2,3...etc. for each client
                // NULL is a special sentinel for us that we should increment any existing account_id we find for the client/account or start with 1 if
                // there are no existing accounts records
                String maxSQL = "SELECT MAX(account_id) from accounts where client_id = ?;";
                PreparedStatement psMax = conn.prepareStatement(maxSQL);
                psMax.setInt(1, account.getClientId());
                ResultSet rsMax = psMax.executeQuery();
                Integer maxAccountId = null;
                while(rsMax.next()) {
                    maxAccountId = rsMax.getInt(1);
                }
                if (maxAccountId==null) {
                    account.setAccountId(1);
                } else {
                    account.setAccountId(maxAccountId+1);
                }
            }
            String sql = "INSERT INTO accounts VALUES (?, ?, ?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,account.getAccountId());
            ps.setInt(2,account.getClientId());
            ps.setDouble(3,account.getBalance());
            if (ps.executeUpdate()==0) success = false;
        }
        catch (SQLException e) {
            logger.error(e);
            success = false;
        }
        return success;
    }

    @Override
    public List<Account> getAllAccounts(Integer clientId) {
        List<Account> accounts = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            String sql = "SELECT * FROM accounts WHERE client_id = ? ORDER BY client_id, account_id;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accounts.add(new Account(rs.getInt(1), rs.getInt(2), rs.getDouble(3)));
            }
        }
        catch (SQLException e) {
            logger.error(e);
        }
        return accounts;
    }

    @Override
    public Account getOneAccount(Integer clientId, Integer accountId) {
        Account account = null;
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            String sql = "SELECT * FROM accounts WHERE account_id = ? AND client_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.setInt(2, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                account = new Account(rs.getInt(1), rs.getInt(2), rs.getDouble(3));
            }
        }
        catch (SQLException e) {
            logger.error(e);
        }
        return account;
    }

    @Override
    public Boolean updateAccount(Account account) {
        boolean success = true;
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            String sql = "UPDATE accounts SET balance = ? WHERE account_id = ? AND client_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1,account.getBalance());
            ps.setInt(2,account.getAccountId());
            ps.setInt(3, account.getClientId());
            if (ps.executeUpdate()==0) success = false;
        }
        catch (SQLException e) {
            logger.error(e);
            success = false;
        }
        return success;
    }

    @Override
    public Boolean deleteAccount(Integer clientId, Integer accountId) {
        boolean success = true;
        try(Connection conn = DriverManager.getConnection(ConnectionUtil.jdbcConnectionURL, ConnectionUtil.databaseUsername, ConnectionUtil.databasePassword)) {
            String sql = "DELETE FROM accounts WHERE account_id = ? AND client_id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.setInt(2, clientId);
            if (ps.executeUpdate()==0) success = false;
        }
        catch (SQLException e) {
            logger.error(e);
            success = false;
        }
        return success;
    }
}
