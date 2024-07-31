import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class implements an evolutionary algorithm to optimize a set of weights for the jumpBoard AI.
 * The algorithm evolves a population of weight vectors over several generations, selecting, crossing over,
 * and mutating individuals to find an optimal solution.
 */
class EvolutionaryAlgorithm {

    // Constants defining the configuration of the evolutionary algorithm
    static final int NUM_GENERATIONS = 2;
    static final int POPULATION_SIZE = 2;
    static final int NUM_WEIGHTS = 6;
    static final double MUTATION_RATE = 0.1;
    static final Random RANDOM = new Random();
    static final String startgame = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
    static final List<String> START_POSITIONS = List.of(
            "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b",
            "b02b01b0/b02b01b02/3b02b01/b01b05/5r01r0/1r01r0r03/2rr3r01/r03r01 b",
            "b0b01b0b0b0/1b0b02b0b01/3b0b03/2b05/3r01r02/2r05/1r01rr1r0r01/r0r02r0r0 b",
            "1b04/1b01rb3b0/3bb1bbbb1/8/1r06/3r01r02/2r01r01r01/1rr1rrr01 r",
            "6/1bbbbbbbbbbbb1/8/8/8/1r0r0r0r0r0r01/8/r0r0r0r0r0r0 b",
            "3b02/2bb2b02/5b0bb1/2r0b04/2rb1br1b01/1rr1rr2r01/5r02/2r02rr b",
            "b0b01b01b0/2b0bbb0bb1b0/8/1b06/8/2r02r02/1r01r0rr1r01/r0r0r02rr b",
            "1b0b0b0b01/1b0b0b0b0b0b01/8/4r0b02/2b03r01/3r04/1r0rr1r0r02/r01r0r01r0 b",
            "6/6b01/3b0b0b02/b0bbbb1b0b02/8/rr1rrr01r01/2r0r0rrrr2/1r04 b",
            "2b02bb/1bb2b02b0/5bb2/8/1r03r02/6r01/8/r01r01rrr0 b",
            "3b01b0/1b01b04/2b05/3r0r03/1r06/3b04/5r0r01/6 b",
            "2b0b0b0b0/1b06/2b05/8/8/2rr1r01r01/8/3rrr01 r",
            "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b",
            "6/7b0/8/8/1r06/4b03/2rr1rrr02/5r0 b",
            "6/r07/2rr5/r07/2b05/8/b0bb6/6 r");
    static int[] score = new int[2];


