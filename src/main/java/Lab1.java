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
    int iterationCount = 1;

    // Фактический ответ
    double functionMin;
    double functionMinPosX;

    // Для золотого сечения:
    private static double GOLDEN_EPSILON = 0.618;
    double x1=0;
    double fX1=0;
    double x2=0;
    double fX2=0;

    // Для фибоначчи
    double epsilonFib;
    int ITER_COUNTS = 20;

    boolean inited = false;

    private double function(double x) {
        countCalls++;
        return Math.tanh(Math.pow(Math.abs(x - 2), 3));
    }

    private void initGoldRation(){
        if (!inited){
            x1 = b - (b - a) * GOLDEN_EPSILON;
            fX1 = function(x1);
            x2 = a + (b - a) * GOLDEN_EPSILON;
            fX2 = function(x2);
            inited = true;
        }
    }
    private void initFibonacci(){
        if (!inited){
            x1 = b - calcLN();
            fX1 = function(x1);
            x2 = a + calcLN();
            fX2 = function(x2);
            inited = true;
            MAX_ITERATIONS = ITER_COUNTS;
        }
    }

    private void calcEpsilonFibonacci(){
        epsilonFib = fibonacci(ITER_COUNTS)/(MAX_X - MIN_X);
    }

    private double calcLN() {
        calcEpsilonFibonacci();
        return  (MAX_X - MIN_X) / fibonacci(ITER_COUNTS) + (double) fibonacci(ITER_COUNTS - 2) /fibonacci(ITER_COUNTS)*epsilonFib;
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
        initGoldRation();
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

    public Consumer<String> findNewABFibonacci() {
        initFibonacci();
        return s -> {
            int k = ITER_COUNTS - iterationCount;
            double LK = fibonacci(k+1)*calcLN()-fibonacci(k-1)*epsilonFib;
            if (fX1 - fX2 < 0) {
                functionMin = fX1;
                functionMinPosX = x1;

                b = x2;
                x2 = x1;
                fX2 = fX1;
                x1 = b - LK;
                fX1 = function(x1);
            } else {
                functionMin = fX2;
                functionMinPosX = x2;

                a = x1;
                x1 = x2;
                fX1 = fX2;
                x2 = a + LK;
                fX2 = function(x2);
            }
        };
    }

    public static long fibonacci(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        if (n == 0 || n == 1) {
            return 1;
        }

        long prev = 1;  // F0
        long current = 1; // F1
        for (int i = 2; i <= n; i++) {
            long next = prev + current;
            prev = current;
            current = next;
        }
        return current;
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
        //lab.findMinOfFunc(lab.findNewABGoldenRation());
        lab.findMinOfFunc(lab.findNewABFibonacci());
    }
}