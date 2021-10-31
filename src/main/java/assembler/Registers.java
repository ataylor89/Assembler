package assembler;
import java.util.Map;
import java.util.HashMap;
public class Registers {
    public static final Map<Register, Register> map32;
    
    static {
        map32 = new HashMap<>();
        map32.put(Register.RAX, Register.EAX);
        map32.put(Register.RDI, Register.EDI);
        map32.put(Register.RSI, Register.ESI);
        map32.put(Register.RDX, Register.EDX);
    }
}