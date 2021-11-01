package assembler;

/**
 *
 * @author andrewtaylor
 */
public class StringConstant {
    
    private String value = "";
    
    public static final char SINGLE_QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '"';
    public static final char ESCAPE_CHARACTER = '\\';
    public static final char COMMA = ',';
    
    public StringConstant() {}
    
    public StringConstant(String expression) {    
        boolean openDoubleQuote = false;
        boolean openSingleQuote = false;
        int numQuotes = 0;
        boolean isQuoted = false;
        int j = 0;
        for (int i = 0; i < expression.length(); i++) {
            char b = (i > 0) ? expression.charAt(i-1) : '\0';
            char c = expression.charAt(i);
            if (c == DOUBLE_QUOTE && b != ESCAPE_CHARACTER && !openSingleQuote) {
                openDoubleQuote = !openDoubleQuote;
                numQuotes++;
            }   
            else if (c == SINGLE_QUOTE && b != ESCAPE_CHARACTER && !openDoubleQuote) {
                openSingleQuote = !openSingleQuote;
                numQuotes++;
            }   
            if (numQuotes == 2)
                isQuoted = true;
            if ((c == COMMA && !openDoubleQuote && !openSingleQuote) || (i == expression.length()-1)) {
                String s = expression.substring(j, i+1);
                if (s.isEmpty())
                    continue;
                else if (isQuoted) {
                    s = s.substring(1, s.length()-1);
                    value += s;
                }   
                else {
                    try {
                        value += Byte.decode(s).toString();
                    } catch (NumberFormatException e) {
                        System.err.println(e);
                    }   
                }   
                numQuotes = 0;
                isQuoted = false;
                j = i + 1;
            }   
        }   
    }
    
    public static boolean isStringConstant(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (i == 0 && c == '"')
                return true;
            if (i > 1 ) {
                char b = expression.charAt(i-1);
                if (c == '"' && b != '\'' && b != '\\')
                    return true;
            }
        }
        return false;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
