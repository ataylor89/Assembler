package assembler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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
        assemblyFile.setSymbolTable(parseSymbolTable());
        assemblyFile.setDataDirectives(parseDataDirectives());
        assemblyFile.setInstructions(parseInstructions());
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
        String text = null;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().endsWith(":") && i < lines.length-1) {
                text = lines[i].trim() + " " + lines[++i].trim();
                Instruction instruction = parseInstruction(text);
                instructions.add(instruction);
            }
            else {
                text = lines[i].trim();
                Instruction instruction = parseInstruction(text);
                instructions.add(instruction);
            }
        }
        Instruction[] arr = new Instruction[instructions.size()];
        return instructions.toArray(arr);
    }
    
    private Instruction parseInstruction(String text) {
        Instruction instruction = new Instruction();
        String[] tokens = text.split("\\s+", 4);
        Opcode opcode = null;
        for (int i = 0; i < tokens.length; i++) {
            switch (i) {
                case 0:
                    if (Opcode.isOpcode(tokens[0])) {
                        opcode = Opcode.parse(tokens[0]);
                        instruction.setOpcode(opcode);
                    }
                    else {
                        instruction.setLabel(tokens[0]);
                    }   break;
                case 1:
                    if (Opcode.isOpcode(tokens[0])) {
                        Operand operand1 = new Operand(tokens[i]);
                        evaluateOperand(operand1);
                        instruction.setOperand1(operand1);
                    }
                    else if (Opcode.isOpcode(tokens[1])) {
                        opcode = Opcode.parse(tokens[1]);
                        instruction.setOpcode(opcode);
                    }   break;
                case 2:
                    if (Opcode.isOpcode(tokens[0])) {
                        Operand operand = new Operand(tokens[2]);
                        evaluateOperand(operand);
                        instruction.setOperand2(operand);
                    }
                    else if (Opcode.isOpcode(tokens[1])) {
                        Operand operand = new Operand(tokens[2]);
                        evaluateOperand(operand);
                        instruction.setOperand1(operand);
                    }   break;
                case 3:
                    if (Opcode.isOpcode(tokens[1])) {
                        Operand operand = new Operand(tokens[3]);
                        evaluateOperand(operand);
                        instruction.setOperand2(operand);
                    }   break;
                default:
                    break;
            }
        }
        return instruction;
    }
    
    private void evaluateOperand(Operand operand) {
        SymbolTable symbolTable = assemblyFile.getSymbolTable();
        String expression = operand.getExpression();
        
        if (symbolTable.map.containsKey(expression)) {
            Symbol symbol = symbolTable.map.get(expression);
            Long value = symbol.getValue();
            if (symbol.getType() == (byte) 0x02)
                operand.setValue(value.intValue());
            else if (symbol.getType() == (byte) 0x0e)
                operand.setValue(value);
        }
        else if (Register.isRegister(expression)) {
            Object value = Register.parse(expression);
            operand.setValue(value);
        }
        else {
            try {
                Object value = Integer.decode(expression);
                operand.setValue(value);
            } catch (NumberFormatException e) {
                System.err.println(e);
            }
        }
    }
    
    private DataDirective[] parseDataDirectives() {
        List<DataDirective> directives = new ArrayList<>();
        String dataSection = assemblyFile.getDatatSection();
        if (dataSection == null)
            return null;
        SymbolTable symbolTable = assemblyFile.getSymbolTable();
        String[] lines = dataSection.split("\n");
        int offset = 0;
        for (int i = 1; i < lines.length; i++) {
            DataDirective directive = parseDataDirective(lines[i].trim());
            String label = directive.getLabel();
            Pseudoopcode opcode = directive.getOpcode();
            Operand operand = directive.getOperand();
            Symbol symbol = symbolTable.map.get(label);
            switch (opcode) {
                case DB:
                    symbol.setValue(symbolTable.offset);
                    String s = (String) operand.getValue();
                    symbol.setSize(s.length());
                    symbol.setType((byte) 0x0e);
                    symbol.setSect((byte) 0x02);
                    symbolTable.offset += s.length();
                    break;
                case EQU:
                    symbol.setValue((Long) operand.getValue());
                    symbol.setType((byte) 0x02);
                    symbol.setSect((byte) 0x00);
                    break;
            }
            directives.add(directive);
        }
        DataDirective[] arr = new DataDirective[directives.size()];
        return directives.toArray(arr);
    }
    
    private DataDirective parseDataDirective(String text) {
        DataDirective directive = new DataDirective();
        String[] tokens = text.split("\\s+", 3);
        Pseudoopcode opcode = null;
        String label = null;
        for (int i = 0; i < tokens.length; i++) {
            switch (i) {
                case 0:
                    label = tokens[0];
                    if (label.endsWith(":"))
                        label = label.substring(0, label.length()-1);
                    directive.setLabel(label);
                    break;
                case 1:
                    opcode = Pseudoopcode.parse(tokens[1]);
                    directive.setOpcode(opcode);
                    break;
                case 2:
                    Operand operand = new Operand(tokens[2]);
                    evaluateOperand(operand, opcode);
                    directive.setOperand(operand);
                    break;
                default:
                    break;
            }
        }
        return directive;
    }
    
    private void evaluateOperand(Operand operand, Pseudoopcode opcode) {
        SymbolTable symbolTable = assemblyFile.getSymbolTable();
        String expression = operand.getExpression();
        switch (opcode) {
            case DB:
                if (StringConstant.isStringConstant(expression)) {
                    StringConstant constant = new StringConstant(expression);
                    operand.setValue(constant.getValue());
                }
                break;
            case EQU:
                if (expression.startsWith("$-")) {
                    Symbol ref = symbolTable.map.get(expression.substring(2, expression.length()));
                    operand.setValue(ref.getSize());
                }     
                break;
            default:
                break;
        }
    }
    
    public BssDirective[] parseBssDirectives() {
        String bssSection = assemblyFile.getBssSection();
        if (bssSection == null)
            return null;
        String[] lines = bssSection.split("\n");
        List<BssDirective> directives = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            BssDirective directive = new BssDirective();
            // directives.add(lines[i].trim());
            directives.add(directive);
        }
        BssDirective[] arr = new BssDirective[directives.size()];
        return directives.toArray(arr);
    }
    
    private SymbolTable parseSymbolTable() {
        String[] symbols = parseSymbols();
        return new SymbolTable(symbols);
    }
    
    private String[] parseSymbols() {
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
        String path = System.getProperty("user.home") + "/nasm/simple.asm";
        Parser parser = new Parser(new File(path));
        AssemblyFile af = parser.parse();
        System.out.println(af);
    }
}
