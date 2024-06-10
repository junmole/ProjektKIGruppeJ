import java.util.Arrays;

import static java.lang.Character.isDigit;

public class BitBoard {

    public static void main(String[] args) {
        importFEN("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b");
    }

    /*public static void initiateBoard(){
        long SingleRed = 0L, SingleBlue = 0L, DoubleRed = 0L, DoubleBlue = 0L, MixedRed = 0L, MixedBlue = 0L;

        String[][] jumpBoard ={

                {"00","01","02","03","04","05","06","07"},
                {"08","09","10","11","12","13","14","15"},
                {"16","17","18","19","20","21","22","23"},
                {"24","25","26","27","28","29","30","31"},
                {"32","33","34","35","36","37","38","39"},
                {"40","41","42","43","44","45","46","47"},
                {"48","49","50","51","52","53","54","55"},
                {"56","57","58","59","60","61","62","63"}};



                {"0L","1L","2L","03","04","05","06","07"},
                {"08","09","10","11","12","13","14","15"},
                {"16","17","18","19","20","21","22","23"},
                {"24","25","26","27","28","29","30","31"},
                {"32","33","34","35","36","37","38","39"},
                {"40","41","42","43","44","45","46","47"},
                {"48","49","50","51","52","53","54","55"},
                {"56","57","58","59","60","61","62","63"}};


                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","",""," "," ",""," "},
                {"","",""," ","","","",""},
                {"rr","rr","rr","rr","rr","rr","rr","rr"},
                {"rr","rr","rr","rr","rr","rr","rr","rr"},
                {"","","","","","","",""}};
        arrayToBitboards(jumpBoard,SingleRed,SingleBlue,DoubleRed,DoubleBlue,MixedRed,MixedBlue);
    }
    */

    static public ValueMove alphaBeta(String[][] jumpBoard, boolean isMax, int depth){
        return alphaBetaRecursion(jumpBoard, depth, -100000.0f, +100000.0f, isMax);
    }

