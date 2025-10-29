import java.util.function.Consumer;

public class Lab1V2 {
    // Метод дихотомии, вар 16
    final static double MIN_X = -3;
    final static double MAX_X = 5;
    private static double STEP_EPSILON = 1E-4;
    private static double PRECISIONX = STEP_EPSILON * 10;
    private static int MAX_ITERATIONS = 30;

    // Общее состояние
    double a = MIN_X;
    double b = MAX_X;
    int countCalls = 0;
    int iterationCount = 0;

    // Результаты
    double functionMin;
    double functionMinPosX;

    // Для методов оптимизации
    double x1, x2, fX1, fX2;
    boolean initialized = false;

    private double function(double x) {
        countCalls++;
        return Math.tanh(Math.pow(Math.abs(x - 2), 3));
    }

    // ==================== ДИХОТОМИЯ ====================
    public Consumer<String> findNewABDichotomy() {
        return s -> {
            double center = (a + b) / 2;
            double leftX = center - STEP_EPSILON;
            double rightX = center + STEP_EPSILON;

            double leftY = function(leftX);
            double rightY = function(rightX);

            if (leftY < rightY) {
                b = center;
                functionMin = leftY;
                functionMinPosX = leftX;
            } else {
                a = center;
                functionMin = rightY;
                functionMinPosX = rightX;
            }
        };
    }

    // ==================== ЗОЛОТОЕ СЕЧЕНИЕ ====================
    private static final double GOLDEN_RATIO = (Math.sqrt(5) - 1) / 2; // ~0.618
    public Consumer<String> findNewABGoldenSection() {
        if (!initialized) {
            x1 = b - GOLDEN_RATIO * (b - a);
            x2 = a + GOLDEN_RATIO * (b - a);
            fX1 = function(x1);
            fX2 = function(x2);
            initialized = true;
        }

        return s -> {
            if (fX1 < fX2) {
                b = x2;
                functionMin = fX1;
                functionMinPosX = x1;

                x2 = x1;
                fX2 = fX1;
                x1 = b - GOLDEN_RATIO * (b - a);
                fX1 = function(x1);
            } else {
                a = x1;
                functionMin = fX2;
                functionMinPosX = x2;

                x1 = x2;
                fX1 = fX2;
                x2 = a + GOLDEN_RATIO * (b - a);
                fX2 = function(x2);
            }
        };
    }

    // ==================== ФИБОНАЧЧИ ====================
    private int fibonacciIterations = 22;
    private long[] fibSequence;

    double lPrevPrev = MAX_X-MIN_X;
    double lPrev;

    public Consumer<String> findNewABFibonacci() {
        if (!initialized) {
            precomputeFibonacciSequence();


            x1 = b - (double)fibSequence[fibonacciIterations - 2] / fibSequence[fibonacciIterations] * (b - a);
            x2 = a + (double)fibSequence[fibonacciIterations - 2] / fibSequence[fibonacciIterations] * (b - a);

            fX1 = function(x1);
            fX2 = function(x2);

            initialized = true;
            MAX_ITERATIONS = fibonacciIterations;
        }

        return s -> {
            int k = iterationCount + 1; // k начинается с 1

            if (k < fibonacciIterations) {
                if (fX1 < fX2) {
                    b = x2;
                    x2 = x1;
                    fX2 = fX1;

                    x1 = b - (double)fibSequence[fibonacciIterations - k - 1] / fibSequence[fibonacciIterations - k] * (b - a);
                    fX1 = function(x1);

                    functionMin = fX1;
                    functionMinPosX = x1;
                } else {
                    a = x1;
                    x1 = x2;
                    fX1 = fX2;

                    x2 = a + (double)fibSequence[fibonacciIterations - k - 1] / fibSequence[fibonacciIterations - k] * (b - a);
                    fX2 = function(x2);

                    functionMin = fX2;
                    functionMinPosX = x2;
                }
            }

            // На последней итерации вычисляем среднюю точку
            if (k == fibonacciIterations) {
                functionMinPosX = (a + b) / 2;
                functionMin = function(functionMinPosX);
            }
        };
    }

