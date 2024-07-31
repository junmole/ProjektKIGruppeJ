import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The BitMoves class provides methods for generating and evaluating possible moves for a board game.
 * It also handles move execution, move undo, and Zobrist hashing for board state representation.
 */
public class BitMoves {
    static long RED_CAPTURE, RED_ROOK, RED_NON_CAPTURE;
    static long BLUE_CAPTURE, BLUE_NON_CAPTURE, BLUE_ROOK;
    static long EMPTY_BLUE, EMPTY_RED, EMPTY_KNIGHT_RED, EMPTY_KNIGHT_BLUE;

    static final long FILE_A = 36170086419038336L, FILE_AB = 217020518514230019L;
    static final long FILE_GH = -4557430888798830400L,  FILE_H = 72340172838076672L;
    static final long EDGE_0 = 1L, EDGE_7 = 128L;
    static final long EDGE_56 =72057594037927936L, EDGE_63 = -9223372036854775808L;

    static boolean colorRed = true;
    static boolean capture = false;

    static String startFigure = "", endFigure = "";

    static final Stack<String> unmakeStack = new Stack<>();
    static boolean mctsBlueStarted;
    static boolean mctsBlueToMove;
    public static int moveCounter;

    public static long aiRunningTime = 0L, aiRunningTime1 = 0L, aiRunningTime2 = 0L;
    public static int redFigureCount, blueFigureCount;
    public static final long[][] zobristTable = new long[64][6];
    public static final Map<Long, TranspositionValues> transpositionTable = new HashMap<>();
    static final Map<Long, Integer> gameStateHistory = new HashMap<>();


    /**
     * The main method to test the functionality of the BitMoves class.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        initZobristTable();
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);

        BitBoard.importFEN("2b0b02/1b0b0b0b0b02/8/8/8/8/3r0r0r0r01/1r0r0r02 b");
        long hashedBoard1 = hashBoard();
        System.out.println("Hash:" + hashedBoard1);

        BitBoard.importFEN("2b0b02/1b0b0b0b03/6b01/8/8/8/3r0r0r0r01/1r0r0r02 b");
        long hashedBoard2 = hashBoard();
        System.out.println(hashedBoard2);

        System.out.println(hashedBoard1==hashedBoard2);

        //addToTranspositionTable(hashedBoard, new TranspositionValues(new BitValueMoves(100, "0123", 3), 0.5f, 0.7f));

    }

    /**
     * Calculates possible moves for blue pieces.
     *
     * @param SingleRed positions of single red pieces
     * @param SingleBlue positions of single blue pieces
     * @param DoubleRed positions of double red pieces
     * @param DoubleBlue positions of double blue pieces
     * @param MixedRed positions of mixed red pieces
     * @param MixedBlue positions of mixed blue pieces
     * @return a string representing all possible moves for blue pieces
     */
    public static String possibleMovesBlue(long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue) {
        BLUE_CAPTURE = SingleRed | DoubleRed | MixedRed;
        BLUE_NON_CAPTURE = ~(DoubleBlue | MixedBlue);
        BLUE_ROOK = ~(SingleBlue);
        EMPTY_BLUE = ~(SingleRed | DoubleRed | MixedRed | DoubleBlue | MixedBlue);
        EMPTY_KNIGHT_BLUE = ~(DoubleBlue | MixedBlue);
        return possibleMovesNB(DoubleBlue) + possibleMovesSB(SingleBlue)+possibleMovesNB(MixedBlue);
    }

    /**
     * Calculates possible moves for red pieces.
     *
     * @param SingleRed positions of single red pieces
     * @param SingleBlue positions of single blue pieces
     * @param DoubleRed positions of double red pieces
     * @param DoubleBlue positions of double blue pieces
     * @param MixedRed positions of mixed red pieces
     * @param MixedBlue positions of mixed blue pieces
     * @return a string representing all possible moves for red pieces
     */
    public static String possibleMovesRed(long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue) {
        RED_CAPTURE = SingleBlue | DoubleBlue | MixedBlue;
        RED_NON_CAPTURE = ~(DoubleRed | MixedRed);
        RED_ROOK = ~(SingleRed);
        EMPTY_RED = ~(DoubleRed | MixedRed | DoubleBlue | MixedBlue| SingleBlue);
        EMPTY_KNIGHT_RED = ~(DoubleRed | MixedRed);
        return possibleMovesNR(DoubleRed) + possibleMovesSR(SingleRed)+possibleMovesNR(MixedRed);
    }

