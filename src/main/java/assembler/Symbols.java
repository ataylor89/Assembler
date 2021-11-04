package assembler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrewtaylor
 */
public class Symbols {
    
    public static List<Symbol> list = new ArrayList<>();
    public static Map<String, Symbol> map = new HashMap<>();
    public static int offset = 0;
    
    class SortOrders {
        public static Comparator<Symbol> symTable = (Symbol s1, Symbol s2) -> {
            return s1.getType().compareTo(s2.getType());
        };
        public static Comparator<Symbol> stringTable = (Symbol s1, Symbol s2) -> {
            Integer i1 = s1.getIndex();
            Integer i2 = s2.getIndex();
            return i1.compareTo(i2);
        };
    }
            
    public static void init(AssemblyFile assemblyFile) {      
        int index = 0;
        int strx = 1;
        for (String extern : assemblyFile.getExterns()) {
            Symbol symbol = new Symbol();
            String name = extern.split("\\s+")[1].trim();
            symbol.setName(name);
            symbol.setIndex(index++);
            symbol.setStrx(strx);
            strx += name.length() + 1;
            symbol.setType(SymbolType.EXTERN);
            Symbols.list.add(symbol);
            Symbols.map.put(name, symbol);
        }
        for (String global : assemblyFile.getGlobals()) {
            Symbol symbol = new Symbol();
            String name = global.split("\\s+")[1].trim();
            symbol.setName(name);
            symbol.setIndex(index++);
            symbol.setStrx(strx);
            strx += name.length() + 1;
            symbol.setType(SymbolType.GLOBAL);
            Symbols.list.add(symbol);
            Symbols.map.put(name, symbol);
        }
        String[] instructions = assemblyFile.getInstructions();
        for (int i = 0; i < instructions.length; i++) {
            String[] tokens = instructions[i].split("\\s+", 4);
            if (tokens[0].endsWith(":")) {
                String label = tokens[0].substring(0, tokens[0].length()-1).trim();
                if (!Symbols.map.containsKey(label)) {
                    Symbol symbol = new Symbol();
                    symbol.setName(label);
                    symbol.setIndex(index++);
                    symbol.setStrx(strx);
                    strx += label.length() + 1;
                    symbol.setType(SymbolType.TEXT);
                    Symbols.list.add(symbol);
                    Symbols.map.put(label, symbol);
                }
            }
        }
        String[] directives = assemblyFile.getDataDirectives();
        for (int i = 0; i < directives.length; i++) {
            String[] tokens = directives[i].split("\\s+", 4);
            if (tokens[0].endsWith(":")) {
                String label = tokens[0].substring(0, tokens[0].length()-1).trim();
                if (!Symbols.map.containsKey(label)) {
                    Symbol symbol = new Symbol();
                    symbol.setName(label);
                    symbol.setIndex(index++);
                    symbol.setStrx(strx);
                    strx += label.length() + 1;
                    if (tokens[1].equals("db"))
                        symbol.setType(SymbolType.DATA);
                    else if (tokens[1].equals("equ"))
                        symbol.setType(SymbolType.ABSOLUTE);
                    Symbols.list.add(symbol);
                    Symbols.map.put(label, symbol);
                }
            }
        }
    }
    
    public static boolean isSymbol(String expression) {
        return map.containsKey(expression);
    }
}
