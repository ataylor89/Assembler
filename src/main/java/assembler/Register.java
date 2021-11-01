package assembler;

/**
 *
 * @author andrewtaylor
 */
public enum Register {
    RAX,
    EAX,
    RDI,
    EDI,
    RSI,
    ESI,
    RDX,
    EDX;
   
    public static boolean isRegister(String expression) {
        return parse(expression) != null;
    }
    
    public static Register parse(String expression) {
        if (expression.endsWith(","))
            expression = expression.substring(0, expression.length()-1);
        try {
            return Register.valueOf(expression.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
