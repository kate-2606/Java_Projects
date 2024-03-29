
package edu.uob;

import java.io.Serial;

    public class InterpreterException extends Exception {
        @Serial
        private static final long serialVersionUID = 1;

        public InterpreterException (String message) {
            super(message);
        }

        public enum RowOrColumn { ROW, COLUMN }

        public static class ExistingAttribute extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public ExistingAttribute(String attributeName) {
                super("Attribute name '" +attributeName +"' already exists in this table");
            }
        }


        public static class NotExistentAttributes extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public NotExistentAttributes (String tableName) {
                super("No attributes in table '" + tableName +"'");
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

        public static class ContainedInTwoTables extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public ContainedInTwoTables(String attributeName) {
                super("Both tables have the same attribute '" + attributeName + "'");
            }
        }

        public static class InvalidAttributeName extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public InvalidAttributeName(String attributeName) {
                super("Attribute '" + attributeName + "' is invalid");
            }
        }

        public static class AccessingNonExistentRow extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public AccessingNonExistentRow(Long rowNumber) {
                super("Row number " + rowNumber + " does not exist");
            }
        }


        public static class FailedToFindPrimaryKey extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public FailedToFindPrimaryKey() {
                super("Failed to make a new primary key there may be an unwanted directory in the database");
            }
        }

        public static class InvalidNumberOfValues extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public InvalidNumberOfValues() {
                super("The number of values does not match the number of attributes in the table");
            }
        }


        public static class MatchingKeyWord extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public MatchingKeyWord() {
                super("An attribute or table name matches a key word");
            }
        }


        public static class PotentialEmptyFile extends InterpreterException {
            @Serial private static final long serialVersionUID = 1;

            public PotentialEmptyFile() {
                super("The table being read is empty");
            }
        }
}
