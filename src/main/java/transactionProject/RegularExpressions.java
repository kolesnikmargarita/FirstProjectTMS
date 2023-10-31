package transactionProject;

import java.util.regex.Pattern;

public enum RegularExpressions {
    ACCOUNT_NUMBER, MONEY, PASSPORT_NUMBER, NAME;

    public Pattern getPattern()  {
        return switch (this){
            case ACCOUNT_NUMBER -> Pattern.compile("\\d{5}-\\d{5}");
            case MONEY -> Pattern.compile("\\d+\\.?\\d+");
            case PASSPORT_NUMBER -> Pattern.compile("[A-Z]{2}\\d{7}");
            default -> Pattern.compile("[А-ЯA-Z][a-zа-я]+");
        };
    }
}
