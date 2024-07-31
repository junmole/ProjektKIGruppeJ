package BitBoard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The {@code BitBoard} class manages the game state and AI for the board game, Jump Board.Board,
 * implemented using bitboards. It includes methods for evaluating the board,
 * handling game states, and executing the alpha-beta pruning algorithm with and without a
 * transposition table as well as MCTS algorithm.
 */
public class BitBoard {

    public static boolean blueWon;
    public static int counter;
    public static boolean draw;

    public static final Map<Integer, Double> valuesTable;
    static {
        valuesTable = new HashMap<>();
        valuesTable.put(0, 47.5);
        valuesTable.put(1, 33.75);
        valuesTable.put(2, 18.5);
        valuesTable.put(3, 15.0);
        valuesTable.put(4, 27.5);
        valuesTable.put(5, 30.0);
    }

    /**
     * The main method to test the functionality of the BitBoard class.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        System.out.println("Initial evaluation: " + BitBoard.evaluatePosition(0));
        boolean isMax;
        while (!isGameFinished()) {
            isMax = BitBoardFigures.blueToMove;
            BitMoves.initZobristTable();
            BitValueMoves vm = alphaBetaWithTransposition(isMax);
            String move = vm.move;
            System.out.println("Board.Move " + vm.move);
            System.out.println("Board.Move made: " + moveToString(vm.move) + " on expected eval " + vm.v);
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
            }
        }

        BitValueMoves move = alphaBetaWithTranspositionTableRecursion(timeDepth, -100000.0f, +100000.0f, isMax);
        long endTime = System.currentTimeMillis();
        BitMoves.aiRunningTime += (endTime - startTime);
        return move;
    }

    /**
     * Same method as alphaBetaWithTranspostition, but wihtout time Management
     * used for Benchmartests for AlphaBeta with Transposition Table.
     *
     * @param isMax A boolean indicating if the current player is maximizing or minimizing
     * @return A {@code BitValueMoves} object representing the best move and its evaluation score
     */
    static public BitValueMoves alphaBetaTranspositionBenchmark(boolean isMax, int depth) {
        BitValueMoves move = alphaBetaWithTranspositionTableRecursion(depth, -100000.0f, +100000.0f, isMax);
        return move;
    }

