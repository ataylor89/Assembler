package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Instruction {
    private String label;
    private Opcode opcode;
    private Operand operand1;
    private Operand operand2;
    private String comment;

    public Instruction() {}

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
    public Operand getOperand1() {
        return operand1;
    }

    /**
     * @param operand1 the operand1 to set
     */
    public void setOperand1(Operand operand1) {
        this.operand1 = operand1;
    }

    /**
     * @return the operand2
     */
    public Operand getOperand2() {
        return operand2;
    }

    /**
     * @param operand2 the operand2 to set
     */
    public void setOperand2(Operand operand2) {
        this.operand2 = operand2;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
