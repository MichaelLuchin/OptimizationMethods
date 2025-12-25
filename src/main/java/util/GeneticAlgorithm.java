package util;

import util.functionalInterfaces.IFunctionND;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    private static final Random random = new Random();

    /**
     * Генетический алгоритм для минимизации функции
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

        // 1. Генерация начальной популяции
        List<DoubleVector> population = generateInitialPopulation(
                xBounds, yBounds, populationSize);

        // Счетчик вызовов функции
        int functionCalls = 0;

        // 2. Основной цикл генетического алгоритма
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            // Создаем новое поколение
            List<DoubleVector> newGeneration = new ArrayList<>(population);

            for (int i = 0; i < populationSize; i++) {
                // Выбираем двух случайных родителей
                int parent1Index = random.nextInt(populationSize);
                int parent2Index = random.nextInt(populationSize);

                DoubleVector parent1 = population.get(parent1Index);
                DoubleVector parent2 = population.get(parent2Index);

                // Создаем потомка (мутация)
                DoubleVector child;
                if (random.nextBoolean()) {
                    child = mutation1(parent1, parent2);
                } else {
                    child = mutation2(parent1, parent2, xBounds, yBounds);
                }

                // Вычисляем значения функции для родителей и потомка
                double valueParent1 = function.call(parent1);
                double valueParent2 = function.call(parent2);
                double valueChild = function.call(child);
                functionCalls += 3;

                // Заменяем худшего родителя потомком, если потомок лучше
                if (valueParent1 < valueParent2) {
                    if (valueChild < valueParent1) {
                        newGeneration.set(parent1Index, child);
                    } else if (valueChild < valueParent2) {
                        newGeneration.set(parent2Index, child);
                    }
                } else if (valueParent2 < valueParent1) {
                    if (valueChild < valueParent2) {
                        newGeneration.set(parent2Index, child);
                    } else if (valueChild < valueParent1) {
                        newGeneration.set(parent1Index, child);
                    }
                } else {
                    newGeneration.set(parent1Index, child);
                }
            }

            // Обновляем популяцию
            population = newGeneration;
        }

        // 3. Находим лучшую особь в популяции
        DoubleVector bestSolution = population.get(0);
        double bestValue = function.call(bestSolution);
        functionCalls++;

        for (int i = 1; i < populationSize; i++) {
            double currentValue = function.call(population.get(i));
            functionCalls++;

            if (currentValue < bestValue) {
                bestValue = currentValue;
                bestSolution = population.get(i);
            }
        }

        if (NumericCommon.SHOW_DEBUG_LOG) {
            System.out.printf("Genetic Algorithm iterations: %s\n", maxIterations);
            System.out.printf("Function calls: %s\n", functionCalls);
            System.out.printf("Best value: %.6f\n", bestValue);
        }

        return bestSolution;
    }

    /**
     * Первая мутация: линейная комбинация родителей
     * child = parent1 + (parent2 - parent1) * random[0, 1]
     */
    private static DoubleVector mutation1(DoubleVector parent1, DoubleVector parent2) {
        double alpha = random.nextDouble();

        // Вычисляем difference = parent2 - parent1
        DoubleVector difference = DoubleVector.sub(parent2, parent1);

        // scaledDiff = difference * alpha
        DoubleVector scaledDiff = DoubleVector.mul(difference, alpha);

        // child = parent1 + scaledDiff
        return DoubleVector.add(parent1, scaledDiff);
    }

    /**
     * Вторая мутация: случайная точка в прямоугольнике, определяемом родителями
     */
    private static DoubleVector mutation2(
            DoubleVector parent1,
            DoubleVector parent2,
            DoubleVector xBounds,
            DoubleVector yBounds) {

        // Определяем границы для генерации случайной точки
        double minX = Math.min(parent1.get(0), parent2.get(0));
        double maxX = Math.max(parent1.get(0), parent2.get(0));
        double minY = Math.min(parent1.get(1), parent2.get(1));
        double maxY = Math.max(parent1.get(1), parent2.get(1));

        // Генерируем случайную точку в этих границах
        double x = minX + random.nextDouble() * (maxX - minX);
        double y = minY + random.nextDouble() * (maxY - minY);

        // Ограничиваем точку заданными границами
        x = NumericUtils.clamp(x, xBounds.get(0), xBounds.get(1));
        y = NumericUtils.clamp(y, yBounds.get(0), yBounds.get(1));

        return new DoubleVector(x, y);
    }

    /**
     * Генерация начальной популяции
     */
    private static List<DoubleVector> generateInitialPopulation(
            DoubleVector xBounds,
            DoubleVector yBounds,
            int populationSize) {

        List<DoubleVector> population = new ArrayList<>(populationSize);

        for (int i = 0; i < populationSize; i++) {
            double x = xBounds.get(0) + random.nextDouble() * (xBounds.get(1) - xBounds.get(0));
            double y = yBounds.get(0) + random.nextDouble() * (yBounds.get(1) - yBounds.get(0));
            population.add(new DoubleVector(x, y));
        }

        return population;
    }
}