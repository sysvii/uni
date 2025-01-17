package RobotParser.BoolFunc;

import Game.Robot;
import RobotParser.*;
import RobotParser.Types.BooleanLiteral;

import java.lang.reflect.Type;
import java.util.Scanner;

/**
 * Created by drb on 08/05/15.
 */
public class Or implements Expression {
    private final Expression a, b;

    public Or() {
        super();
        a = null; b = null;
    }

    private Or(Expression a, Expression b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Expression parseExpression(Scanner scanner, ProgramStack stack) {
        Parser.require("\\(", "missing \'(\'", scanner);

        Expression a = ProgramExpression.parse(scanner, stack);
        Util.CheckTypeErrorBool(a, scanner);

        Parser.require(",", "missing \',\'", scanner);

        Expression b = ProgramExpression.parse(scanner, stack);
        Util.CheckTypeErrorBool(b, scanner);

        Parser.require("\\)", "missing \')\'", scanner);

        return new Or(a, b);
    }
    @Override
    public ProgramObject evaluate(Robot robot, ProgramStack stack) {
        Boolean ab = Util.castBool(a.evaluate(robot, stack));
        Boolean bb = Util.castBool(a.evaluate(robot, stack));

        return new BooleanLiteral(ab || bb);
    }

    @Override
    public String toString() {
        return "or(" + a + ", " + b + ')';
    }

    @Override
    public Type getType() {
        return BooleanLiteral.class;
    }
}
