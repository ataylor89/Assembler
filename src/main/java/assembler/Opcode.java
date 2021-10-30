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
            Opcode opcode = Opcode.valueOf(text.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static Opcode parse(String text) {
        try {
            Opcode opcode = Opcode.valueOf(text.toUpperCase());
            return opcode;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
