package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Symbol {
    private String name;
    private int index;
    private int strx;
    private SymbolType type;
    private int section;
    private long value;
    private int size;

    public Symbol() {}
    
    public Symbol(String name) {
        this.name = name;
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
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
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
    public SymbolType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(SymbolType type) {
        this.type = type;
    }

    /**
     * @return the section
     */
    public int getSection() {
        return section;
    }

    /**
     * @param section the section to set
     */
    public void setSection(int section) {
        this.section = section;
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
}
