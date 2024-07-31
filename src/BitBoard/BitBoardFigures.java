package BitBoard;

/**
 * The BitBoardFigures class represents the state of the game board using bitboards.
 * Bitboards are represented by long integers, where each bit corresponds to a specific square on the board.
 * This class includes fields for different types of pieces (Single, Double, Mixed) and their colors (Red, Blue).
 */
public class BitBoardFigures {
    public static long  SingleRed = 0L;
    public static long SingleBlue = 0L;
    public static long DoubleRed = 0L;
    public static long DoubleBlue = 0L;
    public static long MixedRed = 0L;
    public static long MixedBlue = 0L;
    static long mctsSingleRed = 0L, mctsSingleBlue = 0L, mctsDoubleRed = 0L, mctsDoubleBlue = 0L, mctsMixedRed = 0L, mctsMixedBlue = 0L;
    public static boolean blueToMove = true;
}
