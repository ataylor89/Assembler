package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Operand {
    
    private Object value;
    
    public Operand() {}
    
    public Operand(Opcode opcode, int index, String token, SymbolTable symbolTable) {
        switch (opcode) {
            
        }
    }
    
    public Operand(Pseudoopcode opcode, int index, String token, SymbolTable symbolTable) {
        switch (opcode) {
            case DB:
                
            case EQU:
        }
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return value;
    }
}
