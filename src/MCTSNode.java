import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MCTSNode {
    MCTSNode parent;
    String sourceMove;
    String unexploredMovesLeft;
    double playoutsWon;
    double playoutsSum;
    boolean isTerminal;
    //is explored if this node and every child node (recursively) is explored
    boolean isExpanded;
    Map<String, MCTSNode> children = new HashMap<>();

    public MCTSNode(String sourceMove, String unexploredMovesLeft, MCTSNode parent) {
        this.sourceMove = sourceMove;
        this.unexploredMovesLeft = unexploredMovesLeft;
        this.parent = parent;
        playoutsWon = 0;
        playoutsSum = 0;
    }

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

    public void addChild(String move, MCTSNode child){
        children.put(move, child);
    }

}
