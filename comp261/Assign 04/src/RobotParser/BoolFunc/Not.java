package RobotParser.BoolFunc;

import Game.Robot;
import RobotParser.*;
import RobotParser.Types.BooleanLiteral;

import java.lang.reflect.Type;
import java.util.Scanner;

/**
 * Created by drb on 08/05/15.
 */
public class Not implements Expression {
    private final Expression a;

    public Not() {
        super();
        a = null;
    }

    private Not(Expression a) {
        this.a = a;
    }

    @Override
    public Expression parseExpression(Scanner scanner, ProgramStack stack) {
        Parser.require("\\(", "missing \'(\'", scanner);

        Expression a = ProgramExpression.parse(scanner, stack);
        Util.CheckTypeErrorBool(a, scanner);

        Parser.require("\\)", "missing \')\'", scanner);

        return new Not(a);
    }

    @Override
    public ProgramObject evaluate(Robot robot, ProgramStack stack) {
        Boolean ab = Util.castBool(a.evaluate(robot, stack));

        return new BooleanLiteral(!ab);
    }

    @Override
    public String toString() {
        return "not(" + a  + ')';
    }

    @Override
    public Type getType() {
        return BooleanLiteral.class;
    }
}
