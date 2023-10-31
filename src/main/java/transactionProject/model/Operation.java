package transactionProject.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Operation implements Tabular{

    public static final String OUTPUT_ACCOUNT_ID_COLUMN = "outputAccountId";
    public static final String INPUT_ACCOUNT_ID_COLUMN = "inputAccountId";
    public static final String MONEY_COLUMN = "money";
    public static final String DATE_COLUMN = "date";
    public static final String TIME_COLUMN = "time";
    public static final String STATUS_COLUMN = "status";
    public static final int QUANTITY_OF_COLUMNS = 7;

    private final int outputAccountId;
    private final int inputAccountId;
    private final Float money;
    private final LocalDate date;
    private final LocalTime time;
    private String status;

    public Operation(int setOutputAccountId, int setInputAccountId, Float setMoney) {
        money = setMoney;
        outputAccountId = setOutputAccountId;
        inputAccountId = setInputAccountId;
        date = LocalDate.now();
        time = LocalTime.now();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getColumns() {
        return OUTPUT_ACCOUNT_ID_COLUMN + ", " + INPUT_ACCOUNT_ID_COLUMN + ", " + MONEY_COLUMN + ", " +
                DATE_COLUMN + ", " + TIME_COLUMN + ", " + STATUS_COLUMN;
    }

    public String getDate() {
        return "'" + outputAccountId + "', '" + inputAccountId + "', '" + money + "', '"
                + date + "', '" + time + "', '" + status + "'";
    }

    public int getOutputAccountId() {
        return outputAccountId;
    }

    public int getInputAccountId() {
        return inputAccountId;
    }

    public Float getMoney() {
        return money;
    }

    public String getStatus() {
        return status;
    }
}
