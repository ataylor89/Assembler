package assembler;

/**
 *
 * @author andrewtaylor
 */
public class DataDirective {
    private String label;
    private String opcode;
    private String operand;
    private String comment;

    public DataDirective() {}
    
    public DataDirective(String directive) {
        String[] tokens = directive.split("\\s+", 3);
        for (int i = 0; i < tokens.length; i++) {
            if (i == 0) {
                String label = tokens[0];
                if (label.endsWith(":"))
                    label = label.substring(0, label.length()-1);
                this.setLabel(label);
            }
            if (i == 1)
                this.setOpcode(tokens[1]);
            if (i == 2)
                this.setOperand(tokens[2]);
            if (i == 3)
                this.setComment(tokens[3]);
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
     * @return the operand
     */
    public String getOperand() {
        return operand;
    }

    /**
     * @param operand the operand to set
     */
    public void setOperand(String operand) {
        this.operand = operand;
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
    
    public static DataDirective parse(String directive) {
        return new DataDirective(directive);
    }
    
    @Override
    public String toString() {
        return String.format("Label: %s\tOpcode: %s\tOperand: %s\n", getLabel(), getOpcode(), getOperand());
    }
}
