package transactionProject.database;


import java.sql.*;

public class CurrentConnection {

    private final String NAME_USER = "root";
    private final String PASSWORD = "HelloSQL2023!#";
    private final String URL = "jdbc:mysql://localhost:3306/" + DBActions.DBNAME;
    private Connection connection;
    private Statement statement;
    private static CurrentConnection instance;

    private CurrentConnection() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, NAME_USER, PASSWORD);
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Unable to load class.");
            e.printStackTrace();
        }
    }

    public static CurrentConnection getInstance() {
        if(instance == null) {
            instance = new CurrentConnection();
        }
        return instance;
    }

    public void createDatabase(String databaseName) {
        try {
            statement.executeUpdate("CREATE DATABASE " + databaseName);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTable(String tableName, String ... columnNames) {
        StringBuilder columns = new StringBuilder(" (");
        for(String columnName : columnNames) {
            columns.append(columnName).append(", ");
        }
        columns.replace(columns.length() - 2, columns.length(), ")");
        try {
            statement.executeUpdate("CREATE TABLE " + tableName + columns);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addNote(String tableName, String columns, String date) {
        try {
            statement.executeUpdate("INSERT INTO " + tableName + " (" + columns + ") value (" + date + ")");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet getTableData(String tableName) {
        ResultSet resultSet = null;
        try {
            Statement newStatement = connection.createStatement();
            resultSet = newStatement.executeQuery("SELECT * FROM " + tableName);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultSet;
    }

    public int findFieldSignificance(String tableName, String desiredColumnName, String columnName, String significance) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT " + desiredColumnName + " FROM " + tableName +
                    " WHERE " + columnName + " = '" + significance + "'");
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public  String findFieldSignificance(String tableName, String desiredColumnName, String columnName, int significance) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT " + desiredColumnName + " FROM " + tableName +
                    " WHERE " + columnName + " = " + significance);
            resultSet.next();
            return resultSet.getString(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String transaction(String ... sqlCommands) {
        try {
            connection.setAutoCommit(false);
            for(String command : sqlCommands){
                statement.executeUpdate(command);
            }
            connection.commit();
            return "Выполнено успешно";
        } catch(SQLException e) {
            System.out.println(e.getMessage());
            return "Произошла ошибка";
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
