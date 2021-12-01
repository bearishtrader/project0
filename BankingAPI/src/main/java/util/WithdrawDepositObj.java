package util;
//  De-serialize the JSON from Javalin PATCH handler's obj = context.bodyAsClass(Class.class) method. As per specs only one of the fields is to be populated at a time for each
//      withdraw or deposit PATCH operation hence the null initializations are used as sentinel values.  If you only specify one field in the JSON such as {"withdraw": 250} the object will be
//      populated by context.bodyAsClass(WithdrawDepositObj.class) and leave deposit field null
//  PATCH /clients/17/accounts/12 => Withdraw/deposit given amount (Body: {"deposit":500} or {"withdraw":250} return 404 if no account or client exists return 422 if insufficient funds
public class WithdrawDepositObj {
    public Double withdraw=null;
    public Double deposit=null;
}
