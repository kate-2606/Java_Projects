package edu.uob;

import java.io.Serial;

public class GameExceptions extends Exception {

    @Serial
    private static final long serialVersionUID = 1;

    public GameExceptions(String message) {
        super(message);
    }

    public static class ActionIsNull extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public ActionIsNull() {
            super("Action is null");
        }
    }

    public static class CustomCommandError extends GameExceptions {
        @Serial
        private static final long serialVersionUID = 1;

        public CustomCommandError() {
            super("Custom command error");
        }
    }
}