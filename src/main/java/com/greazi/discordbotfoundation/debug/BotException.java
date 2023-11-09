package com.greazi.discordbotfoundation.debug;

/**
 * Represents our core exception. All exceptions of this
 * kind are logged automatically to the error.log file
 */
public class BotException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new exception and logs it
     *
     * @param t
     */
    public BotException(final Throwable t) {
        super(t);

        //Debugger.saveError(t);
    }

    /**
     * Create a new exception and logs it
     *
     * @param message
     */
    public BotException(final String message) {
        super(message);

        //Debugger.saveError(this, message);
    }

    /**
     * Create a new exception and logs it
     *
     * @param message
     * @param t
     */
    public BotException(final Throwable t, final String message) {
        super(message, t);

        //Debugger.saveError(t, message);
    }

    /**
     * Create a new exception and logs it
     */
    public BotException() {
        //Debugger.saveError(this);
    }

    @Override
    public String getMessage() {
        return "Report: " + super.getMessage();
    }
}