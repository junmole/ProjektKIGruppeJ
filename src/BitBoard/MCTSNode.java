package BitBoard;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The MCTSNode class represents a node in the Monte Carlo Tree Search (MCTS) algorithm.
 * Each node stores information about the game state, including the move leading to this state,
 * the parent node, child nodes, and statistics for the number of playouts and wins.
 */
public class MCTSNode {
    final MCTSNode parent;
    public final String sourceMove;
    String unexploredMovesLeft;
    public double playoutsWon;
    public double playoutsSum;
    boolean isTerminal;
    //is explored if this node and every child node (recursively) is explored
    public boolean isExpanded;
    public final Map<String, MCTSNode> children = new HashMap<>();

    /**
     * Constructs a new MCTSNode.
     *
     * @param sourceMove the move that led to this node's state
     * @param unexploredMovesLeft the unexplored moves from this node's state
     * @param parent the parent node in the MCTS tree
     */
    public MCTSNode(String sourceMove, String unexploredMovesLeft, MCTSNode parent) {
        this.sourceMove = sourceMove;
        this.unexploredMovesLeft = unexploredMovesLeft;
        this.parent = parent;
        playoutsWon = 0;
        playoutsSum = 0;
    }

    /**
     * Selects a random move from the unexplored moves left at this node.
     * Updates the list of unexplored moves and marks the node as expanded if no moves remain.
     *
     * @return the selected random move
     */
    public String getRandomPlayoutMove(){
        //int offset = (int) (Math.random() * (unexploredMovesLeft.length()/4));
        //generates random offsets for playout move
        int offset = ThreadLocalRandom.current().nextInt(0, unexploredMovesLeft.length()/4);

        String randomMove = unexploredMovesLeft.substring(offset*4, offset*4+4);

        unexploredMovesLeft = unexploredMovesLeft.substring(0, offset*4) + unexploredMovesLeft.substring(offset*4+4);

        if (unexploredMovesLeft.isEmpty()){
            isExpanded = true;
        }

        return randomMove;
    }

    /**
     * Adds a child node to this node's list of children.
     *
     * @param move the move leading to the child node
     * @param child the child node to add
     */
    public void addChild(String move, MCTSNode child){
        children.put(move, child);
    }

}
