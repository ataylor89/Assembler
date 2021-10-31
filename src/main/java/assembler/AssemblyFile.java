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
    private SymbolTable symbolTable;

    public AssemblyFile() {}
    
    public AssemblyFile(File file) {
        this.file = file;
    }
    
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setTextSection(String textSection) {
        this.textSection = textSection;
    }

    public String getTextSection() {
        return textSection;
    }

    public void setDataSection(String dataSection) {
        this.dataSection = dataSection;
    }

    public String getDatatSection() {
        return dataSection;
    }

    public void setBssSection(String bssSection) {
        this.bssSection = bssSection;
    }

    public String getBssSection() {
        return bssSection;
    }

    public void setGlobals(String[] globals) {
        this.globals = globals;
    }

    public String[] getGlobals() {
        return globals;
    }

    public void setExterns(String[] externs) {
        this.externs = externs;
    }

    public String[] getExterns() {
        return externs;
    }

    public void setInstructions(Instruction[] instructions) {
        this.instructions = instructions;
    }

    public Instruction[] getInstructions() {
        return instructions;
    }

    public void setDataDirectives(DataDirective[] dataDirectives) {
        this.dataDirectives = dataDirectives;
    }

    public DataDirective[] getDataDirectives() {
        return dataDirectives;
    }

    public void setBssDirectives(BssDirective[] bssDirectives) {
        this.bssDirectives = bssDirectives;
    }

    public BssDirective[] getBssDirectives() {
        return bssDirectives;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
    
    public int getSectionCount() {
        int num = 0;
        if (textSection != null) 
            num++;
        if (dataSection != null) 
            num++;
        if (bssSection != null) 
            num++;
        return num;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Assembly file\n");
        sb.append("Globals: " + Stream.of(globals).collect(Collectors.joining(" ")) + "\n");
        sb.append("Externs: " + Stream.of(externs).collect(Collectors.joining(" ")) + "\n");
        if (textSection != null)
            sb.append("Text section\n" + textSection);
        if (dataSection != null) 
            sb.append("\nData section\n" + dataSection);
        if (bssSection != null) 
            sb.append("\nBSS section\n" + bssSection);
        if (symbolTable != null)
            sb.append("\n" + symbolTable);
        return sb.toString();
    }
}
