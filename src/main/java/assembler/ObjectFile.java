package assembler;
public class ObjectFile {

    private byte[] header;
    private byte[] lcSegment64;
    private byte[] section64Text;
    private byte[] section64Data;
    private byte[] lcSymtab;
    private byte[] dataSection;
    private byte[] textSection;
    private byte[] symTable;
    
    /**
     * @return the header
     */
    public byte[] getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(byte[] header) {
        this.header = header;
    }

    /**
     * @return the lcSegment64
     */
    public byte[] getLcSegment64() {
        return lcSegment64;
    }

    /**
     * @param lcSegment64 the lcSegment64 to set
     */
    public void setLcSegment64(byte[] lcSegment64) {
        this.lcSegment64 = lcSegment64;
    }

    /**
     * @return the section64Text
     */
    public byte[] getSection64Text() {
        return section64Text;
    }

    /**
     * @param section64Text the section64Text to set
     */
    public void setSection64Text(byte[] section64Text) {
        this.section64Text = section64Text;
    }

    /**
     * @return the section64Data
     */
    public byte[] getSection64Data() {
        return section64Data;
    }

    /**
     * @param section64Data the section64Data to set
     */
    public void setSection64Data(byte[] section64Data) {
        this.section64Data = section64Data;
    }

    /**
     * @return the lcSymtab
     */
    public byte[] getLcSymtab() {
        return lcSymtab;
    }

    /**
     * @param lcSymtab the lcSymtab to set
     */
    public void setLcSymtab(byte[] lcSymtab) {
        this.lcSymtab = lcSymtab;
    }

    /**
     * @return the dataSection
     */
    public byte[] getDataSection() {
        return dataSection;
    }

    /**
     * @param dataSection the dataSection to set
     */
    public void setDataSection(byte[] dataSection) {
        this.dataSection = dataSection;
    }

    /**
     * @return the textSection
     */
    public byte[] getTextSection() {
        return textSection;
    }

    /**
     * @param textSection the textSection to set
     */
    public void setTextSection(byte[] textSection) {
        this.textSection = textSection;
    }

    /**
     * @return the symTable
     */
    public byte[] getSymTable() {
        return symTable;
    }

    /**
     * @param symTable the symTable to set
     */
    public void setSymTable(byte[] symTable) {
        this.symTable = symTable;
    }
    
    public byte[] getBytes() {
        ByteArray file = new ByteArray(1000);
        file.addBytes(header);
        file.addBytes(lcSegment64);
        file.addBytes(section64Text);
        file.addBytes(section64Data);
        file.addBytes(lcSymtab);
        file.addBytes(textSection);
        int n = 8 - (file.getIndex() % 8);
        file.addBytes(Bytes.nbytes(0, n));
        file.addBytes(dataSection);
        n = 8 - (file.getIndex() % 8);
        file.addBytes(Bytes.nbytes(0, n));
        file.addBytes(symTable);
        return file.getBytes();
    }
    
    @Override
    public String toString() {
        return Bytes.hexstring(getBytes());
    }
}
