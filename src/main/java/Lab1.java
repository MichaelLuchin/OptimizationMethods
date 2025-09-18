public class Lab1 {
    // Метод дихотомии, вар 16
    private static double GOLDENEPSILON = 0.618;
    private static double EPSILON = 1E-6; //Шаг
    private static double PRECISIONX = EPSILON*10; //Точность
    // Важно, что шаг меньше, чем точность (чтобы не перешагивать лишнего)



    final static double MINX = -3;
    final static double MAXX = 5;

    double a = MINX;
    double b = MAXX;

    double functionMin = function(b - (b - a)*GOLDENEPSILON);
    double functionMinPosX = b - (b - a)*GOLDENEPSILON;
    double prevXMax = a;
    boolean isA = true;


    int countCalls = 0;

    private double function(double x){
        countCalls++;
        return Math.tanh(Math.pow(Math.abs(x-2),3));
    }

    public int getCountCalls() {
        return countCalls;
    }

    public void findMinOfFuncDichotomy(){
        while (Math.abs(a - functionMinPosX) - PRECISIONX > 0 || Math.abs(b - functionMinPosX) - PRECISIONX > 0) {
            findNewABDichotomy();
            System.out.println("Количество вызовов:" + getCountCalls());
            System.out.println("Минимум функции: " + getCalculatedFunctionMin());
            System.out.println("A: " + a + " B: " + b);
        }
    }

    public void findMinOfFuncGoldenRation(){
        while (Math.abs(a - functionMinPosX) - PRECISIONX > 0 || Math.abs(b - functionMinPosX) - PRECISIONX > 0) {
            findNewABGoldenRation();
            System.out.println("Количество вызовов:" + getCountCalls());
            System.out.println("Минимум функции: " + getCalculatedFunctionMin());
            System.out.println("A: " + a + " B: " + b);
        }
    }

    private void findNewABDichotomy(){
        double center = (b - a)/2 + a;
        double rightX = center + EPSILON;
        double leftX = center - EPSILON;

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
        double epsilon = (b - a)*GOLDENEPSILON;

        double rightX;
        double leftX;
        double leftY;
        double rightY;

        if (isA){
            leftX = b - epsilon;
            rightX = prevXMax + epsilon;
            leftY = functionMin;
            rightY = function(rightX);
        }
        else{
            rightX = prevXMax;
            leftX = b - epsilon;
            rightY = functionMin;
            leftY = function(leftX);
        }

        if (leftY - rightY < 0){
            b = rightX;
            functionMin = leftY;
            functionMinPosX = leftX;
            isA = false;
        }
        else {
            a = leftX;
            functionMin = rightY;
            functionMinPosX = rightX;
            isA = true;
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
