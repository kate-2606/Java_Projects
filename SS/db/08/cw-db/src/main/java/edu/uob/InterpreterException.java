
package edu.uob;

import java.io.Serial;

    public class InterpreterException extends Exception {
        @Serial
        private static final long serialVersionUID = 1;

        public InterpreterException (String message) {
            super(message);
        }

        public enum RowOrColumn { ROW, COLUMN }

        public static class AttributeDoesNotExist extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public AttributeDoesNotExist(String attributeName) {
                super("Attribute name '" +attributeName +"' does not exist");
            }
        }

        public static class CannotDeletePrimaryKey extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public CannotDeletePrimaryKey() {
                super("Cannot delete the primary key");
            }
        }

        public static class CreatingTableThatExistsAlready extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public CreatingTableThatExistsAlready(String tableName) {
                super("Table '" + tableName + "' already exists");
            }
        }

        public static class ReferencingWrongTable extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public ReferencingWrongTable(String attributeName) {
                super("Attribute '" + attributeName + "' is not referencing the correct table");
            }
        }

        public static class WorkingDatabaseIsNull extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public WorkingDatabaseIsNull() {
                super("Cannot execute command as not currently in a database");
            }
        }

        public static class AccessingNonExistentTable extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public AccessingNonExistentTable(String tableName) {
                super("Table '" + tableName + "' does not exist");
            }
        }

        public static class FoundInvalidAttributeName extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public FoundInvalidAttributeName(String attributeName) {
                super("Attribute '" + attributeName + "' is invalid");
            }
        }
}
