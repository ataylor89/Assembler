package assembler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author andrewtaylor
 */
public class Parser {
    
    public static final char SINGLE_QUOTE = '\'';
    public static final char DOUBLE_QUOTE = '"';
    public static final char ESCAPE_CHARACTER = '\\';
    public static final char COMMA = ',';
    
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
    }
    
    public Instruction parseInstruction(String instruction, Map<String, Object> symbols) {
        String[] tokens = instruction.split("\\s+", 4);
        String label = null;
        Opcode opcode = null;
        String operand1 = null;
        String operand2 = null;
        if (tokens[0].endsWith(":") && tokens.length == 4) {
            label = tokens[0].substring(0, tokens[0].length()-1);
            opcode = Opcode.valueOf(tokens[1].toUpperCase());
            operand1 = tokens[2];
            operand2 = tokens[3];
        }
        else if (tokens.length == 3) {
            opcode = Opcode.valueOf(tokens[0].toUpperCase());
            operand1 = tokens[1];
            operand2 = tokens[2];
        }
        else if (tokens.length == 2) {
            opcode = Opcode.valueOf(tokens[0].toUpperCase());
            operand1 = tokens[1];
        }
        else {
            opcode = Opcode.valueOf(tokens[0].toUpperCase());
        }
        Instruction obj = new Instruction();
        obj.setLabel(label);
        obj.setOpcode(opcode); 
        obj.setOperand1(parseInstructionOperand(operand1, symbols));
        obj.setOperand2(parseInstructionOperand(operand2, symbols));
        return obj;
    }
    
    public Object parseInstructionOperand(String operand, Map<String, Object> symbols) {
        if (operand == null)
            return null;        
        if (operand.endsWith(","))
            operand = operand.substring(0, operand.length()-1);
        if (symbols.containsKey(operand))
            return symbols.get(operand);
        else if (Registers.map().containsKey(operand))
            return Registers.map().get(operand);
        else {
            try {
                return Long.decode(operand);
            }
            catch (NumberFormatException e) {
                System.err.println(e);
                return null;
            }
        }
    }
    
    public DataDirective parseDataDirective(String directive, Map<String, Object> symbols) {
        String[] tokens = directive.split("\\s+", 3);
        Pseudoopcode opcode = Pseudoopcode.valueOf(tokens[1].toUpperCase());
        switch (opcode) {
            case DB:
                return parseDbDirective(directive, symbols);
            case EQU:
                return parseEquDirective(directive, symbols);
            default:
                return null;
        }
    }
    
    public DataDirective parseDbDirective(String directive, Map<String, Object> symbols) {        
        String[] tokens = directive.split("\\s+", 3);
        String label = tokens[0];
        if (label.endsWith(":"))
            label = label.substring(0, label.length()-1);
        Object operand = parseDbOperand(label, tokens[2], symbols);
        DataDirective dataDirective = new DataDirective();
        dataDirective.setLabel(label);
        dataDirective.setOpcode(Pseudoopcode.DB);
        dataDirective.setOperand(operand);
        return dataDirective;
    }   
    
    public Object parseDbOperand(String label, String operand, Map<String, Object> symbols) {
        if (symbols.containsKey(label))
            return symbols.get(label);
        String value = ""; 
        boolean openDoubleQuote = false;
        boolean openSingleQuote = false;
        int numQuotes = 0;
        boolean isQuoted = false;
        int j = 0;
        for (int i = 0; i < operand.length(); i++) {
            char b = (i > 0) ? operand.charAt(i-1) : '\0';
            char c = operand.charAt(i);
            if (c == DOUBLE_QUOTE && b != ESCAPE_CHARACTER && !openSingleQuote) {
                openDoubleQuote = !openDoubleQuote;
                numQuotes++;
            }   
            else if (c == SINGLE_QUOTE && b != ESCAPE_CHARACTER && !openDoubleQuote) {
                openSingleQuote = !openSingleQuote;
                numQuotes++;
            }   
            if (numQuotes == 2)
                isQuoted = true;
            if ((c == COMMA && !openDoubleQuote && !openSingleQuote) || (i == operand.length()-1)) {
                String s = operand.substring(j, i+1);
                if (s.isEmpty())
                    continue;
                else if (isQuoted) {
                    s = s.substring(1, s.length()-1);
                    value += s;
                }   
                else {
                    try {
                        value += Byte.decode(s).toString();
                    } catch (NumberFormatException e) {
                        System.err.println(e);
                    }   
                }   
                numQuotes = 0;
                isQuoted = false;
                j = i + 1;
            }   
        }   
        return value;
    }
    
    public DataDirective parseEquDirective(String directive, Map<String, Object> symbols) {
        String[] tokens = directive.split("\\s+", 3);
        String label = tokens[0];
        if (label.endsWith(":"))
            label = label.substring(0, label.length()-1);
        Object operand = parseEquOperand(label, tokens[2], symbols);
        DataDirective dataDirective = new DataDirective();
        dataDirective.setLabel(label);
        dataDirective.setOpcode(Pseudoopcode.EQU);
        dataDirective.setOperand(operand);
        return dataDirective;
    }    
    
    public Object parseEquOperand(String label, String operand, Map<String, Object> symbols) {
        Object value = null;
        if (symbols.containsKey(label))
            value = symbols.get(label);
        else if (operand.startsWith("$-")) {
            String s = (String) symbols.get(operand.substring(2));
            value = s.length();
        }   
        else {
            try {
                value = Long.decode(operand);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }   
        }
        return value;
    }
    
    public String getCode(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }
}
