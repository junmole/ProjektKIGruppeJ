package BitBoard;

/**
 * The TranspositionValues class stores information used in the transposition table for an alpha-beta pruning algorithm.
 * It holds a BitValueMoves object and the alpha-beta bounds.
 */
public class TranspositionValues {
    public final BitValueMoves bitValueMove;
    public final float alpha;
    public final float beta;

    /**
     * Constructor for the TranspositionValues class.
     * Initializes the object with a given BitValueMoves object and alpha-beta bounds.
     *
     * @param bitValueMove the BitValueMoves object containing the best move and its value
     * @param alpha the alpha value representing the lower bound
     * @param beta the beta value representing the upper bound
     */
    public TranspositionValues(BitValueMoves bitValueMove, float alpha, float beta) {
        this.bitValueMove = bitValueMove;
        this.alpha = alpha;
        this.beta = beta;
    }
}
