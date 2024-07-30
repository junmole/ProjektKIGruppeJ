import java.util.Objects;

public class Move {
    public final int start_row;
    public final char start_col;
    public final int end_row;
    public final char end_col;

    public Move(int start_row, char start_col, int end_row, char end_col) {
        this.start_row = start_row;
        this.start_col = start_col;
        this.end_row = end_row;
        this.end_col = end_col;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return start_row == move.start_row &&
                start_col == move.start_col &&
                end_row == move.end_row &&
                end_col == move.end_col;
    }




    @Override
    public int hashCode() {
        return Objects.hash(start_row, start_col, end_row, end_col);
    }

    @Override
    public String toString() {
        return start_col + "" + (start_row + 1) + "-" + end_col + (end_row + 1);
    }
}