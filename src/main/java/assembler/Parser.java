package assembler;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
public class Parser {
    
    public AssemblyFile parse(File file) {
        AssemblyFile assemblyFile = new AssemblyFile();
        String code = readFile(file);
        assemblyFile.setCode(code);
        assemblyFile.setTextSection(parseTextSection(code));
        assemblyFile.setDataSection(parseDataSection(code));
        assemblyFile.setBssSection(parseBssSection(code));
        assemblyFile.setGlobals(parseGlobals(code));
        assemblyFile.setExterns(parseExterns(code));
        assemblyFile.setInstructions(parseInstructions(code));
        assemblyFile.setDataDirectives(parseDataDirectives(code));
        assemblyFile.setBssDirectives(parseBssDirectives(code));
        return assemblyFile;
    }   

    public String parseTextSection(String code) {
        int start = code.indexOf("section .text");
        int end = code.indexOf("section", start+13);
        while (end > 0 && code.charAt(end) != '\n')
            end--;
        if (end < 0)
            end = code.length();
        if (start > 0 && end > start)
            return code.substring(start, end);
        return null;
    }

    public String parseDataSection(String code) {
        int start = code.indexOf("section .data");
        int end = code.indexOf("section", start+13);
        if (end < 0)
            end = code.length();
        if (start > 0 && end > start)
            return code.substring(start, end);
        return null;
    }

    public String parseBssSection(String code) {
        int start = code.indexOf("section .bss");
        int end = code.indexOf("section", start+13);
        if (end < 0)
            end = code.length();
        if (start > 0 && end > start)
            return code.substring(start, end);
        return null;
    }

    public String[] parseGlobals(String code) {
        List<String> globals = new ArrayList<>();
        int start = code.indexOf("global");
        int end = code.indexOf("\n", start);
        while (start > 0 && end > start) {
            String global = code.substring(start, end);
            globals.add(global);
            start = code.indexOf("global", end);
            end = code.indexOf("\n", start);
        }
        return globals.toArray(new String[globals.size()]);
    }

    public String[] parseExterns(String code) {
        List<String> externs = new ArrayList<>();
        int start = code.indexOf("extern");
        int end = code.indexOf("\n", start);
        while (start > 0 && end > start) {
            String extern = code.substring(start, end);
            externs.add(extern);
            start = code.indexOf("extern", end);
            end = code.indexOf("\n", start);
        }
        return externs.toArray(new String[externs.size()]);
    }

    public String[] parseInstructions(String code) {
        String textSection = parseTextSection(code);
        if (textSection == null)
            return null;
        String[] lines = textSection.split("\n");
        List<String> instructions = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.endsWith(":") && i < lines.length - 1) {
                instructions.add(line + " " + lines[++i].trim());
            }
            else {
                instructions.add(line);
            }
        }
        return instructions.toArray(new String[instructions.size()]);
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
        return directives.toArray(new String[directives.size()]);
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
        return directives.toArray(new String[directives.size()]);
    }

    public String readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java assembler5.Parser <sourcefile.asm>");
            return;
        }
        Parser parser = new Parser();
        File file = new File(args[0]);
        AssemblyFile assemblyFile = parser.parse(file);
        System.out.println(assemblyFile);
    }
}