    /**
     * Same method as alphaBeta, but wihtout time Management, used for Benchmartests for AlphaBeta.
     *
     * @param isMax A boolean indicating if the current player is a maximizing or minimizing player
     * @return A {@code BitValueMoves} object representing the best move, its evaluation score and its depth.
     */
    static public BitValueMoves alphaBetaBenchmark(boolean isMax, int depth) {
        BitValueMoves move = alphaBetaRecursion(depth, -100000.0f, +100000.0f, isMax);
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
            return new BitValueMoves(BitBoard.evaluatePosition(depth), null, depth);
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
            return new BitValueMoves(BitBoard.evaluatePosition(depth), null, depth);
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
            hashedBoard = BitMoves.hashBoard();
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
            hashedBoard = BitMoves.hashBoard();
            BitMoves.addToTranspositionTable(hashedBoard, new TranspositionValues(bitValueMoves, alpha, beta));
            return bitValueMoves;

        }
    }

    /**
     * Performs Monte Carlo Tree Search with Upper Confidence bounds for Trees (UCT).
     *
     * @param computationalBudget the time budget for the search in milliseconds
     * @return the best move found within the computational budget
     */
    static public String mctsUCT(long computationalBudget){
        String possibleMoves;
        BitMoves.mctsBlueToMove = BitBoardFigures.blueToMove;

        BitBoardFigures.mctsSingleRed = BitBoardFigures.SingleRed;
        BitBoardFigures.mctsDoubleRed = BitBoardFigures.DoubleRed;
        BitBoardFigures.mctsMixedRed = BitBoardFigures.MixedRed;
        BitBoardFigures.mctsSingleBlue = BitBoardFigures.SingleBlue;
        BitBoardFigures.mctsDoubleBlue = BitBoardFigures.DoubleBlue;
        BitBoardFigures.mctsMixedBlue = BitBoardFigures.MixedBlue;

        if(BitMoves.mctsBlueToMove){
            possibleMoves = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
        } else {
            possibleMoves = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
        }

        MCTSNode root = new MCTSNode("", possibleMoves, null);

        long endtime = System.currentTimeMillis() + computationalBudget;

        MCTSNode v_i;
        int playoutReward;

        while(System.currentTimeMillis() < endtime){
            BitMoves.mctsBlueToMove = BitBoardFigures.blueToMove;

            v_i = mctsTreePolicy(root);
            playoutReward = mctsDefaultPolicy();
            mctsBackup(v_i, playoutReward);
        }

        return mctsBestChild(root, 0).sourceMove;
    }

    /**
     * Executes the tree policy for MCTS to select the next node to explore.
     *
     * @param root the root node of the MCTS tree
     * @return the selected node for further exploration
     */
    public static MCTSNode mctsTreePolicy(MCTSNode root){
        MCTSNode nodePointer = root;

        //while(!nodePointer.isTerminal){
        while(!BitBoard.isGameFinished()){
            if(!nodePointer.isExpanded){
                return mctsExpand(nodePointer);
            } else {
                nodePointer = mctsBestChild(nodePointer, Math.sqrt(2));
                BitMoves.makeMove(nodePointer.sourceMove, true);
                BitMoves.mctsBlueToMove = !BitMoves.mctsBlueToMove;
            }
        }

        return nodePointer;
    }

    /**
     * Expands the current node by adding a new child node with a random untried move.
     *
     * @param root the node to expand
     * @return the newly added child node
     */
    public static MCTSNode mctsExpand(MCTSNode root){
        String randomUntriedMove = root.getRandomPlayoutMove();

        randomUntriedMove = BitMoves.makeMove(randomUntriedMove, true);
        BitMoves.mctsBlueToMove = !BitMoves.mctsBlueToMove;

        String possibleMoves;
        if(BitMoves.mctsBlueToMove){
            possibleMoves = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
        } else {
            possibleMoves = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
        }

        MCTSNode newChild = new MCTSNode(randomUntriedMove, possibleMoves, root);

        root.addChild(randomUntriedMove, newChild);
        return newChild;
    }

    /**
     * Selects the best child node based on the UCT value.
     *
     * @param node the current node
     * @param uct_c the exploration parameter for UCT
     * @return the best child node
     */
    static MCTSNode mctsBestChild(MCTSNode node, double uct_c){
        try {
            return node.children.entrySet().stream().reduce((entry1, entry2) -> uctValue(entry1.getValue(), uct_c) > uctValue(entry2.getValue(), uct_c) ? entry1 : entry2).get().getValue();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    /**
     * Calculates the UCT value for a given node.
     *
     * @param node the node for which to calculate the UCT value
     * @param uct_c the exploration parameter for UCT
     * @return the UCT value
     */
    private static double uctValue(MCTSNode node, double uct_c){
        return node.playoutsWon / node.playoutsSum + uct_c * Math.sqrt((Math.log(node.parent.playoutsSum / node.playoutsSum)));
    }

    /**
     * Executes the default policy (random playout) to simulate a game from the given state.
     *
     * @return the reward of the playout for the player that started (1 for win, 0 for draw, -1 for loss)
     */
    public static int mctsDefaultPolicy(){
        String moves;
        while(!BitBoard.isGameFinished()){
            if (BitMoves.mctsBlueToMove){
                moves = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            } else {
                moves = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            }

            //Select move uniformly at random
            //Sometimes BitMoves.mctsBlueToMove is wrong, the try-catch fixes this bug
            int offset;
            try {
                offset = ThreadLocalRandom.current().nextInt(0, moves.length()/4);
            } catch (IllegalArgumentException ignored){
                BitMoves.mctsBlueToMove = !BitMoves.mctsBlueToMove;
                if (BitMoves.mctsBlueToMove){
                    moves = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
                } else {
                    moves = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
                }
                offset = ThreadLocalRandom.current().nextInt(0, moves.length()/4);
            }

            String randomMove = moves.substring(offset*4, offset*4+4);

            randomMove = BitMoves.makeMove(randomMove, true);
            BitMoves.mctsBlueToMove = !BitMoves.mctsBlueToMove;
        }

        counter += 1;
        if(counter == Long.MAX_VALUE - 1) {
            System.out.println("overflowing");
        }

        if(BitMoves.mctsBlueStarted){
            if(BitBoard.blueWon){
                return 1;
            } else {
                //return -1;
                return 0;
            }
        } else {
            if(BitBoard.blueWon){
                //return -1;
                return 0;
            } else {
                return 1;
            }
        }
    }

    /**
     * Backs up the result of a playout to update the tree with the new playout information.
     *
     * @param leaf the leaf node where the playout ended
     * @param reward the reward obtained from the playout
     */
    public static void mctsBackup(MCTSNode leaf, int reward){
        leaf.playoutsSum = 1;
        leaf.playoutsWon = reward;
        MCTSNode parent = leaf.parent;
        while(parent != null){
            parent.playoutsSum++;
            parent.playoutsWon += reward;
            parent = parent.parent;
        }

        BitBoardFigures.SingleRed = BitBoardFigures.mctsSingleRed;
        BitBoardFigures.SingleBlue = BitBoardFigures.mctsSingleBlue;
        BitBoardFigures.MixedRed = BitBoardFigures.mctsMixedRed;
        BitBoardFigures.MixedBlue = BitBoardFigures.mctsMixedBlue;
        BitBoardFigures.DoubleRed = BitBoardFigures.mctsDoubleRed;
        BitBoardFigures.DoubleBlue = BitBoardFigures.mctsDoubleBlue;
    }

    /**
     * Evaluates the current board position and returns a score indicating
     * the relative advantage for either player.
     * @param depth The current depth of the evaluation, used to adjust the score in case of game termination
     * @return A float value representing the evaluation score of the current position. Positive values indicate an
     *  advantage for Blue, while negative values indicate an advantage for Red. A value of zero indicates a draw.
     */
    public static float evaluatePosition(int depth) {
        counter++;
        float value= 0;
        if(isGameFinished()) {
            if(draw){
                return 0.0f;
            }
            else if (blueWon) {
                return 10000.0f + depth;
            } else{
                return -10000.0f - depth;
            }
        }

        long redAttacks = calculateAllAttacks(BitBoardFigures.SingleRed, BitBoardFigures.DoubleRed, BitBoardFigures.MixedRed, true);
        long blueAttacks = calculateAllAttacks(BitBoardFigures.SingleBlue, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedBlue, false);

        value += evaluatePieces(BitBoardFigures.SingleBlue, 19.5,  blueAttacks, 's');
        value += evaluatePieces(BitBoardFigures.DoubleBlue, 24.5,  blueAttacks, 'd');
        value += evaluatePieces(BitBoardFigures.MixedBlue, 24.0,  blueAttacks, 's');
        value -= evaluatePieces(BitBoardFigures.SingleRed, 19.5,  redAttacks, 'S');
        value -= evaluatePieces(BitBoardFigures.DoubleRed, 24.5,  redAttacks, 'D');
        value -= evaluatePieces(BitBoardFigures.MixedRed, 24.0,  redAttacks, 'S');

        return value;
    }

    /**
     * Evaluates the given pieces based on their positions, value, protection, and specific figure type.
     *
     * @param piecePositions the positions of the pieces to evaluate
     * @param pieceValue the base value of the piece
     * @param allAttacks a long value representing all attack positions
     * @param figure the character representing the type of the piece ('s', 'd', 'S', 'D')
     * @return the evaluation value as a float
     */
    public static float evaluatePieces(long piecePositions, double pieceValue, long allAttacks, char figure){
        double value = 0;
        double protectionBonusValue = 2.5;
        while (piecePositions != 0) {
            long lsb = piecePositions & -piecePositions;
            piecePositions ^= lsb;

            int index = Long.numberOfTrailingZeros(lsb);
            int row = index%8;
            double protectionBonus = isPieceProtected(lsb, allAttacks) ? protectionBonusValue : 0;
            value += pieceValue + protectionBonus;
            switch (figure){
                case 's':
                    value += (float) ((index/8+1)*16.5);
                    break;
                case 'd':
                    value += (float) ((index/8+1)*25);
                    break;
                case 'S':
                    value += (float) ((8-index/8)*16.5);
                    break;
                case 'D':
                    value += (float) ((8-index/8)*25);
                    break;
            }
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
                    case 1, 4, 5:
                        value += valuesTable.getOrDefault(4, 0.0);
                        break;
                    case 2, 3, 6:
                        value += valuesTable.getOrDefault(5, 0.0);
                        break;
                }
            }
        }

        return (float) value;
    }

    /**
     * Calculates all possible attacks from given positions for single, double, and mixed pieces.
     *
     * @param singlePositions the positions of single pieces
     * @param doublePositions the positions of double pieces
     * @param mixedPositions the positions of mixed pieces
     * @param isRed indicates if the attacking pieces are red
     * @return a long value representing all possible attack positions
     */
    public static long calculateAllAttacks(long singlePositions, long doublePositions, long mixedPositions, boolean isRed){
        long attacks = 0;
        attacks |= calculateSingleAttacks(singlePositions, isRed);
        attacks |= calculateDoubleAttacks(doublePositions, isRed);
        attacks |= calculateDoubleAttacks(mixedPositions, isRed);
        return attacks;
    }

    /**
     * Calculates possible attacks for single pieces from given positions.
     *
     * @param singlePositions the positions of single pieces
     * @param isRed indicates if the attacking pieces are red
     * @return a long value representing the attack positions for single pieces
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
     * Calculates possible attacks for double pieces from given positions.
     *
     * @param doublePositions the positions of double pieces
     * @param isRed indicates if the attacking pieces are red
     * @return a long value representing the attack positions for double pieces
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
     * Checks if a piece is protected by verifying if its position is covered by any attacks.
     *
     * @param piecePosition the position of the piece to check
     * @param allAttacks a long value representing all attack positions
     * @return {@code true} if the piece is protected, {@code false} otherwise
     */
    public static boolean isPieceProtected(long piecePosition, long allAttacks) {
        return (piecePosition & allAttacks) != 0;
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
    public static String moveToString(String move) {
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