package assembler;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author andrewtaylor
 */
public class Symbols {
    
    public static Map<String, Symbol> map;
        
    public static void init(AssemblyFile assemblyFile) {
        map = new HashMap<>();
        
        int offset = 0;
        int strx = 1;
        for (String global : assemblyFile.getGlobals()) {
            Symbol symbol = new Symbol();
            symbol.setName(global.substring(7));
            symbol.setStrx(strx);
            symbol.setType((byte) 0x0f);
            symbol.setSect((byte) 0x01);
            symbol.setValue(0);
            String name = global.split("\\s+")[1].trim();
            map.put(name, symbol);
            strx += name.length() + 1;
        }
        String[] directives = assemblyFile.getDataDirectives();
        for (int i = 0; i < directives.length; i++) {
            String[] tokens = directives[i].split("\\s+", 3);
            if (tokens[0].endsWith(":")) {
                String name = tokens[0].substring(0, tokens[0].length()-1).trim();
                if (tokens[1].equals("db")) {
                    Symbol symbol = new Symbol();
                    symbol.setName(name);
                    symbol.setStrx(strx);
                    symbol.setType((byte) 0x0e);
                    symbol.setSect((byte) 0x02);                   
                    symbol.setValue(offset);
                    String data = (String) Expressions.eval(tokens[2]);
                    offset += data.length(); 
                    symbol.setSize(data.length());
                    map.put(name, symbol);      
                    strx += name.length() + 1;
                }
                else if (tokens[1].equals("equ")) {
                    Symbol symbol = new Symbol();
                    symbol.setName(name);
                    symbol.setStrx(strx);
                    symbol.setType((byte) 0x02);
                    symbol.setSect((byte) 0);
                    Integer value = (Integer) Expressions.eval(tokens[2]);
                    symbol.setValue(value);
                    map.put(name, symbol);
                    strx += name.length() + 1;
                }               
            }
        }
    }
}
