package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Instruction {
    private String label;
    private String opcode;
    private String operand1;
    private String operand2;
    private String comment;

    public Instruction() {}

    public Instruction(String instruction) {
        String[] tokens = instruction.split("\\s+", 5);
        for (int i = 0; i < tokens.length; i++) {
            if (i == 0) {
                if (Opcode.isOpcode(tokens[0]))
                    this.setOpcode(tokens[0]);
                else {
                    String label = tokens[0];
                    if (label.endsWith(":"))
                        label = label.substring(0, label.length()-1);
                    this.setLabel(label);
                }
            }
            if (i == 1) {
                if (Opcode.isOpcode(tokens[1]))
                    this.setOpcode(tokens[1]);
                else if (Opcode.isOpcode(tokens[0]))
                    this.setOperand1(tokens[1]);
            }
            if (i == 2) {
                if (Opcode.isOpcode(tokens[1]))
                    this.setOperand1(tokens[2]);
                else if (Opcode.isOpcode(tokens[0]))
                    this.setOperand2(tokens[2]);
            }
            if (i == 3) {
                if (Opcode.isOpcode(tokens[1]))
                    this.setOperand2(tokens[3]);
            }
        }
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
    public String getOpcode() {
        return opcode;
    }

    /**
     * @param opcode the opcode to set
     */
    public void setOpcode(String opcode) {
        this.opcode = opcode;
    }

    /**
     * @return the operand1
     */
    public String getOperand1() {
        return operand1;
    }

    /**
     * @param operand1 the operand1 to set
     */
    public void setOperand1(String operand1) {
        this.operand1 = operand1;
    }

    /**
     * @return the operand2
     */
    public String getOperand2() {
        return operand2;
    }

    /**
     * @param operand2 the operand2 to set
     */
    public void setOperand2(String operand2) {
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
    
    public static Instruction parse(String instruction) {
        return new Instruction(instruction); 
    }
    
    @Override
    public String toString() {
        return String.format("Label: %s\tOpcode: %s\tOperand 1: %s\tOperand 2: %s\tComment: %s\n", getLabel(), getOpcode(), getOperand1(), getOperand2(), getComment());
    }
}
