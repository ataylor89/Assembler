package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Opcodes {
    public static byte[] getCode(Opcode opcode) {
        switch (opcode) {
            case SYSCALL:
                return new byte[] {(byte) 0x0f, (byte) 0x05};
            default:
                return null;
        }
    }
    
    public static byte[] getCode(Opcode opcode, Register register) {
        switch (opcode) {
            case MOV:
                return getMovCode(register);
            case XOR:
                return getXorCode(register);
            default: 
                return null;
        }
    }
    
    public static byte[] getMovCode(Register register) {
        switch (register) {
            case RAX:
                return new byte[] {(byte) 0x48, (byte) 0xb8};
            case EAX:
                return new byte[] {(byte) 0xb8};
            case RDX:
                return new byte[] {(byte) 0x48, (byte) 0xba};
            case EDX:
                return new byte[] {(byte) 0xba};
            case RSI:
                return new byte[] {(byte) 0x48, (byte) 0xbe};
            case ESI:
                return new byte[] {(byte) 0xbe};
            case RDI:
                return new byte[] {(byte) 0x48, (byte) 0xbf};
            case EDI:
                return new byte[] {(byte) 0xbf};
            default:
                return null;
        }
    }
    
    public static byte[] getXorCode(Register register) {
        switch (register) {
            case RAX:
                return new byte[] {(byte) 0x48, (byte) 0x31, (byte) 0xc0};
            case RDI:    
                return new byte[] {(byte) 0x48, (byte) 0x31, (byte) 0xff};
            case RSI:
                return new byte[] {(byte) 0x48, (byte) 0x31, (byte) 0xf6};
            case RDX:
               return new byte[] {(byte) 0x48, (byte) 0x31, (byte) 0xd2};
            default:
                return null;
        }
    }
}
