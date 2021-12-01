package services;

import dao.AccountDao;
import model.Account;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import util.AmountObj;
import util.StatusObj;
import util.WithdrawDepositObj;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    AccountDao accountDao = Mockito.mock(AccountDao.class);
    AccountService accountService;
    public AccountServiceTest() { this.accountService = new AccountService(accountDao); }

    @Test
    void createAccount() {
        Account account = new Account(1,1, 200.0);
        StatusObj statusObj = new StatusObj();
        Mockito.when(accountDao.createAccount(account)).thenReturn(true);
        Boolean actualResult = accountService.createAccount(account, statusObj);
        assertTrue(actualResult);
    }

    @Test
    void getAllAccounts() {
        //arrange
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, 1, 225.20));
        accounts.add(new Account(2, 1, 221.10));
        accounts.add(new Account(3, 1, 0.01));
        List<Account> expectedValue = accounts;
        Mockito.when(accountDao.getAllAccounts(1)).thenReturn(accounts);
        //act(ual)
        List<Account> actualResult = accountService.getAllAccounts(1);
        //asset
        assertArrayEquals(expectedValue.toArray(),actualResult.toArray());
    }

    @Test
    void getAllAccountsInRange() {  // Both amountGreaterThan and amountLessThan are set
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, 1, 225.20));
        accounts.add(new Account(2, 1, 221.10));
        accounts.add(new Account(3, 1, 0.01));
        accounts.add(new Account(4, 1, 750.75));
        accounts.add(new Account(5, 1, 1250.75));
        Mockito.when(accountDao.getAllAccounts(1)).thenReturn(accounts);
        List<Account> expectedResult = new ArrayList<>();   // if we screen the list with amountGreaterThan=221.10 and amountLessThan=800
        expectedResult.add(new Account(1, 1, 225.20));
        expectedResult.add(new Account(4, 1, 750.75));
        //act(ual)
        StatusObj statusObj = new StatusObj();
        List<Account> actualResult = accountService.getAllAccountsInRange(1, 221.10, 800.0, statusObj);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void getAllAccountsInRange_OnlyHas_amountGreaterThan() {  // Only has amountGreaterThan set
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, 1, 225.20));
        accounts.add(new Account(2, 1, 221.10));
        accounts.add(new Account(3, 1, 0.01));
        accounts.add(new Account(4, 1, 750.75));
        accounts.add(new Account(5, 1, 1250.75));
        Mockito.when(accountDao.getAllAccounts(1)).thenReturn(accounts);
        List<Account> expectedResult = new ArrayList<>();   // if we screen the list with amountGreaterThan=221.10 and amountLessThan=800
        expectedResult.add(new Account(1, 1, 225.20));
        expectedResult.add(new Account(4, 1, 750.75));
        expectedResult.add(new Account(5, 1, 1250.75));
        //act(ual)
        StatusObj statusObj = new StatusObj();
        List<Account> actualResult = accountService.getAllAccountsInRange(1, 221.10, null, statusObj);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void getAllAccountsInRange_OnlyHas_amountLessThan() {  // Only has amountGreaterThan set
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, 1, 225.20));
        accounts.add(new Account(2, 1, 221.10));
        accounts.add(new Account(3, 1, 0.01));
        accounts.add(new Account(4, 1, 750.75));
        accounts.add(new Account(5, 1, 1250.75));
        Mockito.when(accountDao.getAllAccounts(1)).thenReturn(accounts);
        List<Account> expectedResult = new ArrayList<>();   // if we screen the list with amountGreaterThan=221.10 and amountLessThan=800
        expectedResult.add(new Account(1, 1, 225.20));
        expectedResult.add(new Account(2, 1, 221.10));
        expectedResult.add(new Account(3, 1, 0.01));
        expectedResult.add(new Account(4, 1, 750.75));
        //act(ual)
        StatusObj statusObj = new StatusObj();
        List<Account> actualResult = accountService.getAllAccountsInRange(1, null, 800.0, statusObj);
        assertArrayEquals(expectedResult.toArray(), actualResult.toArray());
    }

    @Test
    void getOneAccount() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, 5, 225.20));
        accounts.add(new Account(2, 5, 221.10));
        accounts.add(new Account(3, 5, 0.01));
        Mockito.when(accountDao.getOneAccount(5,2)).thenReturn(accounts.get(2));
        Account expectedResult = accounts.get(2);
        Account actualResult = accountDao.getOneAccount(5,2);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateAccount() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, 2, 225.20));
        accounts.add(new Account(2, 2, 221.10));
        accounts.add(new Account(3, 2, 0.01));  // update this account client 2 account 3
        accounts.add(new Account(4, 2, 750.75));
        accounts.add(new Account(5, 2, 1250.75));
        Account updatedAccount = accounts.get(2);
        updatedAccount.setBalance(2.0);
        Mockito.when(accountDao.updateAccount(accounts.get(2))).thenReturn(true);   // client 2 account 3
        Boolean expectedResult = accountService.updateAccount(updatedAccount);
        assertTrue(expectedResult);
    }

    @Test
    void deleteAccount() {
        Mockito.when(accountDao.deleteAccount(3,1)).thenReturn(true);
        Boolean actualResult = accountService.deleteAccount(3,1);
        assertTrue(actualResult);
    }

    @Test
    void withdrawDepositAccountValid_Deposit() {
        // The JSON is deserialized in to WithdrawDepositObj class object
        WithdrawDepositObj withdrawDepositObj = new WithdrawDepositObj();
        withdrawDepositObj.deposit = 200.0;
        Account account = new Account(5,2, 175.0);
        Account accountUpdate = new Account(5,2, 375.0);
        Mockito.when(accountDao.getOneAccount(2, 5)).thenReturn(account);   // It's to get existing balance to deposit to
        Mockito.when(accountDao.updateAccount(accountUpdate)).thenReturn(true);
        Boolean expectedResult = accountService.withdrawDepositAccount(2, 5, withdrawDepositObj, new StatusObj());
        assertTrue(expectedResult);
    }

    @Test
    void withdrawDepositAccountValid_Withdraw() {
        // The JSON is deserialized in to WithdrawDepositObj class object
        WithdrawDepositObj withdrawDepositObj = new WithdrawDepositObj();
        withdrawDepositObj.withdraw = 57.95;
        Account account = new Account(4,3, 60.0);
        Account accountUpdate = new Account(4,3, 2.05);
        Mockito.when(accountDao.getOneAccount(3, 4)).thenReturn(account);   // It's to get existing balance to deposit to
        Mockito.when(accountDao.updateAccount(accountUpdate)).thenReturn(true);
        StatusObj statusObj = new StatusObj();
        Boolean expectedResult = accountService.withdrawDepositAccount(3, 4, withdrawDepositObj, statusObj);
        assertTrue(expectedResult);
    }

    @Test
    void withdrawDepositAccountInvalid_Withdraw_Overdraft() {
        // The JSON is deserialized in to WithdrawDepositObj class object
        WithdrawDepositObj withdrawDepositObj = new WithdrawDepositObj();
        withdrawDepositObj.withdraw = 60.01;
        Account account = new Account(4,3, 60.0);
        Account accountUpdate = new Account(4,3, -.01);
        Mockito.when(accountDao.getOneAccount(3, 4)).thenReturn(account);   // It's to get existing balance to deposit to
        Mockito.when(accountDao.updateAccount(accountUpdate)).thenReturn(false);
        StatusObj statusObj = new StatusObj();
        Boolean expectedResult = accountService.withdrawDepositAccount(3, 4, withdrawDepositObj, statusObj);
        assertFalse(expectedResult);
    }

    @Test
    void transferBetweenAccountsValid() {
        AmountObj amountObj = new AmountObj();
        amountObj.amount = 235.0;
        Account account1 = new Account(1,2, 235.0);
        Mockito.when(accountDao.getOneAccount(2, 1)).thenReturn(account1);
        Account account2 = new Account(2,2,25.75);
        Mockito.when(accountDao.getOneAccount(2,2)).thenReturn(account2);
        Account account1Update = new Account(1,2, 0.0);
        Mockito.when(accountDao.updateAccount(account1Update)).thenReturn(true);
        Account account2Update = new Account(2,2, 260.75);
        Mockito.when(accountDao.updateAccount(account2Update)).thenReturn(true);
        StatusObj statusObj = new StatusObj();
        Boolean expectedResult = accountService.transferBetweenAccounts(2,1,2,amountObj, statusObj);
        //System.out.println(statusObj);
        assertTrue(expectedResult);
    }

    @Test
    void transferBetweenAccountsInvalid_Transfer_Overdraft() {
        AmountObj amountObj = new AmountObj();
        amountObj.amount = 235.01;
        Account account1 = new Account(1,2, 235.0);
        Mockito.when(accountDao.getOneAccount(2, 1)).thenReturn(account1);
        Account account2 = new Account(2,2,25.75);
        Mockito.when(accountDao.getOneAccount(2,2)).thenReturn(account2);
        Account account1Update = new Account(1,2, 0.0);
        Mockito.when(accountDao.updateAccount(account1Update)).thenReturn(true);
        Account account2Update = new Account(2,2, 260.75);
        Mockito.when(accountDao.updateAccount(account2Update)).thenReturn(true);
        StatusObj statusObj = new StatusObj();
        Boolean expectedResult = accountService.transferBetweenAccounts(2,1,2,amountObj, statusObj);
        //System.out.println(statusObj);
        assertFalse(expectedResult);
    }
}