import static java.lang.Long.bitCount;

public class EvolutionaryBitBoard {
    static double protectionBonusValue;
    static double SingleValue;
    static double DoubleValue;
    static double MixedValue;
    static double SingleDistance;
    static double DoubleDistance;
    static double MixedDistance;
    static double Square2, Square3, Square4, Square5, Square6, Square7,
            Square9, Square10, Square11, Square12, Square13, Square14, Square15, Square16,
            Square17, Square18, Square19, Square20, Square21, Square22, Square23, Square24,
            Square25, Square26, Square27, Square28, Square29, Square30, Square31, Square32,
            Square33, Square34, Square35, Square36, Square37, Square38, Square39, Square40,
            Square41, Square42, Square43, Square44, Square45, Square46, Square47, Square48,
            Square49, Square50, Square51, Square52, Square53, Square54, Square55, Square56,
            Square58, Square59, Square60, Square61, Square62, Square63;


    public static void main(String[] args) {
//        startGame("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        benchmarkEvaluation();
    }

    public static void startGame(String fen, double[] blue, double[] red){
        BitMoves.gameStateHistory.clear();
        importFEN(fen);
//        System.out.println("Initial evaluation: " + evaluatePosition(0, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue));
        boolean isMax;
        while (!isGameFinished()) {
            if (BitBoardFigures.blueToMove) {
                setEvaluationValues(blue);
                isMax = true;
            } else {
                setEvaluationValues(red);
                isMax = false;
            }
            BitValueMoves vm = alphaBeta(isMax, 2);

            String move = vm.move;
            BitMoves.makeMove(move, true);
            BitBoardFigures.blueToMove = !BitBoardFigures.blueToMove;

        }
    }

    static public void setEvaluationValues(double[] parameters){
        SingleValue = parameters[0];
        DoubleValue = parameters[1];
        MixedValue = parameters[2];
        SingleDistance = parameters[3];
        DoubleDistance = parameters[4];
        MixedDistance = parameters[5];

    }

    static public BitValueMoves alphaBeta(boolean isMax, int depth) {
        return alphaBetaRecursion(depth, -100000.0f, +100000.0f, isMax);
    }

    static public BitValueMoves alphaBetaRecursion(int depth, float alpha, float beta, boolean isMax) {
        if (depth == 0 || isGameFinished()) {
            return new BitValueMoves(evaluatePosition(depth, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue), null, depth);
        }

        float value;
        String bestMove = null;
        int bestDepth = depth;
        if (isMax) {
            value = -100000.0f;
            String moves;


            moves = BitMoves.possibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);


            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);
                BitValueMoves evaluation = alphaBetaRecursion(depth - 1, alpha, beta, false);

                BitMoves.unmakeStack.push(makeMove);
                BitMoves.undoMove();

