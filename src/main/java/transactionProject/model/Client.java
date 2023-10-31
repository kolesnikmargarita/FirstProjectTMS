package transactionProject.model;

public class Client implements Tabular{

    public static final String F_NAME_COLUMN = "firstName";
    public static final String L_NAME_COLUMN = "lastName";
    public static final String PASSPORT_NUMBER_COLUMN = "passportNumber";

    private final String firstName;
    private final String lastName;
    private final String passportNumber;

    public Client(String firstName, String lastName, String passportNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNumber = passportNumber;
    }

    public String getColumns() {
        return F_NAME_COLUMN + ", " + L_NAME_COLUMN + ", " + PASSPORT_NUMBER_COLUMN;
    }

    public String getDate() {
        return "'" + firstName + "', '" + lastName + "', '" + passportNumber + "'";
    }
}
