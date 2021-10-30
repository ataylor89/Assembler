package assembler;

/**
 *
 * @author andrewtaylor
 */
public enum Pseudoopcode {
    
    DB,
    DW,
    DD,
    DQ,
    EQU,
    RESB,
    RESW,
    RESD,
    RESQ;
    
    public static boolean isOpcode(String opcode) {
        return parse(opcode) != null;
    }
    
    public static Pseudoopcode parse(String opcode) {
        try {
            return Pseudoopcode.valueOf(opcode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }     
    }
}
