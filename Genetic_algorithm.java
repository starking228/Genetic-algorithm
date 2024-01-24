import java.util.*;

public class Genetic_algorithm {
    // FINALS
    private static final int VARIANT_NUMBER = 9;
    private static final double Y = 0.47;
    private static final double Z = -1.32 * VARIANT_NUMBER;
    /* the number of bits that make up a population individual */
    private static final int MAX_NUMBER_OF_BIT = 10;
    /* the number of individuals in the population */
    private static final int SIZE_OF_POPULATION = 10;
    // VARIABLES
    private static final Random random = new Random();

    /**
     * A method that asks the user to enter the number of generations and asks whether a mutation will be carried out,
     * after which the first population is generated, based on it, the next population is generated using random
     * crossing, and a check is made to see if the new population has become dominant.
     * Conditions for exiting the cycle:
     * 1) The entered number of generations is performed.
     * 2) The population will become too homogeneous (half or more of the same individuals)
     * 3) If two population deterioration's occur in a row.
     */
    public static void main(String[] args) {
        // ARRAY VARIABLES
        String[] bestPopulation = new String[SIZE_OF_POPULATION];
        int[] bestPopulationInt = new int[SIZE_OF_POPULATION];
        int[] bestResultOfFunction = new int[SIZE_OF_POPULATION];
        double[] bestPopulationRange;
        // NEW
        int[] newPopulationInt = new int[SIZE_OF_POPULATION];
        int[] newResultOfFunction = new int[SIZE_OF_POPULATION];
        //for new population
        String[] newPopulation;
        //______________________________________________________________
        int kilkist = askForLoop();
        boolean mutation = askForMutation();
        int countForWorsePopulation = 0;

        generateFirstPopulation(bestPopulation);
        convertToDec(bestPopulation, bestPopulationInt);
        findFunctionResult(bestPopulationInt, bestResultOfFunction);
        double bestSum = findSum(bestResultOfFunction);
        bestPopulationRange = rangeForPercent(findPercent(bestResultOfFunction, bestSum));
        System.out.println("Популяція № 1");
        showPopulation(bestPopulation, bestPopulationInt, bestResultOfFunction);

        for (int index = 0; index < kilkist; index++) {
            System.out.println("Популяція № " + (index + 2));
            if (countForWorsePopulation == 2) {
                System.out.println("Покращення популяції не очікується!");
                break;
            }
            newPopulation = generateNewPopulation(bestPopulation, bestPopulationRange);
            newPopulation = сrossing(newPopulation, mutation);
            convertToDec(newPopulation, newPopulationInt);
            findFunctionResult(newPopulationInt, newResultOfFunction);
            double newSum = findSum(newResultOfFunction);
            showPopulation(newPopulation, newPopulationInt, newResultOfFunction);
            if (newSum < bestSum) {
                countForWorsePopulation++;
                System.out.println("Ця популяція гірша за найкращу!");
            } else {
                System.out.println("Дана популяція стала найкращою");
                bestSum = newSum;
                bestPopulationInt = newPopulationInt;
                bestPopulation = newPopulation;
                bestResultOfFunction = newResultOfFunction;
                bestPopulationRange = rangeForPercent(findPercent(bestResultOfFunction, bestSum));

                if (!checkPopulation(bestPopulation)) {
                    System.out.println("Дана популяція є занадто однорідною (більша половина)");
                    break;
                }
            }
        }
        System.out.println("Найкраща популяція");
        showPopulation(bestPopulation, bestPopulationInt, bestResultOfFunction);
    }

    /**
     * The method that asks the user to enter the number of generations of new populations.
     *
     * @return an integer, the number of generations of new populations.
     */
    private static int askForLoop() {
        System.out.println("Введіть кількість генерацій нових популяцій");
        Scanner scanner = new Scanner(System.in);
        try {
            int answer = scanner.nextInt();
            if (answer > 0) {
                return answer;
            } else {
                System.out.println("Введені некоректні дані!");
                return askForLoop();
            }
        } catch (Exception e) {
            System.out.println("Введені некоректні дані!");
            return askForLoop();
        }
    }

    /**
     * The method that prompts the user to choose whether the mutation will occur.
     *
     * @return a boolean, returns true if mutation will occur, false if otherwise.
     */
    private static boolean askForMutation() {
        System.out.println("Гени будуть мутувати?(Так/Ні)");
        Scanner scanner = new Scanner(System.in);
        String answer = scanner.nextLine();
        if (answer.equalsIgnoreCase("Так")) {
            return true;
        } else if (answer.equalsIgnoreCase("Ні")) {
            return false;
        } else {
            System.out.println("Некоректне введення!");
            return askForMutation();
        }
    }

