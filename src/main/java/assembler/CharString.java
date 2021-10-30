package assembler;

/**
 *
 * @author andrewtaylor
 */
public class CharString {
    
    private String value;
    
    public static final char SINGLE_QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '"';
    public static final char ESCAPE_CHARACTER = '\\';
    public static final char COMMA = ',';
    
    public CharString() {}
    
    public CharString(String operand) {    
        boolean openDoubleQuote = false;
        boolean openSingleQuote = false;
        int numQuotes = 0;
        boolean isQuoted = false;
        int j = 0;
        for (int i = 0; i < operand.length(); i++) {
            char b = (i > 0) ? operand.charAt(i-1) : '\0';
            char c = operand.charAt(i);
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
            if ((c == COMMA && !openDoubleQuote && !openSingleQuote) || (i == operand.length()-1)) {
                String s = operand.substring(j, i+1);
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
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