    static public ValueMove alphaBetaRecursion(String[][] jumpBoard, int depth, float alpha, float beta, boolean isMax){
        if(depth == 0 || BitMoves.isGameFinished()){
            return new ValueMove(BitMoves.evaluatePosition(depth, BitBoardFigures.SingleRed, BitBoardFigures.SingleBlue, BitBoardFigures.DoubleRed, BitBoardFigures.DoubleBlue, BitBoardFigures.MixedRed, BitBoardFigures.MixedBlue), null, depth);
        }

        if (isMax){
            //float value = alpha;
            float value = -100000.0f;
            Move bestMove = null;
            int bestDepth = depth;



            for (Move move : jumpBoard.getLegalMoves(jumpBoard.getPossibleMoves(jumpBoard.blueToMove))) {
                Board newBoard = new Board();
                newBoard.setBoardContent(jumpBoard.deepCopyBoard(), jumpBoard.deepCopyBoardMap(), jumpBoard.blueToMove);
                newBoard.makeMove(move);

                ValueMove evaluation = alphaBetaRecursion(newBoard, depth - 1, alpha, beta, false);

                //value = Math.max(value, evaluation.v);
                if (evaluation.v > value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)){
                    value = evaluation.v;
                    bestMove = move;
                    bestDepth = evaluation.depth;
                }

                alpha = Math.max(alpha, value);

                if (value >= beta) {
                    //System.out.println("break");
                    break;
                }

                /*if (alpha >= beta) {
                    //System.out.println("break");
                    break;
                }*/
            }
            return new ValueMove(value, bestMove, bestDepth);
        } else {
            //float value = beta;
            float value = 100000.0f;
            Move bestMove = null;
            int bestDepth = depth;

            for (Move move : jumpBoard.getLegalMoves(jumpBoard.getPossibleMoves(jumpBoard.blueToMove))) {
                Board newBoard = new Board();
                newBoard.setBoardContent(jumpBoard.deepCopyBoard(), jumpBoard.deepCopyBoardMap(), jumpBoard.blueToMove);
                newBoard.makeMove(move);

                ValueMove evaluation = alphaBetaRecursion(newBoard, depth - 1, alpha, beta, true);

                //value = Math.max(value, evaluation.v);
                if (evaluation.v < value ||
                        (evaluation.v == value && evaluation.depth > bestDepth)){
                    value = evaluation.v;
                    bestMove = move;
                    bestDepth = evaluation.depth;
                }

                beta = Math.min(beta, value);

                if (value <= alpha) {
                    //System.out.println("break");
                    break;
                }

                /*if (alpha >= beta) {
                    //System.out.println("break");
                    break;
                }*/

            }
            return new ValueMove(value, bestMove, bestDepth);
        }
    }


    public static void arrayToBitboards(String [][] jumpBoard, long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue){
        String Binary;
        for (int i=0;i<64;i++) {
            Binary="0000000000000000000000000000000000000000000000000000000000000000";
            Binary=Binary.substring(i+1)+"1"+Binary.substring(0, i);
            switch (jumpBoard[i/8][i%8]) {
                case "r0": SingleRed+=convertStringToBitboard(Binary);
                    break;
                case "b0": SingleBlue+=convertStringToBitboard(Binary);
                    break;
                case "rr": DoubleRed+=convertStringToBitboard(Binary);
                    break;
                case "bb": DoubleBlue+=convertStringToBitboard(Binary);
                    break;
                case "rb": MixedBlue+=convertStringToBitboard(Binary);
                    break;
                case "br": MixedRed+=convertStringToBitboard(Binary);
                    break;
            }
        }
        drawArray(SingleRed,SingleBlue,DoubleRed,DoubleBlue,MixedRed,MixedBlue);
        BitBoardFigures.SingleRed=SingleRed; BitBoardFigures.SingleBlue=SingleBlue; BitBoardFigures.DoubleRed=DoubleRed;
        BitBoardFigures.DoubleBlue=DoubleBlue; BitBoardFigures.MixedRed=MixedRed; BitBoardFigures.MixedBlue=MixedBlue;
    }

    private static long convertStringToBitboard(String Binary) {
        if (Binary.charAt(0)=='0') {//not going to be a negative number
            return Long.parseLong(Binary, 2);
        } else {
            return Long.parseLong("1"+Binary.substring(2), 2)*2;
        }
    }

    public static void drawArray(long SingleRed, long SingleBlue, long DoubleRed, long DoubleBlue, long MixedRed, long MixedBlue) {
        String[][] jumpBoard = new String[8][8];
        for (int i=0;i<64;i++) {
            jumpBoard[i/8][i%8]=" ";
        }
        for (int i=0;i<64;i++) {
            int row = 7 - (i / 8); // Flip rows here
            int col = i % 8;

            if (((SingleRed>>i)&1)==1) {jumpBoard[row][col]="r0";}
            if (((SingleBlue>>i)&1)==1) {jumpBoard[row][col]="b0";}
            if (((DoubleRed>>i)&1)==1) {jumpBoard[row][col]="rr";}
            if (((DoubleBlue>>i)&1)==1) {jumpBoard[row][col]="bb";}
            if (((MixedRed>>i)&1)==1) {jumpBoard[row][col]="br";}
            if (((MixedBlue>>i)&1)==1) {jumpBoard[row][col]="rb";}
        }
        for (int i=0;i<8;i++) {
            System.out.println(Arrays.toString(jumpBoard[i]));
        }
    }

    public static void importFEN(String fenString) {
        //BitBoardFigures.SingleRed=0; BitBoardFigures.SingleBlue=0; BitBoardFigures.DoubleRed=0;
        //BitBoardFigures.DoubleBlue=0; BitBoardFigures.MixedRed=0; BitBoardFigures.MixedBlue=0;
        BitBoardFigures.SingleRed = 0;
        BitBoardFigures.SingleBlue = 0;
        BitBoardFigures.DoubleRed = 0;
        BitBoardFigures.DoubleBlue = 0;
        BitBoardFigures.MixedRed = 0;
        BitBoardFigures.MixedBlue = 0;

        int charIndex = 0;
        int boardIndex = 0;
        while (fenString.charAt(charIndex) != ' ')
        {

            //System.out.println("BoardIndex: " + boardIndex);
            //System.out.println("Char " +  fenString.charAt(charIndex));
            if(boardIndex == 0 || boardIndex == 7 || boardIndex == 56 || boardIndex == 63){
//                System.out.println("BoardIndex if 0 or 7: " + boardIndex);
                boardIndex++;
                continue;
            }


            //System.out.println("Character at: " + fenString.charAt(charIndex) +  " " + charIndex);
            if (fenString.charAt(charIndex) == 'r') {
                charIndex++;
                switch (fenString.charAt(charIndex)){
                    case '0': BitBoardFigures.SingleRed |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'b': BitBoardFigures.MixedBlue |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'r': BitBoardFigures.DoubleRed |= (1L << boardIndex++);
                        charIndex++;
                        break;
                }
            }
            if (fenString.charAt(charIndex) == 'b') {
                charIndex++;
                switch (fenString.charAt(charIndex)){
                    case '0': BitBoardFigures.SingleBlue |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'b': BitBoardFigures.DoubleBlue |= (1L << boardIndex++);
                        charIndex++;
                        break;
                    case 'r': BitBoardFigures.MixedRed |= (1L << boardIndex++);
                        charIndex++;
                        break;
                }
            }

            if(boardIndex == 0 || boardIndex == 7 || boardIndex == 56 || boardIndex == 63){
//                System.out.println("BoardIndex if 0 or 7: " + boardIndex);
                boardIndex++;
                continue;
            }

            if(Character.isDigit(fenString.charAt(charIndex))) {
                int skip = Character.getNumericValue(fenString.charAt(charIndex));
                boardIndex += skip;
                charIndex++;
            }

            if (fenString.charAt(charIndex) == '/'){
                charIndex++;
            }

        }
        BitBoardFigures.blueToMove = (fenString.charAt(++charIndex) == 'b');
        drawArray(BitBoardFigures.SingleRed,BitBoardFigures.SingleBlue,BitBoardFigures.DoubleRed,BitBoardFigures.DoubleBlue,BitBoardFigures.MixedRed,BitBoardFigures.MixedBlue);
    }
}
