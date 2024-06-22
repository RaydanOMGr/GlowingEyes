package me.andreasmelone.glowingeyes.server.exceptions;

public class ToBeImplementedException extends RuntimeException {
    public ToBeImplementedException() {
        super("This method is not yet implemented.");
    }
}
