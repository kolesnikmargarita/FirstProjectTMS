package transactionProject.exceptions;

public class DataFormatException extends Exception{

    public DataFormatException() {
        super("Файл содержит некорректные данные");
    }
}