    /**
     * The method that fills the specified array with a new one just randomly generated population.
     *
     * @param bestPopulation a String[], the string array that will be filled with a new population.
     */
    private static void generateFirstPopulation(String[] bestPopulation) {
        for (int index = 0; index < SIZE_OF_POPULATION; index++) {
            bestPopulation[index] = generateNumber();
        }
    }

    /**
     * The method that randomly generates a population individual in binary number system.
     *
     * @return a String, the string that contains a population individual in binary number system.
     */
    private static String generateNumber() {
        StringBuilder element = new StringBuilder();
        for (int index = 0; index < MAX_NUMBER_OF_BIT; index++) {
            element.append(random.nextInt(2));
        }
        return element.toString();
    }

    /**
     * The method that converts specified population from binary to decimal number system
     * and writes in the specified array.
     *
     * @param bestPopulation    a String[], the string array containing the population in binary to convert.
     * @param bestPopulationInt an int[], the integer array that will store the converted population.
     */
    private static void convertToDec(String[] bestPopulation, int[] bestPopulationInt) {
        for (int index = 0; index < SIZE_OF_POPULATION; index++) {
            int sum = 0;
            String current = bestPopulation[index];
            for (int power = MAX_NUMBER_OF_BIT; power > 0; power--) {
                sum += (int) (Character.getNumericValue(current.charAt(MAX_NUMBER_OF_BIT - power)) * Math.pow(2, power));
            }
            bestPopulationInt[index] = sum;
        }
    }

    /**
     * The method that calculates the value of the function by substituting individuals of the specified population
     * into it.
     *
     * @param bestPopulationInt    an int[], the integer array that store the converted population.
     * @param bestResultOfFunction an int[], the integer array that will store the values of function.
     */
    private static void findFunctionResult(int[] bestPopulationInt, int[] bestResultOfFunction) {
        for (int index = 0; index < SIZE_OF_POPULATION; index++) {
            int x = bestPopulationInt[index];
            bestResultOfFunction[index] = (int) (Math.pow(Math.abs(Y + x), 0.2) / Math.pow(Math.abs(Z), 1.34) + Math.pow(Y - Z, 2) / (1 + Math.pow(Math.sin(Z), 2)) + Math.pow(Math.abs(Z - Y), 3) / Math.abs(Z / Math.cos(x + 2)));
        }
    }

    /**
     * A method that calculates the sum of the results of the function from the specified array.
     *
     * @param bestResultOfFunction an int[], the integer array that store the results of the function.
     * @return a double, the sum of the results of the function.
     */
    private static double findSum(int[] bestResultOfFunction) {
        double suma = 0;
        for (double number : bestResultOfFunction) {
            suma += number;
        }
        return suma;
    }

    /**
     * The method that calculates limits from an array of percentages.
     *
     * @param bestPopulationPresents a double[], a double array of percentages.
     * @return a double[], a double array that contain ranges.
     */
    private static double[] rangeForPercent(double[] bestPopulationPresents) {
        double[] rangeForPercent = new double[SIZE_OF_POPULATION];
        rangeForPercent[0] = bestPopulationPresents[0];
        rangeForPercent[SIZE_OF_POPULATION - 1] = 100;
        for (int index = 1; index < SIZE_OF_POPULATION - 1; index++) {
            rangeForPercent[index] = rangeForPercent[index - 1] + bestPopulationPresents[index];
        }
        return rangeForPercent;
    }

    /**
     * The method that calculates the percentage of each population individual from the total amount.
     *
     * @param bestResultOfFunction an int[], the integer array that store the results of the function.
     * @param bestSum              a double, the sum of the results of the functions of the best population.
     * @return a double[], the array that will contain the percentage of each population individual from the
     * total amount.
     */
    private static double[] findPercent(int[] bestResultOfFunction, double bestSum) {
        double[] bestPopulationPercent = new double[SIZE_OF_POPULATION];
        for (int index = 0; index < SIZE_OF_POPULATION; index++) {
            double result = (bestResultOfFunction[index] / bestSum) * 100;
            bestPopulationPercent[index] = Math.round(result * 100.0) / 100.0;
        }
        return bestPopulationPercent;
    }

    /**
     * The method that outputs the binary and decimal representation and results of function of the specified population.
     *
     * @param bestPopulation       a String[], the sting array that store the population in binary number system.
     * @param bestPopulationInt    an int[], the integer array that store the population in decimal number system.
     * @param bestResultOfFunction an int[], the integer array that store the results of the function.
     */
    private static void showPopulation(String[] bestPopulation, int[] bestPopulationInt, int[] bestResultOfFunction) {
        for (int index = 0; index < SIZE_OF_POPULATION; index++) {
            System.out.println(bestPopulation[index] + "\t" + bestPopulationInt[index] + "\t" + bestResultOfFunction[index]);
        }
    }

