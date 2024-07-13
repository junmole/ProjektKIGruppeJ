import java.util.Arrays;

import static java.lang.Long.bitCount;

/**
 * The {@code BitBoard} class manages the game state and AI for the board game, Jump Board,
 * implemented using bitboards. It includes methods for evaluating the board,
 * handling game states, and executing the alpha-beta pruning algorithm.
 */
public class BitBoard {
    static boolean blueWon;
    public static int counter;
    public static boolean draw;

    public static void main(String[] args) {
        importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        System.out.println("Initial evaluation: " + BitBoard.evaluatePosition(0, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue));
        boolean isMax;
        while (!isGameFinished()) {
            isMax = BitBoardFigures.blueToMove;
            BitMoves.initZobristTable();
            BitValueMoves vm = alphaBetaWithTransposition(isMax);
            String move = vm.move;
            System.out.println("Move " + vm.move);
            System.out.println("Move made: " + moveToString(vm.move) + " on expected eval " + vm.v);
            BitMoves.makeMove(move, true);
            BitBoard.drawArray(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            BitBoardFigures.blueToMove = !BitBoardFigures.blueToMove;
            System.out.println(isGameFinished());

        }


    }

    /**
     * Executes the alpha-beta pruning algorithm to determine the best move
     * for the current player. This method uses a fixed depth for the search.
     *
     * @param isMax A boolean indicating if the current player is a maximizing or minimizing player
     * @return A {@code BitValueMoves} object representing the best move, its evaluation score and its depth.
     */
    static public BitValueMoves alphaBeta(boolean isMax) {
        BitMoves.moveCounter += 1;
        int timeDepth;
        long startTime = System.currentTimeMillis();
        if (BitMoves.aiRunningTime > 117000) {
            BitValueMoves move = alphaBetaRecursion(2, -100000.0f, +100000.0f, isMax); //panic mode
            long endTime = System.currentTimeMillis();
            BitMoves.aiRunningTime += (endTime - startTime);
            return move;
        } else if (BitMoves.aiRunningTime > 110000) {
            BitValueMoves move = alphaBetaRecursion(4, -100000.0f, +100000.0f, isMax); //slight panic mode
            long endTime = System.currentTimeMillis();
            BitMoves.aiRunningTime += (endTime - startTime);
            return move;
        } else if ((BitBoardFigures.blueToMove && BitMoves.blueFigureCount < 8) ||
                (!BitBoardFigures.blueToMove && BitMoves.redFigureCount < 8)) {
            if (BitMoves.aiRunningTime > 100000) {
                timeDepth = 9; //~3.5s or better in endgame
            } else {
                timeDepth = 10; //~13s or better in endgame
            }
        } else {
            timeDepth = 8; //~3.3s on full board
            if (BitMoves.moveCounter < 5) {
                timeDepth = 6; //fast opening
                //TODO: DEBUG HERE AND COMMENT OUT
            }
        }

        BitValueMoves move = alphaBetaRecursion(timeDepth, -100000.0f, +100000.0f, isMax);
        long endTime = System.currentTimeMillis();
        BitMoves.aiRunningTime += (endTime - startTime);
        return move;
    }

    /**
     * Executes the alpha-beta pruning algorithm to determine the best move
     * for the current player using a transposition table. This method uses a fixed depth for the search.
     *
     * @param isMax A boolean indicating if the current player is maximizing or minimizing
     * @return A {@code BitValueMoves} object representing the best move and its evaluation score
     */
    static public BitValueMoves alphaBetaWithTransposition(boolean isMax) {
        BitMoves.moveCounter += 1;
        int timeDepth = 4;
        long startTime = System.currentTimeMillis();
        if (BitMoves.aiRunningTime > 117000) {
            BitValueMoves move = alphaBetaRecursion(2, -100000.0f, +100000.0f, isMax); //panic mode
            long endTime = System.currentTimeMillis();
            BitMoves.aiRunningTime += (endTime - startTime);
            return move;
        }
        if (BitMoves.aiRunningTime > 110000) {
            BitValueMoves move = alphaBetaRecursion(4, -100000.0f, +100000.0f, isMax); //slight panic mode
            long endTime = System.currentTimeMillis();
            BitMoves.aiRunningTime += (endTime - startTime);
            return move;
        }
        if ((BitBoardFigures.blueToMove && BitMoves.blueFigureCount < 8) ||
                (!BitBoardFigures.blueToMove && BitMoves.redFigureCount < 8)) {
            if (BitMoves.aiRunningTime > 100000) {
                timeDepth = 9; //~3.5s or better in endgame
            } else {
                timeDepth = 10; //~13s or better in endgame
            }
        } else {
            timeDepth = 8; //~3.3s on full board
            if (BitMoves.moveCounter < 5) {
                timeDepth = 6; //fast opening
                //timeDepth = 6; //TODO: DEBUG HERE AND COMMENT OUT
            }
        }

        BitValueMoves move = alphaBetaWithTranspositionTableRecursion(timeDepth, -100000.0f, +100000.0f, isMax);
        long endTime = System.currentTimeMillis();
        BitMoves.aiRunningTime += (endTime - startTime);
        return move;
    }

    /**
     * Recursively executes the alpha-beta pruning algorithm to evaluate
     * board positions.
     *
     * @param depth The current depth of the search
     * @param alpha The alpha value for pruning
     * @param beta The beta value for pruning
     * @param isMax A boolean indicating if the current player is the maximizing or minimizing player.
     * @return A {@code BitValueMoves} object representing the best move, its evaluation score and the depth.
     */
    static public BitValueMoves alphaBetaRecursion(int depth, float alpha, float beta, boolean isMax) {
        if (depth == 0 || isGameFinished()) {
//            System.out.println("copy: " + BitMoves.evaluatePosition(depth, SingleRed, SingleBlue, DoubleRed, DoubleBlue, MixedRed, MixedBlue));
            return new BitValueMoves(BitBoard.evaluatePosition(depth, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue), null, depth);
        }

        float value;
        String bestMove = null;
        int bestDepth = depth;
        if (isMax) {
            //float value = alpha;
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
                    //System.out.println("break");
                    break;
                }
            }

        } else {
            //float value = beta;
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
                    //System.out.println("break");
                    break;
                }

            }

        }
        return new BitValueMoves(value, bestMove, bestDepth);
    }

    /**
     * Recursively executes the alpha-beta pruning algorithm with transposition
     * table optimization to evaluate board positions.
     *
     * @param depth The current depth of the search
     * @param alpha The alpha value for pruning
     * @param beta The beta value for pruning
     * @param isMax A boolean indicating if the current player is maximizing or minimizing
     * @return A {@code BitValueMoves} object representing the best move and its evaluation score
     */
    static public BitValueMoves alphaBetaWithTranspositionTableRecursion(int depth, float alpha, float beta, boolean isMax) {
        if (depth == 0 || isGameFinished()) {
            return new BitValueMoves(BitBoard.evaluatePosition(depth, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue), null, depth);
        }

        long hashedBoard = BitMoves.hashBoard();
        if (BitMoves.elementOfTranspositionTable(hashedBoard) != null) {
            return BitMoves.elementOfTranspositionTable(hashedBoard).bitValueMove;
        }

        if (isMax) {
            float value = -100000.0f;
            String bestMove = null;
            int bestDepth = depth;
            String moves;


            moves = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);


            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);
                //BitValueMoves evaluation = alphaBetaRecursion(depth - 1, alpha, beta, false);

                BitValueMoves evaluation = alphaBetaWithTranspositionTableRecursion(depth - 1, alpha, beta, false);

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

            BitValueMoves bitValueMoves = new BitValueMoves(value, bestMove, bestDepth);
            hashedBoard = BitMoves.hashBoard(); //TODO: maybe remove
            BitMoves.addToTranspositionTable(hashedBoard, new TranspositionValues(bitValueMoves, alpha, beta));
            return bitValueMoves;

        } else {
            float value = 100000.0f;
            String bestMove = null;
            int bestDepth = depth;
            String moves;


            moves = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);


            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);

                BitValueMoves evaluation = alphaBetaWithTranspositionTableRecursion(depth - 1, alpha, beta, true);

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

            BitValueMoves bitValueMoves = new BitValueMoves(value, bestMove, bestDepth);
            hashedBoard = BitMoves.hashBoard(); //TODO: maybe remove
            BitMoves.addToTranspositionTable(hashedBoard, new TranspositionValues(bitValueMoves, alpha, beta));
            return bitValueMoves;

        }
    }

    /**
     * Evaluates the current board position and returns a score indicating
     * the relative advantage for either player.
     * @param depth The current depth of the evaluation, used to adjust the score in case of game termination
     * @param SingleRed A bitboard representing the positions of single Red pieces
     * @param SingleBlue  A bitboard representing the positions of single Blue pieces
     * @param DoubleRed A bitboard representing the positions of double Red pieces
     * @param DoubleBlue A bitboard representing the positions of double Blue pieces
     * @param MixedRed  A bitboard representing the positions of mixed Red pieces
     * @param MixedBlue A bitboard representing the positions of mixed Blue pieces
     * @return A float value representing the evaluation score of the current position. Positive values indicate an
     *  advantage for Blue, while negative values indicate an advantage for Red. A value of zero indicates a draw.
     */
    public static float evaluatePosition(int depth, long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue) {
        BitBoard.counter++;
        float value = 0;
        if (isGameFinished()) {
            if (BitBoard.blueWon) {
                return 1000.0f + depth;
            } else {
                return -1000.0f - depth;
            }
        }

        if (isDraw()){
            return 0.0f;
        }

        value += bitCount(SingleBlue) * 10;
        for (int i = Long.numberOfTrailingZeros(SingleBlue); i < 64 - Long.numberOfLeadingZeros(SingleBlue); i++) {
            if (((SingleBlue >> i) & 1) == 1) {
                value += (float) ((i / 8 + 1) * 2.5);
            }
        }
        value += bitCount(DoubleBlue) * 20;
        for (int i = Long.numberOfTrailingZeros(DoubleBlue); i < 64 - Long.numberOfLeadingZeros(DoubleBlue); i++) {
            if (((DoubleBlue >> i) & 1) == 1) {
                value += (float) (((i / 8 + 1) * 2.5) * 2);
            }
        }
        value -= bitCount(SingleRed) * 10;
        for (int i = Long.numberOfTrailingZeros(SingleRed); i < 64 - Long.numberOfLeadingZeros(SingleRed); i++) {
            if (((SingleRed >> i) & 1) == 1) {
                value -= (float) ((8 - i / 8) * 2.5);
            }
        }
        value -= bitCount(DoubleRed) * 20;
        for (int i = Long.numberOfTrailingZeros(DoubleRed); i < 64 - Long.numberOfLeadingZeros(DoubleRed); i++) {
            if (((DoubleRed >> i) & 1) == 1) {
                value -= (float) (((8 - i / 8) * 2.5) * 2);
            }
        }
        return value;
    }


    /**
     *  Checks if a specific hashed board state has appeared more than three times in the game's history.
     *  This method examines the game state history stored in a hashmap to determine the frequency of the given hashed board situation.
     *  @return true, if game ends in draw and false otherwise.
     */
    public static boolean isDraw() {
        for (int count : BitMoves.gameStateHistory.values()) {
            if (count >= 3) {
                draw = true;
                return true;
            }
        }
        draw = false;
        return false;
    }

    /**
    * Checks if the game is won by the red or blue player and updates the blueWon attribute accordingly.
    * <p>
    * The method evaluates three conditions to determine the winner:
    * </p>
    * <ul>
    *     <li>
    *         Determines if a player has reached the opponent's home row.
    *     </li>
    *     <li>
    *         Checks if a player has no remaining pieces on the board.
    *     </li>
    *     <li>
    *         Verifies if the player whose turn it is has no legal moves available.
    *     </li>
    * </ul>
    *
    * @return true if either blue or red has won the game; false otherwise.
    */
    public static boolean isGameFinished() {
        // Constants for specific bitboard configurations
        final long BLUE_WIN_CONDITION = 9079256848778919936L; // Binary number with the first 6 bits as 1
        final long RED_WIN_CONDITION = 126L; // Specific condition for red

        // Check if blue on red home row
        if ((BitBoardFigures.SingleBlue & BLUE_WIN_CONDITION) != 0 || (BitBoardFigures.MixedBlue & BLUE_WIN_CONDITION) != 0 || (BitBoardFigures.DoubleBlue & BLUE_WIN_CONDITION) != 0) {
            BitBoard.blueWon = true;
            return true;
        }
        // Check if blue on red home row
        else if ((BitBoardFigures.SingleRed & RED_WIN_CONDITION) != 0 || (BitBoardFigures.MixedRed & RED_WIN_CONDITION) != 0 || (BitBoardFigures.DoubleRed & RED_WIN_CONDITION) != 0) {
            BitBoard.blueWon = false;
            return true;
        }

        //No more red pieces on board
        else if (BitBoardFigures.SingleRed == 0 && BitBoardFigures.MixedRed == 0 && BitBoardFigures.DoubleRed == 0) {
            BitBoard.blueWon = true;
            return true;
        }
        //No more blue pieces on board
        else if (BitBoardFigures.SingleBlue == 0 && BitBoardFigures.MixedBlue == 0 && BitBoardFigures.DoubleBlue == 0) {
            BitBoard.blueWon = false;
            return true;
        }
        //No more moves for player who is am zug
        else if (BitBoardFigures.blueToMove) {
            //NEW
            boolean hasMovesLeft = BitMoves.hasPossibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if (!hasMovesLeft) {
                BitBoard.blueWon = false;
                return true;
            }
        } else {
            boolean hasMovesLeft = BitMoves.hasPossibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if (!hasMovesLeft) {
                BitBoard.blueWon = true;
                return true;
            }
        }

        return false;

    }

    /**
     * Draws the current state of the board using a 2D array to represent each position.
     *
     * @param SingleRed   A bitboard representing the positions of single Red pieces
     * @param SingleBlue  A bitboard representing the positions of single Blue pieces
     * @param DoubleRed   A bitboard representing the positions of double Red pieces
     * @param DoubleBlue  A bitboard representing the positions of double Blue pieces
     * @param MixedRed    A bitboard representing the positions of mixed Red pieces
     * @param MixedBlue   A bitboard representing the positions of mixed Blue pieces
     */
    public static void drawArray(long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue) {
        String[][] jumpBoard = new String[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                jumpBoard[i][j] = "  ";
            }
        }

        for (int i = 0; i < 64; i++) {
            int row = 7 - (i / 8); // Flip rows here
            int col = i % 8;

            if (((SingleRed >> i) & 1) == 1) {
                jumpBoard[row][col] = "r0";
            }
            if (((SingleBlue >> i) & 1) == 1) {
                jumpBoard[row][col] = "b0";
            }
            if (((DoubleRed >> i) & 1) == 1) {
                jumpBoard[row][col] = "rr";
            }
            if (((DoubleBlue >> i) & 1) == 1) {
                jumpBoard[row][col] = "bb";
            }
            if (((MixedRed >> i) & 1) == 1) {
                jumpBoard[row][col] = "br";
            }
            if (((MixedBlue >> i) & 1) == 1) {
                jumpBoard[row][col] = "rb";
            }
        }
        for (int i = 0; i < 8; i++) {
            String tmp = Arrays.toString(jumpBoard[i]);
            tmp = tmp.replace(",", " |");
            tmp = tmp.replace("[", "| ");
            tmp = tmp.replace("]", " |");
            System.out.print(8 - i + " ");
            System.out.println(tmp);
        }
        System.out.println("    a    b    c    d    e    f    g    h");
    }

    /**
     * Imports a game state from a FEN string and sets up the board accordingly.
     *
     * @param fenString A string representing the board state in FEN format
     */
    public static void importFEN(String fenString) {
        BitBoardFigures.SingleRed = 0;
        BitBoardFigures.SingleBlue = 0;
        BitBoardFigures.DoubleRed = 0;
        BitBoardFigures.DoubleBlue = 0;
        BitBoardFigures.MixedRed = 0;
        BitBoardFigures.MixedBlue = 0;

        BitMoves.redFigureCount = 0;
        BitMoves.blueFigureCount = 0;

        int charIndex = 0;
        int boardIndex = 0;
        while (fenString.charAt(charIndex) != ' ') {

            if (boardIndex == 0 || boardIndex == 7 || boardIndex == 56 || boardIndex == 63) {
//                System.out.println("BoardIndex if 0 or 7: " + boardIndex);
                boardIndex++;
                continue;
            }

            if (fenString.charAt(charIndex) == 'r') {
                BitMoves.redFigureCount += 1;
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
                BitMoves.blueFigureCount += 1;
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
        drawArray(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
    }


    /**
     * Converts a move string into a more readable format.
     *
     * @param move The move string to convert
     * @return A string representing the move in a human-readable format
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