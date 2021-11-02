package assembler;

import java.util.stream.Stream;
import java.util.stream.Collectors;

public class AssemblyFile {

    private String code;
    private String textSection;
    private String dataSection;
    private String bssSection;
    private String[] globals;
    private String[] externs;
    private String[] instructions;
    private String[] dataDirectives;
    private String[] bssDirectives;

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

    public String getDataSection() {
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

    public void setInstructions(String[] instructions) {
        this.instructions = instructions;
    }

    public String[] getInstructions() {
        return instructions;
    }

    public void setDataDirectives(String[] dataDirectives) {
        this.dataDirectives = dataDirectives;
    }

    public String[] getDataDirectives() {
        return dataDirectives;
    }

    public void setBssDirectives(String[] bssDirectives) {
        this.bssDirectives = bssDirectives;
    }

    public String[] getBssDirectives() {
        return bssDirectives;
    }

    public int getSectionCount() {
        int num = 0;
        if (textSection != null) {
            num++;
        }
        if (dataSection != null) {
            num++;
        }
        if (bssSection != null) {
            num++;
        }
        return num;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Assembly file\n");
        sb.append("Globals: " + Stream.of(globals).collect(Collectors.joining(" ")) + "\n");
        sb.append("Externs: " + Stream.of(externs).collect(Collectors.joining(" ")) + "\n");
        sb.append("Text section\n" + textSection + "\n");
        if (dataSection != null) {
            sb.append("Data section\n" + dataSection + "\n");
        }
        if (bssSection != null) {
            sb.append("BSS section\n" + bssSection + "\n");
        }
        sb.append("Instructions\n" + Stream.of(instructions).collect(Collectors.joining("\n")));
        if (dataDirectives != null && dataDirectives.length > 0) {
            sb.append("Data directives\n" + Stream.of(dataDirectives).collect(Collectors.joining("\n")));
        }
        if (bssDirectives != null && bssDirectives.length > 0) {
            sb.append("BSS directives\n" + Stream.of(bssDirectives).collect(Collectors.joining("\n")));
        }
        return sb.toString();
    }
}
