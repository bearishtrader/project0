package util;
//  De-serialize the JSON from Javalin PATCH handler's obj = context.bodyAsClass(Class.class) method.
// PATCH /clients/12/accounts/7/transfer/8 => transfer funds from account 7 to account 8 (Body: {"amount":500}) return 404 if no client or either account exists return 422 if insufficient funds
public class AmountObj {
    public Double amount = null;
}
