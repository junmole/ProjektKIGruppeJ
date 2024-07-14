public class BenchmarkTests {
    public static void main(String[] args) {

        Board startGame = new Board();
        startGame.fenToBoard("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0");
        startGame.blueToMove = 1;

        Board midGame = new Board();
        midGame.fenToBoard("2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0");
        midGame.blueToMove = 0;

        Board endGame = new Board();
        endGame.fenToBoard("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6");
        endGame.blueToMove  = 1;

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

        alphaBetaTest("Start Game", startGame, 2);
        alphaBetaTest("Start Game", startGame, 3);
        alphaBetaTest("Start Game", startGame, 4);

        alphaBetaTest("Mid Game", midGame, 2);
        alphaBetaTest("Mid Game", midGame, 3);
        alphaBetaTest("Mid Game", midGame, 4);

        alphaBetaTest("End Game", endGame, 2);
        alphaBetaTest("End Game", endGame, 3);
        alphaBetaTest("End Game", endGame, 4);

    }

    private static void evaluationTestsDepth1(String gamePosition, Board board){
        boolean isMax = board.blueToMove == 1;


        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            Board.miniMax(board, isMax, 1);
        }
        long stopTime = System.nanoTime();

        float averageTime = (float) (stopTime - startTime) / (10000*1000000);
        System.out.println("Average time for Evaluation " + gamePosition + ": " + averageTime + " ms \n"
        + "Best move: " + Board.miniMax(board, true, 1).move + "\n");

    }

    private static void miniMaxTest(String gamePosition, Board board, int depth){
        Board.counter = 0;
        boolean isMax = board.blueToMove == 1;


        long startTime = System.nanoTime();
        Board.miniMax(board, isMax, depth);
        long stopTime = System.nanoTime();

        float totalTime = (float) (stopTime - startTime) / (1000000);
        System.out.println("Total time for Minimax for Depth " + depth + " in " + gamePosition + ": " + totalTime + " ms\n" +"Number of visited states in total: " + Board.counter + "\n" + "Number of visited states pro ms: " + Board.counter/(totalTime) + "\n" +
                "Best move: " + Board.miniMax(board, isMax, depth).move + "\n");

    }

    private static void alphaBetaTest(String gamePosition, Board board, int depth){
        Board.counter = 0;
        boolean isMax = board.blueToMove == 1;


        long startTime = System.nanoTime();
        Board.alphaBeta(board, isMax, depth);
        long stopTime = System.nanoTime();

        float totalTime = (float) (stopTime - startTime) / (1000000);
        System.out.println("Total time for AlphaBeta for Depth " + depth + " in " + gamePosition + ": " + totalTime + " ms\n" +"Number of visited states in total: " + Board.counter + "\n"
                + "Number of visited states pro ms: " + Board.counter/(totalTime) + "\n" + "Best move: " + Board.alphaBeta(board, isMax, depth).move + "\n");

    }


}


