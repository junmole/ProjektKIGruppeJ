package BitBoard;

/**
 * The BitValueMoves class represents a move in a board game, including its value, string representation, and depth.
 */
public class BitValueMoves {
    public final float v;
    public final String move;
    public final int depth;

    /**
     * Constructs a new BitValueMoves object.
     *
     * @param v the value of the move
     * @param move the string representation of the move
     * @param depth the depth at which the move was evaluated
     */
    public BitValueMoves(float v, String move, int depth) {
        this.v = v;
        this.move = move;
        this.depth = depth;
    }

    /**
     * Converts the move to a string in a readable format.
     *
     * @return the move in string format, e.g., A1-B2
     */
    public String moveToString(){
        if(move!= null) {
            int coltp = Character.getNumericValue(move.charAt(1));
            char start_col = (char) (coltp + 65);
            coltp = Character.getNumericValue(move.charAt(3));
            char end_col = (char) (coltp + 65);
            int start_row = Character.getNumericValue(move.charAt(0)) + 1;
            int end_row = Character.getNumericValue(move.charAt(2)) + 1;

            return "" + start_col + start_row + "-" + end_col + end_row;
        }else return null;
    }
}

