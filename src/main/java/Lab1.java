public class Lab1 {
    // Метод дихотомии, вар 16
    final static double MIN_X = -3;
    final static double MAX_X = 5;
    private static double STEP_EPSILON = 1E-5; //Шаг
    private static double PRECISIONX = STEP_EPSILON *10; //Точность
    // Важно, что шаг меньше, чем точность (чтобы не перешагивать лишнего)

    // Общее
    double a = MIN_X;
    double b = MAX_X;

    int countCalls = 0;

    // Для дихотомии
    double functionMin;
    double functionMinPosX;

    // Для золотого сечения:
    private static double GOLDEN_EPSILON = 0.618;
    double x1 = b - (b - a)* GOLDEN_EPSILON;
    double fX1 = function(x1);
    double x2 = a + (b - a)* GOLDEN_EPSILON;
    double fX2 = function(x2);

    private double function(double x){
        countCalls++;
        return Math.tanh(Math.pow(Math.abs(x-2),3));
    }

    public int getCountCalls() {
        return countCalls;
    }

    public void findMinOfFuncDichotomy(){
        functionMinPosX = (b - a)/2 + a;
        while (Math.abs(a - functionMinPosX) - PRECISIONX > 0 || Math.abs(b - functionMinPosX) - PRECISIONX > 0) {
            findNewABDichotomy();
            System.out.println("Количество вызовов:" + getCountCalls());
            System.out.println("Минимум функции: " + getCalculatedFunctionMin());
            System.out.println("A: " + a + " B: " + b);
        }
    }

    public void findMinOfFuncGoldenRation(){
        functionMinPosX = x1;
        while (Math.abs(a - functionMinPosX) - PRECISIONX > 0 || Math.abs(b - functionMinPosX) - PRECISIONX > 0) {
            findNewABGoldenRation();
            System.out.println("Количество вызовов:" + getCountCalls());
            System.out.println("Минимум функции: " + getCalculatedFunctionMin());
            System.out.println("A: " + a + " B: " + b);
        }
    }

    private void findNewABDichotomy(){
        double center = (b - a)/2 + a;
        double rightX = center + STEP_EPSILON;
        double leftX = center - STEP_EPSILON;

        double leftY = function(leftX);
        double rightY = function(rightX);
        if (leftY - rightY < 0){
            b = rightX;
            functionMin = leftY;
            functionMinPosX = leftX;
        }
        else {
            a = leftX;
            functionMin = rightY;
            functionMinPosX = rightX;
        }
    }

    private void findNewABGoldenRation(){
        double epsilon;
        if (fX1 - fX2 < 0){
            functionMin = fX1;
            functionMinPosX = x1;

            b = x2;
            epsilon = (b - a)* GOLDEN_EPSILON;
            x2 = x1;
            fX2 = fX1;
            x1 = b - epsilon;
            fX1 = function(x1);
        }
        else {
            functionMin = fX2;
            functionMinPosX = x2;

            a = x1;
            epsilon = (b - a)* GOLDEN_EPSILON;
            x1 = x2;
            fX1 = fX2;
            x2 = a + epsilon;
            fX2 = function(x2);
        }
    }

    public double getCalculatedFunctionMin() {
        return functionMin;
    }

    public double getCalculatedFunctionMinPosX() {
        return functionMinPosX;
    }

    public static void doLab(){
        Lab1 lab = new Lab1();
        lab.findMinOfFuncGoldenRation();
    }
}