    /**
     * The main method to start the evolutionary algorithm.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        //startEvolutionaryAlgorithm();
        /*
        double[] function1 = {39.0, 49.0, 48.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 33.0, 50.0, 33.0}; //3
        double[] function2 = {39.0, 49.0, 48.0, 20.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 33.0, 50.0, 33.0}; //4
        double[] function3 = {39.0, 49.0, 48.0, 0.0, 64.0, 73.0, 46.0, 41.0, 54.0, 60.0, 60.0, 54.0, 33.0, 50.0, 33.0};
        double[] function4 = {39.0, 49.0, 48.0, 5.0, 95.0, 67.5, 37.0, 30.0, 55.0, 60.0, 60.0, 55.0, 33.0, 50.0, 33.0};

        List<double[]> newPopulation = new ArrayList<>();
        newPopulation.add(function1);
        newPopulation.add(function2);
        newPopulation.add(function3);
        newPopulation.add(function4);

        playAgainst(newPopulation);
         */
    }

    /**
     * Starts the evolutionary algorithm, initializing a population and evolving it over a set number of generations.
     * It selects the best individuals based on their fitness scores and performs crossover and mutation to create new generations.
     */
    static void startEvolutionaryAlgorithm(){
        List<double[]> population = initializePopulation(POPULATION_SIZE, NUM_WEIGHTS);

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
            System.out.println("Generation " + generation + " best fitness: " + Collections.max(fitnesses));
            double[] bestIndividual = selectBest(population, fitnesses);
            System.out.println("Best individual: ");
            for (double weight : bestIndividual) {
                System.out.print(weight + " ");
            }
            population = newPopulation;
        }


        double[] bestIndividual = population.getFirst();
        System.out.println("Best individual: ");
        for (double weight : bestIndividual) {
            System.out.print(weight + " ");
        }
    }

    /**
     * Initializes a population with random weight vectors.
     *
     * @param size the size of the population
     * @param numWeights the number of weights in each individual
     * @return a list of weight vectors representing the initial population
     */
    static List<double[]> initializePopulation(int size, int numWeights) {
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double[] individual = new double[numWeights];
            for (int j = 0; j < numWeights; j++) {
                Random rand = new Random();
                int min = 0;
                int max = 100;
                int randomNum = rand.nextInt((max - min) + 1) + min;
                individual[j] = randomNum;
            }
            population.add(individual);
        }
        return population;
    }

    /**
     * Evaluates the fitness of each individual in the population by playing games against each other.
     *
     * @param population the population of individuals
     * @return a list of fitness scores corresponding to each individual in the population
     */
    static List<Integer> evaluatePopulation(List<double[]> population) {
        int populationSize = population.size();
        List<Integer> fitnesses = new ArrayList<>(Collections.nCopies(populationSize, 0));

        for (int i = 0; i < populationSize; i++) {
            for (int j = i+1; j < populationSize; j++) {
                if(i!=j) {
                    //System.out.println("play: " + i + " against " + j);
                    playGames(population.get(i), population.get(j));
                    fitnesses.set(i, fitnesses.get(i) + score[0]);
                    fitnesses.set(j, fitnesses.get(j) + score[1]);
                    score[0] = 0;
                    score[1] = 0;
                }
            }
        }
        return fitnesses;
    }

    /**
     * Plays a series of games between individuals in the population to determine their fitness.
     *
     * @param population the population of individuals
     */
    static void playAgainst(List<double[]> population){
        int populationSize = population.size();
        List<Integer> fitnesses = new ArrayList<>(Collections.nCopies(populationSize, 0));

        for (int i = 0; i < populationSize; i++) {
            for (int j = 0; j < populationSize; j++) {
                if(i!=j) {
                    //System.out.println("play: " + i + " against " + j);
                    playGames(population.get(i), population.get(j));
                    fitnesses.set(i, fitnesses.get(i) + score[0]);
                    fitnesses.set(j, fitnesses.get(j) + score[1]);
                    score[0] = 0;
                    score[1] = 0;
                }
            }
            System.out.println("i: " + i + "fitness: " + fitnesses.get(i));
        }
        for (int i = 0; i < populationSize; i++) {
            System.out.println("function: "+ (i+1) + " fitness: " + fitnesses.get(i));
            for (double weight : population.get(i)) {
                System.out.print(weight + " ");
            }
            System.out.println(" ");
        }
    }

    /**
     * Plays a series of games between two individuals across multiple starting positions and depths.
     *
     * @param individual1 the first individual
     * @param individual2 the second individual
     */
    static void playGames(double[] individual1, double[] individual2) {
        for (int i = 1; i < 7; i++) {
            for (String startPosition : START_POSITIONS) {
                playGame(individual1, individual2, startPosition, true, i); // Individual1 as blue
            }
            //System.out.println("depth: " + i);
        }
/*
        for(int i = 1; i<7; i++) {
            playGame(individual1, individual2, startgame, true, i); // Individual1 as blue
            // System.out.println("depth: " + i + " Startgame");
        }

 */
    }

    /**
     * Simulates a game between two individuals with specified starting positions and depth.
     *
     * @param blue the weights for the blue player
     * @param red the weights for the red player
     * @param startPosition the starting position of the game
     * @param isBlue boolean indicating if the blue player moves first
     * @param depth the depth of search for the game simulation
     */
    static void playGame(double[] blue, double[] red, String startPosition, boolean isBlue, int depth) {
        EvolutionaryBitBoard.startGame(startPosition, blue, red, depth);

        if(BitBoard.draw){
            score[0] += 1;
            score[1] += 1;
        } else if (BitBoard.blueWon) {
            score[0] += 2;

        } else {
            score[1] += 2;
        }
    }

    /**
     * Selects an individual from the population based on their fitness using a roulette wheel selection method.
     *
     * @param population the population of individuals
     * @param fitnesses the fitness scores of the population
     * @return the selected individual
     */
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
        return population.getLast(); // Should not reach here
    }

    /**
     * Selects the best individual from the population based on their fitness.
     *
     * @param population the population of individuals
     * @param fitnesses the fitness scores of the population
     * @return the individual with the highest fitness
     */
    static double[] selectBest(List<double[]> population, List<Integer> fitnesses) {
        int bestFitnessIndex = 0;
        int bestFitness = fitnesses.getFirst();

        for (int i = 1; i < fitnesses.size(); i++) {
            if (fitnesses.get(i) > bestFitness) {
                bestFitness = fitnesses.get(i);
                bestFitnessIndex = i;
            }
        }

        return population.get(bestFitnessIndex);
    }

    /**
     * Performs crossover between two parent individuals to produce a child individual.
     * The child is created by averaging the corresponding weights from both parents.
     *
     * @param parent1 the first parent individual
     * @param parent2 the second parent individual
     * @return the child individual resulting from the crossover
     */
    static double[] crossover(double[] parent1, double[] parent2) {
        double[] child = new double[parent1.length];
        for (int i = 0; i < parent1.length; i++) {
            child[i] = Math.round((parent1[i] + parent2[i]) / 2); // Average and round to nearest integer
        }
        return child;
    }

    /**
     * Mutates an individual by randomly altering its weights.
     * Each weight has a probability defined by MUTATION_RATE to be mutated.
     * The mutation alters the weight by a random value between -5 and 5.
     *
     * @param individual the individual to mutate
     * @return the mutated individual
     */
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
