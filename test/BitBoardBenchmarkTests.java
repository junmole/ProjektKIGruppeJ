public class BitBoardBenchmarkTests {
    public static void main(String[] args) {

        BitBoard.importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        alphaBetaTest("Start Game",2);
        alphaBetaTest("Start Game",3);
        alphaBetaTest("Start Game",4);

        BitBoard.importFEN("2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0 r");
        alphaBetaTest("Mid Game",2);
        alphaBetaTest("Mid Game",3);
        alphaBetaTest("Mid Game",4);

        BitBoard.importFEN("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b");
        alphaBetaTest("End Game",2);
        alphaBetaTest("End Game",3);
        alphaBetaTest("End Game",4);

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
}



