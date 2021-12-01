package dao;

import model.Account;
import model.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.ConnectionUtil;
import util.H2Util;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountDaoImplTest {

    ClientDao clientDao;
    AccountDao accountDao;

    public AccountDaoImplTest() {
        ConnectionUtil.setConnectionParams(H2Util.url, H2Util.username, H2Util.password);
        clientDao = new ClientDaoImpl();
        accountDao = new AccountDaoImpl();
    }
    @BeforeEach
    void setUp() {
        H2Util.createClientsTable();
        H2Util.createAccountsTable();
    }

    @AfterEach
    void tearDown() {
        H2Util.dropAccountsTable();
        H2Util.dropClientsTable();
    }

    @Test
    void createAccountIT() {
        clientDao.createClient(new Client(1, "Jenny", "Codesmith"));
        List<Account> expectedResult = new ArrayList<>();
        expectedResult.add(new Account(1,1,200.25));
        expectedResult.add(new Account(2,1,.25));
        expectedResult.add(new Account(3,1,.25));
        accountDao.createAccount(expectedResult.get(0));
        accountDao.createAccount(expectedResult.get(1));
        accountDao.createAccount(expectedResult.get(2));
        List<Account> actualResult = new ArrayList<>();
        actualResult = accountDao.getAllAccounts(1);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void getAllAccountsIT() {
        clientDao.createClient(new Client(1, "Jenny", "Codesmith"));
        clientDao.createClient(new Client(2, "John", "C Sharp"));
        clientDao.createClient(new Client(3, "Carol", "Cobol"));
        List<Account> expectedResult = new ArrayList<>();
        expectedResult.add(new Account(1,2,200.25));
        expectedResult.add(new Account(2,2,.25));
        expectedResult.add(new Account(3,2,.25));
        accountDao.createAccount(expectedResult.get(0));
        accountDao.createAccount(expectedResult.get(1));
        accountDao.createAccount(expectedResult.get(2));
        List<Account> actualResult = new ArrayList<>();
        actualResult = accountDao.getAllAccounts(2);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void getAllAccountsInvalidHasClientNoAccountsIT() {
        clientDao.createClient(new Client(1, "Jenny", "Codesmith"));
        clientDao.createClient(new Client(2, "John", "C Sharp"));
        clientDao.createClient(new Client(3, "Carol", "Cobol"));
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account(1,2,200.25));
        accountList.add(new Account(2,2,.25));
        accountList.add(new Account(3,2,.25));
        accountDao.createAccount(accountList.get(0));
        accountDao.createAccount(accountList.get(1));
        accountDao.createAccount(accountList.get(2));
        List<Account> actualResult = new ArrayList<>();
        actualResult = accountDao.getAllAccounts(3);
        assertEquals(0, actualResult.size());
    }

    @Test
    void getAllAccountsInvalidHasNoClientOrAccountsIT() {
        List<Account> actualResult = new ArrayList<>();
        actualResult = accountDao.getAllAccounts(2);
        assertEquals(0, actualResult.size());
    }

    @Test
    void getOneAccountIT() {
        clientDao.createClient(new Client(1, "Jenny", "Codesmith"));
        clientDao.createClient(new Client(2, "John", "C Sharp"));
        clientDao.createClient(new Client(3, "Carol", "Cobol"));
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account(1,2,200.25));
        accountList.add(new Account(2,2,.25));
        Account expectedResult = new Account(3,2,.25);
        accountList.add(expectedResult);
        accountDao.createAccount(accountList.get(0));
        accountDao.createAccount(accountList.get(1));
        accountDao.createAccount(accountList.get(2));
        Account actualResult = accountDao.getOneAccount(2, 3);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getOneAccountInvalidAccountNotExistIT() {
        clientDao.createClient(new Client(1, "Jenny", "Codesmith"));
        clientDao.createClient(new Client(2, "John", "C Sharp"));
        clientDao.createClient(new Client(3, "Carol", "Cobol"));
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account(1,2,200.25));
        accountList.add(new Account(2,2,.25));
        accountList.add(new Account(3,2,.25));
        accountDao.createAccount(accountList.get(0));
        accountDao.createAccount(accountList.get(1));
        accountDao.createAccount(accountList.get(2));
        Account actualResult = accountDao.getOneAccount(2, 4);  // client 2 exists but account 4 does not
        assertNull(actualResult);
    }

    @Test
    void getOneAccountInvalidHasNoClientOrAccountsIT() {
        Account actualResult = accountDao.getOneAccount(3,4);
        assertNull(actualResult);
    }

    @Test
    void updateAccountIT() {
        clientDao.createClient(new Client(1, "Jenny", "Codesmith"));
        clientDao.createClient(new Client(2, "John", "C Sharp"));
        clientDao.createClient(new Client(3, "Carol", "Cobol"));
        List<Account> expectedResult = new ArrayList<>();
        expectedResult.add(new Account(1,2,200.25));
        expectedResult.add(new Account(2,2,.25));
        expectedResult.add(new Account(3,2,.25));
        accountDao.createAccount(expectedResult.get(0));
        accountDao.createAccount(expectedResult.get(1));
        accountDao.createAccount(expectedResult.get(2));
        Account updatedAccount = expectedResult.get(1); // account 2, client 2
        updatedAccount.setBalance(300.0);
        accountDao.updateAccount(updatedAccount);
        List<Account> actualResult = accountDao.getAllAccounts(2);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void updateAccountInvalidNoAccountIT() {
        clientDao.createClient(new Client(1, "Jenny", "Codesmith"));
        clientDao.createClient(new Client(2, "John", "C Sharp"));
        clientDao.createClient(new Client(3, "Carol", "Cobol"));
        List<Account> expectedResult = new ArrayList<>();
        expectedResult.add(new Account(1,2,200.25));
        expectedResult.add(new Account(2,2,.25));
        expectedResult.add(new Account(3,2,.25));
        accountDao.createAccount(expectedResult.get(0));
        accountDao.createAccount(expectedResult.get(1));
        accountDao.createAccount(expectedResult.get(2));
        Account updatedAccount = new Account(expectedResult.get(1).getAccountId(), expectedResult.get(1).getClientId(), expectedResult.get(1).getBalance()); // account 2, client 2 copy everything change balance
        updatedAccount.setAccountId(4); // set to non-existent account 4
        updatedAccount.setBalance(300.0);   // change balance
        accountDao.updateAccount(updatedAccount);
        List<Account> actualResult = accountDao.getAllAccounts(2);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());    // there should be no change to any accounts
    }

    @Test
    void updateAccountInvalidHasNoClientOrAccountIT() {
        Boolean actualResult = accountDao.updateAccount(new Account(1,2, 56.75));
        assertFalse(actualResult);
    }

    @Test
    void deleteAccountIT() {
        clientDao.createClient(new Client(1, "Jenny", "Codesmith"));
        clientDao.createClient(new Client(2, "John", "C Sharp"));
        clientDao.createClient(new Client(3, "Carol", "Cobol"));
        List<Account> expectedResult = new ArrayList<>();
        expectedResult.add(new Account(1,2,200.25));
        expectedResult.add(new Account(2,2,.25));
        expectedResult.add(new Account(3,2,.25));
        accountDao.createAccount(expectedResult.get(0));
        accountDao.createAccount(expectedResult.get(1));
        accountDao.createAccount(expectedResult.get(2));
        expectedResult.remove(1); // delete account client 2 account 2
        accountDao.deleteAccount(2, 2);
        List<Account> actualResult = new ArrayList<>();
        actualResult = accountDao.getAllAccounts(2);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void deleteAccountInvalidNoAccountIT() {
        clientDao.createClient(new Client(1, "Jenny", "Codesmith"));
        clientDao.createClient(new Client(2, "John", "C Sharp"));
        clientDao.createClient(new Client(3, "Carol", "Cobol"));
        List<Account> expectedResult = new ArrayList<>();
        expectedResult.add(new Account(1,2,200.25));
        expectedResult.add(new Account(2,2,.25));
        expectedResult.add(new Account(3,2,.25));
        accountDao.createAccount(expectedResult.get(0));
        accountDao.createAccount(expectedResult.get(1));
        accountDao.createAccount(expectedResult.get(2));
        accountDao.deleteAccount(2, 4); // delete non-existent account 4 for client 2
        List<Account> actualResult = new ArrayList<>();
        actualResult = accountDao.getAllAccounts(2);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());    // should be no change in array
    }

    @Test
    void deleteAccountInvalidNoClientOrAccountsIT() {
        Boolean actualResult = accountDao.deleteAccount(1,1);
        assertFalse(actualResult);
    }
}