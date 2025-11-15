import util.DoubleVector;
import util.functionalInterfaces.IFunctionND;
import util.NumericCommon;

public class MultiDimensional {
    public static DoubleVector biSect(IFunctionND function, DoubleVector left, DoubleVector right,
                                      double eps, int maxIterations) {
        DoubleVector lhs = new DoubleVector(left);
        DoubleVector rhs = new DoubleVector(right);
        DoubleVector x_c, dir = DoubleVector.direction(lhs, rhs).mul(eps);
        int iteration = 0;
        for (; iteration != maxIterations && DoubleVector.distance(rhs, lhs) > 2 * eps; iteration++) {
            x_c = DoubleVector.add(rhs, lhs).mul(0.5);
            if (function.call(DoubleVector.add(x_c, dir)) > function.call(DoubleVector.sub(x_c, dir)))
                rhs = x_c;
            else
                lhs = x_c;
        }
        if (NumericCommon.SHOW_ZERO_ORDER_METHODS_DEBUG_LOG) {
            System.out.printf("biSect::function arg range    : %s\n", DoubleVector.distance(rhs, lhs));
            System.out.printf("biSect::function probes count : %s\n", 2 * iteration);
        }
        return DoubleVector.add(rhs, lhs).mul(0.5);
    }

    public static DoubleVector biSect(IFunctionND function, DoubleVector left, DoubleVector right, double eps) {
        return biSect(function, left, right, eps, NumericCommon.ITERATIONS_COUNT_HIGH);
    }

    public static DoubleVector biSect(IFunctionND function, DoubleVector left, DoubleVector right) {
        return biSect(function, left, right, NumericCommon.NUMERIC_ACCURACY_MIDDLE, NumericCommon.ITERATIONS_COUNT_HIGH);
    }

    public static DoubleVector goldenRatio(IFunctionND function, DoubleVector left, DoubleVector right,
                                           double eps, int maxIterations) {
        DoubleVector lhs = new DoubleVector(left);
        DoubleVector rhs = new DoubleVector(right);
        DoubleVector x_l = DoubleVector.sub(rhs, DoubleVector.mul(DoubleVector.sub(rhs, lhs), NumericCommon.PSI));
        DoubleVector x_r = DoubleVector.add(lhs, DoubleVector.mul(DoubleVector.sub(rhs, lhs), NumericCommon.PSI));
        double f_l = function.call(x_l);
        double f_r = function.call(x_r);
        int iteration = 0;
        for (; iteration != maxIterations && DoubleVector.distance(rhs, lhs) > 2 * eps; iteration++) {
            if (f_l > f_r) {
                lhs = x_l;
                x_l = x_r;
                f_l = f_r;
                x_r = DoubleVector.add(lhs, DoubleVector.mul(DoubleVector.sub(rhs, lhs), NumericCommon.PSI));
                f_r = function.call(x_r);
            } else {
                rhs = x_r;
                x_r = x_l;
                f_r = f_l;
                x_l = DoubleVector.sub(rhs, DoubleVector.mul(DoubleVector.sub(rhs, lhs), NumericCommon.PSI));
                f_l = function.call(x_l);
            }
        }
        if (NumericCommon.SHOW_ZERO_ORDER_METHODS_DEBUG_LOG) {
            System.out.printf("goldenRatio::function arg range    : %s\n", DoubleVector.distance(rhs, lhs));
            System.out.printf("goldenRatio::function probes count : %s\n", 2 + iteration);
        }
        return DoubleVector.add(rhs, lhs).mul(0.5);
    }

    public static DoubleVector goldenRatio(IFunctionND function, DoubleVector left, DoubleVector right, double eps) {
        return goldenRatio(function, left, right, eps, NumericCommon.ITERATIONS_COUNT_HIGH);
    }

    public static DoubleVector goldenRatio(IFunctionND f, DoubleVector left, DoubleVector right) {
        return goldenRatio(f, left, right, NumericCommon.NUMERIC_ACCURACY_MIDDLE, NumericCommon.ITERATIONS_COUNT_HIGH);
    }

