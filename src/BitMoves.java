import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Long.bitCount;

public class BitMoves {
    static long RED_CAPTURE;
    static long RED_ROOK;
    static long RED_NON_CAPTURE;
    static long BLUE_CAPTURE;
    static long BLUE_NON_CAPTURE;
    static long BLUE_ROOK;
    static long EMPTY_BLUE;
    static long EMPTY_RED;
    static long EMPTY_KNIGHT_RED;
    static long EMPTY_KNIGHT_BLUE;
    static long FILE_A = 36170086419038336L;
    static long FILE_AB = 217020518514230019L;
    static long FILE_GH = -4557430888798830400L;
    static long FILE_H = 72340172838076672L;
    static long EDGE_0 = 1L;
    static long EDGE_7 = 128L;
    static long EDGE_56 =72057594037927936L;
    static long EDGE_63 = -9223372036854775808L;
    static boolean colorRed = true;
    static boolean capture = false;
    static boolean blueWon;
    static String startFigure = "";
    static String endFigure = "";
    //String format: 0-3 move, 4 source figure, 5 target figure
    static Stack<String> unmakeStack = new Stack<>();
    public static int counter;
    public static int moveCounter;
    public static long aiRunningTime = 0L;
    public static int redFigureCount;
    public static int blueFigureCount;
    public static long[][] zobristTable = new long[64][6];
    public static Map<Long, TranspositionValues> transpositionTable = new HashMap<>();
    private static final Map<Character, Integer> test1 = Map.of('0',0,'1',1,'2',2,'3',3,'4',4,'5',5,'6',6,'7',7,'8',8,'9',9);


    public static void main(String[] args) {
        /*BitBoard.importFEN("bb5/8/rr7/8/8/8/8/6 b");
        String a = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
        String b = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
        //System.out.println("Single Blue: " + BitBoardFigures.SingleBlue);
        //System.out.println("Single Red: " +BitBoardFigures.SingleRed);
        System.out.println("Moves: " + a + "\nLesbare moves: " + possibleMovesToString(a));
        String makeMove = makeMove(a.substring(0, 4), true);
        System.out.println("Make Move: " +makeMove);
        unmakeStack.push(makeMove);
        BitBoard.drawArray(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
        isGameFinished();
        //System.out.println("Single Blue: " +BitBoardFigures.SingleBlue);
        //System.out.println("Single Red: " +BitBoardFigures.SingleRed);
        System.out.println("Undo Move: ");
        undoMove();
        BitBoard.drawArray(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
        //System.out.println("Single Blue: " +BitBoardFigures.SingleBlue);
        //System.out.println("Single Red: " +BitBoardFigures.SingleRed);*/
        long startTime = System.nanoTime();
        initZobristTable();
        long endTime = System.nanoTime();
        System.out.println(endTime - startTime);

        //BitBoard.importFEN("6/4r03/8/8/8/8/8/5r0 b");
        BitBoard.importFEN("2b0b02/1b0b0b0b0b02/8/8/8/8/3r0r0r0r01/1r0r0r02 b");

        startTime = System.nanoTime();
        long hashedBoard = hashBoard();
        endTime = System.nanoTime();
        System.out.println(endTime - startTime);
        //addToTranspositionTable(hashedBoard, new TranspositionValues(new BitValueMoves(100, "0123", 3), 0.5f, 0.7f));

    }

    public static float evaluatePosition(int depth, long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue){
        counter++;
        float value= 0;
        if(isGameFinished()) {
            if (blueWon) {
                return +1000.0f + depth;
            } else{
                return -1000.0f - depth;
            }
        }

        value += bitCount(SingleBlue) * 10;
        for (int i = Long.numberOfTrailingZeros(SingleBlue); i < 64 - Long.numberOfLeadingZeros(SingleBlue); i++) {
            if (((SingleBlue >> i) & 1) == 1) {
                value += (float) ((i/8+1)*2.5);
            }
        }
        value += bitCount(DoubleBlue) * 20;
        for (int i = Long.numberOfTrailingZeros(DoubleBlue); i < 64 - Long.numberOfLeadingZeros(DoubleBlue); i++) {
            if (((DoubleBlue >> i) & 1) == 1) {
                value += (float) (((i/8+1)*2.5)*2);
            }
        }
/*        value += bitCount(MixedBlue) * 20;
        for (int i = Long.numberOfTrailingZeros(MixedBlue); i < 64 - Long.numberOfLeadingZeros(MixedBlue); i++) {
            if (((MixedBlue >> i) & 1) == 1) {
                value += (float) ((i/8+1)*2.5);
            }
        }

 */
        value -= bitCount(SingleRed) * 10;
        for (int i = Long.numberOfTrailingZeros(SingleRed); i < 64 - Long.numberOfLeadingZeros(SingleRed); i++) {
            if (((SingleRed >> i) & 1) == 1) {
                value -= (float) ((8-i/8)*2.5);
            }
        }
        value -= bitCount(DoubleRed) * 20;
        for (int i = Long.numberOfTrailingZeros(DoubleRed); i < 64 - Long.numberOfLeadingZeros(DoubleRed); i++) {
            if (((DoubleRed >> i) & 1) == 1) {
                value -= (float) (((8-i/8)*2.5)*2);
            }
        }
/*        value -= bitCount(MixedRed) * 20;
        for (int i = Long.numberOfTrailingZeros(MixedRed); i < 64 - Long.numberOfLeadingZeros(MixedRed); i++) {
            if (((MixedRed >> i) & 1) == 1) {
                value -= (float) ((8-i/8)*2.5);
            }
        }

 */
        return value;
    }

