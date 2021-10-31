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
    public List<Symbol> symbols;
    public Map<String, Symbol> map;
    public int offset = 0;
    
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
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Symbol table\n");
        for (Symbol symbol : symbols) {
            sb.append(String.format("Symbol name: %s\tvalue: %s\n", symbol.getName(), symbol.getValue()));
        }
        return sb.toString();
    }
}
