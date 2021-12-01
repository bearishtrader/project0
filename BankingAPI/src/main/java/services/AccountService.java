package services;

import dao.AccountDao;
import dao.AccountDaoImpl;
import model.Account;
import util.AmountObj;
import util.StatusObj;
import util.WithdrawDepositObj;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AccountService {
    AccountDao accountDao;

    public AccountService() {
        accountDao = new AccountDaoImpl();
    }
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    public Boolean createAccount(Account account, StatusObj statusObj) {
        Boolean success = false;
        if (account.getBalance()<0) {
            statusObj.status = 400;
            statusObj.message = "Account can not be created with negative balance of "+account.getBalance()+".";
        } else {
            success = accountDao.createAccount(account);
            if (success) {
                statusObj.status = 201;
                statusObj.message = account+" created successfully.";
            }
            else {
                statusObj.status = 400;
                statusObj.message = "An error has occurred, "+account+" not created try setting accountID to null or also client with clientId="+account.getClientId()+" may not exist.";
            }
        }
        return success;
    }
    public List<Account> getAllAccounts(Integer clientId) {
        return accountDao.getAllAccounts(clientId);
    }
    public List<Account> getAllAccountsInRange(Integer clientId, Double amountGreaterThan, Double amountLessThan, StatusObj statusObj) {
        List<Account> allAccounts = accountDao.getAllAccounts(clientId);
        List<Account> filteredAccounts = new ArrayList<>();
        if (allAccounts.size()>0) {
            for (Account account : allAccounts) {
                Boolean removeAccount = false;
                if (amountGreaterThan!=null && account.getBalance()<=amountGreaterThan) removeAccount = true;
                if (!removeAccount && amountLessThan!=null && account.getBalance()>=amountLessThan) removeAccount = true;
                if (!removeAccount) filteredAccounts.add(account);
            }
            if (filteredAccounts.size()>0) {
                statusObj.status = 200;
                statusObj.message = "Accounts successfully retrieved for clientId="+clientId+" in balance range amountGreaterThan="+amountGreaterThan+" to amountLessThan="+amountLessThan+".";
            } else {
                statusObj.status = 404;
                statusObj.message = "No accounts and/or client exists for clientId="+clientId+" in balance range amountGreaterThan="+amountGreaterThan+" to amountLessThan="+amountLessThan+".";
            }
        } else {
            statusObj.status = 404;
            statusObj.message = "No accounts and/or client exists for clientId="+clientId+".";
        }
        return filteredAccounts;
    }
    //GET /clients/9/accounts/4 => get account 4 for client 9 return 404 if no account or client exists
    public Account getOneAccount(Integer clientId, Integer accountId){ return accountDao.getOneAccount(clientId, accountId); }
    //PUT /clients/10/accounts/3 => update account with the id 3 for client 10 return 404 if no account or client exists
    public Boolean updateAccount(Account account) {
        return accountDao.updateAccount(account);
    }
    //DELETE /clients/15/accounts/6 => delete account 6 for client 15 return 404 if no account or client exists
    public Boolean deleteAccount(Integer clientId, Integer accountId) { return accountDao.deleteAccount(clientId, accountId); }
    // PATCH /clients/17/accounts/12 => Withdraw/deposit given amount (Body: {"deposit":500} or {"withdraw":250} return 404 if no account or client exists return 422 if insufficient funds
    public Boolean withdrawDepositAccount(Integer clientId, Integer accountId, WithdrawDepositObj withdrawDepositObj, StatusObj statusObj) {
        Boolean success = false;
        if (withdrawDepositObj.deposit!=null & withdrawDepositObj.withdraw!=null) {
            // The JSON had both fields populated we only allow one at a time so return an error
            statusObj.status = 400;
            statusObj.message = "JSON error: BOTH deposit and withdraw fields sent in same object only one or other allowed.";
        } else {
            Account account = accountDao.getOneAccount(clientId, accountId);
            if (withdrawDepositObj.deposit != null) { // It's a deposit
                if (account != null) {
                    Double newBalance = account.getBalance() + withdrawDepositObj.deposit;
                    newBalance = Math.round(newBalance*100)/100D;
                    if (accountDao.updateAccount(new Account(accountId, clientId, newBalance))) {
                        statusObj.status = 200;
                        statusObj.message = "Account deposit of "+withdrawDepositObj.deposit+" for accountId="+accountId+" and clientId="+clientId+" successful. New balance=" + newBalance;
                        success = true;
                    } else {
                        statusObj.status = 404;
                        statusObj.message = "Deposit unsuccessful. Account update with accountId=" + accountId + " and clientId=" + clientId + " failed.";
                    }
                } else {
                    statusObj.status = 404;
                    statusObj.message = "Deposit unsuccessful.  Account with accountId=" + accountId + " and clientId=" + clientId + " not found.";
                }
            }
            else if (withdrawDepositObj.withdraw != null) { // It's a withdrawal
                if (account != null) {
                    Double newBalance = account.getBalance() - withdrawDepositObj.withdraw;
                    newBalance = Math.round(newBalance*100)/100D;
                    if (newBalance<0) {
                        statusObj.status = 422;
                        statusObj.message = "Insufficient funds for withdrawal of "+withdrawDepositObj.withdraw+" for accountId="+accountId+" and clientId="+clientId+". "+
                            "New balance would be "+newBalance+".";
                    } else {
                        Account accountWithdrawUpdate = new Account(accountId, clientId, newBalance);
                        if (accountDao.updateAccount(accountWithdrawUpdate)) {
                            statusObj.status = 200;
                            statusObj.message = "Withdrawal of " + withdrawDepositObj.withdraw + " for account with accountId=" + accountId + " and clientId=" + clientId + " successful. New balance=" + newBalance +".";
                            success = true;
                        } else {
                            statusObj.status = 404;
                            statusObj.message = "Withdrawal unsuccessful. Account update with accountId=" + accountId + " and clientId=" + clientId + " failed.";
                        }
                    }
                } else {
                    statusObj.status = 404;
                    statusObj.message = "Withdrawal unsuccessful.  Account with accountId=" + accountId + " and clientId=" + clientId + " not found.";
                }
            }
        }
        return success;
    }
    // PATCH /clients/12/accounts/7/transfer/8 => transfer funds from account 7 to account 8 (Body: {"amount":500}) return 404 if no client or either account exists return 422 if insufficient funds
    public Boolean transferBetweenAccounts(Integer clientId, Integer accountId, Integer accountId2, AmountObj amountObj, StatusObj statusObj) {
        Boolean success = false;
        // Make sure we got the right JSON information, if withdraw or deposit is filled in mistakenly it's an error should only have one field.
        if (amountObj.amount > 0) {
            Account account = accountDao.getOneAccount(clientId, accountId);
            if (account != null) {
                Account account2 = accountDao.getOneAccount(clientId, accountId2);
                if (account2 != null) {
                    Double newBalance = account.getBalance() - amountObj.amount;
                    newBalance = Math.round(newBalance*100)/100D;
                    if (newBalance >= 0) {  // Both accounts exist, and we won't get an overdraft so proceed
                        account.setBalance(newBalance);
                        if (accountDao.updateAccount(account)) {
                            Double newBalance2 = account2.getBalance() + amountObj.amount;
                            newBalance2 = Math.round(newBalance2*100)/100D;
                            account2.setBalance(newBalance2);
                            if (accountDao.updateAccount(account2)) {
                                statusObj.status = 200;
                                statusObj.message = "Transfer of "+amountObj.amount+" successful.  Client account (clientId="+clientId+", accountId="+accountId+") now has "+account.getBalance()+" and second account (accountId="+accountId2+") has "+account2.getBalance()+".";
                                success = true;
                            }
                            else {
                                statusObj.status = 404;
                                statusObj.message = "Transfer unsuccessful.  Account with accountId="+accountId2+" and clientId="+clientId+" does not exist.  The account or client may have been deleted in the middle of the transfer.";
                            }
                        } else {    // Not a likely scenario, this will happen if someone else has deleted the account or client in the middle of our transaction
                            statusObj.status = 404;
                            statusObj.message = "Transfer unsuccessful.  Account with accountId="+accountId+" and clientId="+clientId+" does not exist.  The account or client may have been deleted in the middle of the transfer.";
                        }
                    } else {
                        statusObj.status = 422;
                        statusObj.message = "Transfer unsuccessful. Insufficient funds for account with accountId=" + accountId + " clintId=" + clientId + ". Balance would fall to " + newBalance + ".";
                    }
                } else {
                    statusObj.status = 404;
                    statusObj.message = "Transfer unsuccessful.  Account with accountId="+accountId2+" and clientId="+clientId+" does not exist.";
                }
            } else {
                statusObj.status = 404;
                statusObj.message = "Transfer unsuccessful.  Account with accountId="+accountId+" and clientId="+clientId+" does not exist.";
            }
        } else {
            statusObj.status = 400;
            statusObj.message = "Transfer amount="+amountObj.amount+" has to be great than 0.0";
        }
        return success;
    }
}