package assembler;

public class Bytes {

    public static byte[] littleendian(short val) {
        byte[] bytes = new byte[2];
        int bitmask = 0xFF;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((val & bitmask) >> 8 * i);
            bitmask <<= 8;
        }
        return bytes;
    }
    
    public static byte[] littleendian(int val) {
        byte[] bytes = new byte[4];
        int bitmask = 0xFF;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((val & bitmask) >> 8 * i);
            bitmask <<= 8;
        }
        return bytes;
    }
    
    public static byte[] littleendian(long val) {
        byte[] bytes = new byte[8];
        long bitmask = 0xFF;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((val & bitmask) >> 8 * i);
            bitmask <<= 8;
        }
        return bytes;
    }

    public static byte[] bigendian(short val) {
        byte[] bytes = new byte[2];
        int shift = 8 * (bytes.length - 1);
        int bitmask = 0xFF << shift;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((val & bitmask) >> shift);
            shift -= 8;
            bitmask >>= 8;
        }
        return bytes;
    }

    public static byte[] bigendian(int val) {
        byte[] bytes = new byte[4];
        int shift = 8 * (bytes.length - 1);
        int bitmask = 0xFF << shift;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((val & bitmask) >> shift);
            shift -= 8;
            bitmask >>= 8;
        }
        return bytes;
    }

    public static byte[] bigendian(long val) {
        byte[] bytes = new byte[8];
        int shift = 8 * (bytes.length - 1);
        long bitmask = 0xFF << shift;
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((val & bitmask) >> shift);
            shift -= 8;
            bitmask >>= 8;
        }
        return bytes;
    }
    
    public static byte[] bytes(short val, Endian e) {
        return switch (e) {
            case BIG -> bigendian(val);
            case LITTLE -> littleendian(val);
            default -> null;
        };
    }
    
    public static byte[] bytes(int val, Endian e) {
        return switch (e) {
            case BIG -> bigendian(val);
            case LITTLE -> littleendian(val);
            default -> null;
        };
    }

    public static byte[] bytes(long val, Endian e) {
        return switch (e) {
            case BIG -> bigendian(val);
            case LITTLE -> littleendian(val);
            default -> null;
        };
    }

    public static byte[] nbytes(byte b, int n) {
        byte[] bytes = new byte[n];
        for (int i = 0; i < n; n++)
            bytes[i] = b;
        return bytes;
    }
    
    public static byte[] nbytes(int val, int n) {
        byte[] bytes = new byte[n];
        byte b = (byte) (val & 0xFF);
        for (int i = 0; i < n; n++)
            bytes[i] = b;
        return bytes;
    }
    
    public static String hexstring(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < bytes.length) {
            for (int j = 0; j < 16 && (i+j) < bytes.length; j++) {
                sb.append(String.format("%02x", bytes[i+j]));
                if (j % 2 == 1)
                    sb.append(" ");
            }
            sb.append("\n");
            i += 16;
        }
        return sb.toString();
    }
}
