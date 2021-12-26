package assembler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Assembler {
    
    private File src, dest;
    private AssemblyFile assemblyFile;
    private ObjectFile objectFile;

    public Assembler(File src, File dest) {
        this.src = src;
        this.dest = dest;
    }

    public AssemblyFile parse() {
        if (assemblyFile == null) {
            Parser parser = new Parser();
            assemblyFile = parser.parse(src);
        }
        return assemblyFile;
    }
    
    public ObjectFile assemble() {
        if (objectFile == null) {
            objectFile = new ObjectFile();
            assemblyFile = parse();        
            Symbols.init(assemblyFile);            
            objectFile.setDataSection(assembleDataSection());
            objectFile.setTextSection(assembleTextSection());
            objectFile.setHeader(assembleHeader());
            objectFile.setLcSegment64(assembleLcSegment64());
            objectFile.setSection64Text(assembleSection64Text());
            objectFile.setSection64Data(assembleSection64Data());
            objectFile.setStringTable(assembleStringTable());
            objectFile.setLcSymtab(assembleLcSymtab());
            objectFile.setSymTable(assembleSymbolTable());
            return objectFile;
        }
        return objectFile;
    }
    
    private byte[] assembleHeader() {
        int numLoadCommands = assemblyFile.getSectionCount();
        ByteArray header = new ByteArray();
        header.addBytes(new byte[] {(byte) 0xcf, (byte) 0xfa, (byte) 0xed, (byte) 0xfe}); // magic number
        header.addBytes(new byte[] {(byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x01}); // cpu specifier
        header.addBytes(new byte[] {(byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00}); // cpu subtype specifier
        header.addBytes(new byte[] {(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00}); // filetype
        header.addDWord(numLoadCommands, Endian.LITTLE);                                  // number of load commands
        header.addBytes(new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00}); // size of load command region
        header.addBytes(new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}); // flags
        header.addBytes(new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}); // reserved
        return header.getBytes();
    }
   
    private byte[] assembleLcSegment64() {
        int numSections = assemblyFile.getSectionCount();
        int fileSize = objectFile.getTextSection().length + objectFile.getDataSection().length;
        ByteArray loadCommand = new ByteArray();
        loadCommand.addBytes(new byte[] {(byte) 0x19, (byte) 0x00, (byte) 0x00, (byte) 0x00}); // cmd
        loadCommand.addBytes(new byte[] {(byte) 0xe8, (byte) 0x00, (byte) 0x00, (byte) 0x00}); // cmdsize
        loadCommand.addOWord((byte) 0);                                                        // segname
        loadCommand.addQWord((byte) 0);                                                        // vmaddr
        loadCommand.addQWord((byte) 0x35);                                                     // vmsize
        loadCommand.addQWord(0x120, Endian.LITTLE);                                            // fileoffset
        loadCommand.addQWord((byte) 0x35);                                                     // file size
        loadCommand.addDWord((byte) 0x07);                                                     // maxprot
        loadCommand.addDWord((byte) 0x07);                                                     // initprot
        loadCommand.addDWord((byte) numSections);                                              // number of sections
        loadCommand.addDWord((byte) 0);                                                        // flags
        return loadCommand.getBytes();
    }

    private byte[] assembleSection64Text() {
        byte[] ts = objectFile.getTextSection();
        byte[] ds = objectFile.getDataSection();
        int padding = 8 - (ts.length % 8) + 8 - (ds.length % 8);
        int reloffset = 0x120 + ts.length + ds.length + padding;
        ByteArray section64 = new ByteArray();
        section64.addOWord("__text");               // section name
        section64.addOWord("__TEXT");               // segment name
        section64.addQWord(0);                      // address 
        section64.addQWord(ts.length);              // size
        section64.addDWord(0x120);                  // offset
        section64.addDWord(0);                      // align
        section64.addDWord(reloffset);              // reloffset
        section64.addDWord(0x01);                   // nreloc
        section64.addDWord(0x00070080, Endian.BIG); // flags
        section64.addDWord(0);                      // reserved1
        section64.addDWord(0);                      // reserved2
        section64.addDWord(0);                      // making the index a multiple of 8
        return section64.getBytes();
    }
    
    private byte[] assembleSection64Data() {
        byte[] ts = objectFile.getTextSection();
        byte[] ds = objectFile.getDataSection();       
        int address = ts.length;
        int size = ds.length;
        int padding = 8 - (ts.length % 8);
        int offset = 0x120 + ts.length + padding;
        ByteArray section64 = new ByteArray();
        section64.addOWord("__data");               // section name
        section64.addOWord("__DATA");               // segment name
        section64.addQWord(address);                // address
        section64.addQWord(size);                   // size
        section64.addDWord(offset);                 // offset
        section64.addDWord(0);                      // align
        section64.addDWord(0);                      // reloffset
        section64.addDWord(0);                      // nreloc
        section64.addDWord(0);                      // flags
        section64.addDWord(0);                      // reserved1
        section64.addDWord(0);                      // reserved2
        section64.addDWord(0);                      // making the index a multiple of 8
        return section64.getBytes();           
    }
    
    private byte[] assembleLcSymtab() {
        byte[] ts = objectFile.getTextSection();
        byte[] ds = objectFile.getDataSection();       
        int padding = 8 - (ts.length % 8) + 8 - (ds.length % 8);
        int symOffset = 0x120 + ts.length + ds.length + padding + 8;
        int numSymbols = Symbols.map.keySet().size();
        int strOffset = symOffset + numSymbols * 0x10;
        int strSize = objectFile.getStringTable().length;
        ByteArray lcSymtab = new ByteArray();
        lcSymtab.addDWord(0x02);                    // cmd
        lcSymtab.addDWord(0x18);                    // cmd size
        lcSymtab.addDWord(symOffset);               // symoff
        lcSymtab.addDWord(numSymbols);              // nsyms
        lcSymtab.addDWord(strOffset);               // stroff
        lcSymtab.addDWord(strSize);                 // strsize
        return lcSymtab.getBytes();
    }
    
    private byte[] assembleTextSection() {
        ByteArray textSection = new ByteArray();
        String[] instructions = assemblyFile.getInstructions();
        for (String text : instructions) {
            Instruction instruction = new Instruction(text);
            Opcode opcode = Opcode.parse(instruction.getOpcode());
            String operand1 = instruction.getOperand1();
            String operand2 = instruction.getOperand2();
            Object op1 = Expressions.eval(operand1);
            Object op2 = Expressions.eval(operand2);
            switch (opcode) {
                case MOV -> {
                    if (op1 instanceof Register && op2 instanceof Long) {
                        Register register = (Register) op1;
                        Long num = (Long) op2;
                        textSection.addBytes(Opcodes.getMovCode(register));
                        textSection.addQWord(num, Endian.LITTLE); 
                    }
                    else if (op1 instanceof Register && op2 instanceof Integer) {
                        Register register = (Register) op1;
                        Register reg32 = Registers.map32.get(register);
                        Integer num = (Integer) op2;
                        textSection.addBytes(Opcodes.getMovCode(reg32));
                        textSection.addDWord(num, Endian.LITTLE);
                    }
                    else if (op1 instanceof Register && op2 instanceof Symbol) {
                        Register register = (Register) op1;
                        Symbol symbol = (Symbol) op2;
                        Long num = (long) symbol.getValue();
                        textSection.addBytes(Opcodes.getMovCode(register));
                        textSection.addQWord(num, Endian.LITTLE); 
                    }
                }
                case XOR -> {
                    if (op1 instanceof Register && op2 instanceof Register) {
                        Register register1 = (Register) op1;
                        Register register2 = (Register) op2;
                        textSection.addBytes(Opcodes.getXorCode(register1, register2));
                    }
                }
                case SYSCALL -> textSection.addBytes(Opcodes.getCode(Opcode.SYSCALL));
            }
        }
        return textSection.getBytes();
    }
    
    private byte[] assembleDataSection() {
        ByteArray dataSection = new ByteArray();
        String[] directives = assemblyFile.getDataDirectives();
        for (String text : directives) {
            DataDirective directive = new DataDirective(text);
            Pseudoopcode opcode = Pseudoopcode.parse(directive.getOpcode());
            String operand = directive.getOperand();
            switch (opcode) {
                case DB -> {
                    String value = (String) Expressions.eval(operand);
                    dataSection.addBytes(value.getBytes());
                }
            }       
        }
        return dataSection.getBytes();
    }
        
    private byte[] assembleSymbolTable() {
        // https://opensource.apple.com/source/xnu/xnu-4570.71.2/EXTERNAL_HEADERS/mach-o/nlist.h.auto.html
        ByteArray symSection = new ByteArray();
        List<Symbol> symbolTable = new ArrayList<>(Symbols.map.values());
        Collections.sort(symbolTable);
        symSection.addDWord(0x0c, Endian.LITTLE);
        symSection.addDWord(0x0e, Endian.BIG);
        int dataOffset = objectFile.getTextSection().length;
        for (Symbol symbol : symbolTable) {      
            symSection.addDWord(symbol.getStrx());
            symSection.addByte(symbol.getType());
            symSection.addByte(symbol.getSect());
            symSection.addWord(0);
            if (symbol.getType() == 0x0e && symbol.getSect() == 2)
                symSection.addQWord(dataOffset + symbol.getValue());
            else
                symSection.addQWord(symbol.getValue());                    
        }
        return symSection.getBytes();
    }
    
    private byte[] assembleStringTable() {
        return new StringTable().toString().getBytes();
    }
    
    public void writeToFile() {
        try (FileOutputStream fos = new FileOutputStream(dest)) {  
            fos.write(objectFile.getBytes());
            fos.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    public static void main(String[] args) {
        File src = new File("src/main/resources/simple.asm");
        File dest = new File(System.getProperty("user.dir") + "/simple.o");
        Assembler assembler = new Assembler(src, dest);
        ObjectFile objectFile = assembler.assemble();
        System.out.println(objectFile);
        assembler.writeToFile();
    }
}
