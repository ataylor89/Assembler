package assembler;

/**
 *
 * @author andrewtaylor
 */
public class DataDirective {
    private String label;
    private Pseudoopcode opcode;
    private Object operand;

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the opcode
     */
    public Pseudoopcode getOpcode() {
        return opcode;
    }

    /**
     * @param opcode the opcode to set
     */
    public void setOpcode(Pseudoopcode opcode) {
        this.opcode = opcode;
    }

    /**
     * @return the operand
     */
    public Object getOperand() {
        return operand;
    }

    /**
     * @param operand the operand to set
     */
    public void setOperand(Object operand) {
        this.operand = operand;
    }
    
    @Override
    public String toString() {
        return String.format("Label: %s\tOpcode: %s\tOperand: %s\n", label, opcode, operand);
    }
}
