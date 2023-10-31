package transactionProject.database;

import transactionProject.exceptions.BalanceException;
import transactionProject.exceptions.CurrencyException;
import transactionProject.model.Account;
import transactionProject.model.Client;
import transactionProject.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBActions {

    public static final String DBNAME = "transactionsJavaCore";
    public static final String  CLIENTS_TABLE_NAME = "clients";
    public static final String  ACCOUNTS_TABLE_NAME = "accounts";
    public static final String  OPERATIONS_TABLE_NAME = "operations";
    public static final String ID_COLUMN = "id";
    private final CurrentConnection connection = CurrentConnection.getInstance();
    private static DBActions instance;

    public void createDatabase() {
        connection.createDatabase(DBNAME);
    }

    public void createTables() {
        connection.createTable(CLIENTS_TABLE_NAME, ID_COLUMN + " int auto_increment primary key",
                Client.F_NAME_COLUMN + " varchar(25)", Client.L_NAME_COLUMN + " varchar(25)",
                Client.PASSPORT_NUMBER_COLUMN + " varchar(15) NOT NULL UNIQUE");
        connection.createTable(ACCOUNTS_TABLE_NAME, ID_COLUMN + " int auto_increment primary key",
                Account.CLIENT_ID_COLUMN + " int",
                Account.NUMBER_COLUMN + " varchar(11) NOT NULL UNIQUE", Account.OPEN_DATE_COLUMN + " date",
                Account.TYPE_COLUMN + " varchar(50)",
                Account.CURRENCY_COLUMN + " varchar(15)", Account.MONEY_COLUMN + " float",
                "foreign key (" + Account.CLIENT_ID_COLUMN + ") REFERENCES " + CLIENTS_TABLE_NAME + " (" + ID_COLUMN + ")");
        connection.createTable(OPERATIONS_TABLE_NAME, ID_COLUMN + " int auto_increment primary key",
                Operation.OUTPUT_ACCOUNT_ID_COLUMN + " int", Operation.INPUT_ACCOUNT_ID_COLUMN + " int",
                Operation.MONEY_COLUMN + " float", Operation.DATE_COLUMN + " date", Operation.TIME_COLUMN + " time",
                Operation.STATUS_COLUMN + " varchar(50)", "foreign key (" + Operation.OUTPUT_ACCOUNT_ID_COLUMN
                        + ") REFERENCES " + ACCOUNTS_TABLE_NAME + " (" + ID_COLUMN + ")",
                "foreign key (" + Operation.INPUT_ACCOUNT_ID_COLUMN + ") REFERENCES " + ACCOUNTS_TABLE_NAME + " (" + ID_COLUMN + ")");
    }

    private DBActions() {}

    public static DBActions getInstance() {
        if(instance == null) {
            instance = new DBActions();
        }
        return instance;
    }

    public void addClient(Client client) {
        connection.addNote(CLIENTS_TABLE_NAME, client.getColumns(), client.getDate());
    }

    public void addAccount(Account account) {
        connection.addNote(ACCOUNTS_TABLE_NAME, account.getColumns(), account.getDate());
    }

    public void addOperation(Operation operation) {
        connection.addNote(OPERATIONS_TABLE_NAME, operation.getColumns(), operation.getDate());
    }

    public int findAccountId(String accountNumber) {
        return connection.findFieldSignificance(ACCOUNTS_TABLE_NAME, ID_COLUMN, Account.NUMBER_COLUMN, accountNumber);
    }

    public int findClientId(String passportNumber) {
        return connection.findFieldSignificance(CLIENTS_TABLE_NAME, ID_COLUMN, Client.PASSPORT_NUMBER_COLUMN, passportNumber);
    }

    public String getReportText() {
        StringBuilder result = new StringBuilder();
        ResultSet resultSet = connection.getTableData(OPERATIONS_TABLE_NAME);
        try {
            while (resultSet.next()) {
                result.append("Отправитель: ").append(getNameById(resultSet.getInt(2))).append("\n")
                        .append("Получатель: ").append(getNameById(resultSet.getInt(3))).append("\n");
                for(int i = 1; i <= Operation.QUANTITY_OF_COLUMNS; i++) {
                    String str = resultSet.getString(i);
                    result.append(str).append(" ");
                }
                result.append("\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result.toString();
    }

    private int getClientIdByAccountId(int id) {
        return Integer.parseInt(connection.findFieldSignificance(ACCOUNTS_TABLE_NAME, Account.CLIENT_ID_COLUMN, ID_COLUMN, id));
    }

    private String getNameById(int accountId) {
        int clientId = getClientIdByAccountId(accountId);
        return connection.findFieldSignificance(CLIENTS_TABLE_NAME, Client.F_NAME_COLUMN, ID_COLUMN, clientId) +
                " " +
                connection.findFieldSignificance(CLIENTS_TABLE_NAME, Client.L_NAME_COLUMN, ID_COLUMN, clientId);
    }

    public void compareCurrency(int outputAccountId, int inputAccountId) throws CurrencyException {
        String outputAccountCurrency = connection.findFieldSignificance(ACCOUNTS_TABLE_NAME,
                Account.CURRENCY_COLUMN, ID_COLUMN, outputAccountId);
        String inputAccountCurrency = connection.findFieldSignificance(ACCOUNTS_TABLE_NAME,
                Account.CURRENCY_COLUMN, ID_COLUMN, inputAccountId);
        if(!outputAccountCurrency.equals(inputAccountCurrency)) {
            throw new CurrencyException();
        }
    }

    public void compareAmounts(int outputAccountId, float money) throws BalanceException {
        float outputAccountMoney = Float.parseFloat(connection.findFieldSignificance(ACCOUNTS_TABLE_NAME,
                Account.MONEY_COLUMN, ID_COLUMN, outputAccountId));
        if(money > outputAccountMoney) {
            throw new BalanceException();
        }
    }

    public String transaction(int outputAccountId, int inputAccountId, float money) {
        String commandSubMoney = "UPDATE " + ACCOUNTS_TABLE_NAME + " SET " + Account.MONEY_COLUMN +
                " = " + Account.MONEY_COLUMN + " - " +  money + " WHERE " + ID_COLUMN + " = " + outputAccountId;
        String commandAddMoney = "UPDATE " + ACCOUNTS_TABLE_NAME + " SET " + Account.MONEY_COLUMN +
                " = " + Account.MONEY_COLUMN + " + " +  money + " WHERE " + ID_COLUMN + " = " + inputAccountId;
        return connection.transaction(commandSubMoney, commandAddMoney);
    }
}
