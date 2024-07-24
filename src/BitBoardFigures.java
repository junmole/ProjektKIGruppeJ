/**
 * The BitBoardFigures class represents the state of the game board using bitboards.
 * Bitboards are represented by long integers, where each bit corresponds to a specific square on the board.
 * This class includes fields for different types of pieces (Single, Double, Mixed) and their colors (Red, Blue).
 */
public class BitBoardFigures {
    static long  SingleRed = 0L, SingleBlue = 0L, DoubleRed = 0L, DoubleBlue = 0L, MixedRed = 0L, MixedBlue = 0L;
    static long mctsSingleRed = 0L, mctsSingleBlue = 0L, mctsDoubleRed = 0L, mctsDoubleBlue = 0L, mctsMixedRed = 0L, mctsMixedBlue = 0L;
    static boolean blueToMove = true;
}
