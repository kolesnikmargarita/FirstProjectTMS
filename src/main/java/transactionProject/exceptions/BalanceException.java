package transactionProject.exceptions;

public class BalanceException extends Exception {

    public BalanceException() {
        super("На счету отправителя не достаточно средств.");
    }
}
