package assembler;

/**
 *
 * @author andrewtaylor
 */
public class Operand {
    
    private String expression;
    private Object value;
    
    public Operand() {}
    
    public Operand(String expression) {
        this.expression = expression;
    }
    
    public Operand(String expression, Object value) {
        this.expression = expression;
        this.value = value;
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @param expression the expression to set
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.format("Expression: %s\tValue: %s\n", expression, value);
    }
}
