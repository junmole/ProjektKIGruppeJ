import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class EvolutionaryAlgorithm {

    static final int NUM_GENERATIONS = 30;
    static final int POPULATION_SIZE = 10;
    static final int NUM_WEIGHTS = 6;
    static final double MUTATION_RATE = 0.1;
    static final Random RANDOM = new Random();
    static final List<String> START_POSITIONS = List.of(
            "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
    static int[] score = new int[2];

    //Vorgehen für Startpositions:
    //einmal mit 20 Generations, und 100 population size
    //dort die Startstellung mit testen
    //dann andere Stellungen mit diesen Werten testen, um zu sehen, ob sie ausgeglichen sind
    //insgesamt 10 ausgeglichene Stellungen genommen
    //3 mit Vorteil
    //und 5 mit Gewinnstellung

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        List<double[]> population = initializePopulation();

        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            List<Integer> fitnesses = evaluatePopulation(population);
            List<double[]> newPopulation = new ArrayList<>();

            while (newPopulation.size() < POPULATION_SIZE) {
                double[] parent1 = select(population, fitnesses);
                double[] parent2 = select(population, fitnesses);

                double[] child1 = mutate(crossover(parent1, parent2));
                double[] child2 = mutate(crossover(parent1, parent2));

                newPopulation.add(child1);
                newPopulation.add(child2);
            }

            population = newPopulation;
            System.out.println("Generation " + generation + " best fitness: " + Collections.max(fitnesses));
            System.out.println("Best individual: ");
            double[] bestIndividual = population.get(0);
            for (double weight : bestIndividual) {
                System.out.print(weight + " ");
            }
        }

        double[] bestIndividual = population.get(0);
        System.out.println("Best individual: ");
        for (double weight : bestIndividual) {
            System.out.print(weight + " ");
        }
        long endTime = System.nanoTime();
//        System.out.println("Laufzeit: " + (endTime - startTime));
    }

    static List<double[]> initializePopulation() {
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < EvolutionaryAlgorithm.POPULATION_SIZE; i++) {
            double[] individual = new double[EvolutionaryAlgorithm.NUM_WEIGHTS];
            for (int j = 0; j < EvolutionaryAlgorithm.NUM_WEIGHTS; j++) {
                individual[j] = RANDOM.nextInt(101); // Random values between -1 and 1
            }
            population.add(individual);
        }
        return population;
    }

    static List<Integer> evaluatePopulation(List<double[]> population) {
        int populationSize = population.size();
        List<Integer> fitnesses = new ArrayList<>(Collections.nCopies(populationSize, 0));

        for (int i = 0; i < populationSize; i++) {
            for (int j = i+1; j < populationSize; j++) {
//                System.out.println("play: " + i + " against " + j);
                playGames(population.get(i), population.get(j));
                fitnesses.set(i, fitnesses.get(i) + score[0]);
                fitnesses.set(j, fitnesses.get(j) + score[1]);
                score[0] = 0;
                score[1] = 0;
            }
        }
        return fitnesses;
    }

    //TODO: draw implementieren
    static void playGames(double[] individual1, double[] individual2) {
        for (String startPosition : START_POSITIONS) {
            playGame(individual1, individual2, startPosition, true); // Individual1 as blue
            playGame(individual2, individual1, startPosition, false); // Individual1 as red
        }
    }

    static void playGame(double[] blue, double[] red, String startPosition, boolean isBlue) {
        EvolutionaryBitBoard.startGame(startPosition, blue, red);

        if(BitBoard.draw){
            score[0] += 1;
            score[1] += 1;
//            System.out.println("draw");
        } else if (BitBoard.blueWon) {
//            System.out.println("blue won");
            if (isBlue) {
                score[0] += 2;
                // Blue wins and blue is the current player
            } else {
                score[1] += 2;
                // Blue wins and red is the current player
            }
        } else {
//            System.out.println("red won");
            if (isBlue) {
                score[1] += 2;
                // Red wins and blue is the current player
            } else {
                score[0] += 2;
                // Red wins and red is the current player
            }
        }
    }

    static double[] select(List<double[]> population, List<Integer> fitnesses) {
        int totalFitness = fitnesses.stream().mapToInt(Integer::intValue).sum();
        if (totalFitness <= 0) {
            // If total fitness is 0 or less, return a random individual
            return population.get(RANDOM.nextInt(population.size()));
        }

        int randomValue = RANDOM.nextInt(totalFitness);

        int cumulativeFitness = 0;
        for (int i = 0; i < population.size(); i++) {
            cumulativeFitness += fitnesses.get(i);
            if (cumulativeFitness >= randomValue) {
                return population.get(i);
            }
        }
        return population.get(population.size() - 1); // Should not reach here
    }

    static double[] crossover(double[] parent1, double[] parent2) {
        double[] child = new double[parent1.length];
        for (int i = 0; i < parent1.length; i++) {
            child[i] = Math.round((parent1[i] + parent2[i]) / 2); // Average and round to nearest integer
        }
        return child;
    }

    static double[] mutate(double[] individual) {
        double[] mutated = new double[individual.length];
        for (int i = 0; i < individual.length; i++) {
            if (RANDOM.nextDouble() < MUTATION_RATE) {
                int mutation = RANDOM.nextInt(11) - 5; // Random mutation between -5 and 5
                mutated[i] = Math.min(50, Math.max(0, individual[i] + mutation)); // Ensure the value stays between 0 and 50
            } else {
                mutated[i] = individual[i];
            }
        }
        return mutated;
    }

}
