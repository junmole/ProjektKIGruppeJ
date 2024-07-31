package BitBoardTest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import BitBoard.BitBoardFigures;
import BitBoard.BitMoves;
import BitBoard.BitBoard;
import BitBoard.BitValueMoves;

/**
 * Test Class for BitBoard
 */
public class BitBoardTest {

    @Test
    public void testImportFEN() {
        String fen = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
        BitBoard.importFEN(fen);

        assertEquals(BitBoardFigures.SingleRed, 0x7E7E000000000000L);
        assertEquals(BitBoardFigures.SingleBlue,0x0000000000007E7EL);
        assertTrue(BitBoardFigures.blueToMove);
    }

    @Test
    public void testEvaluatePosition() {
        String fen = "2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0 b";
        BitBoard.importFEN(fen);

        float eval = BitBoard.evaluatePosition(0);
        assertNotEquals(0, eval); // Ensure the evaluation is not zero
    }

    @Test
    public void testAlphaBeta() {
        String fen = "2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0 b";
        BitBoard.importFEN(fen);

        BitValueMoves bestMove = BitBoard.alphaBeta(true);
        assertNotNull(bestMove.move);
        assertNotEquals(0, bestMove.v); // Ensure the evaluation value is not zero
    }

    @Test
    public void testAlphaBetaWithTransposition() {
        BitMoves.initZobristTable();
        String fen = "6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b";
        BitBoard.importFEN(fen);

        BitValueMoves bestMove = BitBoard.alphaBetaWithTransposition(true);
        assertNotNull(bestMove.move);
        assertNotEquals(0, bestMove.v); // Ensure the evaluation value is not zero
    }

    @Test
    public void testIsDraw() {
        BitMoves.gameStateHistory.put(BitMoves.hashBoard(), 3);
        assertTrue(BitBoard.isDraw());
    }

    @Test
    public void testIsGameFinished() {
        String fen = "8/8/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
        BitBoard.importFEN(fen);

        assertTrue(BitBoard.isGameFinished());
        assertFalse(BitBoard.blueWon);
    }

    @Test
    public void testMoveToString() {
        String move = "0102";
        String result = BitBoard.moveToString(move);

        assertEquals("b1-c1", result);
    }
}
