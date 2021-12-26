package assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author andrewtaylor
 */
public class StringTable {
    
    private String value = "";
    
    public StringTable() {
        List<Symbol> lst = new ArrayList<>(Symbols.map.values());
        Collections.sort(lst, (Symbol s1, Symbol s2) ->  {
            Integer strx1 = s1.getStrx();
            Integer strx2 = s2.getStrx();
            return strx1.compareTo(strx2);
        });
        value += '\0';
        for (Symbol s : lst) 
            value += s.getName() + '\0';
    }
    
    @Override
    public String toString() {
        return value;
    }
}
