package assembler;
import java.util.Arrays;
public class ByteArray {
    
    private byte[] bytes;
    private int capacity;
    private int index;

    public static final int WORD = 2;
    public static final int DWORD = 4;
    public static final int QWORD = 8;
    public static final int OWORD = 16;

    public ByteArray() {
        this(100);  
    }
    
    public ByteArray(int capacity) {
        this.capacity = capacity;
        bytes = new byte[capacity];
        index = 0;
    }

    public void resize(int capacity) {
        this.capacity = capacity;
        byte[] b = new byte[capacity];
        for (int i = 0; i < bytes.length; i++)
            b[i] = bytes[i];
        bytes = b;
    }

    public void addByte(byte b) {
        if (index + 1 > bytes.length)
            resize(2*capacity);
        bytes[index] = b;
        index++;
    }

    public void addBytes(byte[] b) {
        if (index + b.length > bytes.length)
            resize(2*capacity);
        System.arraycopy(b, 0, bytes, index, b.length);
        index += b.length;
    }

    public void addWord(byte b) {
        byte[] arr = new byte[WORD];
        arr[0] = b;
        Arrays.fill(arr, 1, arr.length, (byte) 0);
        addBytes(arr);
    }
    
    public void addWord(int i) {
        byte[] arr = new byte[WORD];
        arr[0] = (byte) (i & 0xFF00);
        arr[1] = (byte) (i & 0x00FF);
        addBytes(arr);
    } 
           
    public void addDWord(byte b) {
        byte[] arr = new byte[DWORD];
        arr[0] = b;
        Arrays.fill(arr, 1, arr.length, (byte) 0);
        addBytes(arr);
    }
    
    public void addDWord(int i) {
        addDWord(i, Endian.LITTLE);
    }

    public void addDWord(int i, Endian e) {
        byte[] arr = Bytes.bytes(i, e);
        addBytes(arr);
    }

    public void addQWord(byte b) {
        byte[] arr = new byte[QWORD];
        arr[0] = b;
        Arrays.fill(arr, 1, arr.length, (byte) 0);
        addBytes(arr);
    }

    public void addQWord(int i) {
        byte[] arr = Bytes.littleendian((long) i);
        addBytes(arr);
    }

    public void addQWord(long l) {
        byte[] arr = Bytes.littleendian(l);
        addBytes(arr);
    }

    public void addQWord(long l, Endian e) {
        byte[] arr = Bytes.bytes(l, e);
        addBytes(arr);
    }

    public void addOWord(byte b) {
        byte[] arr = new byte[OWORD];
        arr[0] = b;
        Arrays.fill(arr, 1, arr.length, (byte) 0);
        addBytes(arr);
    }

    public void addOWord(char[] c) {
        byte[] arr = new byte[OWORD];
        for (int i = 0; i < c.length; i++)
            arr[i] = (byte) c[i];
        Arrays.fill(arr, c.length, arr.length, (byte) 0);
        addBytes(arr);
    }

    public void addOWord(String s) {
        byte[] arr = new byte[OWORD];
        byte[] arrs = s.getBytes();
        for (int i = 0; i < arrs.length; i++)
            arr[i] = arrs[i];
        Arrays.fill(arr, arrs.length, arr.length, (byte) 0);
        addBytes(arr);
    }

    public byte[] getBytes() {
        byte[] arr = new byte[index];
        System.arraycopy(bytes, 0, arr, 0, index);
        return arr;
    }

    public byte[] getBytes(int start, int end) {
        byte[] arr = new byte[end-start];
        System.arraycopy(bytes, start, arr, 0, end-start);
        return arr;
    }

    public int getCapacity() {
        return capacity;
    }
    
    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return new String(getBytes());
    }
}

