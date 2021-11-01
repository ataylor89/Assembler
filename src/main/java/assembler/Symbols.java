package assembler;

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
}
