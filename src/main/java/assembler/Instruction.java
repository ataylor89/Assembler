package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Instruction {
    private String label;
    private Opcode opcode;
    private Object operand1;
    private Object operand2;

    public Instruction() {}
    
    public Instruction(Opcode opcode) {
        this.opcode = opcode;
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
    public Opcode getOpcode() {
        return opcode;
    }

    /**
     * @param opcode the opcode to set
     */
    public void setOpcode(Opcode opcode) {
        this.opcode = opcode;
    }

    /**
     * @return the operand1
     */
    public Object getOperand1() {
        return operand1;
    }

    /**
     * @param operand1 the operand1 to set
     */
    public void setOperand1(Object operand1) {
        this.operand1 = operand1;
    }

    /**
     * @return the operand2
     */
    public Object getOperand2() {
        return operand2;
    }

    /**
     * @param operand2 the operand2 to set
     */
    public void setOperand2(Object operand2) {
        this.operand2 = operand2;
    }
    
    @Override
    public String toString() {
        return String.format("Label: %s\tOpcode: %s\tOperand 1: %s\tOperand 2: %s\n", label, opcode, operand1, operand2);
    }
}
