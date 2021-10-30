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
                if (isOpcode(tokens[0]))
                    this.setOpcode(tokens[0]);
                else
                    this.setLabel(tokens[0]);
            }
            if (i == 1) {
                if (isOpcode(tokens[1]))
                    this.setOpcode(tokens[1]);
                else if (isComment(tokens[1]))
                    this.setComment(tokens[1]);
                else if (isOpcode(tokens[0]))
                    this.setOperand1(tokens[1]);
            }
            if (i == 2) {
                if (isComment(tokens[2]))
                    this.setComment(tokens[2]);
                else if (isOpcode(tokens[1]))
                    this.setOperand1(tokens[2]);
                else if (isOpcode(tokens[0]))
                    this.setOperand2(tokens[2]);
            }
            if (i == 3) {
                if (isComment(tokens[3]))
                    this.setComment(tokens[3]);
                else if (isOpcode(tokens[1]))
                    this.setOperand2(tokens[3]);
            }
            if (i == 4) {
                if (isComment(tokens[4]))
                    this.setComment(tokens[4]);
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
    
    private boolean isOpcode(String text) {
        try {
            Opcode opcode = Opcode.valueOf(text.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    private boolean isComment(String text) {
        return text.startsWith(";");
    }
    
    private boolean isRegister(String text) {
        if (text.endsWith(","))
            text = text.substring(0, text.length()-1);
        try {
            Register register = Register.valueOf(text.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    private boolean isImmediateValue(String text) {
        try {
            Long.decode(text);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Label: %s\tOpcode: %s\tOperand 1: %s\tOperand 2: %s\tComment: %s\n", getLabel(), getOpcode(), getOperand1(), getOperand2(), getComment());
    }
}
