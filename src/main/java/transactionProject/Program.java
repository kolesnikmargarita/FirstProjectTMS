package transactionProject;

import com.fasterxml.jackson.databind.ObjectMapper;
import transactionProject.database.DBActions;
import transactionProject.exceptions.BalanceException;
import transactionProject.exceptions.CurrencyException;
import transactionProject.exceptions.DataFormatException;
import transactionProject.exceptions.InputException;
import transactionProject.model.Account;
import transactionProject.model.Client;
import transactionProject.model.Operation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import transactionProject.model.builders.AccountBuilder;
import transactionProject.model.builders.ClientBuilder;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Program {

    private static File directory;
    private static String[] files;
    private static final ArrayList<Operation> operations = new ArrayList<>();
    private static final DBActions databaseConnection = DBActions.getInstance();
    private static final String REPORT_FILE_PASS = "src/main/java/files/reportFile.txt";

    private static class JsonOperation {

        public String outputAccountNumber;
        public String inputAccountNumber;
        public float money;
    }

    public static void main(String[] args) {
        setDirectory(args);
        int action;
        boolean executeTransactions = true;
        do {
            action = setInt("выполнить переводы - 1\nотчет - 2\n" +
                    "добавить клиента - 3\nдобавить счет - 4\nсоздать таблицы - 5\n" +
                    "выход - 6\n", t -> t > 0 && t <= 6);
            switch(action) {
                case 1 -> {
                    if(executeTransactions) {
                        parseFiles();
                        for (Operation operation : operations) {
                            if(operation.getStatus().isEmpty()){
                                operation.setStatus(databaseConnection.transaction(
                                        operation.getOutputAccountId(), operation.getInputAccountId(), operation.getMoney()));
                            }
                            databaseConnection.addOperation(operation);
                        }
                        executeTransactions = false;
                    } else {
                        System.out.println("Транзакции были выполнены.\nДля выполнения операции обновите файлы транзакций и перезапустите программу.");
                    }
                    setReport();
                }
                case 2 -> {
                    StringBuilder result = new StringBuilder();
                    try(FileReader fileReader = new FileReader(REPORT_FILE_PASS)) {
                        int symbol;
                        while((symbol = fileReader.read()) != -1) {
                            result.append((char)symbol);
                        }
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                    System.out.println(result);
                }
                case 3 -> {
                    try {
                        databaseConnection.addClient(setClient());
                    } catch (InputException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case 4 -> {
                    try {
                        databaseConnection.addAccount(setAccount());
                    } catch (InputException e) {
                        System.out.println(e.getMessage());
                    }
                }
                //case 5 -> databaseConnection.createDatabase();
                case 5 -> databaseConnection.createTables();
                default -> {}
            }
        } while(action != 6);
    }

    private static void setDirectory(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (args.length == 0) {
            args = new String[]{""};
        }
        while(true) {
            directory = new File(args[0]);
            files = directory.list();
            if (files == null) {
                System.out.print("Введите путь к каталогу: ");
            } else if(files.length < 1){
                System.out.print("Каталог пуст. Введите путь к каталогу: ");
            } else {
                break;
            }
            args[0] = scanner.nextLine();
        }
    }

    private static void parseFiles(){
        File inputFile;
        String outputAccount = "";
        int outputAccountId = 0;
        String inputAccount = "";
        int inputAccountId = 0;
        float money = 0;
        String status;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            for (String file : files) {
                if (file.endsWith(".xml")) {
                    inputFile = new File(directory + "\\"  + file);
                    Document doc = dBuilder.parse(inputFile);
                    doc.getDocumentElement().normalize();
                    status = "";
                    try{
                        outputAccount = doc.getElementsByTagName("outputAccountNumber").item(0).getTextContent();
                        inputAccount = doc.getElementsByTagName("inputAccountNumber").item(0).getTextContent();
                        String moneyString = doc.getElementsByTagName("money").item(0).getTextContent();
                        checkStringFormat(outputAccount, RegularExpressions.ACCOUNT_NUMBER.getPattern());
                        checkStringFormat(inputAccount, RegularExpressions.ACCOUNT_NUMBER.getPattern());
                        checkStringFormat(moneyString, RegularExpressions.MONEY.getPattern());
                        money = Float.parseFloat(moneyString);
                        outputAccountId = databaseConnection.findAccountId(outputAccount);
                        inputAccountId = databaseConnection.findAccountId(inputAccount);
                        databaseConnection.compareCurrency(outputAccountId, inputAccountId);
                        databaseConnection.compareAmounts(outputAccountId, money);
                    } catch (NullPointerException | DataFormatException | CurrencyException | BalanceException e) {
                        System.out.println(e.getMessage());
                        status = "Ошибка выполнения";
                    } finally {
                        Operation operation = new Operation(outputAccountId, inputAccountId, money);
                        operation.setStatus(status);
                        operations.add(operation);
                    }
                } else if(file.endsWith(".json")) {
                    inputFile = new File(directory + "\\"  + file);
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonOperation jsonOperation = objectMapper.readValue(inputFile, JsonOperation.class);
                    outputAccount = jsonOperation.outputAccountNumber;
                    inputAccount = jsonOperation.inputAccountNumber;
                    money = jsonOperation.money;
                    status = "";
                    try {
                        checkStringFormat(outputAccount, RegularExpressions.ACCOUNT_NUMBER.getPattern());
                        checkStringFormat(inputAccount, RegularExpressions.ACCOUNT_NUMBER.getPattern());
                        if (money <= 0) {
                            throw new DataFormatException();
                        }
                        outputAccountId = databaseConnection.findAccountId(outputAccount);
                        inputAccountId = databaseConnection.findAccountId(inputAccount);
                        databaseConnection.compareCurrency(outputAccountId, inputAccountId);
                        databaseConnection.compareAmounts(outputAccountId, money);
                    } catch (DataFormatException | CurrencyException | BalanceException e) {
                        System.out.println(e.getMessage());
                        status = "Ошибка выполнения";
                    } finally {
                        Operation operation = new Operation(outputAccountId, inputAccountId, money);
                        operation.setStatus(status);
                        operations.add(operation);
                    }
                }
            }
        } catch(ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void checkStringFormat(String string, Pattern pattern) throws DataFormatException {
        Matcher matcher = pattern.matcher(string);
        if(!matcher.matches()) {
            throw new DataFormatException();
        }
    }

    public static HashMap<String, String> setData(String ... dateTitles) {
        HashMap<String, String> result = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        for(String title : dateTitles){
            System.out.println("\n" + title + ": ");
            result.put(title, scanner.nextLine());
        }
        return  result;
    }

    public static int setInt(String message, Predicate<Integer> predicate) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(message);
        int result;
        while(true) {
            if(scanner.hasNextInt()) {
                result = scanner.nextInt();
                if(predicate.test(result)) {
                    return result;
                }
            }
            scanner.nextLine();
            System.out.println("Некорреетный ввод. Повторит попытку.");
        }
    }

    public static float setFloat(String message, Predicate<Float> predicate) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(message);
        float result;
        while(true) {
            if(scanner.hasNextFloat()) {
                result = scanner.nextFloat();
                if(predicate.test(result)) {
                    return result;
                }
            }
            scanner.nextLine();
            System.out.println("Некорреетный ввод. Повторит попытку.");
        }
    }

    private static Client setClient() throws InputException {
        HashMap<String, String> data = setData("Имя", "Фамилия", "Номер паспорта");
        return new ClientBuilder().setFirstName(data.get("Имя"))
                .setLastName(data.get("Фамилия"))
                .setPassportNumber(data.get("Номер паспорта")).build();
    }

    private static Account setAccount() throws InputException {
        HashMap<String, String> data = setData("Номер паспорта", "Номер счета", "Тип", "Валюта");
        int clientId = databaseConnection.findClientId(data.get("Номер паспорта"));
        float money = setFloat("Начальная сумма: ", t -> t >= 0);
        return new AccountBuilder().setClientId(clientId)
                .setNumber(data.get("Номер счета"))
                .setType(data.get("Тип"))
                .setCurrency(data.get("Валюта"))
                .setMoney(money).build();
    }

    private static void setReport() {
        File repportFile = new File(REPORT_FILE_PASS);
        try(FileWriter fileWriter = new FileWriter(repportFile)){
            fileWriter.write(databaseConnection.getReportText());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
