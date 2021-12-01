package frontcontroller;

import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Dispatcher {
    public Dispatcher(Javalin javalin) {
        javalin.routes(()-> {
            path("clients", ()->{
                post(ClientController::createClient); // POST /clients => Creates a new client return a 201 status code
                get(ClientController::getAllClients); // GET /clients => gets all clients return 200
                path("{client_id}", () -> {
                    get(ClientController::getOneClient);    // GET /clients/10 => get client with id of 10 return 404 if no such client exist
                    put(ClientController::updateClient);    // PUT /clients/12 => updates client with id of 12 return 404 if no such client exist
                    delete(ClientController::deleteClient); // DELETE /clients/15 => deletes client with the id of 15 return 404 if no such client exist return 205 if success
                    path("accounts", () -> {
                        post(AccountController::createAccount); // POST /clients/5/accounts =>creates a new account for client with the id of 5 return a 201 status code
                        get(AccountController::getAllAccounts); // GET /clients/7/accounts => get all accounts for client 7 return 404 if no client exists
                                                                // GET /clients/7/accounts?amountLessThan=2000&amountGreaterThan400 =>
                                                                //      get all accounts for client 7 between 400 and 2000 return 404 if no client exists
                        path("{account_id}", () -> {
                            get(AccountController::getOneAccount);       // GET /clients/9/accounts/4 => get account 4 for client 9 return 404 if no account or client exists
                            put(AccountController::updateAccount);       // PUT /clients/10/accounts/3 => update account with the id 3 for client 10 return 404 if no account or client exists
                            delete(AccountController::deleteAccount);    // DELETE /clients/15/accounts/6 => delete account 6 for client 15 return 404 if no account or client exists
                            patch(AccountController::withdrawOrDepositAccount); // PATCH /clients/17/accounts/12 => Withdraw/deposit given amount (Body: {"deposit":500} or {"withdraw":250} return 404 if no account or client exists return 422 if insufficient funds
                            path("transfer", () -> {
                                path("{account_id_2}", () -> {
                                    patch(AccountController::transferBetweenAccounts); // PATCH /clients/12/accounts/7/transfer/8 => transfer funds from account 7 to account 8 (Body: {"amount":500}) return 404 if no client or either account exists return 422 if insufficient funds
                                });
                            });
                        });
                    });
                });
            });
        });
    }
}
