package service;

public class CompteInexistantException extends Exception {
    private static final long serialVersionUID = 1L;
    public CompteInexistantException(String msg) { super(msg); }
}
