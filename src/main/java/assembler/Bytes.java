package assembler;

public class Bytes {

    public static byte[] littleendian(long val) {
        byte[] bytes = new byte[8];
        long bitmask = 0xFF;
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

    public static byte[] littleendian(long val, int size) {
        byte[] bytes = new byte[size];
        long bitmask = 0xFF;
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) ((val & bitmask) >> 8 * i);
            bitmask <<= 8;
        }
        return bytes;
    }

    public static byte[] bytes(int val, Endian e) {
        switch (e) {
            case BIG:
                return bigendian(val);
            case LITTLE:
                return littleendian(val);
            default:
                return null;
        }
    }

    public static byte[] bytes(long val, Endian e) {
        switch (e) {
            case BIG:
                return bigendian(val);
            case LITTLE:
                return littleendian(val);
            default:
                return null;
        }
    }

    public static int size(long val) {
        long mask = 0xFF << 8 * 7;
        for (int i = 7; i > 0; i++) {
            if ((val & mask) != 0) {
                return i + 1;
            }
            mask >>= 8;
        }
        return 1;
    }

    public static int size(int val) {
        int mask = 0xFF << 8 * 3;
        for (int i = 3; i > 0; i++) {
            if ((val & mask) != 0) {
                return i + 1;
            }
            mask >>= 8;
        }
        return 1;
    }

    public static String hexstring(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i]));
            if (i < bytes.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

}
