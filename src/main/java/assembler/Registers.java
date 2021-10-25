package assembler;
import java.util.Map;
import java.util.HashMap;
public class Registers {
    private static final Map<String, Register> registers;
    
    static {
        registers = new HashMap<>();
        registers.put("rax", Register.RAX);
        registers.put("eax", Register.EAX);
        registers.put("rdi", Register.RDI);
        registers.put("edi", Register.EDI);
        registers.put("rsi", Register.RSI);
        registers.put("esi", Register.ESI);
        registers.put("rdx", Register.RDX);
        registers.put("edx", Register.EDX);
    }

    public static Map<String, Register> map() {
        return registers;
    }
}