    public static String possibleMovesBlue(long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue) {
        BLUE_CAPTURE = SingleRed | DoubleRed | MixedRed;
        BLUE_NON_CAPTURE = ~(DoubleBlue | MixedBlue);
        BLUE_ROOK = ~(SingleBlue);
        EMPTY_BLUE = ~(SingleRed | DoubleRed | MixedRed | DoubleBlue | MixedBlue);
        EMPTY_KNIGHT_BLUE = ~(DoubleBlue | MixedBlue);
        return possibleMovesNB(DoubleBlue) + possibleMovesSB(SingleBlue)+possibleMovesNB(MixedBlue);
    }

    public static String possibleMovesRed(long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue) {
        RED_CAPTURE = SingleBlue | DoubleBlue | MixedBlue;
        RED_NON_CAPTURE = ~(DoubleRed | MixedRed);
        RED_ROOK = ~(SingleRed);
        EMPTY_RED = ~(DoubleRed | MixedRed | DoubleBlue | MixedBlue| SingleBlue);
        EMPTY_KNIGHT_RED = ~(DoubleRed | MixedRed);
        return possibleMovesNR(DoubleRed) + possibleMovesSR(SingleRed)+possibleMovesNR(MixedRed);
    }
    // Erklärung: i ist die Position, auf die die Figur kann z.B. 51, dann kann die Figur auf das Feld 51
    // Felder werden von unten gezählt = erste Reihe beginnt bei 0
    // es werden immer 4 Zahlen zu dem String hinzugefügt für jeden möglichen Move, die das Feld beschreiben
    // erste Zahl: Reihe des Startzustandes (bei 0 angefangen, von oben nach unten gezählt)
    // zweite Zahl: Feld von links Startzustand
    // dritte Zahl: Abstand nach oben Zielfeldes
    // vierte Zahl: Abstand links Zielfeld
    private static String possibleMovesSR(long singleRed) {
        String singleRedMoves = "";
        //Capture right
        long SINGLERED_MOVES = (singleRed >> 7) & RED_CAPTURE & ~FILE_H;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture right " +(i));
                singleRedMoves += "" + (i / 8 + 1) + (i % 8 - 1) + (i / 8) + (i % 8);
//                System.out.println(singleRedMoves);
            }
        }
        //Capture left
        SINGLERED_MOVES = (singleRed >> 9) & RED_CAPTURE & ~FILE_A;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture left " + (i));
                singleRedMoves += "" + (i / 8 + 1) + (i % 8+1) + (i / 8) + (i % 8);
//                System.out.println(singleRedMoves);
            }
        }
        //Move 1 forward
        SINGLERED_MOVES =(singleRed >> 8) & EMPTY_RED & ~EDGE_7 &~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Move forward " +i);
                singleRedMoves += "" + (i / 8 + 1) + (i % 8) + (i / 8) + (i % 8);
//                System.out.println(singleRedMoves);
            }
        }
        //Move 1 left
        SINGLERED_MOVES = (singleRed >>1) & EMPTY_RED & ~FILE_A & ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 1 left " +i);
                singleRedMoves += "" + (i / 8) + (i % 8 + 1) + (i / 8) + (i % 8);
//                System.out.println(singleRedMoves);
            }
        }
        //Move 1 right
        SINGLERED_MOVES = (singleRed << 1) & EMPTY_RED & ~FILE_H & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 1 right " +i);
                singleRedMoves += "" + (i / 8) + (i % 8 - 1) + (i / 8) + (i % 8);
//                System.out.println(singleRedMoves);
            }
        }
        return singleRedMoves;
    }

    private static String possibleMovesNR(long knightRed) {
        String knightRedMoves = "";

        //1 right 2 up
        long KNIGHTRED_MOVES = (knightRed >> 15) & EMPTY_KNIGHT_RED & ~FILE_H & ~EDGE_7 &~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("1 right 2 up " + i);
                knightRedMoves += "" + (i / 8 + 2) + (i % 8 - 1) + (i / 8) + (i % 8);
//                System.out.println(knightRedMoves);
            }
        }
        //1 left 2 up
        KNIGHTRED_MOVES = (knightRed >> 17) & EMPTY_KNIGHT_RED & ~FILE_A & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("1 left 2 up " +i);
                knightRedMoves += "" + (i / 8 + 2) + (i % 8 + 1) + (i / 8) + (i % 8);
