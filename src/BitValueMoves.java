public class BitValueMoves {
    public float v;
    public String move;
    public int depth;

    public BitValueMoves(float v, String move, int depth) {
        this.v = v;
        this.move = move;
        this.depth = depth;
    }

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

