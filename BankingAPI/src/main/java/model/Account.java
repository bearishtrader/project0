package model;

import java.util.Objects;

public class Account {
    private Integer accountId;
    private Integer clientId;
    private Double balance;

    public Account() {
    };
    public Account(Integer accountId, Integer clientId, Double balance) {
        this.accountId = accountId;
        this.clientId = clientId;
        this.balance = balance;
    };

    public Integer getAccountId() {return accountId;}
    public void setAccountId(Integer accountId) {this.accountId = accountId;}
    public Integer getClientId() {return clientId;}
    public void setClientId(Integer clientId) {this.clientId = clientId;}
    public Double getBalance() {return balance;}
    public void setBalance(Double balance) {this.balance = balance;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountId, account.accountId) && Objects.equals(clientId, account.clientId) && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, clientId, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", clientId=" + clientId +
                ", balance=" + balance +
                '}';
    }
}