package transactionProject.exceptions;

public class CurrencyException extends Exception {

    public CurrencyException() {
        super("Не возможно выполнить перевод между счетами разной валюты");
    }
}
