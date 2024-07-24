import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EvolutionaryAlgorithmTest {

    @BeforeEach
    void setUp() {
        // Set up any necessary state before each test
        EvolutionaryAlgorithm.score = new int[2];
    }

    @Test
    void testInitializePopulation() {
        List<double[]> population = EvolutionaryAlgorithm.initializePopulation(EvolutionaryAlgorithm.POPULATION_SIZE, EvolutionaryAlgorithm.NUM_WEIGHTS);
        assertEquals(EvolutionaryAlgorithm.POPULATION_SIZE, population.size(), "Population size should match");

        for (double[] individual : population) {
            assertEquals(EvolutionaryAlgorithm.NUM_WEIGHTS, individual.length, "Each individual should have the correct number of weights");
            for (double weight : individual) {
                assertTrue(weight >= 0 && weight <= 100, "Each weight should be within the range 0-100");
            }
        }
    }

    @Test
    void testEvaluatePopulation() {
        List<double[]> population = EvolutionaryAlgorithm.initializePopulation(EvolutionaryAlgorithm.POPULATION_SIZE, EvolutionaryAlgorithm.NUM_WEIGHTS);
        List<Integer> fitnesses = EvolutionaryAlgorithm.evaluatePopulation(population);

        assertEquals(EvolutionaryAlgorithm.POPULATION_SIZE, fitnesses.size(), "Fitness list size should match population size");
        for (int fitness : fitnesses) {
            assertTrue(fitness >= 0, "Fitness should be non-negative");
        }
    }

    @Test
    void testSelect() {
        List<double[]> population = EvolutionaryAlgorithm.initializePopulation(EvolutionaryAlgorithm.POPULATION_SIZE, EvolutionaryAlgorithm.NUM_WEIGHTS);
        List<Integer> fitnesses = EvolutionaryAlgorithm.evaluatePopulation(population);

        double[] selected = EvolutionaryAlgorithm.select(population, fitnesses);
        assertNotNull(selected, "Selected individual should not be null");
        assertEquals(EvolutionaryAlgorithm.NUM_WEIGHTS, selected.length, "Selected individual should have the correct number of weights");
    }

    @Test
    void testCrossover() {
        double[] parent1 = {10, 20, 30, 40, 50, 60};
        double[] parent2 = {60, 50, 40, 30, 20, 10};
        double[] child = EvolutionaryAlgorithm.crossover(parent1, parent2);

        assertNotNull(child, "Child should not be null");
        assertEquals(EvolutionaryAlgorithm.NUM_WEIGHTS, child.length, "Child should have the correct number of weights");
        for (int i = 0; i < child.length; i++) {
            assertEquals((parent1[i] + parent2[i]) / 2, child[i], "Child's weights should be the average of the parents' weights");
        }
    }
}