//                System.out.println(knightRedMoves);
            }
        }
        //2 left 1 up
        KNIGHTRED_MOVES = (knightRed >> 10)& EMPTY_KNIGHT_RED & ~FILE_GH & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("2 left 1 up " +i);
                knightRedMoves += "" + (i / 8 + 1) + (i % 8 + 2) + (i / 8) + (i % 8);
//                System.out.println(knightRedMoves);
            }
        }
        //2 right 1 up
        KNIGHTRED_MOVES = (knightRed >> 6) & EMPTY_RED & ~FILE_AB &~EDGE_7;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 2 right 1 up " +i);
                knightRedMoves += "" + (i / 8 + 1) + (i % 8 - 2) + (i / 8) + (i % 8);
//                System.out.println(knightRedMoves);
            }
        }

        return knightRedMoves;
    }

    private static String possibleMovesSB(long singleRed) {
        String singleBlueMoves = "";

        //Capture left
        long SINGLEBLUE_MOVES = (singleRed << 7) & BLUE_CAPTURE & ~FILE_A;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture left: " + i);
                singleBlueMoves += "" + (i / 8 - 1) + (i % 8 + 1) + (i / 8) + (i % 8);
//                System.out.println(singleBlueMoves);
            }
        }
        //Capture right
        SINGLEBLUE_MOVES = (singleRed << 9) & BLUE_CAPTURE & ~FILE_H;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture right: " + i);
                singleBlueMoves += "" + (i / 8 - 1) + (i % 8 - 1) + (i / 8) + (i % 8);
//                System.out.println(singleBlueMoves);
            }
        }
        //Move 1 forward
        SINGLEBLUE_MOVES = (singleRed << 8) & EMPTY_BLUE & ~EDGE_63&~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Move forward: " + i);
                singleBlueMoves += "" + (i / 8 - 1) + (i % 8) + (i / 8) + (i % 8);
            }
        }
        //Move 1 right
        SINGLEBLUE_MOVES = (singleRed << 1) & EMPTY_BLUE & ~FILE_H &~EDGE_7;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 1 right: " + i);
                singleBlueMoves += "" + (i / 8) + (i % 8 - 1) + (i / 8) + (i % 8);
            }
        }
        //Move 1 left
        SINGLEBLUE_MOVES = (singleRed >> 1) & EMPTY_BLUE & ~FILE_A & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 1 left: " + i);
                singleBlueMoves += "" + (i / 8) + (i % 8 + 1) + (i / 8) + (i % 8);
            }
        }
        return singleBlueMoves;
    }

    private static String possibleMovesNB(long knightBlue) {
        String knightBlueMoves = "";
        //1 left 2 down
        long KNIGHTRED_MOVES = (knightBlue << 15) & EMPTY_KNIGHT_BLUE & ~FILE_A & ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("1 left 2 down " + i);
                knightBlueMoves += "" + (i / 8-2) + (i % 8 + 1) + (i / 8) + (i % 8);
//                System.out.println(knightRedMoves);
            }
        }
        //1 right 2 down
        KNIGHTRED_MOVES = (knightBlue << 17) & EMPTY_KNIGHT_BLUE & ~FILE_H & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("1 right 2 down " +i);
                knightBlueMoves += "" + (i / 8 - 2) + (i % 8 - 1) + (i / 8) + (i % 8);
//                System.out.println(knightRedMoves);
            }
        }
        //2 left 1 down
        KNIGHTRED_MOVES = (knightBlue << 6)& EMPTY_KNIGHT_BLUE & ~FILE_GH& ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("2 left 1 down " +i);
                knightBlueMoves += "" + (i / 8 - 1) + (i % 8+2) + (i / 8) + (i % 8);
//                System.out.println(knightRedMoves);
            }
        }
        //2 right 1 down
        KNIGHTRED_MOVES = (knightBlue << 10) & EMPTY_BLUE & ~FILE_AB & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("2 right 1 down " +i);
                knightBlueMoves += "" + (i / 8 - 1) + (i % 8 -2 ) + (i / 8) + (i % 8);
//                System.out.println(knightRedMoves);
            }
        }

        return knightBlueMoves;
    }


    //for improved isGameFinished()

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
    // Erklärung: i ist die Position, auf die die Figur kann z.B. 51, dann kann die Figur auf das Feld 51
    // Felder werden von unten gezählt = erste Reihe beginnt bei 0
    // es werden immer 4 Zahlen zu dem String hinzugefügt für jeden möglichen Move, die das Feld beschreiben
    // erste Zahl: Reihe des Startzustandes (bei 0 angefangen, von oben nach unten gezählt)
    // zweite Zahl: Feld von links Startzustand
    // dritte Zahl: Abstand nach oben Zielfeldes
    // vierte Zahl: Abstand links Zielfeld
    private static boolean hasPossibleMovesSR(long singleRed) {
        //Capture right
        long SINGLERED_MOVES = (singleRed >> 7) & RED_CAPTURE & ~FILE_H;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture right " +(i));
                return true;
//                System.out.println(singleRedMoves);
            }
        }
        //Capture left
        SINGLERED_MOVES = (singleRed >> 9) & RED_CAPTURE & ~FILE_A;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture left " + (i));
                return true;
