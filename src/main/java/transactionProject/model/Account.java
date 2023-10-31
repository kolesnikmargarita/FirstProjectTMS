package transactionProject.model;

import java.time.LocalDate;

public class Account implements Tabular{

    public static final String CLIENT_ID_COLUMN = "clientId";
    public static final String NUMBER_COLUMN = "number";
    public static final String OPEN_DATE_COLUMN = "openDate";
    public static final String TYPE_COLUMN = "type";
    public static final String CURRENCY_COLUMN = "currency";
    public static final String MONEY_COLUMN = "money";

    private final int clientId;
    private final String number;
    private final LocalDate openDate;
    private final String type;
    private final String currency;
    private final Float money;

    public Account(int setClientId, String setNumber, String setType, String setCurrency, float setMoney) {
        clientId = setClientId;
        number = setNumber;
        type = setType;
        currency = setCurrency;
        money = setMoney;
        openDate = LocalDate.now();
    }

    public String getColumns() {
        return CLIENT_ID_COLUMN + ", " + NUMBER_COLUMN + ", " + OPEN_DATE_COLUMN + ", " +
                TYPE_COLUMN + ", " + CURRENCY_COLUMN + ", " + MONEY_COLUMN;
    }

    public String getDate() {
        return "'" + clientId + "', '" + number + "', '" + openDate + "', '" + type + "', '" + currency + "', '" + money + "'";
    }
}
