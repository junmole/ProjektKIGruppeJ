package BitBoardTest;

import BitBoard.BitBoardFigures;
import BitBoard.BitMoves;
import BitBoard.BitBoard;
import BitBoard.BitValueMoves;
/**
 * The BitBoardTest.BitBoardBenchmarkTests class contains methods to benchmark the performance of the Alpha-Beta pruning algorithm
 * in different game states (start, mid, and end game). It evaluates the execution time and the number of states visited.
 */
public class BitBoardBenchmarkTests {

    /**
     * The main method is the entry point of the benchmark tests.
     * It sets up different game states and runs the alpha-beta tests for various depths.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        BitBoard.importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        MoveGeneratorTest("Start Game");

        BitBoard.importFEN("2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0 r");
        MoveGeneratorTest("Mid Game");

        BitBoard.importFEN("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b");
        MoveGeneratorTest("End Game");

        BitBoard.importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        //da um alles erstmal zu initialisieren, weil sonst das Erste Ergebnis verfälscht werden würde
        BitBoard.alphaBetaBenchmark(true, 1);
        alphaBetaTest("Start Game",3);
        alphaBetaTest("Start Game",5);
        alphaBetaTest("Start Game",7);

        BitBoard.importFEN("2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0 r");
        alphaBetaTest("Mid Game",3);
        alphaBetaTest("Mid Game",5);
        alphaBetaTest("Mid Game",7);

        BitBoard.importFEN("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b");
        alphaBetaTest("End Game",3);
        alphaBetaTest("End Game",5);
        alphaBetaTest("End Game",7);


        BitBoard.importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        alphaBetaWithTranspositionTableTest("Start Game",3);
        alphaBetaWithTranspositionTableTest("Start Game",5);
        alphaBetaWithTranspositionTableTest("Start Game",7);

        BitBoard.importFEN("2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0 r");
        alphaBetaWithTranspositionTableTest("Mid Game",3);
        alphaBetaWithTranspositionTableTest("Mid Game",5);
        alphaBetaWithTranspositionTableTest("Mid Game",7);

        BitBoard.importFEN("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b");
        alphaBetaWithTranspositionTableTest("End Game",3);
        alphaBetaWithTranspositionTableTest("End Game",5);
        alphaBetaWithTranspositionTableTest("End Game",7);

    }

    private static void alphaBetaWithTranspositionTableTest(String gamePosition, int depth) {
        BitBoard.counter = 0;

        long startTime = System.nanoTime();

        boolean isMax = BitBoardFigures.blueToMove;

        BitMoves.initZobristTable();

        BitValueMoves vm = BitBoard.alphaBetaTranspositionBenchmark(isMax, depth);

        long stopTime = System.nanoTime();

        float totalTime = (float) (stopTime - startTime) / (1000000);
        System.out.println("Total time for AlphaBeta with Transposition Table for Depth " + depth + " in " + gamePosition + ": " + totalTime + " ms\n" + "Number of visited states in total: " + BitBoard.counter + "\n"
                + "Number of visited states pro ms: " + BitBoard.counter / (totalTime) + "\n" + "Best move: " + vm.move + "\n");
    }

    /**
     * Runs the Alpha-Beta pruning algorithm on a given game position at a specified search depth.
     * Measures the execution time and the number of visited states.
     *
     * @param gamePosition the description of the game position (e.g., "Start Game", "Mid-Game", "End Game")
     * @param depth the depth of the search tree for the Alpha-Beta algorithm
     */
    private static void alphaBetaTest(String gamePosition, int depth) {
        BitBoard.counter = 0;

        long startTime = System.nanoTime();

        boolean isMax = BitBoardFigures.blueToMove;

        BitValueMoves vm = BitBoard.alphaBetaBenchmark(isMax, depth);

        long stopTime = System.nanoTime();

        float totalTime = (float) (stopTime - startTime) / (1000000);
        System.out.println("Total time for AlphaBeta for Depth " + depth + " in " + gamePosition + ": " + totalTime + " ms\n" + "Number of visited states in total: " + BitBoard.counter + "\n"
                + "Number of visited states pro ms: " + BitBoard.counter / (totalTime) + "\n" + "Best move: " + vm.move + "\n");

    }


    private static void MoveGeneratorTest(String gamePosition) {

        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {

            if(BitBoardFigures.blueToMove){
                BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            }
            else{
                BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            }
        }

        long stopTime = System.nanoTime();

        float averageTime = (float) (stopTime - startTime) / (1000000);
        System.out.println("Average time for generating moves for " + gamePosition + ": " + averageTime + " ms");
    }
}

