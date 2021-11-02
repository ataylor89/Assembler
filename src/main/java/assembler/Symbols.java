package assembler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrewtaylor
 */
public class Symbols {
    
    public static List<Symbol> list;
    public static Map<String, Symbol> map;
    public static int offset;
    
    static {
        list = new ArrayList<>();
        map = new HashMap<>();
        offset = 0;
    }
    
    public static void init(File file) {
        Parser parser = new Parser();
        AssemblyFile assemblyFile = parser.parse(file);
        init(assemblyFile);
    }
    
    public static void init(AssemblyFile assemblyFile) {        
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
}
