package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Opcodes {
    
    public static final byte[][] SIB8;
    public static final byte[][] SIB16;
    public static final byte[][] SIB32;
    public static final byte[][] SIB64;
    
    static {
        SIB8 = new byte[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                SIB8[i][j] = (byte) (j + 8 * i);
            }
        }
        SIB16 = new byte[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                SIB16[i][j] = (byte) (SIB8[7][7] + 1 + j + 8 * i);
            }
        }
        SIB32 = new byte[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                SIB32[i][j] = (byte) (SIB16[7][7] + 1 + j + 8 * i);
            }
        }
        SIB64 = new byte[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                SIB64[i][j] = (byte) (SIB32[7][7] + 1 + j + 8 * i);
            }
        }
    }
    
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
            default: 
                return null;
        }
    }
    
    public static byte[] getCode(Opcode opcode, Register reg1, Register reg2) {
        switch (opcode) {
            case XOR:
                return getXorCode(reg1, reg2);
            default:
                return null;
        }
    }
    
    public static byte[] getMovCode(Register reg) {
        switch (reg.size) {
            case 64:
                return new byte[] {(byte) 0x48, (byte) (0xb8 + reg.index)};
            case 32:
                return new byte[] {(byte) (0xb8 + reg.index)};
            default:
                return null;
        }
            
        /*switch (reg) {
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
        }*/
    }
    
    public static byte[] getXorCode(Register reg1, Register reg2) {
        ByteArray code = new ByteArray();
        switch (reg1.size) {
            case 64:
                code.addByte((byte) 0x48);
                code.addByte((byte) 0x31);
                code.addByte(SIB64[reg1.index][reg2.index]);
                break;
            case 32:
                code.addByte((byte) 0x31);
                code.addByte(SIB32[reg1.index][reg2.index]);
                break;                
        }
        return code.getBytes();
  
        /*switch () {
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
        }*/
    }
}
