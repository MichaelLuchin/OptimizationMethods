import java.util.function.Consumer;

public class Lab1 {
    // Метод дихотомии, вар 16
    final static double MIN_X = -3;
    final static double MAX_X = 5;
    private static double STEP_EPSILON = 1E-10;
    private static double PRECISIONX = STEP_EPSILON * 10;
    private static int MAX_ITERATIONS = 1000; // Защита от бесконечного цикла

    // Общее
    double a = MIN_X;
    double b = MAX_X;
    int countCalls = 0;
    int iterationCount = 0;

    // Для дихотомии
    double functionMin;
    double functionMinPosX;

    // Для золотого сечения:
    private static double GOLDEN_EPSILON = 0.618;
    double x1 = b - (b - a) * GOLDEN_EPSILON;
    double fX1 = function(x1);
    double x2 = a + (b - a) * GOLDEN_EPSILON;
    double fX2 = function(x2);

    private double function(double x) {
        countCalls++;
        return Math.tanh(Math.pow(Math.abs(x - 2), 3));
    }

    public void findMinOfFunc(Consumer<String> method) {
        while (Math.abs(b - a) > PRECISIONX && iterationCount < MAX_ITERATIONS) {
            iterationCount++;
            method.accept("");

            System.out.println("Итерация: " + iterationCount);
            System.out.println("Вызовов функции: " + getCountCalls());
            System.out.println("Интервал: " + (b - a));
            System.out.println("A: " + a + " B: " + b);
            System.out.println("Минимум: " + getCalculatedFunctionMin() + " в x=" + getCalculatedFunctionMinPosX());
            System.out.println("---");
        }

        if (iterationCount >= MAX_ITERATIONS) {
            System.out.println("Достигнуто максимальное количество итераций!");
        }
    }

    // Методы теперь возвращают Consumer<String>
    public Consumer<String> findNewABDichotomy() {
        return s -> {
            double center = (b - a) / 2 + a;
            double rightX = center + STEP_EPSILON;
            double leftX = center - STEP_EPSILON;

            double leftY = function(leftX);
            double rightY = function(rightX);
            if (leftY - rightY < 0) {
                b = rightX;
                functionMin = leftY;
                functionMinPosX = leftX;
            } else {
                a = leftX;
                functionMin = rightY;
                functionMinPosX = rightX;
            }
        };
    }

    public Consumer<String> findNewABGoldenRation() {
        return s -> {
            double epsilon;
            if (fX1 - fX2 < 0) {
                functionMin = fX1;
                functionMinPosX = x1;

                b = x2;
                epsilon = (b - a) * GOLDEN_EPSILON;
                x2 = x1;
                fX2 = fX1;
                x1 = b - epsilon;
                fX1 = function(x1);
            } else {
                functionMin = fX2;
                functionMinPosX = x2;

                a = x1;
                epsilon = (b - a) * GOLDEN_EPSILON;
                x1 = x2;
                fX1 = fX2;
                x2 = a + epsilon;
                fX2 = function(x2);
            }
        };
    }

    public double getCalculatedFunctionMin() {
        return functionMin;
    }

    public double getCalculatedFunctionMinPosX() {
        return functionMinPosX;
    }

    public int getCountCalls() {
        return countCalls;
    }

    public static void doLab() {
        Lab1 lab = new Lab1();

        // Выбираем метод оптимизации
        //lab.findMinOfFunc(lab.findNewABDichotomy());
        lab.findMinOfFunc(lab.findNewABGoldenRation());
    }
}