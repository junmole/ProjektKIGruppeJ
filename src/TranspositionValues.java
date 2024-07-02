public class TranspositionValues {
    public BitValueMoves bitValueMove;
    public float alpha;
    public float beta;

    public TranspositionValues(BitValueMoves bitValueMove, float alpha, float beta) {
        this.bitValueMove = bitValueMove;
        this.alpha = alpha;
        this.beta = beta;
    }
}
