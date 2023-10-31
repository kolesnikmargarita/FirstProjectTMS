package transactionProject.model.builders;

import transactionProject.exceptions.InputException;
import transactionProject.model.Client;
import transactionProject.RegularExpressions;

public class ClientBuilder {

    private String firstName;
    private String lastName;
    private String passportNumber;

    public ClientBuilder setFirstName(String setFirstName) throws InputException {
        if(!RegularExpressions.NAME.getPattern().matcher(setFirstName).matches()) {
            throw new InputException("Имя введено некорректно.");
        }
        firstName = setFirstName;
        return this;
    }

    public ClientBuilder setLastName(String setLastName) throws InputException {
        if(!RegularExpressions.NAME.getPattern().matcher(setLastName).matches()) {
            throw new InputException("Фамилия введена некорректно.");
        }
        lastName = setLastName;
        return this;
    }

    public ClientBuilder setPassportNumber(String setPassportNumber) throws InputException {
        if(!RegularExpressions.PASSPORT_NUMBER.getPattern().matcher(setPassportNumber).matches()) {
            throw new InputException("Номер паспорта введен некорректно.");
        }
        passportNumber = setPassportNumber;
        return this;
    }

    public Client build() {
        return new Client(firstName, lastName, passportNumber);
    }
}
