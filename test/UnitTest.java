import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {


    @Test
    public void miniMaxGruppeH1(){
        Board board = new Board();
        String init =  "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(2,'d',3,'b' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeH2(){
        Board board = new Board();
        String init =  "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 r";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 0;
        assertEquals(new Move(4,'f',2,'g' ), Board.miniMax(board, false, 4).move);
    }

    @Test
    public void miniMaxGruppeH3(){
        Board board = new Board();
        String init =  "6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(3,'f',5,'e' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeH4(){
        Board board = new Board();
        String init =  "6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 r";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 0;
        assertEquals(new Move(2,'d',1,'e' ), Board.miniMax(board, false, 4).move);
    }

    @Test
    public void miniMaxGruppeF1(){
        Board board = new Board();
        String init =  "6/7b0/8/8/1r06/4b03/2rr1rrr02/5r0 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(5,'e',5,'d' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeF2(){
        Board board = new Board();
        String init =  "6/4bbb02/b02b01b02/1b02b03/2b01rrrr2/6r01/r01r0r0r03/5r0 r";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 0;
        assertEquals(new Move(4,'e',2,'d' ), Board.miniMax(board, false, 4).move);
    }

    @Test
    public void miniMaxGruppeT1(){
        Board board = new Board();
        String init =  "1b0b0b02/8/3b04/3b04/r0r06/2b05/5r0r01/6 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(5,'c',6,'c' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeT2(){
        Board board = new Board();
        String init =  "6/4bb3/8/8/4b0r0b01/8/8/6 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(1,'e',2,'g' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeAG1(){
        Board board = new Board();
        String init =  "6/8/8/8/b0b02b0b02/2b05/2r0r0r0r02/6 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(5,'c',6,'d' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeAG2(){
        Board board = new Board();
        String init =  "3b01b0/3bb1b02/8/8/8/2r0b0r03/8/0r04r0 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(5,'d',6,'d' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeC1(){
        Board board = new Board();
        String init =  "6/4b01b01/8/5b01b0/2b04r0/1b04r01/5r01rr/1r04 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(4,'c',5,'c' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeC2(){
        Board board = new Board();
        String init =  "3bb2/b02b02b01/3b02bbb0/1b06/1r0r02r01r0/6r01/5r0r0r0/6 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(3,'b',4,'c' ), Board.miniMax(board, true, 4).move);
    }

    public void miniMaxGruppeS1(){
        Board board = new Board();
        String init =  "2b03/1b0b05/6b01/3bb2r01/3r02r01/2b05/2r03r01/3r02 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(3,'d',5,'c' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeJ1(){
        Board board = new Board();
        String init =  "6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(3,'f',5,'e' ), Board.miniMax(board, true, 4).move);
    }

    @Test
    public void miniMaxGruppeJ2(){
        Board board = new Board();
        String init =  "6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 r";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 0;
        assertEquals(new Move(2,'d',1,'e' ), Board.miniMax(board, false, 4).move);
    }

    @Test
    public void alphaBetaGruppeH1(){
        Board board = new Board();
        String init =  "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(2,'d',3,'b' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeH2(){
        Board board = new Board();
        String init =  "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 r";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 0;
        assertEquals(new Move(4,'f',2,'g' ), Board.alphaBeta(board, false, 4).move);
    }

    @Test
    public void alphaBetaGruppeH3(){
        Board board = new Board();
        String init =  "6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(3,'f',5,'e' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeH4(){
        Board board = new Board();
        String init =  "6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 r";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 0;
        assertEquals(new Move(2,'d',1,'e' ), Board.alphaBeta(board, false, 4).move);
    }

    @Test
    public void alphaBetaGruppeF1(){
        Board board = new Board();
        String init =  "6/7b0/8/8/1r06/4b03/2rr1rrr02/5r0 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(5,'e',5,'d' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeF2(){
        Board board = new Board();
        String init =  "6/4bbb02/b02b01b02/1b02b03/2b01rrrr2/6r01/r01r0r0r03/5r0 r";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 0;
        assertEquals(new Move(4,'e',2,'d' ), Board.alphaBeta(board, false, 4).move);
    }

    @Test
    public void alphaBetaGruppeT1(){
        Board board = new Board();
        String init =  "1b0b0b02/8/3b04/3b04/r0r06/2b05/5r0r01/6 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(5,'c',6,'c' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeT2(){
        Board board = new Board();
        String init =  "6/4bb3/8/8/4b0r0b01/8/8/6 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(1,'e',2,'g' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeAG1(){
        Board board = new Board();
        String init =  "6/8/8/8/b0b02b0b02/2b05/2r0r0r0r02/6 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(5,'c',6,'d' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeAG2(){
        Board board = new Board();
        String init =  "3b01b0/3bb1b02/8/8/8/2r0b0r03/8/0r04r0 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(5,'d',6,'d' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeC1(){
        Board board = new Board();
        String init =  "6/4b01b01/8/5b01b0/2b04r0/1b04r01/5r01rr/1r04 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(4,'c',5,'c' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeC2(){
        Board board = new Board();
        String init =  "3bb2/b02b02b01/3b02bbb0/1b06/1r0r02r01r0/6r01/5r0r0r0/6 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(3,'b',4,'c' ), Board.alphaBeta(board, true, 4).move);
    }

    public void alphaBetaGruppeS1(){
        Board board = new Board();
        String init =  "2b03/1b0b05/6b01/3bb2r01/3r02r01/2b05/2r03r01/3r02 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(3,'d',5,'c' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeJ1(){
        Board board = new Board();
        String init =  "6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 b";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 1;
        assertEquals(new Move(3,'f',5,'e' ), Board.alphaBeta(board, true, 4).move);
    }

    @Test
    public void alphaBetaGruppeJ2(){
        Board board = new Board();
        String init =  "6/3b0b03/3r02bb1/b0b03bb2/rrrr1bb2rr1/2b01b01r01/2r01r02r0/4r01 r";
        String fen = init.substring(0, init.length() - 2);

        board.fenToBoard(fen);
        board.blueToMove = 0;
        assertEquals(new Move(2,'d',1,'e' ), Board.alphaBeta(board, false, 4).move);
    }

}




