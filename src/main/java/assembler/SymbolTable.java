package assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrewtaylor
 */
public class SymbolTable {
    private List<Symbol> symbols;
    private Map<String, Symbol> map;

    public SymbolTable() {
        symbols = new ArrayList<>();
        map = new HashMap<>();
    }
    
    public SymbolTable(String[] names) {
        this();
        for (String name : names) {
            Symbol symbol = new Symbol(name);
            this.symbols.add(symbol);
            this.map.put(name, symbol);
        }
    }

    /**
     * @return the symbols
     */
    public List<Symbol> getSymbols() {
        return symbols;
    }

    /**
     * @param symbols the symbols to set
     */
    public void setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    /**
     * @return the map
     */
    public Map<String, Symbol> getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(Map<String, Symbol> map) {
        this.map = map;
    }
}
