package frontcontroller;

import dao.ClientDaoImpl;
import io.javalin.http.Context;
import model.Account;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import services.AccountService;
import util.WithdrawDepositObj;
import util.AmountObj;
import util.StatusObj;
import java.util.List;
import java.util.Map;

public class AccountController {
    static AccountService accountService = new AccountService();
    static Logger logger = Logger.getLogger(AccountController.class);

    //POST /clients/5/accounts =>creates a new account for client with the id of 5 return a 201 status code
    public static void createAccount(Context context) {
        Account account = context.bodyAsClass(Account.class);
        account.setClientId(Integer.parseInt(context.pathParam("client_id")));
        StatusObj statusObj = new StatusObj();
        if (accountService.createAccount(account,statusObj)) {
            logger.info(statusObj);
        } else {
            logger.error(statusObj);
        }
        context.status(statusObj.status);
        context.json(statusObj);
    }
    //GET /clients/7/accounts => get all accounts for client 7 return 404 if no client exists
    //GET /clients/7/accounts?amountLessThan=2000&amountGreaterThan400 => get all accounts for client 7 between 400 and 2000 return 404 if no client exists
    public static void getAllAccounts(Context context) {
        Integer clientId = Integer.parseInt(context.pathParam("client_id"));
        Map<String, List<String>> mapOfQueryParams = context.queryParamMap();
        StatusObj statusObj = new StatusObj();
        if (mapOfQueryParams.isEmpty()) {   // No query parameters specified, so we just want all accounts with given clientId
            List<Account> accounts = accountService.getAllAccounts(clientId);
            if (accounts.size() > 0) {
                statusObj.status = 200;
                statusObj.message = "Accounts for clientId="+clientId+" retrieved successfully.";
                logger.info(statusObj);
                context.status(statusObj.status);
                context.json(accounts); // SUCCESS #1/2 case return all accounts
            } else {
                statusObj.status = 404;
                statusObj.message = "No accounts found with clientId=" + clientId + ".";
                logger.error(statusObj);
                context.status(statusObj.status);
                context.json(statusObj);
            }
        }
        else if (mapOfQueryParams.size()>2) {    // Can't have more than two query parameters
            statusObj.status = 400;
            statusObj.message = "Too many query parameters specified can only have both amountGreaterThan, amountLessThan or just one or the other.";
            logger.error(statusObj);
            context.status(statusObj.status);
            context.json(statusObj.status);
        } else if ( mapOfQueryParams.size()==2 &&
                    !(mapOfQueryParams.containsKey("amountGreaterThan") && mapOfQueryParams.containsKey("amountLessThan")) ) {
            statusObj.status = 400;
            statusObj.message = "Likely spelling error there can only be one or the other or both amountGreaterThan, amountLessThan specified as query parameters.";
            logger.error(statusObj);
            context.status(statusObj.status);
            context.json(statusObj);
        } else if (mapOfQueryParams.size()==1 && !(mapOfQueryParams.containsKey("amountGreaterThan") || mapOfQueryParams.containsKey("amountLessThan")) ) {
            statusObj.status = 400;
            statusObj.message = "Likely spelling error there can only be one or the other or both amountGreaterThan, amountLessThan specified as query parameters.";
            logger.error(statusObj);
            context.status(statusObj.status);
            context.json(statusObj);
        } else { // We got one or both of the parameters amountLessThan and amountGreaterThan, and they are spelled right, so we can proceed with the range filtered account retrieval
            String amountGreaterThanStr = context.queryParam("amountGreaterThan");
            String amountLessThanStr = context.queryParam("amountLessThan");
            Double amountGreaterThan = null;
            Double amountLessThan = null;
            if (amountGreaterThanStr != null) amountGreaterThan = Double.parseDouble(amountGreaterThanStr);
            if (amountLessThanStr != null) amountLessThan = Double.parseDouble(amountLessThanStr);
            List<Account> accounts = accountService.getAllAccountsInRange(clientId, amountGreaterThan, amountLessThan, statusObj);
            if (accounts.size()>0) {    // SUCCESS case #2/2 got one or more accounts back
                logger.info(statusObj);
                context.status(statusObj.status);
                context.json(accounts);
            } else {
                logger.error(statusObj);
                context.status(statusObj.status);
                context.json(statusObj);
            }
        }
    }
    //GET /clients/9/accounts/4 => get account 4 for client 9 return 404 if no account or client exists
    public static void getOneAccount(Context context) {
        Integer clientId = Integer.parseInt(context.pathParam("client_id"));
        Integer accountId = Integer.parseInt(context.pathParam("account_id"));
        Account account = accountService.getOneAccount(clientId, accountId);
        StatusObj statusObj = new StatusObj();
        if (account != null) {
            statusObj.status = 200;
            statusObj.message = "Account "+account+" successfully retrieved.";
            logger.info(statusObj);
            context.status(statusObj.status);
            context.json(account);
        }
        else {
            statusObj.status = 404;
            statusObj.message = "No account found with clientId="+clientId+" accountId="+accountId+".";
            logger.error(statusObj);
            context.status(statusObj.status);
            context.json(statusObj);
        }
    }
    // PUT /clients/10/accounts/3 => update account with the id 3 for client 10 return 404 if no account or client exists
    public static void updateAccount(Context context) {
        Account account = context.bodyAsClass(Account.class);
        StatusObj statusObj = new StatusObj();
        if (account!=null) {
            account.setAccountId(Integer.parseInt(context.pathParam("account_id")));
            account.setClientId(Integer.parseInt(context.pathParam("client_id")));
            if (accountService.updateAccount(account)) {
                statusObj.status = 200;
                statusObj.message = "Account " + account.getAccountId() + " for clientId=" + account.getClientId() + " successfully updated.  Balance is now "+account.getBalance()+".";
                logger.info(statusObj);
                context.status(statusObj.status);
                context.json(statusObj);
            } else {
                statusObj.status = 404;
                statusObj.message = "No account found with clientId="+account.getClientId().toString()+" accountId="+account.getAccountId().toString();
                logger.error(statusObj);
                context.status(statusObj.status);
                context.json(statusObj);
            }
        } else {
            statusObj.status = 400;
            statusObj.message = "Body of request has improperly formatted JSON string.";
            logger.error(statusObj);
            context.status(statusObj.status);
            context.json(statusObj);
        }
    }
    //DELETE /clients/15/accounts/6 => delete account 6 for client 15 return 404 if no account or client exists
    public static void deleteAccount(Context context) {
        Integer clientId = Integer.parseInt(context.pathParam("client_id"));
        Integer accountId = Integer.parseInt(context.pathParam("account_id"));
        StatusObj statusObj = new StatusObj();
        if (accountService.deleteAccount(clientId, accountId)) {
            statusObj.status = 205;
            statusObj.message = "Account with accountId=" + accountId + " and clientId=" + clientId + " successfully deleted.";
            logger.info(statusObj);
        }
        else {
            statusObj.status = 404;
            statusObj.message = "Delete account unsuccessful.  Account (accountId=" + accountId + ") and/or client (clientId=" + clientId + ") not found.";
            logger.error(statusObj);
        }
        context.status(statusObj.status);
        context.json(statusObj);
    }
    //PATCH /clients/17/accounts/12 => Withdraw/deposit given amount (Body: {"deposit":500} or {"withdraw":250} return 404 if no account or client exists return 422 if insufficient funds
    public static void withdrawOrDepositAccount(Context context) {
        WithdrawDepositObj withdrawDepositObj = context.bodyAsClass(WithdrawDepositObj.class);
        Integer clientId = Integer.parseInt(context.pathParam("client_id"));
        Integer accountId = Integer.parseInt(context.pathParam("account_id"));
        if (withdrawDepositObj != null) {
            StatusObj statusObj = new StatusObj();
            if (accountService.withdrawDepositAccount(clientId, accountId, withdrawDepositObj, statusObj)) {
                logger.info(statusObj);
            } else {
                logger.error(statusObj);
            }
            context.status(statusObj.status);   // An error occurred 404 no account or client, 422 insufficient funds or 400 malformed JSON request
            context.json(statusObj);
        }
    }
    //PATCH /clients/12/accounts/7/transfer/8 => transfer funds from account 7 to account 8 (Body: {"amount":500}) return 404 if no client or either account exists return 422 if insufficient funds
    public static void transferBetweenAccounts(Context context) {
        AmountObj amountObj = context.bodyAsClass(AmountObj.class);
        Integer clientId = Integer.parseInt(context.pathParam("client_id"));
        Integer accountId = Integer.parseInt(context.pathParam("account_id"));
        Integer accountId2 = Integer.parseInt(context.pathParam("account_id_2"));
        StatusObj statusObj = new StatusObj();
        if (accountService.transferBetweenAccounts(clientId, accountId, accountId2, amountObj, statusObj)) {
            logger.info(statusObj);
        } else {    // error case can be status 404 no client or account or 422 insufficient funds
            logger.error(statusObj);
        }
        context.status(statusObj.status);
        context.json(statusObj);
    }
}