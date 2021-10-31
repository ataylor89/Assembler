package assembler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class Assembler {
    
    private File src;
    private File dest;
    private ObjectFile objectFile;
    private AssemblyFile assemblyFile;
    private SymbolTable symbolTable;
    
    public Assembler(File src, File dest) {
        this.src = src;
        this.dest = dest;
        objectFile = new ObjectFile();
    }

    public ObjectFile assemble() {
        Parser parser = new Parser(src);
        assemblyFile = parser.parse();
        symbolTable = assemblyFile.getSymbolTable();
        byte[] header = assembleHeader(assemblyFile.getSectionCount());
        byte[] lcSegment64 = assembleLcSegment64();
        byte[] section64Text = assembleSection64Text();
        byte[] section64Data = assembleSection64Data();
        byte[] lcSymtab = assembleLcSymtab();
        byte[] dataSection = assembleDataSection();
        byte[] textSection = assembleTextSection();
        objectFile.addSection(header, "MachO-64 header");  
        objectFile.addSection(lcSegment64, "Load command LC_SEGMENT_64");
        objectFile.addSection(section64Text, "SECTION_64 text");
        objectFile.addSection(section64Data, "SECTION_64 data");
        objectFile.addSection(lcSymtab, "LC_SYMTAB");
        objectFile.addSection(textSection, "Text section");
        objectFile.addSection(dataSection, "Data section");
        return objectFile;
    }
 
    public byte[] assembleHeader(int numLoadCommands){
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
        loadCommand.addDWord((byte) 0x02);                                                     // number of sections
        loadCommand.addDWord((byte) 0);                                                        // flags
        return loadCommand.getBytes();
    }

    public byte[] assembleSection64Text() {
        ByteArray section64 = new ByteArray();
        section64.addOWord("__text");               // section name
        section64.addOWord("__TEXT");               // segment name
        section64.addQWord(0);                      // address 
        section64.addQWord(0x25, Endian.LITTLE);    // size
        section64.addDWord(0x120, Endian.LITTLE);   // offset
        section64.addDWord(0);                      // align
        section64.addDWord(0x0158, Endian.LITTLE);  // reloffset
        section64.addDWord(0x01, Endian.LITTLE);    // nreloc
        section64.addDWord(0x00070080, Endian.BIG); // flags
        section64.addDWord(0);                      // reserved1
        section64.addDWord(0);                      // reserved2
        section64.addDWord(0);                      // making the index a multiple of 8
        return section64.getBytes();
    }
    
    public byte[] assembleSection64Data() {
        ByteArray section64 = new ByteArray();
        section64.addOWord("__data");               // section name
        section64.addOWord("__DATA");               // segment name
        section64.addQWord(0x25);                   // address
        section64.addQWord(0x0b);                   // size
        section64.addDWord(0x148);                  // offset
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
        ByteArray lcSymtab = new ByteArray();
        lcSymtab.addDWord(0x02);                    // cmd
        lcSymtab.addDWord(0x18);                    // cmd size
        lcSymtab.addDWord(0x160);                   // symoff
        lcSymtab.addDWord(0x03);                    // nsyms
        lcSymtab.addDWord(0x190);                   // stroff
        lcSymtab.addDWord(0x13);                    // strsize
        return lcSymtab.getBytes();
    }
    
    public byte[] assembleTextSection() {
        ByteArray textSection = new ByteArray();
        Instruction[] instructions = assemblyFile.getInstructions();
        for (int i = 0; i < instructions.length; i++) {
            Instruction instruction = instructions[i];
            Opcode opcode = instruction.getOpcode();
            Operand operand1 = instruction.getOperand1();
            Operand operand2 = instruction.getOperand2();
            byte[] bytes = null;
            switch (opcode) {
                case MOV:
                    bytes = assembleMov(operand1, operand2);
                    textSection.addBytes(bytes);
                    break;
                case XOR:
                    bytes = assembleXor(operand1, operand2);
                    textSection.addBytes(bytes);
                    break;
                case SYSCALL:
                    bytes = assembleSyscall();
                    textSection.addBytes(bytes);
                    break;
            }
        }
        int n = textSection.getIndex() % 8;
        for (; n < 8; n++)
            textSection.addByte((byte) 0x00);
        return textSection.getBytes();
    }
    
    public byte[] assembleMov(Operand operand1, Operand operand2) {
        ByteArray byteArray = new ByteArray();
        Object value1 = operand1.getValue();
        Object value2 = operand2.getValue();
        if (value1 instanceof Register && value2 instanceof Long) {
            Register register = (Register) value1;
            Long num = (Long) value2;
            byteArray.addBytes(assembleMovCode(register));
            byteArray.addQWord(num, Endian.LITTLE); 
        }
        else if (value1 instanceof Register && value2 instanceof Integer) {
            Register register = (Register) value1;
            if (register.size() == 64)
                register = Registers.map32.get(register);
            Integer num = (Integer) value2;
            byteArray.addBytes(assembleMovCode(register));
            byteArray.addDWord(num, Endian.LITTLE);
        }
        return byteArray.getBytes();
    }

    public byte[] assembleMovCode(Register register) {
        switch (register) {
            case RAX:
                return new byte[] {(byte) 0x48, (byte) 0xb8};
            case EAX:
                return new byte[] {(byte) 0xb8};
            case RDX:
                return new byte[] {(byte) 0x48, (byte) 0xba};
            case EDX:
                return new byte[] {(byte) 0xba};
            case RSI:
                return new byte[] {(byte) 0x48, (byte) 0xbe};
            case ESI:
                return new byte[] {(byte) 0xbe};
            case RDI:
                return new byte[] {(byte) 0x48, (byte) 0xbf};
            case EDI:
                return new byte[] {(byte) 0xbf};
        }
        return new byte[] {};
    }

    public byte[] assembleXor(Operand operand1, Operand operand2) {
        ByteArray byteArray = new ByteArray();
        Object value1 = operand1.getValue(); 
        Object value2 = operand2.getValue();
        if (value1 instanceof Register && value2 instanceof Register) {
            Register register1 = (Register) value1;
            Register register2 = (Register) value2;
            if (register1 == Register.RAX && register2 == Register.RAX) 
                byteArray.addBytes(new byte[] {(byte) 0x48, (byte) 0x31, (byte) 0xc0});
            else if (register1 == Register.RDI && register2 == Register.RDI)
                byteArray.addBytes(new byte[] {(byte) 0x48, (byte) 0x31, (byte) 0xff});
            else if (register1 == Register.RSI && register2 == Register.RSI)
                byteArray.addBytes(new byte[] {(byte) 0x48, (byte) 0x31, (byte) 0xf6});
            else if (register1 == Register.RDX && register2 == Register.RDX) 
                byteArray.addBytes(new byte[] {(byte) 0x48, (byte) 0x31, (byte) 0xd2});
        }
        return byteArray.getBytes();
    }

    public byte[] assembleSyscall() {
        return new byte[] {(byte) 0x0f, (byte) 0x05};
    }

    public byte[] assembleDataSection() {
        ByteArray dataSection = new ByteArray();
        DataDirective[] directives = assemblyFile.getDataDirectives();
        for (int i = 0; i < directives.length; i++) {      
            DataDirective directive = directives[i];
            System.out.println(directive);
            Pseudoopcode opcode = directive.getOpcode();
            switch (opcode) {
                case DB:
                    Operand operand = directive.getOperand();
                    String value = (String) operand.getValue();
                    dataSection.addBytes(value.getBytes());
                    break;
            }       
        }
        return dataSection.getBytes();
    }
    /*
    public byte[] assembleSymbolTable() {
        
    }
    */
    public void writeToFile() {
        try (FileOutputStream fos = new FileOutputStream(dest)) {  
            fos.write(objectFile.getFile().getBytes());
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
        Assembler assembler = new Assembler(src, dest);
        ObjectFile objectFile = assembler.assemble();
        System.out.println(objectFile);
        assembler.writeToFile();
    }
}
