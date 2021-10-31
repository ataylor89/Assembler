package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Symbol {
    private String name;
    private int strx;
    private byte type;
    private byte sect;
    private short desc;
    private long value;
    private long size;

    public Symbol() {}
    
    public Symbol(String name) {
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
    public long getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(long value) {
        this.value = value;
    }

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
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }
}
