package ru.mirea.lang;

import ru.mirea.lang.ast.BinOpNode;
import ru.mirea.lang.ast.ExprNode;
import ru.mirea.lang.ast.NumberNode;
import ru.mirea.lang.ast.VarNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Parser {

    private final List<Token> tokens;
    private static List<VarNode> variables;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.variables = new ArrayList<>();
    }

    public static VarNode exists(
            Token id) {

        for (VarNode var : variables) {
            if (var.id.text.equals(id.text)) {
                return var;
            }
        }
        return null;
    }

    private void error(String message) {
        if (pos < tokens.size()) {
            Token t = tokens.get(pos);
            throw new RuntimeException(message + " в позиции " + t.pos);
        } else {
            throw new RuntimeException(message + " в конце файла");
        }
    }

    private Token match(TokenType... expected) {
        if (pos < tokens.size()) {
            Token curr = tokens.get(pos);
            if (Arrays.asList(expected).contains(curr.type)) {
                pos++;
                return curr;
            }
        }
        return null;
    }

    private Token require(TokenType... expected) {
        Token t = match(expected);
        if (t == null)
            error("Ожидается " + Arrays.toString(expected));
        return t;
    }

    private ExprNode parseElem() {
        Token num = match(TokenType.NUMBER);
        if (num != null)
            return new NumberNode(num);
        Token id = match(TokenType.ID);
        if (id != null)

            return new VarNode(id, null);
        error("Ожидается число или переменная");
        return null;
    }

    private ExprNode parseMnozh() {
        if (match(TokenType.LPAR) != null) {
            ExprNode e = parseExpression();
            require(TokenType.RPAR);
            return e;
        } else {
            return parseElem();
        }
    }

    public ExprNode parseSlag() {
        ExprNode e1 = parseMnozh();
        Token op;
        while ((op = match(TokenType.MUL, TokenType.DIV)) != null) {
            ExprNode e2 = parseMnozh();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    public ExprNode parseExpression() {
        ExprNode e1 = parseSlag();
        Token op;
        while ((op = match(TokenType.ADD, TokenType.SUB)) != null) {
            ExprNode e2 = parseSlag();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    public void parseLine() {
        while (pos <= tokens.size()) {
            Token id = match(TokenType.ID);
            if (id != null) {
                parseAssign(id);
                require(TokenType.SEMICOLON);
            }
            else {
                Token print = match(TokenType.PRINT);
                if (print != null) {
                    parsePrint();
                    require(TokenType.SEMICOLON);
                }
            }

        }
    }

    public void parseAssign(Token id) {
        require(TokenType.ASSIGN);
        ExprNode e = parseExpression();
        VarNode var1 = new VarNode(id, eval(e));
        variables.add(var1);
    }

    public void parsePrint() {
        ExprNode e = parseExpression();
        int res = eval(e);
        System.out.printf("%x \n", res);
        //System.out.println(variables);
    }


    public static int eval(ExprNode node) {
        if (node instanceof NumberNode) {
            NumberNode num = (NumberNode) node;
            return Integer.parseInt(num.number.text, 16);
        } else if (node instanceof BinOpNode) {
            BinOpNode binOp = (BinOpNode) node;
            int l = eval(binOp.left);
            int r = eval(binOp.right);
            switch (binOp.op.type) {
            case ADD: return l + r;
            case SUB: return l - r;
            case MUL: return l * r;
            case DIV: return l / r;
            }
        } else if (node instanceof VarNode) {
            VarNode var = (VarNode) node;
            //System.out.println(var.value);
            if (exists(var.id)==null) {
                System.out.println("Введите значение " + var.id.text + ":");
                String line = new Scanner(System.in).nextLine();
                var.value = Integer.parseInt(line, 16);
                variables.add(var);
            }
            else
                var = exists(var.id);
            int val = var.value;
            return val;
        }
        throw new IllegalStateException();
    }

    public static void main(String[] args) {
        String text = "a := 0f + 0a;" +
                "print a;" +
                "print a + 5;" +
                "b := 0f;" +
                "print a+b;";

        Lexer l = new Lexer(text);
        List<Token> tokens = l.lex();
        tokens.removeIf(t -> t.type == TokenType.SPACE);

        Parser p = new Parser(tokens);
//        ExprNode node = p.parseExpression();
//
//        int result = eval(node);
//        System.out.printf("%x", result);
        p.parseLine();
    }
}
