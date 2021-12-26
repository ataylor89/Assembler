package assembler;
// https://opensource.apple.com/source/xnu/xnu-4903.221.2/EXTERNAL_HEADERS/mach-o/loader.h.auto.html
// https://opensource.apple.com/source/xnu/xnu-4570.71.2/EXTERNAL_HEADERS/mach-o/nlist.h.auto.html
public class ObjectFile {

    private byte[] header;
    private byte[] lcSegment64;
    private byte[] section64Text;
    private byte[] section64Data;
    private byte[] lcSymtab;
    private byte[] dataSection;
    private byte[] textSection;
    private byte[] symTable;
    private byte[] stringTable;
    
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

    /**
     * @return the stringTable
     */
    public byte[] getStringTable() {
        return stringTable;
    }

    /**
     * @param stringTable the stringTable to set
     */
    public void setStringTable(byte[] stringTable) {
        this.stringTable = stringTable;
    }
    
    public byte[] getBytes() {
        ByteArray file = new ByteArray(1000);
        file.addBytes(header);
        file.addBytes(lcSegment64);
        file.addBytes(section64Text);
        file.addBytes(section64Data);
        file.addBytes(lcSymtab);
        file.addBytes(textSection);
        pad8(file);
        file.addBytes(dataSection);
        pad8(file);
        file.addBytes(symTable);
        file.addBytes(stringTable);
        return file.getBytes();
    }
    
    public void pad8(ByteArray file) {
        byte[] padding = new byte[8 - (file.getIndex() % 8)];
        for (int i = 0; i < padding.length; i++)
            padding[i] = 0;
        file.addBytes(padding);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
            .append(String.format("Macho64 header: (%d bytes)\n", header.length))
            .append(Bytes.hexstring(header))
            .append("\n")
            .append(String.format("LC_SEGMENT_64: (%d bytes)\n", lcSegment64.length))
            .append(Bytes.hexstring(lcSegment64))
            .append("\n")
            .append(String.format("SECTION_64_TEXT: (%d bytes)\n", section64Text.length))
            .append(Bytes.hexstring(section64Text))
            .append("\n")
            .append(String.format("SECTION_64_DATA: (%d bytes)\n", section64Data.length))
            .append(Bytes.hexstring(section64Data))
            .append("\n")
            .append(String.format("LC_SYMTAB: (%d bytes)\n", lcSymtab.length))
            .append(Bytes.hexstring(lcSymtab))
            .append("\n")
            .append(String.format("Text section: (%d bytes)\n", textSection.length))
            .append(Bytes.hexstring(textSection))
            .append("\n")
            .append(String.format("Data section: (%d bytes)\n", dataSection.length))
            .append(Bytes.hexstring(dataSection))
            .append("\n")
            .append(String.format("Symbol table: (%d bytes)\n", symTable.length))
            .append(Bytes.hexstring(symTable))
            .append("\n")
            .append(String.format("String table: (%d bytes)\n", stringTable.length))
            .append(Bytes.hexstring(stringTable))
            .append("\n");
        return sb.toString();
        // return Bytes.hexstring(getBytes());
    }
}
