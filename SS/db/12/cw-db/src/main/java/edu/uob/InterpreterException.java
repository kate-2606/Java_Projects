
package edu.uob;

import java.io.Serial;

    public class InterpreterException extends Exception {
        @Serial
        private static final long serialVersionUID = 1;

        public InterpreterException (String message) {
            super(message);
        }

        public enum RowOrColumn { ROW, COLUMN }

        public static class ReferencingNonExistentAttribute extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public ReferencingNonExistentAttribute(String attributeName) {
                super("Attribute name '" +attributeName +"' does not exist");
            }
        }

        public static class ReferencingExistingAttribute extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public ReferencingExistingAttribute(String attributeName) {
                super("Attribute name '" +attributeName +"' already exists");
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

        public static class AccessingNonExistentAttribute extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public AccessingNonExistentAttribute(String attributeName) {
                super("Attribute '" + attributeName + "' does not exist");
            }
        }

        public static class AccessingNonExistentRow extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public AccessingNonExistentRow(Long rowNumber) {
                super("Row number " + rowNumber + " does not exist");
            }
        }
}
