package assembler;

/**
 *
 * @author andrewtaylor
 */
public enum Opcode {
    
    MOV,
    AND,
    OR,
    XOR,
    SYSCALL;
    
    public static boolean isOpcode(String text) {
        try {
            Opcode.valueOf(text.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static Opcode parse(String text) {
        try {
            return Opcode.valueOf(text.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
