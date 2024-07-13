import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


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
    static final long FILE_A = 36170086419038336L;
    static final long FILE_AB = 217020518514230019L;
    static final long FILE_GH = -4557430888798830400L;
    static final long FILE_H = 72340172838076672L;
    static final long EDGE_0 = 1L;
    static final long EDGE_7 = 128L;
    static final long EDGE_56 =72057594037927936L;
    static final long EDGE_63 = -9223372036854775808L;
    static boolean colorRed = true;
    static boolean capture = false;

    static String startFigure = "";
    static String endFigure = "";
    //String format: 0-3 move, 4 source figure, 5 target figure
    static Stack<String> unmakeStack = new Stack<>();
    public static int moveCounter;
    public static long aiRunningTime = 0L;
    public static int redFigureCount;
    public static int blueFigureCount;
    public static long[][] zobristTable = new long[64][6];
    public static Map<Long, TranspositionValues> transpositionTable = new HashMap<>();
    static Map<Long, Integer> gameStateHistory = new HashMap<>();


    public static void main(String[] args) {
        long startTime = System.nanoTime();
        initZobristTable();
        long endTime = System.nanoTime();
        //System.out.println(Arrays.deepToString(zobristTable));
        System.out.println(endTime - startTime);

        //BitBoard.importFEN("6/4r03/8/8/8/8/8/5r0 b");
        BitBoard.importFEN("2b0b02/1b0b0b0b0b02/8/8/8/8/3r0r0r0r01/1r0r0r02 b");


        long hashedBoard1 = hashBoard();
        System.out.println(hashedBoard1);

        BitBoard.importFEN("2b0b02/1b0b0b0b03/6b01/8/8/8/3r0r0r0r01/1r0r0r02 b");
        long hashedBoard2 = hashBoard();
        System.out.println(hashedBoard2);

        System.out.println(hashedBoard1==hashedBoard2);
        //addToTranspositionTable(hashedBoard, new TranspositionValues(new BitValueMoves(100, "0123", 3), 0.5f, 0.7f));

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

    private static boolean hasPossibleMovesSB(long singleBlue) {
        //Capture left
        long SINGLEBLUE_MOVES = (singleBlue << 7) & BLUE_CAPTURE & ~FILE_A;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture left: " + i);
                return true;
//                System.out.println(singleBlueMoves);
            }
        }
        //Capture right
        SINGLEBLUE_MOVES = (singleBlue << 9) & BLUE_CAPTURE & ~FILE_H;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Capture right: " + i);
                return true;
//                System.out.println(singleBlueMoves);
            }
        }
        //Move 1 forward
        SINGLEBLUE_MOVES = (singleBlue << 8) & EMPTY_BLUE & ~EDGE_63&~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Move forward: " + i);
                return true;
            }
        }
        //Move 1 right
        SINGLEBLUE_MOVES = (singleBlue << 1) & EMPTY_BLUE & ~FILE_H &~EDGE_7;
        for (int i = Long.numberOfTrailingZeros(SINGLEBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(SINGLEBLUE_MOVES); i++) {
            if (((SINGLEBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("Move 1 right: " + i);
                return true;
            }
        }
        //Move 1 left
        SINGLEBLUE_MOVES = (singleBlue >> 1) & EMPTY_BLUE & ~FILE_A & ~EDGE_0;
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
        long KNIGHTBLUE_MOVES = (knightBlue << 15) & EMPTY_KNIGHT_BLUE & ~EDGE_63 & ~-9187201950435770368L;
        for (int i = Long.numberOfTrailingZeros(KNIGHTBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTBLUE_MOVES); i++) {
            if (((KNIGHTBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("1 left 2 down " + i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }
        //1 right 2 down
        KNIGHTBLUE_MOVES = (knightBlue << 17) & EMPTY_KNIGHT_BLUE & ~FILE_H & ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTBLUE_MOVES); i++) {
            if (((KNIGHTBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("1 right 2 down " +i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }
        //2 left 1 down
        KNIGHTBLUE_MOVES = (knightBlue << 6)& EMPTY_KNIGHT_BLUE & ~FILE_GH& ~EDGE_56;
        for (int i = Long.numberOfTrailingZeros(KNIGHTBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTBLUE_MOVES); i++) {
            if (((KNIGHTBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("2 left 1 down " +i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }
        //2 right 1 down
        KNIGHTBLUE_MOVES = (knightBlue << 10) & EMPTY_BLUE & ~FILE_AB & ~EDGE_63;
        for (int i = Long.numberOfTrailingZeros(KNIGHTBLUE_MOVES); i < 64 - Long.numberOfLeadingZeros(KNIGHTBLUE_MOVES); i++) {
            if (((KNIGHTBLUE_MOVES >> i) & 1) == 1) {
//                System.out.println("2 right 1 down " +i);
                return true;
//                System.out.println(knightRedMoves);
            }
        }

        return false;
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

    //If no other pieces/capture at end position
    public static void makeEndMove(String move) {
        int end = (getNumericalCharValue(move.charAt(2)) * 8) + (getNumericalCharValue(move.charAt(3)));
        if (colorRed){
            BitBoardFigures.SingleRed|=(1L<<end);
        }else{
            BitBoardFigures.SingleBlue|=(1L<<end);
        }
    }

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

    //essentially Character.getNumericValue() but only for chars ranging from '0' to '9'
    private static int getNumericalCharValue(char ch){
        return (int) ch + ((402667017 & 0x3E0) >> 5) & 0x1F;
    }

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
}