    /**
     * Calculates possible moves for single red pieces.
     *
     * @param singleRed positions of single red pieces
     * @return a string representing all possible moves for single red pieces
     */
    private static String possibleMovesSR(long singleRed) {
        String singleRedMoves = "";
        //Capture right
        long SINGLERED_MOVES = (singleRed >> 7) & RED_CAPTURE & ~FILE_H;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                singleRedMoves += "" + (i / 8 + 1) + (i % 8 - 1) + (i / 8) + (i % 8);
            }
        }
        //Capture left
        SINGLERED_MOVES = (singleRed >> 9) & RED_CAPTURE & ~FILE_A;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                singleRedMoves += "" + (i / 8 + 1) + (i % 8+1) + (i / 8) + (i % 8);
            }
        }
        //Move 1 forward
        SINGLERED_MOVES =(singleRed >> 8) & EMPTY_RED & ~EDGE_7 &~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                singleRedMoves += "" + (i / 8 + 1) + (i % 8) + (i / 8) + (i % 8);
            }
        }
        //Move 1 left
        SINGLERED_MOVES = (singleRed >>1) & EMPTY_RED & ~FILE_A & ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                singleRedMoves += "" + (i / 8) + (i % 8 + 1) + (i / 8) + (i % 8);
            }
        }
        //Move 1 right
        SINGLERED_MOVES = (singleRed << 1) & EMPTY_RED & ~FILE_H & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                singleRedMoves += "" + (i / 8) + (i % 8 - 1) + (i / 8) + (i % 8);
            }
        }
        return singleRedMoves;
    }

    /**
     * Calculates possible moves for knight red pieces.
     *
     * @param knightRed positions of knight red pieces
     * @return a string representing all possible moves for knight red pieces
     */
    private static String possibleMovesNR(long knightRed) {
        String knightRedMoves = "";

        //1 right 2 up
        long KNIGHTRED_MOVES = (knightRed >> 15) & EMPTY_KNIGHT_RED & ~FILE_H & ~EDGE_7 &~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                knightRedMoves += "" + (i / 8 + 2) + (i % 8 - 1) + (i / 8) + (i % 8);
            }
        }
        //1 left 2 up
        KNIGHTRED_MOVES = (knightRed >> 17) & EMPTY_KNIGHT_RED & ~FILE_A & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                knightRedMoves += "" + (i / 8 + 2) + (i % 8 + 1) + (i / 8) + (i % 8);
            }
        }
        //2 left 1 up
        KNIGHTRED_MOVES = (knightRed >> 10)& EMPTY_KNIGHT_RED & ~FILE_GH & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                knightRedMoves += "" + (i / 8 + 1) + (i % 8 + 2) + (i / 8) + (i % 8);
            }
        }
        //2 right 1 up
        KNIGHTRED_MOVES = (knightRed >> 6) & EMPTY_RED & ~FILE_AB &~EDGE_7;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                knightRedMoves += "" + (i / 8 + 1) + (i % 8 - 2) + (i / 8) + (i % 8);
            }
        }
        return knightRedMoves;
    }

    /**
     * Calculates possible moves for single blue pieces.
     *
     * @param singleBlue positions of single blue pieces
     * @return a string representing all possible moves for single blue pieces
     */
    private static String possibleMovesSB(long singleBlue) {
        String singleBlueMoves = "";

        //Capture left
        long SINGLEBLUE_MOVES = (singleBlue << 7) & BLUE_CAPTURE & ~FILE_A;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                singleBlueMoves += "" + (i / 8 - 1) + (i % 8 + 1) + (i / 8) + (i % 8);
            }
        }
        //Capture right
        SINGLEBLUE_MOVES = (singleBlue << 9) & BLUE_CAPTURE & ~FILE_H;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                singleBlueMoves += "" + (i / 8 - 1) + (i % 8 - 1) + (i / 8) + (i % 8);
            }
        }
        //Move 1 forward
        SINGLEBLUE_MOVES = (singleBlue << 8) & EMPTY_BLUE & ~EDGE_63&~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                singleBlueMoves += "" + (i / 8 - 1) + (i % 8) + (i / 8) + (i % 8);
            }
        }
        //Move 1 right
        SINGLEBLUE_MOVES = (singleBlue << 1) & EMPTY_BLUE & ~FILE_H &~EDGE_7;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                singleBlueMoves += "" + (i / 8) + (i % 8 - 1) + (i / 8) + (i % 8);
            }
        }
        //Move 1 left
        SINGLEBLUE_MOVES = (singleBlue >> 1) & EMPTY_BLUE & ~FILE_A & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                singleBlueMoves += "" + (i / 8) + (i % 8 + 1) + (i / 8) + (i % 8);
            }
        }
        return singleBlueMoves;
    }

    /**
     * Calculates possible moves for knight blue pieces.
     *
     * @param knightBlue positions of knight blue pieces
     * @return a string representing all possible moves for knight red pieces
     */
    private static String possibleMovesNB(long knightBlue) {
        String knightBlueMoves = "";
        //1 left 2 down
        long KNIGHTRED_MOVES = (knightBlue << 15) & EMPTY_KNIGHT_BLUE & ~-9187201950435770368L & ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                knightBlueMoves += "" + (i / 8-2) + (i % 8 + 1) + (i / 8) + (i % 8);
            }
        }
        //1 right 2 down
        KNIGHTRED_MOVES = (knightBlue << 17) & EMPTY_KNIGHT_BLUE & ~FILE_H & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                knightBlueMoves += "" + (i / 8 - 2) + (i % 8 - 1) + (i / 8) + (i % 8);
            }
        }
        //2 left 1 down
        KNIGHTRED_MOVES = (knightBlue << 6)& EMPTY_KNIGHT_BLUE & ~FILE_GH& ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                knightBlueMoves += "" + (i / 8 - 1) + (i % 8+2) + (i / 8) + (i % 8);
            }
        }
        //2 right 1 down
        KNIGHTRED_MOVES = (knightBlue << 10) & EMPTY_BLUE & ~FILE_AB & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                knightBlueMoves += "" + (i / 8 - 1) + (i % 8 -2 ) + (i / 8) + (i % 8);
            }
        }

        return knightBlueMoves;
    }


    public static boolean hasPossibleMovesBlue(long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue) {
        BLUE_CAPTURE = SingleRed | DoubleRed | MixedRed;
        BLUE_NON_CAPTURE = ~(DoubleBlue | MixedBlue);
        BLUE_ROOK = ~(SingleBlue);
        EMPTY_BLUE = ~(SingleRed | DoubleRed | MixedRed | DoubleBlue | MixedBlue);
        EMPTY_KNIGHT_BLUE = ~(DoubleBlue | MixedBlue);

        return hasPossibleMovesNB(DoubleBlue) || hasPossibleMovesSB(SingleBlue) || hasPossibleMovesNB(MixedBlue);
    }

    public static boolean hasPossibleMovesRed(long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue) {
        RED_CAPTURE = SingleBlue | DoubleBlue | MixedBlue;
        RED_NON_CAPTURE = ~(DoubleRed | MixedRed);
        RED_ROOK = ~(SingleRed);
        EMPTY_RED = ~(DoubleRed | MixedRed | DoubleBlue | MixedBlue| SingleBlue);
        EMPTY_KNIGHT_RED = ~(DoubleRed | MixedRed);

        return hasPossibleMovesNR(DoubleRed) || hasPossibleMovesSR(SingleRed) || hasPossibleMovesNR(MixedRed);
    }


    private static boolean hasPossibleMovesSR(long singleRed) {
        //Capture right
        long SINGLERED_MOVES = (singleRed >> 7) & RED_CAPTURE & ~FILE_H;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //Capture left
        SINGLERED_MOVES = (singleRed >> 9) & RED_CAPTURE & ~FILE_A;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //Move 1 forward
        SINGLERED_MOVES =(singleRed >> 8) & EMPTY_RED & ~EDGE_7 &~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //Move 1 left
        SINGLERED_MOVES = (singleRed >>1) & EMPTY_RED & ~FILE_A & ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //Move 1 right
        SINGLERED_MOVES = (singleRed << 1) & EMPTY_RED & ~FILE_H & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasPossibleMovesNR(long knightRed) {
        //1 right 2 up
        long KNIGHTRED_MOVES = (knightRed >> 15) & EMPTY_KNIGHT_RED & ~FILE_H & ~EDGE_7 &~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //1 left 2 up
        KNIGHTRED_MOVES = (knightRed >> 17) & EMPTY_KNIGHT_RED & ~FILE_A & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //2 left 1 up
        KNIGHTRED_MOVES = (knightRed >> 10)& EMPTY_KNIGHT_RED & ~FILE_GH & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //2 right 1 up
        KNIGHTRED_MOVES = (knightRed >> 6) & EMPTY_RED & ~FILE_AB &~EDGE_7;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasPossibleMovesSB(long singleBlue) {
        //Capture left
        long SINGLEBLUE_MOVES = (singleBlue << 7) & BLUE_CAPTURE & ~FILE_A;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //Capture right
        SINGLEBLUE_MOVES = (singleBlue << 9) & BLUE_CAPTURE & ~FILE_H;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //Move 1 forward
        SINGLEBLUE_MOVES = (singleBlue << 8) & EMPTY_BLUE & ~EDGE_63&~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //Move 1 right
        SINGLEBLUE_MOVES = (singleBlue << 1) & EMPTY_BLUE & ~FILE_H &~EDGE_7;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //Move 1 left
        SINGLEBLUE_MOVES = (singleBlue >> 1) & EMPTY_BLUE & ~FILE_A & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasPossibleMovesNB(long knightBlue) {
        //1 left 2 down
        long KNIGHTBLUE_MOVES = (knightBlue << 15) & EMPTY_KNIGHT_BLUE & ~EDGE_63 & ~-9187201950435770368L;
        for (int i = Long.numberOfTrailingZeros(KNIGHTBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTBLUE_MOVES); i++) {
            if (((KNIGHTBLUE_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //1 right 2 down
        KNIGHTBLUE_MOVES = (knightBlue << 17) & EMPTY_KNIGHT_BLUE & ~FILE_H & ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTBLUE_MOVES); i++) {
            if (((KNIGHTBLUE_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //2 left 1 down
        KNIGHTBLUE_MOVES = (knightBlue << 6)& EMPTY_KNIGHT_BLUE & ~FILE_GH& ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTBLUE_MOVES); i++) {
            if (((KNIGHTBLUE_MOVES >> i) & 1) == 1) {
                return true;
            }
        }
        //2 right 1 down
        KNIGHTBLUE_MOVES = (knightBlue << 10) & EMPTY_BLUE & ~FILE_AB & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(KNIGHTBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTBLUE_MOVES); i++) {
            if (((KNIGHTBLUE_MOVES >> i) & 1) == 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Performs a move given a string representing the move.
     *
     * @param moves the move in string format
     * @param start indicates if it is the starting move
     * @return a string representing the result of the move with the start and end field
     */
    public static String makeMove(String moves, boolean start){

        if (start) {
            String start_result = "";

            start_result += BitMoves.makeStartMove(BitBoardFigures.SingleRed, moves, 'S');
            if (start_result.isEmpty()) {
                start_result += BitMoves.makeStartMove(BitBoardFigures.SingleBlue, moves, 's');
            }
            if(start_result.isEmpty()) {
                start_result += BitMoves.makeStartMove(BitBoardFigures.DoubleRed, moves, 'D');
            }
            if (start_result.isEmpty()) {
                start_result += BitMoves.makeStartMove(BitBoardFigures.DoubleBlue, moves, 'd');
            }
            if(start_result.isEmpty()) {
                start_result += BitMoves.makeStartMove(BitBoardFigures.MixedRed, moves, 'M');
            }
            if (start_result.isEmpty()) {
                start_result += BitMoves.makeStartMove(BitBoardFigures.MixedBlue, moves, 'm');
            }
            startFigure = start_result;
            //Zielfeld für capture Moves
        }
        else {
            String end_result = "";

            end_result +=BitMoves.makeEndCaptureMove(BitBoardFigures.SingleRed, moves, 'S');
            if(end_result.isEmpty()){
                end_result += BitMoves.makeEndCaptureMove(BitBoardFigures.SingleBlue, moves, 's');
            }
            if(end_result.isEmpty()){
                end_result += BitMoves.makeEndCaptureMove(BitBoardFigures.DoubleRed, moves, 'D');
            }
            if(end_result.isEmpty()){
                end_result += BitMoves.makeEndCaptureMove(BitBoardFigures.DoubleBlue, moves, 'd');
            }
            if(end_result.isEmpty()) {
                end_result += BitMoves.makeEndCaptureMove(BitBoardFigures.MixedRed, moves, 'M');
            }
            if(end_result.isEmpty()){
                end_result +=BitMoves.makeEndCaptureMove(BitBoardFigures.MixedBlue, moves, 'm');
            }
            endFigure = end_result;
            if(!endFigure.isEmpty()){
                long currentStateHash = getGameStateHash(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue, BitBoardFigures.blueToMove);
                gameStateHistory.put(currentStateHash, gameStateHistory.getOrDefault(currentStateHash, 0) + 1);
            }
        }
        //wenn auf dem Zielfeld kein capture stattgefunden hat
        if(endFigure.isEmpty()) {
            makeEndMove(moves);
            endFigure = "e";
            long currentStateHash = getGameStateHash(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue, BitBoardFigures.blueToMove);
            gameStateHistory.put(currentStateHash, gameStateHistory.getOrDefault(currentStateHash, 0) + 1);
        }

        return moves + startFigure + endFigure;
    }

    /**
     * @param board represents the current state of the specified bitboard
     * @param move represents a move in a numerical form, e.g. A6-B2 would be 5011
     * @param type represents the bitboard type <ul> <li>'S' is for single red pieces</li> <li>'D' is for the fully red stack</li><li>'M' is for the red on blue stack</li><li>'s' is for single blue pieces</li> <li>'d' is for the fully blue stack</li><li>'m' is for the blue on red stack</li></ul>
     * @return the type of bitboard, if it has a piece at the starting position of the move, otherwise an empty string
     */
    public static String makeStartMove(long board, String move, char type) {
        int start=(getNumericalCharValue(move.charAt(0))*8)+(getNumericalCharValue(move.charAt(1)));
        String result = "";
        //if statement is only true, when the current bitboard is occupied at the start position
        if (((board>>>start)&1)==1) {
            switch(type){
                case 'S':
                    BitBoardFigures.SingleRed&=~(1L<<start);
                    result += "S";
                    colorRed= true;
                    makeMove(move, false);
                    break;
                case 'D':
                    BitBoardFigures.DoubleRed&=~(1L<<start);
                    BitBoardFigures.SingleRed|=(1L<<start);
                    result += "D";
                    colorRed= true;
                    makeMove(move, false);
                    break;
                case 'M':
                    BitBoardFigures.MixedRed&=~(1L<<start);
                    BitBoardFigures.SingleBlue|=(1L<<start);
                    result += "M";
                    colorRed= true;
                    makeMove(move, false);
                    break;
                case 's':
                    BitBoardFigures.SingleBlue&=~(1L<<start);
                    result += "s";
                    colorRed= false;
                    makeMove(move, false);
                    break;
                case 'd':
                    BitBoardFigures.DoubleBlue&=~(1L<<start);
                    BitBoardFigures.SingleBlue|=(1L<<start);
                    result += "d";
                    colorRed= false;
                    makeMove(move, false);
                    break;
                case 'm':
                    BitBoardFigures.MixedBlue&=~(1L<<start);
                    BitBoardFigures.SingleRed|=(1L<<start);
                    result += "m";
                    colorRed= false;
                    makeMove(move, false);
                    break;
            }
        }
        return result;
    }

    /**
     * Handles the end of a capture move on the board.
     *
     * @param board the current state of the specified bitboard
     * @param move the move in numerical form
     * @param type the type of bitboard
     * @return the type of bitboard, if it has a piece at the end position of the move, otherwise an empty string
     */
    public static String makeEndCaptureMove(long board, String move, char type){
        int end=(getNumericalCharValue(move.charAt(2))*8)+(getNumericalCharValue(move.charAt(3)));
        String result = "";
        //if statement evaluates to true, when the current bitboard is occupied at the end position
        if ((((board>>>end)&1)==1)&&colorRed) {
            switch(type){
                case 'S':
                    BitBoardFigures.SingleRed&=~(1L<<end);
                    BitBoardFigures.DoubleRed|=(1L<<end);
                    result += "S";
                    capture = true;
                    break;
                case 's': BitBoardFigures.SingleBlue&=~(1L<<end);
                    BitBoardFigures.SingleRed|=(1L<<end);
                    result += "s";
                    capture = true;
                    break;
                case 'd':
                    BitBoardFigures.DoubleBlue&=~(1L<<end);
                    result += "d";
                    BitBoardFigures.MixedRed|=(1L<<end);
                    capture = true;
                    break;
                case 'm':
                    BitBoardFigures.MixedBlue&=~(1L<<end);
                    BitBoardFigures.DoubleRed|=(1L<<end);
                    result += "m";
                    capture = true;
                    break;
            }
        }else if((((board>>>end)&1)==1)&&!colorRed) {
            switch(type){
                case 'S':
                    BitBoardFigures.SingleRed &=~(1L<<end);
                    BitBoardFigures.SingleBlue|=(1L<<end);
                    result += "S";
                    capture = true;
                    break;
                case 'D':
                    BitBoardFigures.DoubleRed&=~(1L<<end);
                    BitBoardFigures.MixedBlue|=(1L<<end);
                    result += "D";
                    capture = true;
                    break;
                case 'M':
                    BitBoardFigures.MixedRed&=~(1L<<end);
                    BitBoardFigures.DoubleBlue|=(1L<<end);
                    result += "M";
                    capture = true;
                    break;
                case 's':
                    BitBoardFigures.SingleBlue&=~(1L<<end);
                    BitBoardFigures.DoubleBlue|=(1L<<end);
                    result += "s";
                    capture = true;
                    break;
            }
        }
        return result;
    }

    /**
     * Handles the end of a move on the board when no capture occurs.
     *
     * @param move the move in string format
     */
    public static void makeEndMove(String move) {
        int end = (getNumericalCharValue(move.charAt(2)) * 8) + (getNumericalCharValue(move.charAt(3)));
        if (colorRed){
            BitBoardFigures.SingleRed|=(1L<<end);
        }else{
            BitBoardFigures.SingleBlue|=(1L<<end);
        }
    }

    /**
     * Undoes the last move made on the board.
     */
    public static void undoMove(){
        String move = unmakeStack.pop();
        long currentStateHash = getGameStateHash(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue, BitBoardFigures.blueToMove);
        gameStateHistory.compute(currentStateHash, (key, value) -> {
            int newValue = value - 1;
            return newValue > 0 ? newValue : null;
        });
        int start=(getNumericalCharValue(move.charAt(0))*8)+(getNumericalCharValue(move.charAt(1)));
        int end=(getNumericalCharValue(move.charAt(2))*8)+(getNumericalCharValue(move.charAt(3)));
        char start_figure =move.charAt(4);
        char end_figure =move.charAt(5);

        switch (start_figure){
            case 'S':
                BitBoardFigures.SingleRed|=(1L<<start);
                colorRed = true;
                break;
            case 'D':
                BitBoardFigures.DoubleRed|=(1L<<start);
                BitBoardFigures.SingleRed&=~(1L<<start);
                colorRed= true;
                break;
            case 'M':
                BitBoardFigures.MixedRed|=(1L<<start);
                BitBoardFigures.SingleBlue&=~(1L<<start);
                colorRed= true;
                break;
            case 's':
                BitBoardFigures.SingleBlue|=(1L<<start);
                colorRed= false;
                break;
            case 'd':
                BitBoardFigures.DoubleBlue|=(1L<<start);
                BitBoardFigures.SingleBlue&=~(1L<<start);
                colorRed= false;
                break;
            case 'm':
                BitBoardFigures.MixedBlue|=(1L<<start);
                BitBoardFigures.SingleRed&=~(1L<<start);
                colorRed= false;
                break;
        }
        switch (end_figure){
            case 'S':
                BitBoardFigures.SingleRed|=(1L<<end);
                if(colorRed) {
                    BitBoardFigures.DoubleRed &= ~(1L << end);
                }else {
                    BitBoardFigures.SingleBlue&= ~(1L<<end);
                }
                break;
            case 'D':
                BitBoardFigures.DoubleRed|=(1L<<end);
                BitBoardFigures.MixedBlue&=~(1L<<end);
                break;
            case 'M':
                BitBoardFigures.MixedRed|=(1L<<end);
                BitBoardFigures.DoubleBlue&=~(1L<<end);
                break;
            case 's':
                BitBoardFigures.SingleBlue|=(1L<<end);
                if (!colorRed) {
                    BitBoardFigures.DoubleBlue &= ~(1L << end);
                }else {
                    BitBoardFigures.SingleRed&= ~(1L<<end);
                }
                break;
            case 'd':
                BitBoardFigures.DoubleBlue|=(1L<<end);
                BitBoardFigures.MixedRed&=~(1L<<end);
                break;
            case 'm':
                BitBoardFigures.MixedBlue|=(1L<<end);
                BitBoardFigures.DoubleRed&=~(1L<<end);
                break;
            case 'e':
                if(colorRed){
                    BitBoardFigures.SingleRed&=~(1L<<end);
                }
                else{
                    BitBoardFigures.SingleBlue&=~(1L<<end);
                }

        }
    }

    /**
     * Converts a character representing a numerical value to its integer value.
     *
     * @param ch the character to convert
     * @return the integer value of the character
     */
    private static int getNumericalCharValue(char ch){
        return (int) ch + ((402667017 & 0x3E0) >> 5) & 0x1F;
    }

    /**
     * Generates a hash representing the current game state.
     *
     * @param SingleRed positions of single red pieces
     * @param SingleBlue positions of single blue pieces
     * @param DoubleRed positions of double red pieces
     * @param DoubleBlue positions of double blue pieces
     * @param MixedRed positions of mixed red pieces
     * @param MixedBlue positions of mixed blue pieces
     * @param blueToMove indicates if it's blue's turn to move
     * @return the hash representing the current game state
     */
    public static long getGameStateHash(long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue, boolean blueToMove) {
        long hash = 7;
        hash = 31 * hash + SingleRed;
        hash = 31 * hash + SingleBlue;
        hash = 31 * hash + DoubleRed;
        hash = 31 * hash + DoubleBlue;
        hash = 31 * hash + MixedRed;
        hash = 31 * hash + MixedBlue;
        hash = 31 * hash + (blueToMove ? 1 : 0);
        return hash;
    }

    /**
     * Adds a transposition value to the transposition table.
     *
     * @param hashedBoard the hash of the board state
     * @param transpositionValues the transposition values to add
     */
    public static void addToTranspositionTable(long hashedBoard, TranspositionValues transpositionValues){
        transpositionTable.put(hashedBoard, transpositionValues);
    }

    /**
     * Returns the transposition values for a given hashed board if present in the transposition table.
     *
     * @param hashedBoard the hash of the board state
     * @return the transposition values if the hashed board is in the transposition table, else null
     */
    public static TranspositionValues elementOfTranspositionTable(long hashedBoard){
        return transpositionTable.get(hashedBoard);
    }

    /**
     * Initializes the Zobrist hash table with unique random bit sequences.
     */
    public static void initZobristTable(){
        Set<Long> uniqueSet = new HashSet<>();

        for (int i = 0; i < 64; i++){
            for (int j = 0; j < 6; j++){
                long randomBitSequence = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
                while (uniqueSet.contains(randomBitSequence) || randomBitSequence == 0L){
                    randomBitSequence = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
                }
                uniqueSet.add(randomBitSequence);
                zobristTable[i][j] = randomBitSequence;
            }
        }
    }

    /**
     * Generates a hash for the current board state.
     *
     * @return the hash representing the current board state
     */
    public static long hashBoard(){
        long hashedBoard = 0L;
        //i only up to 62 because 63 is the corner that isn't used in part of the board in jumpsturdy
        //powerN instead of Math.pow because Math.pow(2, 63) overflows
        for (int i = 1; i < 63; i++){
            //check if board is empty at index i
            long iAsBitBoardPosition = powerN(2, i);
            if((iAsBitBoardPosition & BitBoardFigures.SingleRed) != 0){
                hashedBoard = hashedBoard ^ zobristTable[i][0];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.DoubleRed) != 0){
                hashedBoard = hashedBoard ^ zobristTable[i][1];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.MixedRed) != 0){
                hashedBoard = hashedBoard ^ zobristTable[i][2];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.SingleBlue) != 0){
                hashedBoard = hashedBoard ^ zobristTable[i][3];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.DoubleBlue) != 0){
                hashedBoard = hashedBoard ^ zobristTable[i][4];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.MixedBlue) != 0){
                hashedBoard = hashedBoard ^ zobristTable[i][5];
            }
        }
        return hashedBoard;
    }

    /**
     * Calculates the power of a number.
     * @see <a href="https://stackoverflow.com/questions/29996070/using-int-double-and-long-in-calculation-of-powers">https://stackoverflow.com/questions/29996070/using-int-double-and-long-in-calculation-of-powers</a>}
     * @param number the base number
     * @param power the exponent
     * @return the result of raising the number to the specified power
     */
    public static long powerN(long number, int power){
        long res = 1;
        long sq = number;
        while(power > 0){
            if(power % 2 == 1){
                res *= sq;
            }
            sq = sq * sq;
            power /= 2;
        }
        return res;
    }
}
