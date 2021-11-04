package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Expressions {
        
    public static Object eval(String expression) {
        if (expression == null)
            return null;
        else if (Symbols.isSymbol(expression)) {
            Symbol symbol = Symbols.map.get(expression);
            if (symbol.getType() == SymbolType.ABSOLUTE)
                return (int) symbol.getValue();
            else
                return symbol.getValue();
        }
        else if (Register.isRegister(expression))
            return Register.parse(expression);
        else if (StringConstant.isStringConstant(expression))
            return new StringConstant(expression).getValue();
        else if (expression.startsWith("$-")) 
            return Symbols.map.get(expression.substring(2, expression.length())).getSize();
        else {
            try {
                Long l = Long.decode(expression);
                if (l > Integer.MAX_VALUE)
                    return l;
                else
                    return Integer.decode(expression);
            } catch (NumberFormatException e) {
                System.err.println(e);
                return null;
            }
        }
    }
}
