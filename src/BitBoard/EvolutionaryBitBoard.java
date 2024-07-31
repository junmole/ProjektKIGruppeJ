package BitBoard;

import static java.lang.Long.bitCount;

/**
 * The BitBoard.EvolutionaryBitBoard class provides the main methods for managing the state and evaluation of a game using bitboards.
 * It includes functions to evaluate board positions, make and undo moves, and simulate game outcomes using an evolutionary algorithm approach.
 */
public class EvolutionaryBitBoard {
    static double protectionBonusValue;
    static double SingleValue;
    static double DoubleValue;
    static double MixedValue;
    static double SingleDistance;
    static double DoubleDistance;
    static double MixedDistance;
    static double Square2, Square3, Square4, Square5, Square6, Square7,
            Square9, Square10, Square11, Square12, Square13, Square14, Square15, Square16,
            Square17, Square18, Square19, Square20, Square21, Square22, Square23, Square24,
            Square25, Square26, Square27, Square28, Square29, Square30, Square31, Square32,
            Square33, Square34, Square35, Square36, Square37, Square38, Square39, Square40,
            Square41, Square42, Square43, Square44, Square45, Square46, Square47, Square48,
            Square49, Square50, Square51, Square52, Square53, Square54, Square55, Square56,
            Square58, Square59, Square60, Square61, Square62, Square63;


