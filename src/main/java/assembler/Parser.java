package assembler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
/**
 *
 * @author andrewtaylor
 */
public class Parser {
    
    public AssemblyFile parse(File file) {
        String code = readFile(file);
        return parse(code);
    }
    
    public AssemblyFile parse(String code) {
        AssemblyFile assemblyFile = new AssemblyFile();
        assemblyFile.setCode(code);
        assemblyFile.setTextSection(parseTextSection(code));
        assemblyFile.setDataSection(parseDataSection(code));
        assemblyFile.setBssSection(parseBssSection(code));
        assemblyFile.setGlobals(parseGlobals(code));
        assemblyFile.setExterns(parseExterns(code));
        assemblyFile.setInstructions(parseInstructions(code));
        assemblyFile.setDataDirectives(parseDataDirectives(code));
        assemblyFile.setBssDirectives(parseBssDirectives(code));
        assemblyFile.setSymbols(parseSymbols(code));
        return assemblyFile;
    }
    
    public String parseTextSection(String code) {
        int start = code.indexOf("section .text");
        int end = code.indexOf("section", start+13);
        if (end < 0)
            end = code.length();
        if (start > 0 && end > start)
            return code.substring(start, end).trim();
        return null;
    }
    
    public String parseDataSection(String code) {
        int start = code.indexOf("section .data");
        int end = code.indexOf("section", start+13);
        if (end < 0)
            end = code.length();
        if (start > 0 && end > start)
            return code.substring(start, end).trim();
        return null;
    }
    
    public String parseBssSection(String code) {
        int start = code.indexOf("section .bss");
        int end = code.indexOf("section", start+12);
        if (end < 0)
            end = code.length();
        if (start > 0 && end > start)
            return code.substring(start, end).trim();
        return null;
    }
    
    public String[] parseGlobals(String code) {
        List<String> globals = new ArrayList<>();
        int start = code.indexOf("global");
        int end = code.indexOf("\n", start);
        while (start > 0 && end > start) {
            String s = code.substring(start, end);
            if (!s.contains("\"") && !s.contains("'")) 
                globals.add(s);
            start = code.indexOf("global", end);
            end = code.indexOf("\n", start);
        }
        String[] arr = new String[globals.size()];
        return globals.toArray(arr);
    }
    
    public String[] parseExterns(String code) {
        List<String> externs = new ArrayList<>();
        int start = code.indexOf("extern");
        int end = code.indexOf("\n", start);
        while (start > 0 && end > start) {
            String s = code.substring(start, end);
            if (!s.contains("\"") && !s.contains("'"))
                externs.add(s);
            start = code.indexOf("extern", end);
            end = code.indexOf("\n", start);
        }
        String[] arr = new String[externs.size()];
        return externs.toArray(arr);
    }
    
    public String[] parseInstructions(String code) {
        String textSection = parseTextSection(code);
        if (textSection == null)
            return null;
        String[] lines = textSection.split("\n");
        List<String> instructions = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().endsWith(":") && i < lines.length-1)
                instructions.add(lines[i].trim() + " " + lines[++i].trim());
            else
                instructions.add(lines[i].trim());
        }
        String[] arr = new String[instructions.size()];
        return instructions.toArray(arr);
    }
    
    public String[] parseDataDirectives(String code) {
        String dataSection = parseDataSection(code);
        if (dataSection == null)
            return null;
        String[] lines = dataSection.split("\n");
        List<String> directives = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            directives.add(lines[i].trim());
        }
        String[] arr = new String[directives.size()];
        return directives.toArray(arr);
    }
    
    public String[] parseBssDirectives(String code) {
        String bssSection = parseBssSection(code);
        if (bssSection == null)
            return null;
        String[] lines = bssSection.split("\n");
        List<String> directives = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            directives.add(lines[i].trim());
        }
        String[] arr = new String[directives.size()];
        return directives.toArray(arr);
    }
    
    public String[] parseSymbols(String code) {
        Set<String> symbols = new LinkedHashSet<>();
        String[] lines = code.split("\n");
        String name = null;
        for (int i = 0; i < lines.length; i++) {
            String[] tokens = lines[i].split("\\s+");
            int numt = tokens.length;
            if (numt > 0) {
                if (tokens[0].equals("global"))
                    symbols.add(tokens[1]);
                else if (tokens[0].equals("extern"))
                    symbols.add(tokens[1]);
                else if (tokens[0].endsWith(":")) {
                    name = tokens[0];
                    name = name.substring(0, name.length()-1);
                    symbols.add(name);
                }
                else if (numt > 1 && (tokens[1].equals("db") || tokens[1].equals("equ"))) {
                    name = tokens[0];
                    if (name.endsWith(":"))
                        name = name.substring(0, name.length()-1);
                    symbols.add(tokens[0]);
                }
            }
        }
        String[] arr = new String[symbols.size()];
        return symbols.toArray(arr);
    }
    
    /*
    public SymbolTable parseSymbols(String code) {
        SymbolTable symTable = new SymbolTable();
        String[] directives = parseDataDirectives(code);
        for (int i = 0; i < directives.length; i++) {
            DataDirective directive = parseDataDirective(directives[i], symTable.getMap());
            String label = directive.getLabel();
            Object operand = directive.getOperand();
            if (label != null && operand != null) {
                symTable.getSequence().add(label);
                symTable.getMap().put(label, operand);
            }
        }
        return symTable;
    }*/
        
    public String readFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }
    
    public static void main(String[] args) {
        Parser parser = new Parser();
        String path = System.getProperty("user.home") + "/nasm/simple.asm";
        AssemblyFile af = parser.parse(new File(path));
        System.out.println(af);
    }
}
