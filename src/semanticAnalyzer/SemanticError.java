package semanticAnalyzer;

public class SemanticError extends Exception {
    private final int lineNumber;
    private final String message;
    private final int columnNumber;

    public SemanticError(int lineNumber, String message, int columnNumber) {
        super(message);
        this.lineNumber = lineNumber;
        this.message = message;
        this.columnNumber = columnNumber;
    }

    @Override
    public String getMessage() {
        return message + ", line " + lineNumber + " column : " + columnNumber;
    }

}
