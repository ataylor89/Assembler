package assembler;

import java.io.File;
import java.util.stream.Stream;
import java.util.stream.Collectors;

/**
 *
 * @author andrewtaylor
 */
public class AssemblyFile {

    private File file;
    private String code;
    private String textSection;
    private String dataSection;
    private String bssSection;
    private String[] globals;
    private String[] externs;
    private Instruction[] instructions;
    private DataDirective[] dataDirectives;
    private BssDirective[] bssDirectives;
    private String[] symbols;

    public AssemblyFile(File file) {
        this.file = file;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }
    
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the textSection
     */
    public String getTextSection() {
        return textSection;
    }

    /**
     * @param textSection the textSection to set
     */
    public void setTextSection(String textSection) {
        this.textSection = textSection;
    }

    /**
     * @return the dataSection
     */
    public String getDataSection() {
        return dataSection;
    }

    /**
     * @param dataSection the dataSection to set
     */
    public void setDataSection(String dataSection) {
        this.dataSection = dataSection;
    }

    /**
     * @return the bssSection
     */
    public String getBssSection() {
        return bssSection;
    }

    /**
     * @param bssSection the bssSection to set
     */
    public void setBssSection(String bssSection) {
        this.bssSection = bssSection;
    }

    /**
     * @return the globals
     */
    public String[] getGlobals() {
        return globals;
    }

    /**
     * @param globals the globals to set
     */
    public void setGlobals(String[] globals) {
        this.globals = globals;
    }

    /**
     * @return the externs
     */
    public String[] getExterns() {
        return externs;
    }

    /**
     * @param externs the externs to set
     */
    public void setExterns(String[] externs) {
        this.externs = externs;
    }

    /**
     * @return the instructions
     */
    public Instruction[] getInstructions() {
        return instructions;
    }

    /**
     * @param instructions the instructions to set
     */
    public void setInstructions(Instruction[] instructions) {
        this.instructions = instructions;
    }

    /**
     * @return the dataDirectives
     */
    public DataDirective[] getDataDirectives() {
        return dataDirectives;
    }

    /**
     * @param dataDirectives the dataDirectives to set
     */
    public void setDataDirectives(DataDirective[] dataDirectives) {
        this.dataDirectives = dataDirectives;
    }

    /**
     * @return the bssDirectives
     */
    public BssDirective[] getBssDirectives() {
        return bssDirectives;
    }

    /**
     * @param bssDirectives the bssDirectives to set
     */
    public void setBssDirectives(BssDirective[] bssDirectives) {
        this.bssDirectives = bssDirectives;
    }

    /**
     * @return the symbols
     */
    public String[] getSymbols() {
        return symbols;
    }

    /**
     * @param symbols the symbols to set
     */
    public void setSymbols(String[] symbols) {
        this.symbols = symbols;
    }
    
    public int getSectionCount() {
        int num = 0;
        if (getTextSection() != null) 
            num++;
        if (getDataSection() != null) 
            num++;
        if (getBssSection() != null) 
            num++;
        return num;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Assembly file\n");
        sb.append("Globals: " + Stream.of(getGlobals()).collect(Collectors.joining(" ")) + "\n");
        sb.append("Externs: " + Stream.of(getExterns()).collect(Collectors.joining(" ")) + "\n");
        sb.append("Text section\n" + getTextSection() + "\n");
        if (getDataSection() != null) 
            sb.append("Data section\n" + getDataSection() + "\n");
        if (getBssSection() != null) 
            sb.append("BSS section\n" + getBssSection() + "\n");
        if (getSymbols() != null && getSymbols().length > 0)
            sb.append("\nSymbols\n" + Stream.of(getSymbols()).map(s->s.toString()).collect(Collectors.joining("\n")));
        return sb.toString();
    }
}
