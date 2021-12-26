package assembler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author andrewtaylor
 */
public class StringConstant {
    
    private String value = "";
        
    public StringConstant() {}
    
    public StringConstant(String expression) {    
        boolean openDoubleQuote = false, openSingleQuote = false;
        boolean precedingEscapeCharacter = false;
        int begin = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);       
            if (c == '\\' && !precedingEscapeCharacter) {
                precedingEscapeCharacter = true;
                continue;
            }
            if (!openDoubleQuote && !precedingEscapeCharacter && c == '"')
                openDoubleQuote = true;
            else if (openDoubleQuote && !precedingEscapeCharacter && c == '"') 
                openDoubleQuote = false;
            else if (!openSingleQuote && !precedingEscapeCharacter && c == '\'')
                openDoubleQuote = true;
            else if (openSingleQuote && !precedingEscapeCharacter && c == '\'') 
                openDoubleQuote = false;
            else if (!openDoubleQuote && !openSingleQuote && c == ',') {
                String s = expression.substring(begin, i);
                if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
                    s = s.substring(1, s.length()-1);
                    value += s;
                }
                else {
                    int num = Integer.parseInt(s);
                    char ch = (char) num;
                    value += ch;
                }
                begin = i+1;
            }
            if (i == expression.length() - 1) {
                String s = expression.substring(begin, i+1);
                if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
                    s = s.substring(1, s.length()-1);
                    value += s;
                }
                else {
                    int num = Integer.parseInt(s);
                    char ch = (char) num;
                    value += ch;
                }
            }
            precedingEscapeCharacter = false;
        }
    }
    
    public static boolean isStringConstant(String expression) {
        // One or more characters enclosed in double quotes, an optional comma, optional characters
        // One or more characters enclosed in single quotes, an optional comma, optional characters
        // One or more digits, a comma, one or more digits, optional characters
        Pattern p = Pattern.compile("\".+\",?.*|'.+',?.*|\\d+,\\d+.*");
        Matcher m = p.matcher(expression);
        return m.matches();
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
