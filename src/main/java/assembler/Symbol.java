package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Symbol implements Comparable {
    private String name;
    private int strx;
    private byte type;
    private byte sect;
    private short desc;
    private int value;
    private int size;

    public Symbol() {}

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the strx
     */
    public int getStrx() {
        return strx;
    }

    /**
     * @param strx the strx to set
     */
    public void setStrx(int strx) {
        this.strx = strx;
    }

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * @return the sect
     */
    public byte getSect() {
        return sect;
    }

    /**
     * @param sect the sect to set
     */
    public void setSect(byte sect) {
        this.sect = sect;
    }

    /**
     * @return the desc
     */
    public short getDesc() {
        return desc;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(short desc) {
        this.desc = desc;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Symbol) {
            Symbol s2 = (Symbol) o;
        
            if (type != 0x0f && s2.getType() == 0x0f)
                return -1;
            if (type == 0x0f && s2.getType() != 0x0f)
                return 1;

            Integer index1 = this.getStrx();
            Integer index2 = s2.getStrx();
            return index1.compareTo(index2);
        }
        return this.compareTo(o);
    }
        
}