    public Consumer<String> findNewABFibonacciV2() {
        if (!initialized) {
            precomputeFibonacciSequence();

            lPrev = fibSequence[fibonacciIterations -1-1]*calcLN() - fibSequence[fibonacciIterations - 3-1]*calcEpsilon();

            x1 = b - calcLI();
            x2 = a + calcLI();

            fX1 = function(x1);
            fX2 = function(x2);


            initialized = true;
            MAX_ITERATIONS = fibonacciIterations;
        }

        return s ->
        {
            if (fX1 < fX2) {
                functionMin = fX1;
                functionMinPosX = x1;

                b = x2;
                x2 = x1;
                fX2 = fX1;
                x1 = b - calcLI();
                fX1 = function(x1);
            } else {
                functionMin = fX2;
                functionMinPosX = x2;

                a = x1;
                x1 = x2;
                fX1 = fX2;
                x2 = a + calcLI();
                fX2 = function(x2);
            }


            System.out.println("li-2: " + lPrevPrev + " li-1: " + lPrev);
            var tmp = lPrev;
            lPrev = calcLI();
            lPrevPrev = tmp;
        };
    }

    private double calcLI(){
        // l(I) = l(i-2) - l(i-1)
        return lPrevPrev - lPrev;
    }

    public boolean checkLN(){
        System.out.println("B-A: " + (b-a));
        System.out.println("LN: " + calcLN());
        return Math.abs(b - a - calcLN()) < 1E-3;
    }

    private double calcLN(){
        return (MAX_X-MIN_X)/fibSequence[fibonacciIterations-1] + (double) fibSequence[fibonacciIterations - 2-1] /fibSequence[fibonacciIterations-1]*calcEpsilon();
    }
    private double calcEpsilon(){
        return (MAX_X-MIN_X)/fibSequence[fibonacciIterations];
    }
    private void precomputeFibonacciSequence() {
        fibSequence = new long[fibonacciIterations + 10];
        fibSequence[0] = 1;
        fibSequence[1] = 1;

        for (int i = 2; i <= fibonacciIterations; i++) {
            fibSequence[i] = fibSequence[i - 1] + fibSequence[i - 2];
        }
    }

    // ==================== ОБЩИЙ МЕТОД ОПТИМИЗАЦИИ ====================
    public void findMinOfFunc(Consumer<String> method) {
        resetState();

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

    public void findMinOfFuncNoPrecision(Consumer<String> method) {
        resetState();

        while (getCountCalls() < MAX_ITERATIONS) {
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

    private void resetState() {
        a = MIN_X;
        b = MAX_X;
        countCalls = 0;
        iterationCount = 0;
        initialized = false;
        functionMin = 0;
        functionMinPosX = 0;
        x1 = x2 = fX1 = fX2 = 0;
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    public double getCalculatedFunctionMin() {
        return functionMin;
    }

    public double getCalculatedFunctionMinPosX() {
        return (a + (b-a)/2);
    }

    public int getCountCalls() {
        return countCalls;
    }

    // ==================== ТЕСТИРОВАНИЕ ====================
    public static void doLab() {
        Lab1V2 lab = new Lab1V2();

//        System.out.println("=== МЕТОД ДИХОТОМИИ ===");
//        lab.findMinOfFunc(lab.findNewABDichotomy());
//        printResults(lab);

//        System.out.println("\n=== МЕТОД ЗОЛОТОГО СЕЧЕНИЯ ===");
//        lab.findMinOfFunc(lab.findNewABGoldenSection());
//        printResults(lab);


        System.out.println("\n=== МЕТОД ФИБОНАЧЧИ2 ===");
        lab.findMinOfFuncNoPrecision(lab.findNewABFibonacciV2());
        printResults(lab);
        if(lab.checkLN()){
            System.out.println("Теоретическое сошлось с практическим");
        }
    }

    private static void printResults(Lab1V2 lab) {
        System.out.println("ФИНАЛЬНЫЙ РЕЗУЛЬТАТ:");
        System.out.println("Минимум: " + lab.getCalculatedFunctionMin() + " в x=" + lab.getCalculatedFunctionMinPosX());
        System.out.println("Всего вызовов функции: " + lab.getCountCalls());
        System.out.println("Финальный интервал: " + (lab.b - lab.a));
    }

    public static void main(String[] args) {
        doLab();
    }
}