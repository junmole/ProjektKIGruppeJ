import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for BitMoves
 */
public class BitMovesTest {
    @Test
    public void testPossibleMovesBlue() {
        long singleRed = 0x0000000000000010L;
        long singleBlue = 0x0000000000000020L;
        long doubleRed = 0x0000000000000040L;
        long doubleBlue = 0x0000000000000080L;
        long mixedRed = 0x0000000000000100L;
        long mixedBlue = 0x0000000000000200L;

        String moves = BitMoves.possibleMovesBlue(singleRed, singleBlue, doubleRed, doubleBlue, mixedRed, mixedBlue);
        assertNotNull(moves);
        assertFalse(moves.isEmpty());
    }

    @Test
    public void testPossibleMovesRed() {
        long singleRed = 0x0000000000000010L;
        long singleBlue = 0x0000000000000020L;
        long doubleRed = 0x0000000000000040L;
        long doubleBlue = 0x0000000000000080L;
        long mixedRed = 0x0000000000000100L;
        long mixedBlue = 0x0000000000000200L;

        String moves = BitMoves.possibleMovesRed(singleRed, singleBlue, doubleRed, doubleBlue, mixedRed, mixedBlue);
        assertNotNull(moves);
        assertFalse(moves.isEmpty());
    }

    @Test
    public void testHasPossibleMovesBlue() {
        long singleRed = 0x0000000000000010L;
        long singleBlue = 0x0000000000000020L;
        long doubleRed = 0x0000000000000040L;
        long doubleBlue = 0x0000000000000080L;
        long mixedRed = 0x0000000000000100L;
        long mixedBlue = 0x0000000000000200L;

        boolean hasMoves = BitMoves.hasPossibleMovesBlue(singleRed, singleBlue, doubleRed, doubleBlue, mixedRed, mixedBlue);
        assertTrue(hasMoves);
    }

    @Test
    public void testHasPossibleMovesRed() {
        long singleRed = 0x0000000000000010L;
        long singleBlue = 0x0000000000000020L;
        long doubleRed = 0x0000000000000040L;
        long doubleBlue = 0x0000000000000080L;
        long mixedRed = 0x0000000000000100L;
        long mixedBlue = 0x0000000000000200L;

        boolean hasMoves = BitMoves.hasPossibleMovesRed(singleRed, singleBlue, doubleRed, doubleBlue, mixedRed, mixedBlue);
        assertTrue(hasMoves);
    }

    @Test
    public void testMakeMove() {
        BitBoard.importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        String move = "0111ss"; // Example move
        long singleBlue = BitBoardFigures.SingleBlue;
        long doubleBlue = BitBoardFigures.DoubleBlue;
        BitMoves.makeMove(move, true);


        assertNotEquals(singleBlue, BitBoardFigures.SingleBlue);
        assertNotEquals(doubleBlue, BitBoardFigures.DoubleBlue);

        assertEquals(0x0000000000000200L, BitBoardFigures.DoubleBlue);
        assertEquals( 0x0000000000007C7CL, BitBoardFigures.SingleBlue);
    }

    @Test
    public void testUndoMove() {
        BitBoard.importFEN("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        long singleBlue = BitBoardFigures.SingleBlue;
        long doubleBlue = BitBoardFigures.DoubleBlue;

        // Move b1-b2 to form blue stack
        String move = "0111ss";
        BitMoves.makeMove(move, true);

        //Unmake Move
        BitMoves.unmakeStack.push(move);
        BitMoves.undoMove();

        //Assert that bitboards are back to previous state
        assertEquals(singleBlue, BitBoardFigures.SingleBlue);
        assertEquals(doubleBlue, BitBoardFigures.DoubleBlue);
    }
}
