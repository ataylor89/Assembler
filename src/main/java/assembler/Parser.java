package assembler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
/**
 *
 * @author andrewtaylor
 */
public class Parser {
    
    private AssemblyFile assemblyFile;
    private String code;
    
    public Parser(File file) {
        assemblyFile = new AssemblyFile(file);
        code = readFile(file);
        assemblyFile.setCode(code);
    }
    
    public AssemblyFile parse() {
        assemblyFile.setTextSection(parseTextSection());
        assemblyFile.setDataSection(parseDataSection());
        assemblyFile.setBssSection(parseBssSection());
        assemblyFile.setGlobals(parseGlobals());
        assemblyFile.setExterns(parseExterns());
        parseSymbols();
        assemblyFile.setInstructions(parseInstructions());
        assemblyFile.setDataDirectives(parseDataDirectives());
        assemblyFile.setBssDirectives(parseBssDirectives());
        return assemblyFile;
    }
    
    private String parseTextSection() {
        int start = code.indexOf("section .text");
        int end = code.indexOf("section", start+13);
        if (end < 0)
            end = code.length();
        if (start > 0 && end > start)
            return code.substring(start, end).trim();
        return null;
    }
    
    private String parseDataSection() {
        int start = code.indexOf("section .data");
        int end = code.indexOf("section", start+13);
        if (end < 0)
            end = code.length();
        if (start > 0 && end > start)
            return code.substring(start, end).trim();
        return null;
    }
    
    private String parseBssSection() {
        int start = code.indexOf("section .bss");
        int end = code.indexOf("section", start+12);
        if (end < 0)
            end = code.length();
        if (start > 0 && end > start)
            return code.substring(start, end).trim();
        return null;
    }
    
    private String[] parseGlobals() {
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
    
    private String[] parseExterns() {
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
    
    private Instruction[] parseInstructions() {
        String textSection = assemblyFile.getTextSection();
        if (textSection == null)
            return null;
        String[] lines = textSection.split("\n");
        List<Instruction> instructions = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().endsWith(":") && i < lines.length-1) {
                Instruction instruction = new Instruction(lines[i].trim() + " " + lines[++i].trim());
                instructions.add(instruction);
            }
            else {
                Instruction instruction = new Instruction(lines[i].trim());
                instructions.add(instruction);
            }
        }
        Instruction[] arr = new Instruction[instructions.size()];
        return instructions.toArray(arr);
    }
    
    private DataDirective[] parseDataDirectives() {
        String dataSection = assemblyFile.getDataSection();
        if (dataSection == null)
            return null;
        String[] lines = dataSection.split("\n");
        List<DataDirective> directives = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            DataDirective directive = new DataDirective(lines[i].trim());
            String label = directive.getLabel();
            Pseudoopcode opcode = Pseudoopcode.parse(directive.getOpcode());
            String operand = directive.getOperand();
            Symbol symbol = Symbols.map.get(label);
            switch (opcode) {
                case DB:
                    String str = (String) Expressions.eval(operand);
                    symbol.setValue(Symbols.offset);
                    symbol.setSize(str.length());
                    symbol.setType('d');
                    symbol.setSect(2);
                    Symbols.offset += str.length();
                    break;
                case EQU:
                    Integer num = (Integer) Expressions.eval(operand);
                    symbol.setValue((long) num);
                    symbol.setType('a');
                    break;
            }
            directives.add(directive);       
        }
        DataDirective[] arr = new DataDirective[directives.size()];
        return directives.toArray(arr);
    }
    
    private BssDirective[] parseBssDirectives() {
        String bssSection = assemblyFile.getBssSection();
        if (bssSection == null)
            return null;
        String[] lines = bssSection.split("\n");
        List<BssDirective> directives = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            BssDirective directive = new BssDirective();
            directives.add(directive);
        }
        BssDirective[] arr = new BssDirective[directives.size()];
        return directives.toArray(arr);
    }
    
    private void parseSymbols() {
        int strx = 1;
        for (String extern : assemblyFile.getExterns()) {
            Symbol symbol = new Symbol();
            String name = extern.split("\\s+")[1].trim();
            symbol.setName(name);
            symbol.setIndex(strx);
            strx += name.length() + 1;
            symbol.setType('e');
            Symbols.list.add(symbol);
            Symbols.map.put(name, symbol);
        }
        for (String global : assemblyFile.getGlobals()) {
            Symbol symbol = new Symbol();
            String name = global.split("\\s+")[1].trim();
            symbol.setName(name);
            symbol.setIndex(strx);
            strx += name.length() + 1;
            symbol.setType('g');
            Symbols.list.add(symbol);
            Symbols.map.put(name, symbol);
        }
        String[] lines = assemblyFile.getTextSection().split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] tokens = lines[i].split("\\s+", 4);
            if (tokens[0].endsWith(":")) {
                String label = tokens[0].substring(0, tokens[0].length()-1).trim();
                if (!Symbols.map.containsKey(label)) {
                    Symbol symbol = new Symbol();
                    symbol.setName(label);
                    symbol.setIndex(strx);
                    strx += label.length() + 1;
                    symbol.setType('t');
                    Symbols.list.add(symbol);
                    Symbols.map.put(label, symbol);
                }
            }
        }
        lines = assemblyFile.getDataSection().split("\n");
        for (int i = 0; i < lines.length; i++) {
            String[] tokens = lines[i].split("\\s+", 4);
            if (tokens[0].endsWith(":")) {
                String label = tokens[0].substring(0, tokens[0].length()-1).trim();
                if (!Symbols.map.containsKey(label)) {
                    Symbol symbol = new Symbol();
                    symbol.setName(label);
                    symbol.setIndex(strx);
                    strx += label.length() + 1;
                    if (tokens[1].equals("db"))
                        symbol.setType('d');
                    else if (tokens[1].equals("equ"))
                        symbol.setType('a');
                    Symbols.list.add(symbol);
                    Symbols.map.put(label, symbol);
                }
            }
        }
    }
            
    public String readFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }
    
    public static void main(String[] args) {
        String path = System.getProperty("user.home") + "/nasm/simple.asm";
        Parser parser = new Parser(new File(path));
        AssemblyFile af = parser.parse();
        System.out.println(af);
    }
}