//                System.out.println(singleRedMoves);
            }
        }
        //Move 1 forward
        SINGLERED_MOVES =(singleRed >> 8) & EMPTY_RED & ~EDGE_7 &~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Move forward " +i);
                return true;
//                System.out.println(singleRedMoves);
            }
        }
        //Move 1 left
        SINGLERED_MOVES = (singleRed >>1) & EMPTY_RED & ~FILE_A & ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 1 left " +i);
                return true;
//                System.out.println(singleRedMoves);
            }
        }
        //Move 1 right
        SINGLERED_MOVES = (singleRed << 1) & EMPTY_RED & ~FILE_H & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(SINGLERED_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLERED_MOVES); i++) {
            if (((SINGLERED_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 1 right " +i);
                return true;
//                System.out.println(singleRedMoves);
            }
        }
        return false;
    }

    private static boolean hasPossibleMovesNR(long knightRed) {
        //1 right 2 up
        long KNIGHTRED_MOVES = (knightRed >> 15) & EMPTY_KNIGHT_RED & ~FILE_H & ~EDGE_7 &~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("1 right 2 up " + i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }
        //1 left 2 up
        KNIGHTRED_MOVES = (knightRed >> 17) & EMPTY_KNIGHT_RED & ~FILE_A & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("1 left 2 up " +i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }
        //2 left 1 up
        KNIGHTRED_MOVES = (knightRed >> 10)& EMPTY_KNIGHT_RED & ~FILE_GH & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("2 left 1 up " +i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }
        //2 right 1 up
        KNIGHTRED_MOVES = (knightRed >> 6) & EMPTY_RED & ~FILE_AB &~EDGE_7;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 2 right 1 up " +i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }

        return false;
    }

    private static boolean hasPossibleMovesSB(long singleRed) {
        //Capture left
        long SINGLEBLUE_MOVES = (singleRed << 7) & BLUE_CAPTURE & ~FILE_A;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture left: " + i);
                return true;
//                System.out.println(singleBlueMoves);
            }
        }
        //Capture right
        SINGLEBLUE_MOVES = (singleRed << 9) & BLUE_CAPTURE & ~FILE_H;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture right: " + i);
                return true;