    /**
     * The method that generates a new population based on the best one and returns it.
     *
     * @param bestPopulation      a String[], the sting array that store the population in binary number system.
     * @param bestPopulationRange a double[], the double array that store the ranges based on the best population.
     * @return a String[], the new generated population based on the best population.
     */
    private static String[] generateNewPopulation(String[] bestPopulation, double[] bestPopulationRange) {
        String[] newPopulation = new String[SIZE_OF_POPULATION];
        for (int index = 0; index < SIZE_OF_POPULATION; index++) {
            newPopulation[index] = returnValue(random.nextInt(101), bestPopulationRange, bestPopulation);
        }
        return newPopulation;
    }

    /**
     * The method that selects from the best population an individual for a new population using
     * the ranges of the best population.
     *
     * @param number              an integer, the number with which the required limit is selected.
     * @param bestPopulationRange a double[], the double array that store the ranges based on the best population.
     * @param bestPopulation      a String[], the sting array that store the population in binary number system.
     * @return a String, the individual for a new population using.
     */
    private static String returnValue(int number, double[] bestPopulationRange, String[] bestPopulation) {
        for (int index = 0; index < SIZE_OF_POPULATION; index++) {
            if (number <= bestPopulationRange[index]) return bestPopulation[index];
        }
        return null;
    }

    /**
     * The method that randomly crosses pairs of the specified population and, if necessary, eradicates the mutation.
     *
     * @param newPopulation a String[], the sting array that store the new population in binary number system.
     * @param mutation      a boolean, the boolean that indicates whether to cause a mutation.
     * @return a String[], the string array that store new crossed population.
     */
    private static String[] сrossing(String[] newPopulation, boolean mutation) {
        ArrayList<String> pairs = createUniquePairs(newPopulation);
        String[] population = cross(pairs);
        if (mutation) {
            mutation(population);
        }
        return population;
    }

    /**
     * The method that randomly chooses pairs from specified population.
     *
     * @param newPopulation a String[], the sting array that store the new population in binary number system.
     * @return an ArrayList<String>, the ArrayList that contain unique pairs from specified population.
     */
    private static ArrayList<String> createUniquePairs(String[] newPopulation) {
        List<String> population = new ArrayList<>(Arrays.asList(newPopulation));
        ArrayList<String> pairs = new ArrayList<>();
        while (population.size() > 2) {
            pairs.add(population.remove(random.nextInt(population.size())));
        }
        pairs.add(population.get(0));
        pairs.add(population.get(1));
        return pairs;
    }

    /**
     * The method that crosses the specified pairs of population using their random division.
     *
     * @param pairs an ArrayList<String>, the ArrayList that contain unique pairs for crossing.
     * @return a String[], the string array that contains crossed population.
     */
    private static String[] cross(ArrayList<String> pairs) {
        String[] population = new String[SIZE_OF_POPULATION];
        for (int index = 1; index < SIZE_OF_POPULATION; index += 2) {
            int separationIndex = random.nextInt(MAX_NUMBER_OF_BIT - 2) + 1;
            String parent = pairs.get(index - 1);
            String child = pairs.get(index);
            population[index - 1] = parent.substring(0, separationIndex) + child.substring(separationIndex);
            population[index] = child.substring(0, separationIndex) + parent.substring(separationIndex);
        }
        return population;
    }

    /**
     * The method that implements mutation, namely, in a randomly selected individual of the population,
     * changes a random bit to the opposite.
     *
     * @param population a String[], the string array that store population in binary number system for mutation.
     */
    private static void mutation(String[] population) {
        int index = random.nextInt(population.length);
        int bit = random.nextInt(MAX_NUMBER_OF_BIT);
        population[index] = replaceCharacterAtIndex(population[index], bit);
    }

    /**
     * The method that changes the specified bit of the specified individuals of the population on the opposite.
     *
     * @param string a String, the specified individuals of the population for mutation.
     * @param index  an integer, the index of bit that will be mutated.
     * @return a String, the mutated individuals of the population.
     */
    private static String replaceCharacterAtIndex(String string, int index) {
        String replacement = (string.charAt(index) == '1') ? "0" : "1";
        return string.substring(0, index) + replacement + string.substring(index + 1);
    }

    /**
     * The method that tests the population for homogeneity.
     *
     * @param population a String[], the array that store population in binary number system to be tested.
     * @return a boolean, returns false if at least half of the population is the same, returns true if it is not.
     */
    private static boolean checkPopulation(String[] population) {
        for (String element : population) {
            int notUnique = 0;
            for (String s : population) {
                if (element.equals(s)) {
                    notUnique++;
                }
                if (notUnique >= population.length / 2) {
                    return false;
                }
            }
        }
        return true;
    }
}
