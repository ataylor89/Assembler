package assembler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class Assembler {
    
    private AssemblyFile assemblyFile;
    private ObjectFile objectFile;

    public Assembler(File src) {
        Parser parser = new Parser();
        assemblyFile = parser.parse(src);
        objectFile = new ObjectFile();
    }

    public ObjectFile assemble() {
        Symbols.init(assemblyFile);
        objectFile.setDataSection(assembleDataSection());
        objectFile.setTextSection(assembleTextSection());
        objectFile.setHeader(assembleHeader());
        objectFile.setLcSegment64(assembleLcSegment64());
        objectFile.setSection64Text(assembleSection64Text());
        objectFile.setSection64Data(assembleSection64Data());
        objectFile.setLcSymtab(assembleLcSymtab());
        objectFile.setSymTable(assembleSymbolTable());
        return objectFile;
    }
    
    public byte[] assembleHeader() {
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
   
    public byte[] assembleLcSegment64() {
        int numSections = assemblyFile.getSectionCount();
        ByteArray loadCommand = new ByteArray();
        loadCommand.addBytes(new byte[] {(byte) 0x19, (byte) 0x00, (byte) 0x00, (byte) 0x00}); // cmd
        loadCommand.addBytes(new byte[] {(byte) 0xe8, (byte) 0x00, (byte) 0x00, (byte) 0x00}); // cmdsize
        loadCommand.addOWord((byte) 0);                                                        // segname
        loadCommand.addQWord((byte) 0);                                                        // vmaddr
        loadCommand.addQWord((byte) 0x33);                                                     // vmsize
        loadCommand.addQWord(0x120, Endian.LITTLE);                                            // fileoffset
        loadCommand.addQWord((byte) 0x33);                                                     // file size
        loadCommand.addDWord((byte) 0x07);                                                     // maxprot
        loadCommand.addDWord((byte) 0x07);                                                     // initprot
        loadCommand.addDWord((byte) numSections);                                              // number of sections
        loadCommand.addDWord((byte) 0);                                                        // flags
        return loadCommand.getBytes();
    }

    public byte[] assembleSection64Text() {
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
    
    public byte[] assembleSection64Data() {
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
    
    public byte[] assembleLcSymtab() {
        byte[] ts = objectFile.getTextSection();
        byte[] ds = objectFile.getDataSection();       
        int padding = 8 - (ts.length % 8) + 8 - (ds.length % 8);
        int symOffset = 0x120 + ts.length + ds.length + padding + 8;
        int numSymbols = Symbols.list.size();
        int strOffset = symOffset + numSymbols * 0x10;
        int strSize = Symbols.stringTable.length();
        ByteArray lcSymtab = new ByteArray();
        lcSymtab.addDWord(0x02);                    // cmd
        lcSymtab.addDWord(0x18);                    // cmd size
        lcSymtab.addDWord(symOffset);               // symoff
        lcSymtab.addDWord(numSymbols);              // nsyms
        lcSymtab.addDWord(strOffset);               // stroff
        lcSymtab.addDWord(strSize);                 // strsize
        return lcSymtab.getBytes();
    }
    
    public byte[] assembleTextSection() {
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
                }
                case XOR -> {
                    if (op1 instanceof Register && op2 instanceof Register) {
                        Register register1 = (Register) op1;
                        Register register2 = (Register) op2;
                        textSection.addBytes(Opcodes.getXorCode(register1));
                    }
                }
                case SYSCALL -> textSection.addBytes(Opcodes.getCode(Opcode.SYSCALL));
            }
        }
        return textSection.getBytes();
    }
    
    public byte[] assembleDataSection() {
        ByteArray dataSection = new ByteArray();
        String[] directives = assemblyFile.getDataDirectives();
        for (String text : directives) {
            DataDirective directive = new DataDirective(text);
            Pseudoopcode opcode = Pseudoopcode.parse(directive.getOpcode());
            String label = directive.getLabel();
            String operand = directive.getOperand();
            Symbol symbol = Symbols.map.get(label);
            switch (opcode) {
                case DB -> {
                    String value = (String) Expressions.eval(operand);
                    dataSection.addBytes(value.getBytes());
                    symbol.setValue((long) Symbols.offset);
                    symbol.setSize(value.length());
                    symbol.setType(SymbolType.DATA);
                    Symbols.offset += value.length();
                }
                case EQU -> {
                    Integer num = (Integer) Expressions.eval(operand);
                    symbol.setValue(num.longValue());
                    symbol.setType(SymbolType.ABSOLUTE);
                }
            }       
        }
        return dataSection.getBytes();
    }
        
    public byte[] assembleSymbolTable() {
        ByteArray symSection = new ByteArray();
        Symbols.list.sort(Symbols.SortOrders.symTable);
        int offset = objectFile.getTextSection().length;
        symSection.addDWord(0x0c, Endian.LITTLE);
        symSection.addDWord(0x0e, Endian.BIG);
        for (Symbol symbol : Symbols.list) {      
            long value = symbol.getValue();
            symSection.addDWord(symbol.getStrx());
            switch (symbol.getType()) {
                case TEXT -> {
                    symSection.addByte((byte) 0x0e);    // type
                    symSection.addByte((byte) 0x01);    // sect
                    symSection.addWord(0);              // desc
                    symSection.addQWord(value);
                }
                case DATA -> {
                    symSection.addByte((byte) 0x0e);    // type 
                    symSection.addByte((byte) 0x02);    // sect
                    symSection.addWord(0);              // desc
                    symSection.addQWord(offset + value);
                }
                case ABSOLUTE -> {
                    symSection.addByte((byte) 0x02);    // type
                    symSection.addByte((byte) 0x00);    // sect
                    symSection.addWord(0);              // desc
                    symSection.addQWord(value);
                }
                case GLOBAL -> {
                    symSection.addByte((byte) 0x0f);
                    symSection.addByte((byte) 0x01);
                    symSection.addWord(0);              // desc
                    symSection.addQWord(value);
                }
            }
        }
        symSection.addBytes(Symbols.stringTable.getBytes());
        return symSection.getBytes();
    }
    
    public void writeToFile(ObjectFile objectFile, File dest) {
        try (FileOutputStream fos = new FileOutputStream(dest)) {  
            fos.write(objectFile.getBytes());
            fos.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] args) {
        /*if (args.length < 2) {
            System.out.println("Usage: java assembler5.Assembler <sourcefile.asm> <objectfile.o>");
            return;
        }*/
        File src = new File(System.getProperty("user.home") + "/nasm/simple.asm");
        File dest = new File(System.getProperty("user.dir") + "/simple.o");
        Assembler assembler = new Assembler(src);
        ObjectFile objectFile = assembler.assemble();
        System.out.println(objectFile);
        assembler.writeToFile(objectFile, dest);
    }
}
