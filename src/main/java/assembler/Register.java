package assembler;

/**
 *
 * @author andrewtaylor
 */
public enum Register {
    RAX(64, 0),
    EAX(32, 0),
    RCX(64, 1),
    ECX(32, 1),
    RDX(64, 2),
    EDX(32, 2),
    RBX(64, 3),
    EBX(32, 3),
    RSP(64, 4),
    RBP(64, 5),
    RSI(64, 6),
    ESI(32, 6),
    RDI(64, 7),
    EDI(32, 7);
    
    public final int size;
    public final int index;
    
    Register(int size, int index) {
        this.size = size;
        this.index = index;
    }
   
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