    public static DoubleVector fibonacci(IFunctionND function, DoubleVector left, DoubleVector right, double eps) {
        DoubleVector a = new DoubleVector(left);
        DoubleVector b = new DoubleVector(right);

        // Предварительное вычисление последовательности Фибоначчи
        int fibonacciIterations = 20; // или вычислить динамически
        long[] fibSequence = precomputeFibonacciSequence(fibonacciIterations);

        // Инициализация длин интервалов
        double L_prev_prev = DoubleVector.distance(b, a);
        double L_prev = (double)fibSequence[fibonacciIterations-1] / fibSequence[fibonacciIterations] * L_prev_prev;

        // Начальные точки
        DoubleVector x1 = DoubleVector.sub(b, DoubleVector.mul(
                DoubleVector.sub(b, a).normalized(), L_prev));
        DoubleVector x2 = DoubleVector.add(a, DoubleVector.mul(
                DoubleVector.sub(b, a).normalized(), L_prev));

        double f_x1 = function.call(x1);
        double f_x2 = function.call(x2);

        for (int i = 0; i < fibonacciIterations - 2; i++) {
            if (f_x1 < f_x2) {
                // Минимум слева - отбрасываем правую часть
                b = new DoubleVector(x2);
                x2 = new DoubleVector(x1);
                f_x2 = f_x1;

                // Вычисляем новую длину интервала
                double L_current = L_prev_prev - L_prev;
                x1 = DoubleVector.sub(b, DoubleVector.mul(
                        DoubleVector.sub(b, a).normalized(), L_current));
                f_x1 = function.call(x1);

                // Обновляем длины интервалов
                L_prev_prev = L_prev;
                L_prev = L_current;
            } else {
                // Минимум справа - отбрасываем левую часть
                a = new DoubleVector(x1);
                x1 = new DoubleVector(x2);
                f_x1 = f_x2;

                // Вычисляем новую длину интервала
                double L_current = L_prev_prev - L_prev;
                x2 = DoubleVector.add(a, DoubleVector.mul(
                        DoubleVector.sub(b, a).normalized(), L_current));
                f_x2 = function.call(x2);

                // Обновляем длины интервалов
                L_prev_prev = L_prev;
                L_prev = L_current;
            }

            // Ранняя остановка если интервал достаточно мал
            if (DoubleVector.distance(b, a) < eps) {
                break;
            }
        }

        if (NumericCommon.SHOW_ZERO_ORDER_METHODS_DEBUG_LOG) {
            System.out.printf("fibonacci::function arg range    : %s\n", DoubleVector.distance(b, a));
            System.out.printf("fibonacci::function probes count : %s\n", 2 + (fibonacciIterations - 2));
        }

        return DoubleVector.add(a, b).mul(0.5);
    }

    private static long[] precomputeFibonacciSequence(int n) {
        long[] fibSequence = new long[n + 1];
        fibSequence[0] = 1;
        fibSequence[1] = 1;

        for (int i = 2; i <= n; i++) {
            fibSequence[i] = fibSequence[i - 1] + fibSequence[i - 2];
        }
        return fibSequence;
    }

    public static DoubleVector fibonacci(IFunctionND function, DoubleVector left, DoubleVector right) {
        return fibonacci(function, left, right, NumericCommon.NUMERIC_ACCURACY_MIDDLE);
    }

    public static DoubleVector perCordDescend(IFunctionND function, DoubleVector xStart, double eps, int maxIterations) {
        DoubleVector x_0 = new DoubleVector(xStart);
        DoubleVector x_1 = new DoubleVector(xStart);
        double step = 1.0;
        double x_i, y_1, y_0;
        int optCoordinatesCount = 0, coordinateId;
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            coordinateId = iteration % x_0.size();

            x_1.set(coordinateId, x_1.get(coordinateId) - eps);
            y_0 = function.call(x_1);

            x_1.set(coordinateId, x_1.get(coordinateId) + 2 * eps);
            y_1 = function.call(x_1);

            x_1.set(coordinateId, x_1.get(coordinateId) - eps);

            x_1.set(coordinateId, y_0 > y_1 ?
                    x_1.get(coordinateId) + step :
                    x_1.get(coordinateId) - step);

            x_i = x_0.get(coordinateId);
            x_1 = biSect(function, x_0, x_1, eps, maxIterations);
            x_0 = new DoubleVector(x_1);

            if (Math.abs(x_1.get(coordinateId) - x_i) < 2 * eps) {
                optCoordinatesCount++;
                if (optCoordinatesCount == x_1.size()) {
                    if (NumericCommon.SHOW_DEBUG_LOG) {
                        System.out.printf("per cord descend iterations number : %s\n", iteration + 1);
                    }
                    return x_0;
                }
                continue;
            }
            optCoordinatesCount = 0;
        }
        if (NumericCommon.SHOW_DEBUG_LOG) System.out.printf("per cord descend iterations number : %s\n", maxIterations);
        return x_0;
    }

    public static DoubleVector perCordDescend(IFunctionND function, DoubleVector xStart, double eps) {
        return perCordDescend(function, xStart, eps, NumericCommon.ITERATIONS_COUNT_HIGH);
    }

    public static DoubleVector perCordDescend(IFunctionND function, DoubleVector xStart) {
        return perCordDescend(function, xStart, NumericCommon.NUMERIC_ACCURACY_MIDDLE, NumericCommon.ITERATIONS_COUNT_HIGH);
    }
}