                if (evaluation.v > value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)) {
                    value = evaluation.v;
                    bestMove = moves.substring(i, i + 4);
                    bestDepth = evaluation.depth;
                }


                alpha = Math.max(alpha, value);

                if (alpha >= beta) {
                    break;
                }
            }
        } else {
            value = 100000.0f;
            String moves;


            moves = BitMoves.possibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);


            for (int i = 0; i < moves.length(); i += 4) {
                String makeMove = BitMoves.makeMove(moves.substring(i, i + 4), true);

                BitValueMoves evaluation = alphaBetaRecursion(depth - 1, alpha, beta, true);
                BitMoves.unmakeStack.push(makeMove);
                BitMoves.undoMove();

                if (evaluation.v < value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)) {
                    value = evaluation.v;
                    bestMove = moves.substring(i, i + 4);
                    bestDepth = evaluation.depth;
                }

                beta = Math.min(beta, value);

                if (alpha >= beta) {
                    break;
                }

            }
        }
        return new BitValueMoves(value, bestMove, bestDepth);
    }

    public static void importFEN(String fenString) {
        BitBoardFigures.SingleRed = 0;
        BitBoardFigures.SingleBlue = 0;
        BitBoardFigures.DoubleRed = 0;
        BitBoardFigures.DoubleBlue = 0;
        BitBoardFigures.MixedRed = 0;
        BitBoardFigures.MixedBlue = 0;

        int charIndex = 0;
        int boardIndex = 0;
        while (fenString.charAt(charIndex) != ' ') {

            if (boardIndex == 0 || boardIndex == 7 || boardIndex == 56 || boardIndex == 63) {
                boardIndex++;
                continue;
            }

            if (fenString.charAt(charIndex) == 'r') {
                charIndex++;
                switch (fenString.charAt(charIndex)) {
                    case '0':
                        BitBoardFigures.SingleRed |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'b':
                        BitBoardFigures.MixedBlue |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'r':
                        BitBoardFigures.DoubleRed |= (1L << boardIndex++);
                        charIndex++;
                        break;
                }
            }
            if (fenString.charAt(charIndex) == 'b') {
                charIndex++;
                switch (fenString.charAt(charIndex)) {
                    case '0':
                        BitBoardFigures.SingleBlue |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'b':
                        BitBoardFigures.DoubleBlue |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'r':
                        BitBoardFigures.MixedRed |= (1L << boardIndex++);
                        charIndex++;
                        break;
                }
            }

            if (boardIndex == 0 || boardIndex == 7 || boardIndex == 56 || boardIndex == 63) {
                boardIndex++;
                continue;
            }

            if (Character.isDigit(fenString.charAt(charIndex))) {
                int skip = Character.getNumericValue(fenString.charAt(charIndex));
                boardIndex += skip;
                charIndex++;
            }

            if (fenString.charAt(charIndex) == '/') {
                charIndex++;
            }

        }
        BitBoardFigures.blueToMove = (fenString.charAt(++charIndex) == 'b');
    }

    public static float evaluatePositionSimple(int depth, long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue){
        BitBoard.counter++;
        float value= 0;
        if(isGameFinished()) {
            if (BitBoard.blueWon) {
                return 1000.0f + depth;
            } else{
                return -1000.0f - depth;
            }
        }

        value += (float) (bitCount(SingleBlue) * SingleValue);
        for (int i = Long.numberOfTrailingZeros(SingleBlue); i < 64 - Long.numberOfLeadingZeros(SingleBlue); i++) {
            if (((SingleBlue >> i) & 1) == 1) {
                value += (float) ((i/8+1)*SingleDistance);
            }
        }
        value += (float) (bitCount(DoubleBlue) * DoubleValue);
        for (int i = Long.numberOfTrailingZeros(DoubleBlue); i < 64 - Long.numberOfLeadingZeros(DoubleBlue); i++) {
            if (((DoubleBlue >> i) & 1) == 1) {
                value += (float) (((i/8+1)*DoubleDistance));
            }
        }
        value += (float) (bitCount(MixedBlue) * MixedValue);
        for (int i = Long.numberOfTrailingZeros(MixedBlue); i < 64 - Long.numberOfLeadingZeros(MixedBlue); i++) {
            if (((MixedBlue >> i) & 1) == 1) {
                value += (float) ((i/8+1)*MixedDistance);
            }
        }


        value -= (float) (bitCount(SingleRed) * SingleValue);
        for (int i = Long.numberOfTrailingZeros(SingleRed); i < 64 - Long.numberOfLeadingZeros(SingleRed); i++) {
            if (((SingleRed >> i) & 1) == 1) {
                value -= (float) ((8-i/8)*SingleDistance);
            }
        }
        value -= (float) (bitCount(DoubleRed) * DoubleValue);
        for (int i = Long.numberOfTrailingZeros(DoubleRed); i < 64 - Long.numberOfLeadingZeros(DoubleRed); i++) {
            if (((DoubleRed >> i) & 1) == 1) {
                value -= (float) (((8-i/8)*DoubleDistance));
            }
        }
        value -= (float) (bitCount(MixedRed) * MixedValue);
        for (int i = Long.numberOfTrailingZeros(MixedRed); i < 64 - Long.numberOfLeadingZeros(MixedRed); i++) {
            if (((MixedRed >> i) & 1) == 1) {
                value -= (float) ((8-i/8)*MixedDistance);
            }
        }

        return value;
    }

    public static float evaluatePosition(int depth, long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue){
        BitBoard.counter++;
        float value= 0;
        if(isGameFinished()) {
            if (BitBoard.blueWon) {
                return 10000.0f + depth;
            } else{
                return -10000.0f - depth;
            }
        }
        if(BitBoard.draw){
            return 0.0f;
        }

        double[] valuesTable ={
                0.0f, Square2, Square3, Square4, Square5, Square6, Square7, 0.0f,
                Square9, Square10, Square11, Square12, Square13, Square14, Square15, Square16,
                Square17, Square18, Square19, Square20, Square21, Square22, Square23, Square24,
                Square25, Square26, Square27, Square28, Square29, Square30, Square31, Square32,
                Square33, Square34, Square35, Square36, Square37, Square38, Square39, Square40,
                Square41, Square42, Square43, Square44, Square45, Square46, Square47, Square48,
                Square49, Square50, Square51, Square52, Square53, Square54, Square55, Square56,
                0.0f, Square58, Square59, Square60, Square61, Square62, Square63, 0.0f,
        };

        value += (float) (bitCount(SingleBlue) * SingleValue);
        for (int i = Long.numberOfTrailingZeros(SingleBlue); i < 64 - Long.numberOfLeadingZeros(SingleBlue); i++) {
            if (((SingleBlue >> i) & 1) == 1) {
                value += (float) valuesTable[i];
            }
        }
        value += (float) (bitCount(DoubleBlue) * DoubleValue);
        for (int i = Long.numberOfTrailingZeros(DoubleBlue); i < 64 - Long.numberOfLeadingZeros(DoubleBlue); i++) {
            if (((DoubleBlue >> i) & 1) == 1) {
                value += (float) valuesTable[i];
            }
        }
        value += (float) (bitCount(MixedBlue) * MixedValue);
        for (int i = Long.numberOfTrailingZeros(MixedBlue); i < 64 - Long.numberOfLeadingZeros(MixedBlue); i++) {
            if (((MixedBlue >> i) & 1) == 1) {
                value += (float) valuesTable[i];
            }
        }


        value -= (float) (bitCount(SingleRed) * SingleValue);
        for (int i = Long.numberOfTrailingZeros(SingleRed); i < 64 - Long.numberOfLeadingZeros(SingleRed); i++) {
            if (((SingleRed >> i) & 1) == 1) {
                value -= (float) valuesTable[i];
            }
        }
        value -= (float) (bitCount(DoubleRed) * DoubleValue);
        for (int i = Long.numberOfTrailingZeros(DoubleRed); i < 64 - Long.numberOfLeadingZeros(DoubleRed); i++) {
            if (((DoubleRed >> i) & 1) == 1) {
                value -= (float) valuesTable[i];
            }
        }
        value -= (float) (bitCount(MixedRed) * MixedValue);
        for (int i = Long.numberOfTrailingZeros(MixedRed); i < 64 - Long.numberOfLeadingZeros(MixedRed); i++) {
            if (((MixedRed >> i) & 1) == 1) {
                value -= (float) valuesTable[i];
            }
        }

        return value;
    }

    public static float evaluatePositionComplex(int depth){
        BitBoard.counter++;
        float value= 0;
        if(isGameFinished()) {
            if (BitBoard.blueWon) {
                return 10000.0f + depth;
            } else{
                return -10000.0f - depth;
            }
        }
        if(BitBoard.draw){
            return 0.0f;
        }
        long redAttacks = calculateAllAttacks(BitBoardFigures.SingleRed, BitBoardFigures.DoubleRed, BitBoardFigures.MixedRed, true);
        long blueAttacks = calculateAllAttacks(BitBoardFigures.SingleBlue, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedBlue, false);

        value += evaluatePieces(BitBoardFigures.SingleBlue, SingleValue,  blueAttacks, false);
        value += evaluatePieces(BitBoardFigures.DoubleBlue, DoubleValue,  blueAttacks, false);
        value += evaluatePieces(BitBoardFigures.MixedBlue, MixedValue,  blueAttacks, false);
        value -= evaluatePieces(BitBoardFigures.SingleRed, SingleValue,  redAttacks, true);
        value -= evaluatePieces(BitBoardFigures.DoubleRed, DoubleValue,  redAttacks, true);
        value -= evaluatePieces(BitBoardFigures.MixedRed, MixedValue,  redAttacks, true);

        return value;
    }

    public static long calculateSingleAttacks(long singlePositions, boolean isRed){
        long attacks = 0;
        if (isRed) {
            attacks |= (singlePositions >> 7 & ~BitMoves.FILE_H);
            attacks |= (singlePositions >> 9 & ~BitMoves.FILE_A);
        } else {
            attacks |= (singlePositions << 7 & ~BitMoves.FILE_A);
            attacks |= (singlePositions << 9 & ~BitMoves.FILE_H);

        }
        return attacks;
    }

    public static long calculateDoubleAttacks(long doublePositions, boolean isRed){
        long attacks = 0;
        if (isRed) {
            attacks |= (doublePositions >> 15 & ~BitMoves.FILE_H);
            attacks |= (doublePositions >> 17 & ~BitMoves.FILE_A);
            attacks |= (doublePositions >> 10 & ~BitMoves.FILE_GH);
            attacks |= (doublePositions >> 6 &  ~BitMoves.FILE_AB);
        } else {
            attacks |= (doublePositions << 15 & ~BitMoves.FILE_A);
            attacks |= (doublePositions << 17 & ~BitMoves.FILE_H);
            attacks |= (doublePositions << 10 & ~BitMoves.FILE_AB);
            attacks |= (doublePositions << 6 &  ~BitMoves.FILE_GH);
        }
        return attacks;
    }

    public static long calculateAllAttacks(long singlePositions, long doublePositions, long mixedPositions, boolean isRed){
        long attacks = 0;
        attacks |= calculateSingleAttacks(singlePositions, isRed);
        attacks |= calculateDoubleAttacks(doublePositions, isRed);
        attacks |= calculateDoubleAttacks(mixedPositions, isRed);
        return attacks;
    }

    public static float evaluatePieces(long piecePositions, double pieceValue, long allAttacks, boolean isRed){

        double[] valuesTable = {
                0.0f, Square2, Square3, Square4, Square5, Square6, Square7, 0.0f,
                Square9, Square10, Square11, Square12, Square13, Square14, Square15, Square16,
                Square17, Square18, Square19, Square20, Square21, Square22, Square23, Square24,
                Square25, Square26, Square27, Square28, Square29, Square30, Square31, Square32,
                Square33, Square34, Square35, Square36, Square37, Square38, Square39, Square40,
                Square41, Square42, Square43, Square44, Square45, Square46, Square47, Square48,
                Square49, Square50, Square51, Square52, Square53, Square54, Square55, Square56,
                0.0f, Square58, Square59, Square60, Square61, Square62, Square63, 0.0f,
        };


        double value = 0;
        while (piecePositions != 0) {
            long lsb = piecePositions & -piecePositions;
            piecePositions ^= lsb;

            int index = Long.numberOfTrailingZeros(lsb);
            double protectionBonus = isPieceProtected(lsb, allAttacks) ? protectionBonusValue : 0;
            value += pieceValue + protectionBonus + valuesTable[index];
        }
        return (float) value;
    }

    public static boolean isPieceProtected(long piecePosition, long allAttacks) {
        return (piecePosition & allAttacks) != 0;
    }

    public static boolean isGameFinished(){
        // Constants for specific bitboard configurations
        final long BLUE_WIN_CONDITION = 9079256848778919936L; // Binary number with the first 6 bits as 1
        final long RED_WIN_CONDITION = 126L; // Specific condition for red

        if(BitBoard.isDraw()){
            return true;
        }
        // Check if blue on red home row
        else if ((BitBoardFigures.SingleBlue & BLUE_WIN_CONDITION) != 0 || (BitBoardFigures.MixedBlue & BLUE_WIN_CONDITION) != 0 || (BitBoardFigures.DoubleBlue & BLUE_WIN_CONDITION) != 0){
            BitBoard.blueWon= true;
            return true;
        }
        // Check if blue on red home row
        else if ((BitBoardFigures.SingleRed & RED_WIN_CONDITION) != 0 || (BitBoardFigures.MixedRed & RED_WIN_CONDITION) != 0 || (BitBoardFigures.DoubleRed & RED_WIN_CONDITION) != 0){
            BitBoard.blueWon =false;
            return true;
        }

        //No more red pieces on board
        else if(BitBoardFigures.SingleRed == 0 && BitBoardFigures.MixedRed == 0 && BitBoardFigures.DoubleRed == 0){
            BitBoard.blueWon =true;
            return true;
        }
        //No more blue pieces on board
        else if(BitBoardFigures.SingleBlue == 0 && BitBoardFigures.MixedBlue == 0 && BitBoardFigures.DoubleBlue == 0){
            BitBoard.blueWon =false;
            return true;
        }
        //No more moves for player who is am zug
        else if (BitBoardFigures.blueToMove) {

            boolean hasMovesLeft = BitMoves.hasPossibleMovesBlue(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if (!hasMovesLeft){
                BitBoard.blueWon = false;
                return true;
            }
        }
        else{

            boolean hasMovesLeft = BitMoves.hasPossibleMovesRed(BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            if (!hasMovesLeft){
                BitBoard.blueWon = true;
                return true;
            }
        }


        return false;

    }

    public static void benchmarkEvaluation(){
        protectionBonusValue = 0;
        SingleValue = 10;
        DoubleValue = 20;
        MixedValue = 25;
        SingleDistance = 1; DoubleDistance = 1; MixedDistance =1;
        Square2 = 1; Square3 = 1; Square4 = 1; Square5 = 1; Square6 = 1; Square7 = 1;
        Square9 = 1; Square10 = 1; Square11 = 1; Square12 = 1; Square13 = 1; Square14 = 1; Square15 = 1; Square16 = 1;
        Square17 = 1; Square18 = 1; Square19 = 1; Square20 = 1; Square21 = 1; Square22 = 1; Square23 = 1; Square24 = 1;
        Square25 = 1; Square26 = 1; Square27 = 1; Square28 = 1; Square29 = 1; Square30 = 1; Square31 = 1; Square32 = 1;
        Square33 = 1; Square34 = 1; Square35 = 1; Square36 = 1; Square37 = 1; Square38 = 1; Square39 = 1; Square40 = 1;
        Square41 = 1; Square42 = 1; Square43 = 1; Square44 = 1; Square45 = 1; Square46 = 1; Square47 = 1; Square48 = 1;
        Square49 = 1; Square50 = 1; Square51 = 1; Square52 = 1; Square53 = 1; Square54 = 1; Square55 = 1; Square56 = 1;
        Square58 = 1; Square59 = 1; Square60 = 1; Square61 = 1; Square62 = 1;Square63 = 1;
        importFEN("b0b0b0b0b0bb/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
        float evaluation = 0.0f;
        long startTime;
        long endTime;
        long totalTime = 0;
        for (int i = 0; i < 10000; i++) {
            startTime = System.nanoTime();
            evaluation = evaluatePositionSimple(1, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        double averageTime = (totalTime / (double) 10000)/ 1_000_000.0;
        System.out.println("simple evaluation function. Evaluation: " +  evaluation + ". Duration: " + averageTime);
        for (int i = 0; i < 10000; i++) {
            startTime = System.nanoTime();
            evaluation = evaluatePosition(1, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue);
            endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        averageTime = (totalTime / (double) 10000)/ 1_000_000.0;
        System.out.println("middle complex evaluation function. Evaluation: " +  evaluation + ". Duration: " + averageTime);
        for (int i = 0; i < 10000; i++) {
            startTime = System.nanoTime();
            evaluation = evaluatePositionComplex(1);
            endTime = System.nanoTime();
            totalTime += (endTime - startTime);
        }
        averageTime = (totalTime / (double) 10000)/ 1_000_000.0;
        System.out.println("complex evaluation function. Evaluation: " +  evaluation + ". Duration: " + averageTime);

    }

    static String moveToString(String move) {
        if (move != null) {
            int coltp = Character.getNumericValue(move.charAt(1));
            char start_col = (char) (coltp + 97);
            coltp = Character.getNumericValue(move.charAt(3));
            char end_col = (char) (coltp + 97);
            int start_row = Character.getNumericValue(move.charAt(0)) + 1;
            int end_row = Character.getNumericValue(move.charAt(2)) + 1;

            return "" + start_col + start_row + "-" + end_col + end_row;
        } else return null;
    }

}