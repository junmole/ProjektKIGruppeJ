import java.util.Objects;

/**
 * The Field class represents a field on a grid with a row and a column.
 */
public class Field {
    /**
     * The row of the field.
     */
    public final int row;
    /**
     * The column of the field.
     */
    public final char col;

    /**
     * Constructs a Field object with the specified row and column.
     *
     * @param row the row of the field
     * @param col the column of the field
     */
    public Field(int row, char col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns a string representation of the Field object.
     * The format is the column followed by the row incremented by 1.
     *
     * @return a string representation of the Field object
     */
    @Override
    public String toString() {
        return col + "" + (row + 1);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * The other object is considered equal if it is an instance of Field and
     * has the same row and column.
     *
     * @param o the reference object with which to compare
     * @return {@code true} if this object is the same as the obj argument;
     *         {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return row == field.row && col == field.col;
    }

    /**
     * Returns a hash code value for the object.
     * This method is supported for the benefit of hash tables such as those
     * provided by HashMap.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
