package RobotParser.Sensor;

import Game.Robot;
import RobotParser.Expression;
import RobotParser.ProgramExpression;
import RobotParser.ProgramStack;
import RobotParser.Util;

import java.lang.reflect.Type;
import java.util.Scanner;

/**
 * Created by drb on 06/05/15.
 */
public class BarrelLR implements Expression {
    private final Expression expression;

    public BarrelLR() {
        expression = null;
    }
    private BarrelLR(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object evaluate(Robot robot, ProgramStack stack) {
        if (expression == null) {
            return robot.getBarrelFB(0);
        } else {
            Integer Int = Util.castInt(expression.evaluate(robot, stack));
            return robot.getBarrelFB(Int.intValue());
        }
    }

    @Override
    public Expression parseExpression(Scanner scanner, ProgramStack stack) {
        Expression n = null;
        if (ProgramExpression.isNext(scanner)) {
            n = ProgramExpression.parse(scanner, stack);
            Util.CheckTypeError(Integer.class, n, scanner);
        }
        return new BarrelLR(n);
    }

    @Override
    public String toString() {
        return "barrelLR " + expression;
    }

    @Override
    public Type getType() {
        return Integer.class;
    }
}
