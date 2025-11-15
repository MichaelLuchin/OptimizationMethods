import util.DoubleVector;
import util.NumericCommon;
import util.NumericUtils;

public class Main {

    public static void main(String[] args) {
        DoubleVector x_0 = new DoubleVector (-2.0, -2.0);
        DoubleVector x_1 = new DoubleVector (5.0, 5.0);
        DoubleVector x   = new DoubleVector (1.0, 1.0 );
        NumericCommon.SHOW_DEBUG_LOG = true;
        NumericCommon.SHOW_ZERO_ORDER_METHODS_DEBUG_LOG = true;
        //System.out.printf("Fibonacci              : %s\n", MultiDimensional.fibonacci(NumericUtils.testFunc2d, x_1, x_0));
        System.out.printf("Per coordinate descend : %s\n", MultiDimensional.perCordDescend     (NumericUtils.testFunc2d, x));
        NumericCommon.SHOW_DEBUG_LOG = false;
        System.out.println("");
        //Lab1.doLab();
    }
}
