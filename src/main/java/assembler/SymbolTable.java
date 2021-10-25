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
    private List<String> sequence;
    private Map<String, Object> map;

    public SymbolTable() {
        sequence = new ArrayList<>();
        map = new HashMap<>();
    }
    
    /**
     * @return the sequence
     */
    public List<String> getSequence() {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(List<String> sequence) {
        this.sequence = sequence;
    }

    /**
     * @return the map
     */
    public Map<String, Object> getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
