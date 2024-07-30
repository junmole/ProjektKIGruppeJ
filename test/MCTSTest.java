import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for MCTS Implementation
 */
class MCTSTest {

    private MCTSNode mockRootNode;

    @BeforeEach
    void setUp() {
        // Set up the game state and MCTS root node for each test
        mockRootNode = new MCTSNode("", "mockMoves", null);
        BitBoardFigures.blueToMove = true;
        BitMoves.mctsBlueToMove = true;
        BitBoard.importFEN("2b01b01/2bb5/3b02b01/3r0brb0r01/1b06/8/2r0rr4/2r01r0r0 r");
    }

    @Test
    void testMctsUCT() {
        long computationalBudget = 1000; // 1 second

        String bestMove = BitBoard.mctsUCT(computationalBudget);

        assertNotNull(bestMove, "Best move should not be null");
    }

    @Test
    void testMctsTreePolicy_Expansion() {
        MCTSNode node = BitBoard.mctsTreePolicy(mockRootNode);

        assertNotNull(node, "Expanded node should not be null");
        assertTrue(node.isExpanded || node.children.isEmpty(), "Node should either be fully expanded or have no children");
    }

    @Test
    void testMctsExpand() {
        MCTSNode newChild = BitBoard.mctsExpand(mockRootNode);

        assertNotNull(newChild, "Expanded child should not be null");
        assertTrue(mockRootNode.children.containsKey(newChild.sourceMove), "New child should be added to the parent's children");
    }

    @Test
    void testMctsDefaultPolicy() {
        int reward = BitBoard.mctsDefaultPolicy();

        assertTrue(reward >= -1 && reward <= 1, "Reward should be between -1 and 1");
    }

    @Test
    void testMctsBackup() {
        MCTSNode leafNode = new MCTSNode("move", "", mockRootNode);
        int reward = 1;

        BitBoard.mctsBackup(leafNode, reward);

        assertEquals(1, leafNode.playoutsSum, "Leaf node playouts sum should be updated");
        assertEquals(reward, leafNode.playoutsWon, "Leaf node playouts won should be updated");
    }
}
