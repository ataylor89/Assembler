package assembler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Assembler {
    
    private Parser parser;

    public Assembler() {
        parser = new Parser();
    }

    public ObjectFile assemble(File file) {
        ObjectFile objectFile = new ObjectFile();
        String code = parser.getCode(file);
        SymbolTable symTable = parser.parseSymbols(code);
        String[] instructions = parser.parseInstructions(code);
        String[] dataDirectives = parser.parseDataDirectives(code);
        byte[] header = assembleHeader(2);
        byte[] lcSegment64 = assembleLcSegment64();
        byte[] section64Text = assembleSection64Text();
        byte[] section64Data = assembleSection64Data();
        byte[] lcSymtab = assembleLcSymtab();
        byte[] textSection = assembleTextSection(instructions, symTable.getMap());
        byte[] dataSection = assembleDataSection(dataDirectives, symTable.getMap());
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
    
    public byte[] assembleTextSection(String[] instructions, Map<String, Object> symbols) {
        ByteArray textSection = new ByteArray();
        for (int i = 0; i < instructions.length; i++) {
            Instruction instruction = parser.parseInstruction(instructions[i], symbols);
            System.out.println("Instruction:\n" + instruction);
            Opcode opcode = instruction.getOpcode();
            if (opcode == null)
                continue;
            Object operand1 = instruction.getOperand1();
            Object operand2 = instruction.getOperand2();
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
        return textSection.getBytes();
    }
    
    public byte[] assembleMov(Object operand1, Object operand2) {
        ByteArray byteArray = new ByteArray();
        if (operand1 instanceof Register && operand2 instanceof Long) {
            Register register = (Register) operand1;
            Long num = (Long) operand2;
            byteArray.addBytes(assembleMovCode(register));
            byteArray.addQWord(num, Endian.LITTLE); 
        }
        else if (operand1 instanceof Register && operand2 instanceof Integer) {
            Register register = (Register) operand1;
            Integer num = (Integer) operand2;
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

    public byte[] assembleXor(Object operand1, Object operand2) {
        ByteArray byteArray = new ByteArray();
        if (operand1 instanceof Register && operand2 instanceof Register) {
            Register register1 = (Register) operand1;
            Register register2 = (Register) operand2;
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

    public byte[] assembleDataSection(String[] directives, Map<String, Object> symbols) {
        ByteArray dataSection = new ByteArray();
        for (int i = 0; i < directives.length; i++) {
            System.out.println("Assembling data directive: " + directives[i]);
            DataDirective directive = parser.parseDataDirective(directives[i], symbols);
            System.out.println(directive);
            Pseudoopcode opcode = directive.getOpcode();
            switch (opcode) {
                case DB:
                    String operand = (String) directive.getOperand();
                    dataSection.addBytes(operand.getBytes());
                    break;
            }       
        }
        return dataSection.getBytes();
    }
    
    public void writeToFile(ObjectFile objectFile, File dest) {
        try (FileOutputStream fos = new FileOutputStream(dest)) {  
            fos.write(objectFile.getFile().getBytes());
            fos.flush();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java assembler5.Assembler <sourcefile.asm> <objectfile.o>");
            return;
        }
        Assembler assembler = new Assembler();
        File src = new File(args[0]);
        File dest = new File(args[1]);
        ObjectFile objectFile = assembler.assemble(src);
        System.out.println(objectFile);
        assembler.writeToFile(objectFile, dest);
    }
}
