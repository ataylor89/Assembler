package assembler;

/**
 *
 * @author andrewtaylor
 */
public class DataDirective {
    private String label;
    private Pseudoopcode opcode;
    private Operand operand;

    public DataDirective() {}
    
    @Override
    public String toString() {
        return String.format("Label: %s\tOpcode: %s\tOperand: {%s}\n", getLabel(), getOpcode(), getOperand());
    }

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
    public Operand getOperand() {
        return operand;
    }

    /**
     * @param operand the operand to set
     */
    public void setOperand(Operand operand) {
        this.operand = operand;
    }
}
