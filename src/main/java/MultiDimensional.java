import util.*;
import util.functionalInterfaces.IFunctionND;

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
        int fibonacciIterations = 20; // todo: или вычислить динамически
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



    public static DoubleVector gradientDescend(IFunctionND function, DoubleVector xStart, double eps, int maxIterations) {
        DoubleVector x_i = new DoubleVector(xStart);
        DoubleVector x_i_1 = new DoubleVector(xStart);
        int cntr = 0;
        for (; cntr != maxIterations; cntr++) {
            x_i_1 = DoubleVector.sub(x_i, NumericUtils.computeGradient2ND(x_i));
            x_i_1 = biSect(function, x_i, x_i_1, eps, maxIterations);
            if (DoubleVector.distance(x_i_1, x_i) < 2 * eps) break;
            x_i = x_i_1;
        }

        if (NumericCommon.SHOW_DEBUG_LOG) System.out.printf("gradient descend iterations number : %s\n", cntr + 1);

        return DoubleVector.add(x_i_1, x_i).mul(0.5);
    }

    public static DoubleVector gradientDescend(IFunctionND function, DoubleVector xStart, double eps) {
        return gradientDescend(function, xStart, eps, NumericCommon.ITERATIONS_COUNT_HIGH);
    }

    public static DoubleVector gradientDescend(IFunctionND function, DoubleVector xStart) {
        return gradientDescend(function, xStart, NumericCommon.NUMERIC_ACCURACY_MIDDLE, NumericCommon.ITERATIONS_COUNT_HIGH);
    }

    public static DoubleVector conjGradientDescend(IFunctionND function, DoubleVector xStart, double eps, int maxIterations) {
        DoubleVector x_i = new DoubleVector(xStart);
        DoubleVector x_i_1 = new DoubleVector(xStart);
        DoubleVector s_i = NumericUtils.computeGradient2ND(xStart).mul(-1.0), s_i_1;
        double omega;
        int iteration = 0;
        for (; iteration != maxIterations; iteration++) {
            x_i_1 = DoubleVector.add(x_i, s_i);
            x_i_1 = biSect(function, x_i, x_i_1, eps, maxIterations);
            if (DoubleVector.distance(x_i_1, x_i) < 2 * eps) break;
            s_i_1 = NumericUtils.computeGradient2ND(x_i_1);
            omega = Math.pow((s_i_1).magnitude(), 2) / Math.pow((s_i).magnitude(), 2);
            s_i.mul(omega).sub(s_i_1);
            x_i = x_i_1;
        }

        if (NumericCommon.SHOW_DEBUG_LOG)
            System.out.printf("Conj gradient descend iterations number : %s\n", iteration + 1);
        return DoubleVector.add(x_i_1, x_i).mul(0.5);
    }

    public static DoubleVector conjGradientDescend(IFunctionND function, DoubleVector xStart, double eps) {
        return conjGradientDescend(function, xStart, eps, NumericCommon.ITERATIONS_COUNT_HIGH);
    }

    public static DoubleVector conjGradientDescend(IFunctionND function, DoubleVector xStart) {
        return conjGradientDescend(function, xStart, NumericCommon.NUMERIC_ACCURACY_MIDDLE, NumericCommon.ITERATIONS_COUNT_HIGH);
    }

    public static DoubleVector newtoneRaphson(IFunctionND function, DoubleVector xStart, double eps, int maxIterations) {
        DoubleVector x_i = new DoubleVector(xStart);
        DoubleVector x_i_1 = new DoubleVector(xStart);
        int iteration = -1;

        for (; iteration != maxIterations; iteration++) {
            DoubleMatrix hessian = new DoubleMatrix(2, 2);

            double x0 = x_i.get(0);
            double x1 = x_i.get(1);

            // Элемент H[0,0] = ∂²f/∂x₀²
            double diff0 = x0 - 2;
            double abs0 = Math.abs(diff0);
            if (abs0 > eps) {
                double cube0 = abs0 * abs0 * abs0;  // |x₀-2|³
                double tanh0 = Math.tanh(cube0);
                double sech2_0 = 1 - tanh0 * tanh0;  // 1 - tanh²(|x₀-2|³)
                double h11 = 6 * abs0 * sech2_0 - 18 * Math.pow(diff0, 4) * tanh0 * sech2_0;
                hessian.set(0, 0, h11);
            } else {
                hessian.set(0, 0, 0.0);  // Вблизи x=2
            }

            // Элемент H[1,1] = ∂²f/∂x₁²
            double diff1 = x1 - 2;
            double abs1 = Math.abs(diff1);
            if (abs1 > eps) {
                double cube1 = abs1 * abs1 * abs1;  // |x₁-2|³
                double tanh1 = Math.tanh(cube1);
                double sech2_1 = 1 - tanh1 * tanh1;  // 1 - tanh²(|x₁-2|³)
                double h22 = 6 * abs1 * sech2_1 - 18 * Math.pow(diff1, 4) * tanh1 * sech2_1;
                hessian.set(1, 1, h22);
            } else {
                hessian.set(1, 1, 0.0);  // Вблизи y=2
            }

            // Недиагональные элементы = 0
            hessian.set(0, 1, 0.0);
            hessian.set(1, 0, 0.0);

            // Обратная матрица Гессе
            DoubleMatrix invHessian = DoubleMatrix.invert(hessian);

            if (invHessian == null) {
                if (NumericCommon.SHOW_DEBUG_LOG) {
                    System.out.println("Hessian is singular, assuming minimum found");
                }
                return x_i;  // Возвращаем текущую точку как результат
            }

            // Градиент (можно тоже аналитически, но оставим численно)
            DoubleVector grad = NumericUtils.computeGradient2ND(x_i);

            // Шаг Ньютона: x_{k+1} = x_k - H^{-1} * ∇f(x_k)
            x_i_1 = DoubleVector.sub(x_i, DoubleMatrix.mul(invHessian, grad));

            // Условие остановки
            double stepNorm = DoubleVector.distance(x_i_1, x_i);
            double gradNorm = grad.magnitude();

            if (stepNorm < eps || gradNorm < eps) {
                break;
            }

            x_i = x_i_1;
        }

        if (NumericCommon.SHOW_DEBUG_LOG) {
            System.out.printf("Newton-Raphson (analytical Hessian) iterations: %s\n", iteration + 1);
        }

        return DoubleVector.add(x_i_1, x_i).mul(0.5);
    }

    public static DoubleVector newtoneRaphson(IFunctionND function, DoubleVector xStart, double eps) {
        return newtoneRaphson(function, xStart, eps, NumericCommon.ITERATIONS_COUNT_LOW);
    }

    public static DoubleVector newtoneRaphson(IFunctionND function, DoubleVector xStart) {
        return newtoneRaphson(function, xStart, NumericCommon.NUMERIC_ACCURACY_MIDDLE, NumericCommon.ITERATIONS_COUNT_LOW);
    }

    public static DoubleVector external_penalty(IFunctionND function, IFunctionND penaltyF, DoubleVector xStart, DoubleMatrix constraints, double eps, int maxIterations) {
        IFunctionND penalizedFunction = x -> {
            if (x.get(0) < constraints.get(0).get(0) || x.get(1) < constraints.get(1).get(0)
                    || x.get(0) > constraints.get(0).get(1) || x.get(1) > constraints.get(1).get(1)) {
                double mainValue = function.call(x);
                double penaltyValue = penaltyF.call(x);
                return mainValue + 10 * penaltyValue;
            }else return function.call(x);
        };

        return gradientDescend(penalizedFunction, xStart, eps, maxIterations);
    }
    public static DoubleVector external_penalty(IFunctionND function, DoubleVector xStart, double eps) {
        DoubleMatrix constraints = new DoubleMatrix(
                new DoubleVector(2.5,3.0),
                new DoubleVector(2.5,3.0));

        IFunctionND penaltyF = x ->{
            return Math.exp(Math.abs(x.get(0) - (constraints.get(0).get(0)+constraints.get(0).get(1))/2)) //смещаем функцию к середине ограничений
                    + Math.exp(Math.abs(x.get(1) - (constraints.get(1).get(0)+constraints.get(1).get(1))/2));
        };

        return external_penalty(function, penaltyF, xStart, constraints, eps, NumericCommon.ITERATIONS_COUNT_HIGH);
    }
    public static DoubleVector external_penalty(IFunctionND function, DoubleVector xStart) {
        return external_penalty(function, xStart, NumericCommon.NUMERIC_ACCURACY_MIDDLE);
    }

    public static DoubleVector internal_penalty(IFunctionND function, IFunctionND penaltyF, DoubleVector xStart, DoubleMatrix constraints, double eps, int maxIterations) {
        IFunctionND penalizedFunction = x -> {
            double mainValue = function.call(x);
            double penaltyValue = penaltyF.call(x);
            return mainValue + 10 * penaltyValue;
        };

        return gradientDescend(penalizedFunction, xStart, eps, maxIterations);
    }
    public static DoubleVector internal_penalty(IFunctionND function, DoubleVector xStart, double eps) {
        DoubleMatrix constraints = new DoubleMatrix(
                new DoubleVector(1.5,3.0), //xmin xmax
                new DoubleVector(1.5,3.0));//ymin ymax
        IFunctionND penaltyF = x ->{
            return Math.pow(x.get(0) - (constraints.get(0).get(0)+constraints.get(0).get(1))/2, 4)//смещаем функцию к середине ограничений
                    + Math.pow(x.get(1) - (constraints.get(1).get(0)+constraints.get(1).get(1))/2, 4);
        };



        return internal_penalty(function, penaltyF, xStart, constraints, eps, NumericCommon.ITERATIONS_COUNT_HIGH);
    }
    public static DoubleVector internal_penalty(IFunctionND function, DoubleVector xStart) {
        return internal_penalty(function, xStart, NumericCommon.NUMERIC_ACCURACY_MIDDLE);
    }

    /**
     * Генетический алгоритм оптимизации
     *
     * @param function Целевая функция
     * @param xBounds Границы по X [min, max]
     * @param yBounds Границы по Y [min, max]
     * @param populationSize Размер популяции
     * @param maxIterations Максимальное число итераций
     * @return Найденная точка минимума
     */
    public static DoubleVector geneticAlgorithm(
            IFunctionND function,
            DoubleVector xBounds,
            DoubleVector yBounds,
            int populationSize,
            int maxIterations) {

        return GeneticAlgorithm.geneticAlgorithm(
                function, xBounds, yBounds,
                populationSize, maxIterations
        );
    }

    /**
     * Упрощенная версия генетического алгоритма
     */
    public static DoubleVector geneticAlgorithm(
            IFunctionND function,
            DoubleVector xBounds,
            DoubleVector yBounds) {

        return geneticAlgorithm(function, xBounds, yBounds, 50, NumericCommon.ITERATIONS_COUNT_LOW);
    }

    /**
     * Генетический алгоритм со стандартными границами
     */
    public static DoubleVector geneticAlgorithm(IFunctionND function) {
        return geneticAlgorithm(
                function,
                new DoubleVector(-10.0, 10.0),
                new DoubleVector(-10.0, 10.0)
        );
    }

    /**
     * Метод Хука-Дживса (Hooke-Jeeves) для минимизации функции
     *
     * @param function Целевая функция
     * @param startPoint Начальная точка поиска
     * @param initialStep Начальный размер шага
     * @param stepReduction Коэффициент уменьшения шага (alpha, обычно 0.5)
     * @param patternStepFactor Коэффициент увеличения шага для паттерн-хода (beta, обычно 2.0)
     * @param eps Точность остановки
     * @param maxIterations Максимальное число итераций
     * @return Найденная точка минимума
     */
    public static DoubleVector hookeJeeves(
            IFunctionND function,
            DoubleVector startPoint,
            double initialStep,
            double stepReduction,
            double patternStepFactor,
            double eps,
            int maxIterations) {

        int dimension = startPoint.size();
        DoubleVector currentPoint = new DoubleVector(startPoint);
        DoubleVector basePoint = new DoubleVector(startPoint);

        double currentValue = function.call(currentPoint);
        double baseValue = currentValue;

        double step = initialStep;

        int iteration = 0;
        int functionCalls = 1; // уже посчитали baseValue

        while (iteration < maxIterations && step > eps) {
            iteration++;

            // 1. Исследующий поиск (Exploratory Move)
            DoubleVector explorePoint = new DoubleVector(basePoint);
            double exploreValue = baseValue;

            for (int i = 0; i < dimension; i++) {
                // Пробуем шаг в положительном направлении
                DoubleVector tryPoint = new DoubleVector(explorePoint);
                tryPoint.set(i, tryPoint.get(i) + step);
                double tryValue = function.call(tryPoint);
                functionCalls++;

                if (tryValue < exploreValue) {
                    explorePoint = tryPoint;
                    exploreValue = tryValue;
                    continue; // Успешный шаг, переходим к следующей координате
                }

                // Пробуем шаг в отрицательном направлении
                tryPoint = new DoubleVector(explorePoint);
                tryPoint.set(i, tryPoint.get(i) - step);
                tryValue = function.call(tryPoint);
                functionCalls++;

                if (tryValue < exploreValue) {
                    explorePoint = tryPoint;
                    exploreValue = tryValue;
                }
                // Если оба шага неудачны, остаемся на месте
            }

            // 2. Проверяем успешность исследующего поиска
            if (exploreValue < baseValue) {
                // 3. Паттерн-ход (Pattern Move)
                DoubleVector direction = DoubleVector.sub(explorePoint, basePoint);
                DoubleVector patternPoint = DoubleVector.add(explorePoint,
                        DoubleVector.mul(direction, patternStepFactor));

                double patternValue = function.call(patternPoint);
                functionCalls++;

                // 4. Проверяем успешность паттерн-хода
                if (patternValue < exploreValue) {
                    // Успешный паттерн-ход
                    basePoint = patternPoint;
                    baseValue = patternValue;
                    currentPoint = patternPoint;
                    currentValue = patternValue;
                } else {
                    // Паттерн-ход неудачен, используем точку из исследующего поиска
                    basePoint = explorePoint;
                    baseValue = exploreValue;
                    currentPoint = explorePoint;
                    currentValue = exploreValue;
                }
            } else {
                // Исследующий поиск неудачен - уменьшаем шаг
                step *= stepReduction;
            }
        }

        if (NumericCommon.SHOW_DEBUG_LOG) {
            System.out.printf("Hooke-Jeeves iterations: %s\n", iteration);
            System.out.printf("Function calls: %s\n", functionCalls);
            System.out.printf("Final step size: %.6f\n", step);
        }

        return currentPoint;
    }

    /**
     * Упрощенная версия метода Хука-Дживса с параметрами по умолчанию
     */
    public static DoubleVector hookeJeeves(
            IFunctionND function,
            DoubleVector startPoint,
            double eps) {

        return hookeJeeves(
                function,
                startPoint,
                1.0,           // initialStep
                0.5,           // stepReduction
                2.0,           // patternStepFactor
                eps,
                NumericCommon.ITERATIONS_COUNT_HIGH
        );
    }

    /**
     * Метод Хука-Дживса с параметрами по умолчанию
     */
    public static DoubleVector hookeJeeves(
            IFunctionND function,
            DoubleVector startPoint) {

        return hookeJeeves(
                function,
                startPoint,
                NumericCommon.NUMERIC_ACCURACY_MIDDLE
        );
    }
}
