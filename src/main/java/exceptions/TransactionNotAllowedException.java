package exceptions;

public class TransactionNotAllowedException extends Exception {
    public TransactionNotAllowedException(String s) {
        super(s);
    }
}
