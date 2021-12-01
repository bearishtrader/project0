package dao;

import model.Account;

import java.util.List;

public interface AccountDao {
    Boolean createAccount(Account account);
    List<Account> getAllAccounts(Integer clientId);
    Account getOneAccount(Integer clientId, Integer accountId);
    Boolean updateAccount(Account account);
    Boolean deleteAccount(Integer clientId, Integer accountId);
}
