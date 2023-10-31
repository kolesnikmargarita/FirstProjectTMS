package transactionProject.model.builders;

import transactionProject.RegularExpressions;
import transactionProject.exceptions.InputException;
import transactionProject.model.Account;

public class AccountBuilder {

    private int clientId;
    private String number;
    private String type;
    private String currency;
    private Float money;

    public AccountBuilder setClientId(int setClientId) {
       clientId = setClientId;
       return this;
    }

    public AccountBuilder setNumber(String setNumber) throws InputException {
        if(!RegularExpressions.ACCOUNT_NUMBER.getPattern().matcher(setNumber).matches()) {
            throw new InputException("Номер счета введен некорректно.");
        }
        number = setNumber;
        return this;
    }

    public AccountBuilder setType(String setType) {
        type = setType;
        return this;
    }

    public AccountBuilder setCurrency(String setCurrency) {
        currency = setCurrency;
        return this;
    }

    public AccountBuilder setMoney(Float setMoney) {
        money = setMoney;
        return this;
    }

    public Account build() {
        return new Account(clientId, number, type, currency, money);
    }
}
