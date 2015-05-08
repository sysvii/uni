package RobotParser.BoolFunc;

import Game.Robot;
import RobotParser.*;

import java.lang.reflect.Type;
import java.util.Scanner;

/**
 * Created by drb on 08/05/15.
 */
public class And implements Expression {
    private final Expression a, b;

    public And() {
        super();
        a = null; b = null;
    }

    private And(Expression a, Expression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Expression parseExpression(Scanner scanner, ProgramStack stack) {
        Parser.require("\\(", "missing \'(\'", scanner);

        Expression a = ProgramExpression.parse(scanner, stack);
        Util.CheckTypeError(Boolean.class, a, scanner);

        Parser.require(",", "missing \',\'", scanner);

        Expression b = ProgramExpression.parse(scanner, stack);
        Util.CheckTypeError(Boolean.class, b, scanner);

        Parser.require("\\)", "missing \')\'", scanner);

        return new And(a, b);
    }
    @Override
    public Object evaluate(Robot robot, ProgramStack stack) {
        Boolean ab = Util.castBool(a.evaluate(robot, stack));
        Boolean bb = Util.castBool(a.evaluate(robot, stack));

        return ab && bb;
    }

    @Override
    public String toString() {
        return "and(" + a + ", " + b + ')';
    }

    @Override
    public Type getType() {
        return Boolean.class;
    }
}
