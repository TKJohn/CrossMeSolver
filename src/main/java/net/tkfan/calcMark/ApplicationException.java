package net.tkfan.calcMark;

public class ApplicationException extends Exception {

    private static final long serialVersionUID = -4761586344071889116L;

    public ApplicationException() {
        super();
    }

    public ApplicationException(Throwable t) {
        super(t);
    }

    public ApplicationException(String s) {
        super(s);
    }

    public ApplicationException(String s, Throwable t) {
        super(s, t);
    }
}
