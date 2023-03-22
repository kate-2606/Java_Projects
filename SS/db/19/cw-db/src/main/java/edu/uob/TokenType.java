package edu.uob;

    public enum TokenType {
        USE, CREATE, DROP, ALTER, INSERT, SELECT, JOIN, DATABASE,
        //10
        TABLE, INTO, UPDATE, DELETE, FROM, SET, WHERE, ON, VALUES,
        //20
        ADD, NULL, OPEN_BR, CLOSE_BR, SPACE, COMMA, SEMI_COL,
        //28
        WILD_CRD, EQUALS, COMPARATOR, BOOL_OP, STRING_LIT, BOOL_LIT,
        //34
        FLOAT_LIT, INT_LIT, PLAIN_TXT, ATTRIB_NAME
    }
