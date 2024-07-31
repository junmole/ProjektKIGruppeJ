public class BitBoardBenchmarkTests {
    public static void main(String[] args) {

/*
        evaluationTestsDepth1("Start Game", startGame);
        evaluationTestsDepth1("Mid Game", midGame);
        evaluationTestsDepth1("End Game", endGame);

        miniMaxTest("Start Game", startGame, 2);
        miniMaxTest("Start Game", startGame, 3);
        miniMaxTest("Start Game", startGame, 4);

        miniMaxTest("Mid Game", midGame, 2);
        miniMaxTest("Mid Game", midGame, 3);
        miniMaxTest("Mid Game", midGame, 4);

        miniMaxTest("End Game", endGame, 2);
        miniMaxTest("End Game", endGame, 3);
        miniMaxTest("End Game", endGame, 4);

 */
        BitBoard.importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        benchmarkTests("Start Game");

        BitBoard.importFEN("2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0 r");
        benchmarkTests("Mid Game");

        BitBoard.importFEN("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b");
        benchmarkTests("End Game");

        BitBoard.importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
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
        alphaBetaWithTranspositiontTableTest("Start Game",3);
        alphaBetaWithTranspositiontTableTest("Start Game",5);
        alphaBetaWithTranspositiontTableTest("Start Game",7);

        BitBoard.importFEN("2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0 r");
        alphaBetaWithTranspositiontTableTest("Mid Game",3);
        alphaBetaWithTranspositiontTableTest("Mid Game",5);
        alphaBetaWithTranspositiontTableTest("Mid Game",7);

        BitBoard.importFEN("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b");
        alphaBetaWithTranspositiontTableTest("End Game",3);
        alphaBetaWithTranspositiontTableTest("End Game",5);
        alphaBetaWithTranspositiontTableTest("End Game",7);

    }

    private static void alphaBetaWithTranspositiontTableTest(String gamePosition, int depth) {
        BitBoard.counter = 0;

        long startTime = System.nanoTime();

        boolean isMax = BitBoardFigures.blueToMove;

        BitValueMoves vm = BitBoard.alphaBetaWithTransposition(isMax);

        long stopTime = System.nanoTime();

        float totalTime = (float) (stopTime - startTime) / (1000000);
        System.out.println("Total time for AlphaBeta with Transposition Table for Depth " + depth + " in " + gamePosition + ": " + totalTime + " ms\n" + "Number of visited states in total: " + BitBoard.counter + "\n"
                + "Number of visited states pro ms: " + BitBoard.counter / (totalTime) + "\n" + "Best move: " + vm.move + "\n");
    }

    private static void alphaBetaTest(String gamePosition, int depth) {
        BitBoard.counter = 0;

        long startTime = System.nanoTime();

        boolean isMax = BitBoardFigures.blueToMove;

        BitValueMoves vm = BitBoard.alphaBeta(isMax);

        long stopTime = System.nanoTime();

        float totalTime = (float) (stopTime - startTime) / (1000000);
        System.out.println("Total time for AlphaBeta for Depth " + depth + " in " + gamePosition + ": " + totalTime + " ms\n" + "Number of visited states in total: " + BitBoard.counter + "\n"
                + "Number of visited states pro ms: " + BitBoard.counter / (totalTime) + "\n" + "Best move: " + vm.move + "\n");

    }


    private static void benchmarkTests(String gamePosition) {

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