//                System.out.println(singleBlueMoves);
            }
        }
        //Move 1 forward
        SINGLEBLUE_MOVES = (singleRed << 8) & EMPTY_BLUE & ~EDGE_63&~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Move forward: " + i);
                return true;
            }
        }
        //Move 1 right
        SINGLEBLUE_MOVES = (singleRed << 1) & EMPTY_BLUE & ~FILE_H &~EDGE_7;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 1 right: " + i);
                return true;
            }
        }
        //Move 1 left
        SINGLEBLUE_MOVES = (singleRed >> 1) & EMPTY_BLUE & ~FILE_A & ~EDGE_0;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 1 left: " + i);
                return true;
            }
        }
        return false;
    }

    private static boolean hasPossibleMovesNB(long knightBlue) {
        //1 left 2 down
        long KNIGHTRED_MOVES = (knightBlue << 15) & EMPTY_KNIGHT_BLUE & ~FILE_A & ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("1 left 2 down " + i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }
        //1 right 2 down
        KNIGHTRED_MOVES = (knightBlue << 17) & EMPTY_KNIGHT_BLUE & ~FILE_H & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("1 right 2 down " +i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }
        //2 left 1 down
        KNIGHTRED_MOVES = (knightBlue << 6)& EMPTY_KNIGHT_BLUE & ~FILE_GH& ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("2 left 1 down " +i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }
        //2 right 1 down
        KNIGHTRED_MOVES = (knightBlue << 10) & EMPTY_BLUE & ~FILE_AB & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(KNIGHTRED_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTRED_MOVES); i++) {
            if (((KNIGHTRED_MOVES >> i) & 1) == 1) {
//                System.out.println("2 right 1 down " +i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }

        return false;
    }




    public static void undoMove(){
        String move = unmakeStack.pop();
        //int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
        //int end=(Character.getNumericValue(move.charAt(2))*8)+(Character.getNumericValue(move.charAt(3)));
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

    public static String makeMove(String moves, boolean start){
        //startfeld

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
        }
        //wenn auf dem Zielfeld kein capture stattgefunden hat
        if(endFigure.isEmpty()) {
            makeEndMove(moves);
            endFigure = "e";
        }

        return moves + startFigure + endFigure;
    }

    public static String makeStartMove(long board, String move, char type) {
        //int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
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

    //essentially Character.getNumericValue() but only for chars ranging from '0' to '9'
    private static int getNumericalCharValue(char ch){
        return (int) ch + ((402667017 & 0x3E0) >> 5) & 0x1F;
    }



    public static String makeEndCaptureMove(long board, String move, char type) {
        //int end=(Character.getNumericValue(move.charAt(2))*8)+(Character.getNumericValue(move.charAt(3)));
        int end=(getNumericalCharValue(move.charAt(2))*8)+(getNumericalCharValue(move.charAt(3)));
        String result = "";
        //if statement is only true, when the current bitboard is occupied at the end position
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
//            System.out.println("capture blue");
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

    //If no other pieces/capture at end position
    public static void makeEndMove(String move) {
        //int end = (Character.getNumericValue(move.charAt(2)) * 8) + (Character.getNumericValue(move.charAt(3)));
        int end = (getNumericalCharValue(move.charAt(2)) * 8) + (getNumericalCharValue(move.charAt(3)));
        if (colorRed){
            BitBoardFigures.SingleRed|=(1L<<end);
        }else{
            BitBoardFigures.SingleBlue|=(1L<<end);
        }
    }

    public static void drawBitboard(long bitBoard) {
        String chessBoard[][]=new String[8][8];
        for (int i=0;i<64;i++) {
            chessBoard[i/8][i%8]="";
        }
        for (int i=0;i<64;i++) {
            if (((bitBoard>>>i)&1)==1) {chessBoard[i/8][i%8]="P";}
            if ("".equals(chessBoard[i/8][i%8])) {chessBoard[i/8][i%8]=" ";}
        }
        for (int i=0;i<8;i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    }

    public static boolean isGameFinished(){
        // Constants for specific bitboard configurations
        final long BLUE_WIN_CONDITION = 9079256848778919936L; // Binary number with the first 6 bits as 1
        final long RED_WIN_CONDITION = 126L; // Specific condition for red

        // Check if blue on red home row
        if ((BitBoardFigures.SingleBlue & BLUE_WIN_CONDITION) != 0 || (BitBoardFigures.MixedBlue & BLUE_WIN_CONDITION) != 0 || (BitBoardFigures.DoubleBlue & BLUE_WIN_CONDITION) != 0){
            blueWon= true;
            return true;
        }
        // Check if blue on red home row
        else if ((BitBoardFigures.SingleRed & RED_WIN_CONDITION) != 0 || (BitBoardFigures.MixedRed & RED_WIN_CONDITION) != 0 || (BitBoardFigures.DoubleRed & RED_WIN_CONDITION) != 0){
            blueWon =false;
            return true;
        }

        //No more red pieces on board
        else if(BitBoardFigures.SingleRed == 0 && BitBoardFigures.MixedRed == 0 && BitBoardFigures.DoubleRed == 0){
            blueWon =true;
            return true;
        }
        //No more blue pieces on board
        else if(BitBoardFigures.SingleBlue == 0 && BitBoardFigures.MixedBlue == 0 && BitBoardFigures.DoubleBlue == 0){
            blueWon =false;
            return true;
        }
        //No more moves for player who is am zug
        else if (BitBoardFigures.blueToMove) {
            //OLD
            /*String moves = possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if(moves.isEmpty()){
                System.out.println("no moves left");
                blueWon =false;
                return true;
            }*/
            //NEW
            boolean hasMovesLeft = hasPossibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if (!hasMovesLeft){
                blueWon = false;
                return true;
            }
        }
        else if(!BitBoardFigures.blueToMove){
            //OLD
            /*String moves = possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if(moves.isEmpty()){
                System.out.println("no moves left");
                blueWon = true;
                return true;
            }*/
            //NEW
            boolean hasMovesLeft = hasPossibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if (!hasMovesLeft){
                blueWon = true;
                return true;
            }
        }

        return false;

    }

    public static void addToTranspositionTable(long hashedBoard, TranspositionValues transpositionValues){
        //long hashedBoard = hashBoard();
        transpositionTable.put(hashedBoard, transpositionValues);
    }

    //returns TranspositionValues if hashed Board is in transpositionTable, else null
    public static TranspositionValues elementOfTranspositionTable(long hashedBoard){
        //long hashedBoard = hashBoard();
        return transpositionTable.get(hashedBoard);
    }

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

    public static long hashBoard(){
        long hashedBoard = 0L;
        //i only up to 62 because 63 is the corner that isn't used in part of the board in jumpsturdy
        //powerN instead of Math.pow because Math.pow(2, 63) overflows
        for (int i = 1; i < 63; i++){
            //check if board is empty at index i
            long iAsBitBoardPosition = powerN(2, i);
            if((iAsBitBoardPosition & BitBoardFigures.SingleRed) != 0){
                hashedBoard = hashedBoard | zobristTable[i][0];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.DoubleRed) != 0){
                hashedBoard = hashedBoard | zobristTable[i][1];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.MixedRed) != 0){
                hashedBoard = hashedBoard | zobristTable[i][2];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.SingleBlue) != 0){
                hashedBoard = hashedBoard | zobristTable[i][3];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.DoubleBlue) != 0){
                hashedBoard = hashedBoard | zobristTable[i][4];
                continue;
            }
            if((iAsBitBoardPosition & BitBoardFigures.MixedBlue) != 0){
                hashedBoard = hashedBoard | zobristTable[i][5];
                continue;
            }
        }
        return hashedBoard;
    }

    //https://stackoverflow.com/questions/29996070/using-int-double-and-long-in-calculation-of-powers
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

    public static String possibleMovesToString(String moves){
        String readableMoves = "";
        for (int i = 0; i<moves.length(); i+=4){
            readableMoves += BitBoard.moveToString(moves.substring(i, i + 4)) + ",";
        }
        return readableMoves;
    }

    /*
        public static void makeMove(String moves, boolean start){
        //startfeld
        if (start) {
            BitMoves.makeStartMove(BitBoard.SingleRedCopy, moves, 'S');
            BitMoves.makeStartMove(BitBoard.SingleBlueCopy, moves, 's');
            BitMoves.makeStartMove(BitBoard.DoubleRedCopy, moves, 'D');
            BitMoves.makeStartMove(BitBoard.DoubleBlueCopy, moves, 'd');
            BitMoves.makeStartMove(BitBoard.MixedRedCopy, moves, 'M');
            BitMoves.makeStartMove(BitBoard.MixedBlueCopy, moves, 'm');

            //Zielfeld für capture Moves
        }else{
            BitMoves.makeEndCaptureMove(BitBoard.SingleRedCopy, moves, 'S');
            BitMoves.makeEndCaptureMove(BitBoard.SingleBlueCopy, moves, 's');
            BitMoves.makeEndCaptureMove(BitBoard.DoubleRedCopy, moves, 'D');
            BitMoves.makeEndCaptureMove(BitBoard.DoubleBlueCopy, moves, 'd');
            BitMoves.makeEndCaptureMove(BitBoard.MixedRedCopy, moves, 'M');
            BitMoves.makeEndCaptureMove(BitBoard.MixedBlueCopy, moves, 'm');
        }
        //wenn auf dem Zielfeld kein capture stattgefunden hat
        if (!capture){
            makeEndMove(moves);
        }
    }

    public static void makeStartMove(long board, String move, char type) {
        int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
        //if statement is only true, when the current bitboard is occupied at the start position
        if (((board>>>start)&1)==1) {
            switch(type){
                case 'S':
                    BitBoard.SingleRedCopy&=~(1L<<start);
                    makeMove(move, false);
                    break;
                case 'D':
                    BitBoard.DoubleRedCopy&=~(1L<<start);
                    BitBoard.SingleRedCopy|=(1L<<start);
                    makeMove(move, false);
                    break;
                case 'M':
                    BitBoard.MixedRedCopy&=~(1L<<start);
                    BitBoard.SingleBlueCopy|=(1L<<start);
                    makeMove(move, false);
                    break;
                case 's':
                    BitBoard.SingleBlueCopy&=~(1L<<start);
                    colorRed= false;
                    makeMove(move, false);
                    break;
                case 'd':
                    BitBoard.DoubleBlueCopy&=~(1L<<start);
                    BitBoard.SingleBlueCopy|=(1L<<start);
                    colorRed= false;
                    makeMove(move, false);
                    break;
                case 'm': BitBoardFigures.MixedBlue&=~(1L<<start);
                    BitBoard.SingleRedCopy|=(1L<<start);
                    colorRed= false;
                    makeMove(move, false);
                    break;
            }
        }
    }

    public static void makeEndCaptureMove(long board, String move, char type) {
        int end=(Character.getNumericValue(move.charAt(2))*8)+(Character.getNumericValue(move.charAt(3)));
        //if statement is only true, when the current bitboard is occupied at the end position
        if ((((board>>>end)&1)==1)&&colorRed) {
            switch(type){
                case 'S': BitBoard.SingleRedCopy&=~(1L<<end);
                    BitBoard.DoubleRedCopy|=(1L<<end);
                    capture = true;
                    break;
                case 's': BitBoard.SingleBlueCopy&=~(1L<<end);
                    capture = true;
                    break;
                case 'd': BitBoard.DoubleBlueCopy&=~(1L<<end);
                    BitBoard.MixedRedCopy|=(1L<<end);
                    capture = true;
                    break;
                case 'm': BitBoard.MixedBlueCopy&=~(1L<<end);
                    BitBoard.DoubleRedCopy|=(1L<<end);
                    capture = true;
                    break;
            }
        }else if((((board>>>end)&1)==1)&&!colorRed) {
//            System.out.println("capture blue");
            switch(type){
                case 'S': BitBoard.SingleRedCopy&=~(1L<<end);
                    capture = true;
                    break;
                case 'D': BitBoard.DoubleRedCopy&=~(1L<<end);
                    BitBoard.MixedBlueCopy|=(1L<<end);
                    capture = true;
                    break;
                case 'M': BitBoard.MixedRedCopy&=~(1L<<end);
                    BitBoard.DoubleBlueCopy|=(1L<<end);
                    capture = true;
                    break;
                case 's': BitBoard.SingleBlueCopy&=~(1L<<end);
                    BitBoard.DoubleBlueCopy|=(1L<<end);
                    capture = true;
                    break;
            }
        }
    }

    //If no other pieces/capture at end position
    public static void makeEndMove(String move) {
        int end = (Character.getNumericValue(move.charAt(2)) * 8) + (Character.getNumericValue(move.charAt(3)));
        if (colorRed){
            BitBoard.SingleRedCopy|=(1L<<end);
        }else{
            BitBoard.SingleBlueCopy|=(1L<<end);
        }
    }
*/
    /*

    long  SingleRedt  = BitMoves.makeMove(BitBoardFigures.SingleRed, moves.substring(i, i + 4), 'S'), SingleBluet = BitMoves.makeMove(BitBoardFigures.SingleBlue, moves.substring(i, i + 4), 'S'),
            DoubleRedt = BitMoves.makeMove(BitBoardFigures.DoubleRed, moves.substring(i, i + 4), 'B'), DoubleBluet = BitMoves.makeMove(BitBoardFigures.DoubleBlue, moves.substring(i, i + 4), 'R'),
            MixedRedt  = BitMoves.makeMove(BitBoardFigures.MixedRed, moves.substring(i, i + 4), 'Q'), MixedBluet  = BitMoves.makeMove(BitBoardFigures.MixedBlue, moves.substring(i, i + 4), 'K');



    public static void makeMoveFEN(String fen, String moves, String type) {
        BitBoard.importFEN(fen);
        long SingleRedt = 0;
        long SingleBluet = 0;
        long DoubleRedt = 0;
        long DoubleBluet = 0;
        long MixedRedt = 0;
        long MixedBluet = 0;
        for (int i = 0; i <= (moves.length() - 4); i++) {
            SingleRedt = BitMoves.makeMove(BitBoardFigures.SingleRed, moves.substring(i, i + 4), type);
            SingleBluet = BitMoves.makeMove(BitBoardFigures.SingleBlue, moves.substring(i, i + 4), type);
            DoubleRedt = BitMoves.makeMove(BitBoardFigures.DoubleRed, moves.substring(i, i + 4), type);
            DoubleBluet = BitMoves.makeMove(BitBoardFigures.DoubleBlue, moves.substring(i, i + 4), type);
            MixedRedt = BitMoves.makeMove(BitBoardFigures.MixedRed, moves.substring(i, i + 4), type);
            MixedBluet = BitMoves.makeMove(BitBoardFigures.MixedBlue, moves.substring(i, i + 4), type);
        }
        BitBoard.drawArray(SingleRedt, SingleBluet, DoubleRedt, DoubleBluet, MixedRedt, MixedBluet);
    }

    public static long makeMove(long board, String move, String type){
        //Single move
        //if(type == 'S')

        int start=(Character.getNumericValue(move.charAt(0))*8)+(Character.getNumericValue(move.charAt(1)));
        int end=(Character.getNumericValue(move.charAt(2))*8)+(Character.getNumericValue(move.charAt(3)));
        //if statement is only true, when the current bitboard is occupied at the start position
        if (((board>>>start)&1)==1) {
            //Remove piece from start position
            board&=~(1L<<start);
            if (Objects.equals(type, "r0")){
                //end position has a SingleRed
                if(((board>>>end)&1)==1){
                    //Remove r0 at end position, as it has been stacked on
                    board &=~(1L<<end);
                    BitBoardFigures.DoubleRed |=(1L<<end);
                }
                //if end position has a SingleBlue
                else if (((BitBoardFigures.SingleBlue>>>end)&1) == 1) {
                    //Remove b0 from end position, as it has been captured
                    BitBoardFigures.SingleBlue &=~(1L<<end);
                    //r0 is moved to the end position, board must be SingleRed if this point is reached
                    board|=(1L<<end);
                }
                //if end position has a DoubleBlue
                else if (((BitBoardFigures.DoubleBlue>>>end)&1) == 1) {
                    //Remove bb from end position, as it has been captured
                    BitBoardFigures.DoubleBlue &=~(1L<<end);
                    //br is formed at the end position
                    BitBoardFigures.MixedRed |=(1L<<end);
                    //if end position has a MixedBlue
                } else if (((BitBoardFigures.MixedBlue>>>end)&1) == 1) {
                    //Remove rb from end position, as it has been captured
                    BitBoardFigures.MixedBlue &=~(1L<<end);
                    //rr is formed at the end position
                    BitBoardFigures.DoubleRed |=(1L<<end);
                }
                //if end position is empty
                else{
                    board|=(1L<<end);
                }
            }
            else if (Objects.equals(type, "b0")){
                //end position has a SingleBlue
                if(((board>>>end)&1)==1){
                    //Remove b0 at end position, as it has been stacked on
                    board &=~(1L<<end);
                    //bb is formed at end position
                    BitBoardFigures.DoubleBlue |=(1L<<end);
                }
                //if end position has a SingleRed
                else if (((BitBoardFigures.SingleRed>>>end)&1) == 1) {
                    //Remove r0 from end position, as it has been captured
                    BitBoardFigures.SingleRed &=~(1L<<end);
                    //b0 is moved to the end position, board must be SingleBlue if this point is reached
                    board|=(1L<<end);
                }
                //if end position has a DoubleRed
                else if (((BitBoardFigures.DoubleRed>>>end)&1) == 1) {
                    //Remove rr from end position, as it has been captured
                    BitBoardFigures.DoubleRed &=~(1L<<end);
                    //rb is formed at the end position
                    BitBoardFigures.MixedBlue |=(1L<<end);

                }
                //if end position has a MixedRed
                else if (((BitBoardFigures.MixedRed>>>end)&1) == 1) {
                    //Remove br from end position, as it has been captured
                    BitBoardFigures.MixedRed &=~(1L<<end);
                    //bb is formed at the end position
                    BitBoardFigures.DoubleBlue |=(1L<<end);
                }
                //if end position is empty
                else{
                    board|=(1L<<end);
                }

            }
            else if(Objects.equals(type, "rr") || Objects.equals(type, "br")){
                switch (type) {
                    case "rr":
                        //r0 is left at start position
                        BitBoardFigures.SingleRed |=(1L<<start);
                    case "br":
                        //b0 remains at start position
                        BitBoardFigures.SingleBlue |=(1L<<start);
                }
                //if end position has a r0
                if(((BitBoardFigures.SingleRed>>>end)&1) == 1){
                    //Remove r0 from end position, as it has been stacked on
                    BitBoardFigures.SingleRed &=~(1L<<end);
                    //Add rr at end position
                    BitBoardFigures.DoubleRed |=(1L<<end);
                }
                //if end position has a b0
                if(((BitBoardFigures.SingleBlue>>>end)&1) == 1){
                    //Remove b0 from end position, as it has been captured
                    BitBoardFigures.SingleBlue &=~(1L<<end);
                    //Add br at end position
                    BitBoardFigures.MixedRed |=(1L<<end);
                }
                //if end position has a bb
                if(((BitBoardFigures.DoubleBlue>>>end)&1) == 1){
                    //Remove bb from end position, as it has been captured
                    BitBoardFigures.DoubleBlue &=~(1L<<end);
                    //Add br at end position
                    BitBoardFigures.MixedRed |=(1L<<end);

                }
                //if end position has a rb
                if(((BitBoardFigures.MixedBlue>>>end)&1) == 1){
                    //Remove rb from end position, as it has been captured
                    BitBoardFigures.MixedBlue &=~(1L<<end);
                    //Add rr at end position
                    BitBoardFigures.DoubleRed |=(1L<<end);
                }
                //if end position is empty
                else{
                    //r0 is moved to end position
                    BitBoardFigures.SingleRed |=(1L<<end);
                }
            }
            else if(Objects.equals(type, "bb") || Objects.equals(type, "rb")){
                switch (type) {
                    case "rb":
                        //r0 is left at start position
                        BitBoardFigures.SingleRed |=(1L<<start);
                    case "bb":
                        //b0 remains at start position
                        BitBoardFigures.SingleBlue |=(1L<<start);
                }
                //if end position has a r0
                if(((BitBoardFigures.SingleRed>>>end)&1) == 1){
                    //Remove r0 from end position, as it has been captured
                    BitBoardFigures.SingleRed &=~(1L<<end);
                    //Add rb at end position
                    BitBoardFigures.MixedBlue |=(1L<<end);
                }
                //if end position has a b0
                if(((BitBoardFigures.SingleBlue>>>end)&1) == 1){
                    //Remove b0 from end position, as it has been stacked on
                    BitBoardFigures.SingleBlue &=~(1L<<end);
                    //Add bb at end position
                    BitBoardFigures.DoubleBlue |=(1L<<end);
                }
                //if end position has a rr
                if(((BitBoardFigures.DoubleRed>>>end)&1) == 1){
                    //Remove rr from end position, as it has been captured
                    BitBoardFigures.DoubleRed &=~(1L<<end);
                    //Add rb at end position
                    BitBoardFigures.MixedBlue |=(1L<<end);

                }
                //if end position has a br
                if(((BitBoardFigures.MixedRed>>>end)&1) == 1){
                    //Remove br from end position, as it has been captured
                    BitBoardFigures.MixedRed &=~(1L<<end);
                    //Add bb at end position
                    BitBoardFigures.DoubleBlue |=(1L<<end);
                }
                //if end position is empty
                else{
                    //b0 is moved to end position
                    BitBoardFigures.SingleBlue |=(1L<<start);
                }

            }
        }
        return board;
    }

     */
}
