package assembler;

/**
 *
 * @author andrewtaylor
 */
public enum Register {
    
    RAX(64),
    EAX(32),
    RDI(64),
    EDI(32),
    RSI(64),
    ESI(32),
    RDX(64),
    EDX(32);
    
    private final int size;
    
    Register(int size) {
        this.size = size;
    }
    
    public int size() {
        return size;
    }
    
    public static boolean isRegister(String text) {
        return parse(text) != null;
    }
    
    public static Register parse(String text) {
        if (text.endsWith(","))
            text = text.substring(0, text.length()-1);
        try {
            return Register.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
