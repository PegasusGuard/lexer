package ru.mirea.lang;

import java.util.regex.Pattern;

public enum TokenType {
    NUMBER("[0-9][0-9a-fA-F]*"),
    PRINT("print"),
    ASSIGN(":="),
    ID("[a-zA-Z_][a-zA-Z_0-9]*"),
    SEMICOLON("\\;"),
    ADD("\\+"),
    SUB("\\-"),
    MUL("\\*"),
    DIV("\\/"),
    LPAR("\\("),
    RPAR("\\)"),
    SPACE("[ \t\r\n]+");

    final Pattern pattern;

    TokenType(String regexp) {
        pattern = Pattern.compile(regexp);
    }
}