    /**
     * Main method for the BitBoard.EvolutionaryBitBoard class, used for testing and development purposes.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        /* Für Vergleich der Bewertungsfunktionen
        importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        benchmarkEvaluation();
         */
        /*  Zwei Bewertungsfunktion gegeneinander spielen lassen
            for(int i = 1; i<7; i++) {
            System.out.println("depth " + i);
            compareEvaluation("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", true, 5);
            System.out.println(BitMoves.blueWon);
            compareEvaluation("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", false, 5);
            System.out.println(BitMoves.blueWon);
        }
        */

    }

    /**
     * Starts a game from a given FEN string, using specified weights for the blue and red players,
     * and a specified depth for search.
     *
     * @param fen the FEN string representing the starting position
     * @param blue the weight parameters for the blue player
     * @param red the weight parameters for the red player
     * @param depth the search depth for the game simulation
     */
    public static void startGame(String fen, double[] blue, double[] red, int depth){
        BitMoves.gameStateHistory.clear();
        importFEN(fen);

//      System.out.println("Initial evaluation: " + evaluatePositionComplex(0, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue));
        boolean isMax;
        //long startTime = System.nanoTime();
        while (!isGameFinished()) {
            if (BitBoardFigures.blueToMove) {
                setEvaluationValues(blue);
                isMax = true;
            } else {
                setEvaluationValues(red);
                isMax = false;
            }
            BitValueMoves vm = alphaBeta(isMax, depth);

            String move = vm.move;

//         System.out.println("before Board.Move: " + BitMoves.gameStateHistory);
//         System.out.println("Board.Move made: " + moveToString(vm.move) + " on expected eval " + vm.v);
            BitMoves.makeMove(move, true);
//          System.out.println("after Board.Move: " + BitMoves.gameStateHistory);
//          drawArray(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            BitBoardFigures.blueToMove = !BitBoardFigures.blueToMove;
        }
        //long endTime = System.nanoTime();
        //long totalTime = endTime-startTime;
        //System.out.println("Movecounter: "+movecounter);
        //System.out.println("total Time: " + (totalTime/1000000) +"ms");
    }

    /**
     * Sets the evaluation values for game evaluation based on the provided parameters.
     *
     * @param parameters an array containing the evaluation parameters for the game
     */
    static public void setEvaluationValues(double[] parameters){
        SingleValue = parameters[0];
        DoubleValue = parameters[1];
        MixedValue = parameters[2];
        SingleDistance = parameters[3];
        DoubleDistance = parameters[4];
        MixedDistance = parameters[5];

    }

    /**
     * Implements the alpha-beta pruning algorithm to evaluate moves and decide the best move.
     *
     * @param isMax a boolean indicating if the current move is a maximizing move
     * @param depth the depth of the search
     * @return a BitValueMoves object containing the value of the best move and the move itself
     */
    static public BitValueMoves alphaBeta(boolean isMax, int depth) {
        return alphaBetaRecursion(depth, -100000.0f, +100000.0f, isMax);
    }

    /**
     * A version of the alpha-beta pruning algorithm used for comparing evaluation functions.
     * This version also handles different time depths and panic modes.
     *
     * @param isMax a boolean indicating if the current move is a maximizing move
     * @param depth the depth of the search
     * @param evaluation a boolean indicating whether to use the complex evaluation function
     * @return a BitValueMoves object containing the value of the best move and the move itself
     */
    static public BitValueMoves alphaBetaComparison(boolean isMax, int depth, boolean evaluation) {
        BitMoves.moveCounter += 1;
        int timeDepth = 4;
        long startTime = System.currentTimeMillis();
        if(evaluation){
            if(BitMoves.aiRunningTime2 == 120000){
                System.out.println("Simple out of time");
                BitBoard.blueWon = !evaluation;
            }
            else if (BitMoves.aiRunningTime1 > 117000){
                BitValueMoves move = alphaBetaRecursionNormal(2, -100000.0f, +100000.0f, isMax); //panic mode
                long endTime = System.currentTimeMillis();
                BitMoves.aiRunningTime1 += (endTime - startTime);
                return move;
            } else if (BitMoves.aiRunningTime1 > 110000){
                BitValueMoves move = alphaBetaRecursionNormal(4, -100000.0f, +100000.0f, isMax); //slight panic mode
                long endTime = System.currentTimeMillis();
                BitMoves.aiRunningTime1 += (endTime - startTime);
                return move;
            } else if ((BitBoardFigures.blueToMove && BitMoves.blueFigureCount < 8) ||
                    (!BitBoardFigures.blueToMove && BitMoves.redFigureCount < 8)){
                if (BitMoves.aiRunningTime1 > 100000){
                    timeDepth = 9; //~3.5s or better in endgame
                } else {
                    timeDepth = 10; //~13s or better in endgame
                }
            } else {
                timeDepth = 8; //~3.3s on full board
                if(BitMoves.moveCounter < 5){
                    timeDepth = 6; //fast opening
                }
            }
            BitValueMoves move = alphaBetaRecursionNormal(timeDepth, -100000.0f, +100000.0f, isMax);
            long endTime = System.currentTimeMillis();
            BitMoves.aiRunningTime1 += (endTime - startTime);
            return move;

        }else {
            if(BitMoves.aiRunningTime2 == 120000){
                System.out.println("Complex out of time");
                BitBoard.blueWon = !evaluation;
            }
            else if (BitMoves.aiRunningTime2 > 117000){
                BitValueMoves move = alphaBetaRecursionComplex(2, -100000.0f, +100000.0f, isMax); //panic mode
                long endTime = System.currentTimeMillis();
                BitMoves.aiRunningTime2 += (endTime - startTime);
                return move;
            } else if (BitMoves.aiRunningTime2 > 110000){
                BitValueMoves move = alphaBetaRecursionComplex(4, -100000.0f, +100000.0f, isMax); //slight panic mode
                long endTime = System.currentTimeMillis();
                BitMoves.aiRunningTime2 += (endTime - startTime);
                return move;
            } else if ((BitBoardFigures.blueToMove && BitMoves.blueFigureCount < 8) ||
                    (!BitBoardFigures.blueToMove && BitMoves.redFigureCount < 8)){
                if (BitMoves.aiRunningTime2 > 100000){
                    timeDepth = 9; //~3.5s or better in endgame
                } else {
                    timeDepth = 10; //~13s or better in endgame
                }
            } else {
                timeDepth = 8; //~3.3s on full board
                if(BitMoves.moveCounter < 5){
                    timeDepth = 6; //fast opening
                }
            }
            BitValueMoves move = alphaBetaRecursionComplex(timeDepth, -100000.0f, +100000.0f, isMax);
            long endTime = System.currentTimeMillis();
            BitMoves.aiRunningTime2 += (endTime - startTime);
            return move;
        }
    }

    /**
     * Recursive method implementing the alpha-beta pruning algorithm.
     *
     * @param depth the depth of the search
     * @param alpha the alpha value for pruning
     * @param beta the beta value for pruning
     * @param isMax a boolean indicating if the current move is a maximizing move
     * @return a BitValueMoves object containing the value of the best move and the move itself
     */
    static public BitValueMoves alphaBetaRecursion(int depth, float alpha, float beta, boolean isMax) {
        if (depth == 0 || isGameFinished()) {
            return new BitValueMoves(evaluatePosition(depth, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue), null, depth);
        }

        float value;
        String bestMove = null;
        int bestDepth = depth;
        if (isMax) {
            value = -100000.0f;
            String moves;


            moves = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);


            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);
                BitValueMoves evaluation = alphaBetaRecursion(depth - 1, alpha, beta, false);

                BitMoves.unmakeStack.push(makeMove);
                BitMoves.undoMove();

                if (evaluation.v > value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)) {
                    value = evaluation.v;
                    bestMove = moves.substring(i, i + 4);
                    bestDepth = evaluation.depth;
                }


                alpha = Math.max(alpha, value);

                if (alpha >= beta) {
                    break;
                }
            }
        } else {
            value = 100000.0f;
            String moves;


            moves = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);


            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);

                BitValueMoves evaluation = alphaBetaRecursion(depth - 1, alpha, beta, true);
                BitMoves.unmakeStack.push(makeMove);
                BitMoves.undoMove();

                if (evaluation.v < value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)) {
                    value = evaluation.v;
                    bestMove = moves.substring(i, i + 4);
                    bestDepth = evaluation.depth;
                }

                beta = Math.min(beta, value);

                if (alpha >= beta) {
                    break;
                }

            }
        }
        return new BitValueMoves(value, bestMove, bestDepth);
    }

    /**
     * Implements the alpha-beta pruning algorithm with a simplified evaluation function.
     *
     * @param depth the depth of the search
     * @param alpha the alpha value for pruning
     * @param beta the beta value for pruning
     * @param isMax a boolean indicating if the current move is a maximizing move
     * @return a BitValueMoves object containing the value of the best move and the move itself
     */
    static public BitValueMoves alphaBetaRecursionNormal(int depth, float alpha, float beta, boolean isMax) {
        if (depth == 0 || isGameFinished()) {
            return new BitValueMoves(evaluatePositionSimple(depth, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue), null, depth);
        }

        float value;
        String bestMove = null;
        int bestDepth = depth;
        if (isMax) {
            value = -100000.0f;
            String moves;


            moves = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);


            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);
                BitValueMoves evaluation = alphaBetaRecursionNormal(depth - 1, alpha, beta, false);

                BitMoves.unmakeStack.push(makeMove);
                BitMoves.undoMove();

                if (evaluation.v > value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)) {
                    value = evaluation.v;
                    bestMove = moves.substring(i, i + 4);
                    bestDepth = evaluation.depth;
                }


                alpha = Math.max(alpha, value);

                if (alpha >= beta) {
                    break;
                }
            }
        } else {
            value = 100000.0f;
            String moves;


            moves = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);

            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);

                BitValueMoves evaluation = alphaBetaRecursionNormal(depth - 1, alpha, beta, true);
                BitMoves.unmakeStack.push(makeMove);
                BitMoves.undoMove();

                if (evaluation.v < value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)) {
                    value = evaluation.v;
                    bestMove = moves.substring(i, i + 4);
                    bestDepth = evaluation.depth;
                }

                beta = Math.min(beta, value);

                if (alpha >= beta) {
                    break;
                }

            }
        }
        return new BitValueMoves(value, bestMove, bestDepth);
    }

    /**
     * Implements the alpha-beta pruning algorithm with a complex evaluation function.
     *
     * @param depth the depth of the search
     * @param alpha the alpha value for pruning
     * @param beta the beta value for pruning
     * @param isMax a boolean indicating if the current move is a maximizing move
     * @return a BitValueMoves object containing the value of the best move and the move itself
     */
    static public BitValueMoves alphaBetaRecursionComplex(int depth, float alpha, float beta, boolean isMax) {
        if (depth == 0 || isGameFinished()) {
            return new BitValueMoves(evaluatePositionComplex(depth), null, depth);
        }

        float value;
        String bestMove = null;
        int bestDepth = depth;
        if (isMax) {
            value = -100000.0f;
            String moves;


            moves = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);


            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);
                BitValueMoves evaluation = alphaBetaRecursion(depth - 1, alpha, beta, false);

                BitMoves.unmakeStack.push(makeMove);
                BitMoves.undoMove();

                if (evaluation.v > value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)) {
                    value = evaluation.v;
                    bestMove = moves.substring(i, i + 4);
                    bestDepth = evaluation.depth;
                }


                alpha = Math.max(alpha, value);

                if (alpha >= beta) {
                    break;
                }
            }
        } else {
            value = 100000.0f;
            String moves;


            moves = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);

            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);

                BitValueMoves evaluation = alphaBetaRecursion(depth - 1, alpha, beta, true);
                BitMoves.unmakeStack.push(makeMove);
                BitMoves.undoMove();

                if (evaluation.v < value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)) {
                    value = evaluation.v;
                    bestMove = moves.substring(i, i + 4);
                    bestDepth = evaluation.depth;
                }

                beta = Math.min(beta, value);

                if (alpha >= beta) {
                    break;
                }

            }
        }
        return new BitValueMoves(value, bestMove, bestDepth);
    }

    /**
     * Imports a game state from a FEN string.
     *
     * @param fenString the FEN string representing the board state
     */
    public static void importFEN(String fenString) {
        BitBoardFigures.SingleRed = 0;
        BitBoardFigures.SingleBlue = 0;
        BitBoardFigures.DoubleRed = 0;
        BitBoardFigures.DoubleBlue = 0;
        BitBoardFigures.MixedRed = 0;
        BitBoardFigures.MixedBlue = 0;

        int charIndex = 0;
        int boardIndex = 0;
        while (fenString.charAt(charIndex) != ' ') {

            if (boardIndex == 0 || boardIndex == 7 || boardIndex == 56 || boardIndex == 63) {
                boardIndex++;
                continue;
            }

            if (fenString.charAt(charIndex) == 'r') {
                charIndex++;
                switch (fenString.charAt(charIndex)) {
                    case '0':
                        BitBoardFigures.SingleRed |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'b':
                        BitBoardFigures.MixedBlue |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'r':
                        BitBoardFigures.DoubleRed |= (1L << boardIndex++);
                        charIndex++;
                        break;
                }
            }
            if (fenString.charAt(charIndex) == 'b') {
                charIndex++;
                switch (fenString.charAt(charIndex)) {
                    case '0':
                        BitBoardFigures.SingleBlue |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'b':
                        BitBoardFigures.DoubleBlue |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'r':
                        BitBoardFigures.MixedRed |= (1L << boardIndex++);
                        charIndex++;
                        break;
                }
            }

            if (boardIndex == 0 || boardIndex == 7 || boardIndex == 56 || boardIndex == 63) {
                boardIndex++;
                continue;
            }

            if (Character.isDigit(fenString.charAt(charIndex))) {
                int skip = Character.getNumericValue(fenString.charAt(charIndex));
                boardIndex += skip;
                charIndex++;
            }

            if (fenString.charAt(charIndex) == '/') {
                charIndex++;
            }

        }
        BitBoardFigures.blueToMove = (fenString.charAt(++charIndex) == 'b');
    }

    /**
     * A simplified evaluation function for quick assessment of a position.
     *
     * @param depth the depth of the search
     * @param SingleRed the bitboard for single red pieces
     * @param SingleBlue the bitboard for single blue pieces
     * @param DoubleRed the bitboard for double red pieces
     * @param DoubleBlue the bitboard for double blue pieces
     * @param MixedRed the bitboard for mixed red pieces
     * @param MixedBlue the bitboard for mixed blue pieces
     * @return the evaluation score of the position
     */
    public static float evaluatePositionSimple(int depth, long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue){
        BitBoard.counter++;
        float value= 0;
        if(isGameFinished()) {
            if (BitBoard.blueWon) {
                return 1000.0f + depth;
            } else{
                return -1000.0f - depth;
            }
        }

        value += (float) (bitCount(SingleBlue) * SingleValue);
        for (int i = Long.numberOfTrailingZeros(SingleBlue); i < 64 - Long.numberOfLeadingZeros(SingleBlue); i++) {
            if (((SingleBlue >> i) & 1) == 1) {
                value += (float) ((i/8+1)*SingleDistance);
            }
        }
        value += (float) (bitCount(DoubleBlue) * DoubleValue);
        for (int i = Long.numberOfTrailingZeros(DoubleBlue); i < 64 - Long.numberOfLeadingZeros(DoubleBlue); i++) {
            if (((DoubleBlue >> i) & 1) == 1) {
                value += (float) (((i/8+1)*DoubleDistance));
            }
        }
        value += (float) (bitCount(MixedBlue) * MixedValue);
        for (int i = Long.numberOfTrailingZeros(MixedBlue); i < 64 - Long.numberOfLeadingZeros(MixedBlue); i++) {
            if (((MixedBlue >> i) & 1) == 1) {
                value += (float) ((i/8+1)*MixedDistance);
            }
        }


        value -= (float) (bitCount(SingleRed) * SingleValue);
        for (int i = Long.numberOfTrailingZeros(SingleRed); i < 64 - Long.numberOfLeadingZeros(SingleRed); i++) {
            if (((SingleRed >> i) & 1) == 1) {
                value -= (float) ((8-i/8)*SingleDistance);
            }
        }
        value -= (float) (bitCount(DoubleRed) * DoubleValue);
        for (int i = Long.numberOfTrailingZeros(DoubleRed); i < 64 - Long.numberOfLeadingZeros(DoubleRed); i++) {
            if (((DoubleRed >> i) & 1) == 1) {
                value -= (float) (((8-i/8)*DoubleDistance));
            }
        }
        value -= (float) (bitCount(MixedRed) * MixedValue);
        for (int i = Long.numberOfTrailingZeros(MixedRed); i < 64 - Long.numberOfLeadingZeros(MixedRed); i++) {
            if (((MixedRed >> i) & 1) == 1) {
                value -= (float) ((8-i/8)*MixedDistance);
            }
        }

        return value;
    }

    /**
     * A more detailed evaluation function for assessing a position using an advanced evaluation method.
     *
     * @param depth the depth of the search
     * @return the evaluation score of the position
     */
    public static float evaluatePositionFunction2(int depth){
        BitBoard.counter++;
        float value= 0;
        if(isGameFinished()) {
            if(BitBoard.draw){
                return 0.0f;
            }
            else if (BitBoard.blueWon) {
                return 10000.0f + depth;
            } else{
                return -10000.0f - depth;
            }
        }

        long redAttacks = calculateAllAttacks(BitBoardFigures.SingleRed, BitBoardFigures.DoubleRed, BitBoardFigures.MixedRed, true);
        long blueAttacks = calculateAllAttacks(BitBoardFigures.SingleBlue, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedBlue, false);

        value += evaluatePiecesFunction2(BitBoardFigures.SingleBlue, SingleValue,  blueAttacks, 's');
        value += evaluatePiecesFunction2(BitBoardFigures.DoubleBlue, DoubleValue,  blueAttacks, 'd');
        value += evaluatePiecesFunction2(BitBoardFigures.MixedBlue, MixedValue,  blueAttacks, 's');
        value -= evaluatePiecesFunction2(BitBoardFigures.SingleRed, SingleValue,  redAttacks, 'S');
        value -= evaluatePiecesFunction2(BitBoardFigures.DoubleRed, DoubleValue,  redAttacks, 'D');
        value -= evaluatePiecesFunction2(BitBoardFigures.MixedRed, MixedValue,  redAttacks, 'S');

        return value;
    }

    /**
     * Evaluates the current position using a comprehensive evaluation function.
     *
     * @param depth the depth of the search
     * @param SingleRed the bitboard for single red pieces
     * @param SingleBlue the bitboard for single blue pieces
     * @param DoubleRed the bitboard for double red pieces
     * @param DoubleBlue the bitboard for double blue pieces
     * @param MixedRed the bitboard for mixed red pieces
     * @param MixedBlue the bitboard for mixed blue pieces
     * @return the evaluation score of the position
     */
    public static float evaluatePosition(int depth, long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue){
        BitBoard.counter++;
        float value= 0;
        if(isGameFinished()) {
            if (BitBoard.blueWon) {
                return 10000.0f + depth;
            } else{
                return -10000.0f - depth;
            }
        }
        if(BitBoard.draw){
            return 0.0f;
        }

        double[] valuesTable ={
                0.0f, Square2, Square3, Square4, Square5, Square6, Square7, 0.0f,
                Square9, Square10, Square11, Square12, Square13, Square14, Square15, Square16,
                Square17, Square18, Square19, Square20, Square21, Square22, Square23, Square24,
                Square25, Square26, Square27, Square28, Square29, Square30, Square31, Square32,
                Square33, Square34, Square35, Square36, Square37, Square38, Square39, Square40,
                Square41, Square42, Square43, Square44, Square45, Square46, Square47, Square48,
                Square49, Square50, Square51, Square52, Square53, Square54, Square55, Square56,
                0.0f, Square58, Square59, Square60, Square61, Square62, Square63, 0.0f,
        };

        value += (float) (bitCount(SingleBlue) * SingleValue);
        for (int i = Long.numberOfTrailingZeros(SingleBlue); i < 64 - Long.numberOfLeadingZeros(SingleBlue); i++) {
            if (((SingleBlue >> i) & 1) == 1) {
                value += (float) valuesTable[i];
            }
        }
        value += (float) (bitCount(DoubleBlue) * DoubleValue);
        for (int i = Long.numberOfTrailingZeros(DoubleBlue); i < 64 - Long.numberOfLeadingZeros(DoubleBlue); i++) {
            if (((DoubleBlue >> i) & 1) == 1) {
                value += (float) valuesTable[i];
            }
        }
        value += (float) (bitCount(MixedBlue) * MixedValue);
        for (int i = Long.numberOfTrailingZeros(MixedBlue); i < 64 - Long.numberOfLeadingZeros(MixedBlue); i++) {
            if (((MixedBlue >> i) & 1) == 1) {
                value += (float) valuesTable[i];
            }
        }


        value -= (float) (bitCount(SingleRed) * SingleValue);
        for (int i = Long.numberOfTrailingZeros(SingleRed); i < 64 - Long.numberOfLeadingZeros(SingleRed); i++) {
            if (((SingleRed >> i) & 1) == 1) {
                value -= (float) valuesTable[i];
            }
        }
        value -= (float) (bitCount(DoubleRed) * DoubleValue);
        for (int i = Long.numberOfTrailingZeros(DoubleRed); i < 64 - Long.numberOfLeadingZeros(DoubleRed); i++) {
            if (((DoubleRed >> i) & 1) == 1) {
                value -= (float) valuesTable[i];
            }
        }
        value -= (float) (bitCount(MixedRed) * MixedValue);
        for (int i = Long.numberOfTrailingZeros(MixedRed); i < 64 - Long.numberOfLeadingZeros(MixedRed); i++) {
            if (((MixedRed >> i) & 1) == 1) {
                value -= (float) valuesTable[i];
            }
        }

        return value;
    }

    /**
     * A complex evaluation function that includes additional heuristics for a more refined analysis.
     *
     * @param depth the depth of the search
     * @return the evaluation score of the position
     */
    public static float evaluatePositionComplex(int depth){
        BitBoard.counter++;
        float value= 0;
        if(isGameFinished()) {
            if(BitBoard.draw){
                return 0.0f;
            }
            else if (BitBoard.blueWon) {
                return 10000.0f + depth;
            } else{
                return -10000.0f - depth;
            }
        }

        //System.out.println(valuesTable.get(1));
        long redAttacks = calculateAllAttacks(BitBoardFigures.SingleRed, BitBoardFigures.DoubleRed, BitBoardFigures.MixedRed, true);
        long blueAttacks = calculateAllAttacks(BitBoardFigures.SingleBlue, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedBlue, false);

        value += evaluatePieces(BitBoardFigures.SingleBlue, SingleValue,  blueAttacks, 's');
        value += evaluatePieces(BitBoardFigures.DoubleBlue, DoubleValue,  blueAttacks, 'd');
        value += evaluatePieces(BitBoardFigures.MixedBlue, MixedValue,  blueAttacks, 's');
        value -= evaluatePieces(BitBoardFigures.SingleRed, SingleValue,  redAttacks, 'S');
        value -= evaluatePieces(BitBoardFigures.DoubleRed, DoubleValue,  redAttacks, 'D');
        value -= evaluatePieces(BitBoardFigures.MixedRed, MixedValue,  redAttacks, 'S');

        return value;
    }

    /**
     * Calculates the attack positions for single pieces.
     *
     * @param singlePositions the bitboard representing the positions of single pieces
     * @param isRed a boolean indicating if the pieces are red
     * @return a long value representing the bitboard of attack positions
     */
    public static long calculateSingleAttacks(long singlePositions, boolean isRed){
        long attacks = 0;
        if (isRed) {
            attacks |= (singlePositions >> 7 & ~BitMoves.FILE_H);
            attacks |= (singlePositions >> 9 & ~BitMoves.FILE_A);
        } else {
            attacks |= (singlePositions << 7 & ~BitMoves.FILE_A);
            attacks |= (singlePositions << 9 & ~BitMoves.FILE_H);

        }
        return attacks;
    }

    /**
     * Calculates the attack positions for double pieces.
     *
     * @param doublePositions the bitboard representing the positions of double pieces
     * @param isRed a boolean indicating if the pieces are red
     * @return a long value representing the bitboard of attack positions
     */
    public static long calculateDoubleAttacks(long doublePositions, boolean isRed){
        long attacks = 0;
        if (isRed) {
            attacks |= (doublePositions >> 15 & ~BitMoves.FILE_H);
            attacks |= (doublePositions >> 17 & ~BitMoves.FILE_A);
            attacks |= (doublePositions >> 10 & ~BitMoves.FILE_GH);
            attacks |= (doublePositions >> 6 &  ~BitMoves.FILE_AB);
        } else {
            attacks |= (doublePositions << 15 & ~BitMoves.FILE_A);
            attacks |= (doublePositions << 17 & ~BitMoves.FILE_H);
            attacks |= (doublePositions << 10 & ~BitMoves.FILE_AB);
            attacks |= (doublePositions << 6 &  ~BitMoves.FILE_GH);
        }
        return attacks;
    }

    /**
     * Calculates all attack positions for both single and double pieces.
     *
     * @param singlePositions the bitboard representing the positions of single pieces
     * @param doublePositions the bitboard representing the positions of double pieces
     * @param mixedPositions the bitboard representing the positions of mixed pieces
     * @param isRed a boolean indicating if the pieces are red
     * @return a long value representing the bitboard of all attack positions
     */
    public static long calculateAllAttacks(long singlePositions, long doublePositions, long mixedPositions, boolean isRed){
        long attacks = 0;
        attacks |= calculateSingleAttacks(singlePositions, isRed);
        attacks |= calculateDoubleAttacks(doublePositions, isRed);
        attacks |= calculateDoubleAttacks(mixedPositions, isRed);
        return attacks;
    }

    /**
     * Evaluates the pieces on the board based on their positions, value, and potential attacks.
     *
     * @param piecePositions the bitboard representing the positions of the pieces
     * @param pieceValue the base value of the pieces
     * @param allAttacks the bitboard representing all attack positions
     * @param figure a character indicating the type of pieces ('s' for single, 'd' for double, 'S' for single red, 'D' for double red)
     * @return the evaluation score for the pieces
     */
    public static float evaluatePieces(long piecePositions, double pieceValue, long allAttacks, char figure){
        double[]valuesTable2 ={0.0f, Square2, Square3, Square4, Square4, Square3, Square2, 0.0f,
                Square5, Square6, Square7, Square9, Square10, Square10, Square9, Square5,
                Square5, Square6, Square7, Square9, Square10, Square10, Square9, Square5,
                Square5, Square6, Square7, Square9, Square10, Square10, Square9, Square5,
                Square5, Square6, Square7, Square9, Square10, Square10, Square9, Square5,
                Square5, Square6, Square7, Square9, Square10, Square10, Square9, Square5,
                Square5, Square6, Square7, Square9, Square10, Square10, Square9, Square5,
                0.0f, Square2, Square3, Square4, Square4, Square3, Square2, 0.0f};

        //System.out.println(valuesTable2[30]);


        double value = 0;
        while (piecePositions != 0) {
            long lsb = piecePositions & -piecePositions;
            piecePositions ^= lsb;

            int index = Long.numberOfTrailingZeros(lsb);
            int row = index%8;
            double protectionBonus = isPieceProtected(lsb, allAttacks) ? protectionBonusValue : 0;
            value += pieceValue + protectionBonus + valuesTable2[index];
            switch (figure){
                case 's':
                    value += (float) ((index/8+1)*SingleDistance);
                    break;
                case 'd':
                    value += (float) ((index/8+1)*DoubleDistance);
                    break;
                case 'S':
                    value += (float) ((8-index/8)*SingleDistance);
                    break;
                case 'D':
                    value += (float) ((8-index/8)*DoubleDistance);
                    break;
            }/*
            if((index/8)==0||(index/8)==7){
                switch (row){
                    case 1, 6:
                        value += valuesTable.getOrDefault(0,0.0);
                        break;
                    case 2, 5:
                        value += valuesTable.getOrDefault(1,0.0);
                        break;
                    case 3, 4:
                        value += valuesTable.getOrDefault(2,0.0);
                        break;
                }
            }else{
                switch (row) {
                    case 0, 7:
                        value += valuesTable.getOrDefault(3, 0.0);
                        break;
                    case 1:
                        value += valuesTable.getOrDefault(4, 0.0);
                        break;
                    case 2:
                        value += valuesTable.getOrDefault(5, 0.0);
                        break;
                    case 3, 6:
                        value += valuesTable.getOrDefault(6, 0.0);
                        break;
                    case 4, 5:
                        value += valuesTable.getOrDefault(7, 0.0);
                        break;
                }
            }
            */
        }

        return (float) value;
    }

    /**
     * Evaluates pieces using an alternative evaluation function.
     *
     * @param piecePositions the bitboard representing the positions of the pieces
     * @param pieceValue the base value of the pieces
     * @param allAttacks the bitboard representing all attack positions
     * @param figure a character indicating the type of pieces ('s' for single, 'd' for double, 'S' for single red, 'D' for double red)
     * @return the evaluation score for the pieces
     */
    public static float evaluatePiecesFunction2(long piecePositions, double pieceValue, long allAttacks, char figure){
        double value = 0;
        while (piecePositions != 0) {
            long lsb = piecePositions & -piecePositions;
            piecePositions ^= lsb;

            int index = Long.numberOfTrailingZeros(lsb);
            int row = index%8;
            double protectionBonus = isPieceProtected(lsb, allAttacks) ? protectionBonusValue : 0;
            value += pieceValue + protectionBonus;
            switch (figure){
                case 's':
                    value += (float) ((index/8+1)*SingleDistance);
                    break;
                case 'd':
                    value += (float) ((index/8+1)*DoubleDistance);
                    break;
                case 'S':
                    value += (float) ((8-index/8)*SingleDistance);
                    break;
                case 'D':
                    value += (float) ((8-index/8)*DoubleDistance);
                    break;
            }
        }
        return (float) value;
    }


    /**
     * Checks if a piece is protected by other pieces.
     *
     * @param piecePosition the bitboard representing the position of the piece
     * @param allAttacks the bitboard representing all attack positions
     * @return true if the piece is protected, false otherwise
     */
    public static boolean isPieceProtected(long piecePosition, long allAttacks) {
        return (piecePosition & allAttacks) != 0;
    }

    /**
     * Determines if the game has ended based on the current board state.
     *
     * @return true if the game is finished, false otherwise
     */
    public static boolean isGameFinished(){
        // Constants for specific bitboard configurations
        final long BLUE_WIN_CONDITION = 9079256848778919936L; // Binary number with the first 6 bits as 1
        final long RED_WIN_CONDITION = 126L; // Specific condition for red

        if(BitBoard.isDraw()){
            return true;
        }
        // Check if blue on red home row
        else if ((BitBoardFigures.SingleBlue & BLUE_WIN_CONDITION) != 0 || (BitBoardFigures.MixedBlue & BLUE_WIN_CONDITION) != 0 || (BitBoardFigures.DoubleBlue & BLUE_WIN_CONDITION) != 0){
            BitBoard.blueWon= true;
            return true;
        }
        // Check if blue on red home row
        else if ((BitBoardFigures.SingleRed & RED_WIN_CONDITION) != 0 || (BitBoardFigures.MixedRed & RED_WIN_CONDITION) != 0 || (BitBoardFigures.DoubleRed & RED_WIN_CONDITION) != 0){
            BitBoard.blueWon =false;
            return true;
        }

        //No more red pieces on board
        else if(BitBoardFigures.SingleRed == 0 && BitBoardFigures.MixedRed == 0 && BitBoardFigures.DoubleRed == 0){
            BitBoard.blueWon =true;
            return true;
        }
        //No more blue pieces on board
        else if(BitBoardFigures.SingleBlue == 0 && BitBoardFigures.MixedBlue == 0 && BitBoardFigures.DoubleBlue == 0){
            BitBoard.blueWon =false;
            return true;
        }
        //No more moves for player who is am zug
        else if (BitBoardFigures.blueToMove) {

            boolean hasMovesLeft = BitMoves.hasPossibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if (!hasMovesLeft){
                BitBoard.blueWon = false;
                return true;
            }
        }
        else{

            boolean hasMovesLeft = BitMoves.hasPossibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if (!hasMovesLeft){
                BitBoard.blueWon = true;
                return true;
            }
        }


        return false;

    }

    /**
     * Benchmarks different evaluation functions by comparing their execution times and results.
     */
    public static void benchmarkEvaluation(){
        protectionBonusValue = 0;
        SingleValue = 10;
        DoubleValue = 20;
        MixedValue = 25;
        SingleDistance = 1; DoubleDistance = 1; MixedDistance =1;
        Square2 = 1; Square3 = 1; Square4 = 1; Square5 = 1; Square6 = 1; Square7 = 1;
        Square9 = 1; Square10 = 1;
        importFEN("b0b0b0b0b0bb/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        float evaluation = 0.0f;
        long startTime;
        long endTime;
        long totalTime = 0;
        for (int i = 0; i < 10000; i++) {
            startTime = System.nanoTime();
            evaluation = evaluatePositionSimple(1, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        double averageTime = (totalTime / (double) 10000);
        System.out.println("simple evaluation function (function 1). Evaluation: " +  evaluation + ". Duration: " + averageTime);
        totalTime = 0;
        for (int i = 0; i < 10000; i++) {
            startTime = System.nanoTime();
            evaluation = evaluatePositionFunction2(1);
            endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        averageTime = (totalTime / (double) 10000);
        System.out.println("middle evaluation function (function 2). Evaluation: " +  evaluation + ". Duration: " + averageTime);
        totalTime = 0;
        for (int i = 0; i < 10000; i++) {
            startTime = System.nanoTime();
            evaluation = evaluatePosition(1, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        averageTime = (totalTime / (double) 10000);
        System.out.println("middle complex evaluation function (function 3). Evaluation: " +  evaluation + ". Duration: " + averageTime);
        totalTime = 0;
        for (int i = 0; i < 10000; i++) {
            startTime = System.nanoTime();
            evaluation = evaluatePositionComplex(1);
            endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        averageTime = (totalTime / (double) 10000);
        System.out.println("complex evaluation function (function 4). Evaluation: " +  evaluation + ". Duration: " + averageTime);

    }

    /**
     * Converts a move in numeric format to a human-readable string format.
     *
     * @param move the move in numeric format
     * @return the move in human-readable string format
     */
    static String moveToString(String move) {
        if (move != null) {
            int coltp = Character.getNumericValue(move.charAt(1));
            char start_col = (char) (coltp + 97);
            coltp = Character.getNumericValue(move.charAt(3));
            char end_col = (char) (coltp + 97);
            int start_row = Character.getNumericValue(move.charAt(0)) + 1;
            int end_row = Character.getNumericValue(move.charAt(2)) + 1;

            return "" + start_col + start_row + "-" + end_col + end_row;
        } else return null;
    }

}