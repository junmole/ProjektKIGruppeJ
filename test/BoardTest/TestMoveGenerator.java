package BoardTest;

import Board.Board;
import Board.Move;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class TestMoveGenerator {
    public static Set<Move> parseMoves(String movesString) {
        Set<Move> moves = new HashSet<>();
        // Regex to match moves in lowercase and ensure proper capturing of moves
        Pattern movePattern = Pattern.compile("([a-h])(\\d+)-([a-h])(\\d+)");
        Matcher matcher = movePattern.matcher(movesString);

        while (matcher.find()) {
            char startCol = matcher.group(1).charAt(0);
            int startRow = Integer.parseInt(matcher.group(2)) - 1; // Convert to 0-indexed
            char endCol = matcher.group(3).charAt(0);
            int endRow = Integer.parseInt(matcher.group(4)) - 1; // Convert to 0-indexed


            moves.add(new Move(startRow, startCol, endRow, endCol));
        }

        return moves;
    }



    public static Stream<Arguments> provideFenStrings2() {
        return Stream.of(
                Arguments.of("6/1b06/1r0b02bb2/2r02b02/8/5rr2/2r03r01/6 b",parseMoves("b2-a2, b2-c2, c3-d3, f4-e4, f4-g4, f4-f5, f3-d4, f3-e5, f3-g5, f3-h4")),
                Arguments.of("b0b01b0b0b0/1b0b02b0b01/3b0b03/2b05/3r04/2r05/1r01rr1r0r01/r0r02r0r0 b", parseMoves("b1-b2, b1-c1, c1-b1, c1-c2, c1-d1, e1-d1, e1-e2, e1-f1, f1-e1, f1-f2, f1-g1, g1-f1, g1-g2, b2-a2, b2-b3, b2-c2, c2-b2, c2-c3, c2-d2, d3-c3, d3-d4, d3-e3, e3-d3, e3-e4, e3-f3, f2-e2, f2-f3, f2-g2, g2-f2, g2-g3, g2-h2, c4-b4, c4-c5, c4-d5, c4-d4")),
                Arguments.of("b0b04/b02bb2b01/2b05/4rb3/6b01/2r04r0/1r01r0r01r01/r0r04 r", parseMoves("b8-b7, b8-c8, c8-b8, c8-c7, c8-d8, b7-a7, b7-b6, b7-c7, d7-c7, d7-d6, d7-e7, e7-d7, e7-e6, e7-f7, g7-f7, g7-g6, g7-h7, c6-b6, c6-c5, c6-d6, h6-g6, h6-h5, h6-g5")),
                Arguments.of("b0b01b01b0/2b0bbb0bb1b0/8/1b06/5r02/2r05/1r01r0rr1r01/r0r0r02rr b",parseMoves("b1-b2, b1-c1, c1-b1, c1-c2, c1-d1, e1-d1, e1-e2, e1-f1, g1-f1, g1-g2, c2-b2, c2-c3, d2-b3, d2-c4, d2-e4, d2-f3, e2-e3, f2-d3, f2-e4, f2-g4, f2-h3, h2-g2, h2-h3, b4-a4, b4-b5, b4-c4")),
                Arguments.of("1b01b02/2b01b01b0b0/2r02b02/4r03/2b02b02/b07/3r02r01/rr4rr b", parseMoves("c1-b1, c1-c2, c1-d1, e1-d1, e1-e2, e1-f1, c2-b2, c2-d2, e2-d2, e2-e3, e2-f2, g2-f2, g2-g3, g2-h2, h2-g2, h2-h3, f3-e3, f3-e4, f3-f4, f3-g3, c5-b5, c5-c6, c5-d5, f5-e5, f5-f6, f5-g5, a6-a7, a6-b6")),
                Arguments.of("6/1bbbbbbbbbbbb1/8/8/8/1r0r0r0r0r0r01/8/r0r0r0r0r0r0 b", parseMoves("b2-a4, b2-c4, b2-d3, c2-b4, c2-d4, c2-a3, c2-e3, d2-c4, d2-e4, d2-b3, d2-f3, e2-d4, e2-f4, e2-c3, e2-g3, f2-e4, f2-g4, f2-d3, f2-h3, g2-f4, g2-h4, g2-e3")),
                Arguments.of("6/2b02b02/2r02r02/8/8/2b02b02/2r02r02/6 b", parseMoves("c6-b6, c6-d6, f6-e6, f6-g6, c2-b2, c2-d2, f2-e2, f2-g2")),
                Arguments.of("1b0b0b0b01/1b0b0b0b0b0b01/8/4r0b02/2b05/3r04/1r0rr1r0r0r01/r01r0r01r0 b", parseMoves("c1-b1, c1-c2, c1-d1, d1-c1, d1-d2, d1-e1, e1-d1, e1-e2, e1-f1, f1-e1, f1-f2, f1-g1, b2-a2, b2-b3, b2-c2, c2-b2, c2-c3, c2-d2, d2-c2, d2-d3, d2-e2, e2-d2, e2-e3, e2-f2, f2-e2, f2-f3, f2-g2, g2-f2, g2-g3, g2-h2, f4-f5, f4-g4, c5-b5, c5-c6, c5-d6, c5-d5")),
                Arguments.of("3b02/1b0b03r01/5b02/8/1b0bb3r01/1r06/2r05/3r02 r", parseMoves("b6-a6, b6-c6, c7-b7, c7-c6, c7-d7, e8-d8, e8-e7, e8-f8, g2-f2, g2-g1, g2-h2, g5-f5, g5-g4, g5-h5, b6-c5")),
                Arguments.of("6/1b06/8/2b01bbb0rb1/1rbr0rr1r0r01/8/b07/6 b", parseMoves("b2-a2, b2-b3, b2-c2, c4-b4, c4-d4, c4-d5, e4-c5, e4-d6, e4-f6, e4-g5, f4-g5, g4-e5, g4-f6, g4-h6, b5-a7, b5-c7, b5-d6, a7-b7")),
                Arguments.of("6/8/6rr1/8/8/8/b0b0b05/6 r", parseMoves("g3-f1, g3-e2")),
                Arguments.of("3b02/2bb2b02/5b0bb1/2r0b04/2rb3b01/1rr1rr2r0r0/5r02/2rr3 b", parseMoves("c2-a3, c2-b4, c2-d4, c2-e3, c5-a6, c5-b7, c5-d7, c5-e6, d4-d5, d4-e4, e1-d1, e1-e2, e1-f1, f2-e2, f2-f3, f2-g2, f3-e3, f3-f4, g3-e4, g3-f5, g3-h5, g5-f5, g5-h5, g5-h6")),
                Arguments.of("b02b01b0/3b01b02/b02b02b01/b01b05/5r02/1r02r02r0/2rrr02r01/r03r01 b",parseMoves("b1-b2, b1-c1, e1-e2, e1-d1, e1-f1, g1-g2, g1-f1, d2-d3, d2-c2, d2-e2, f2-f3, f2-e2, f2-g2, a3-a4, a3-b3, d3-d4, d3-c3, d3-e3, g3-g4, g3-f3, g3-h3, a4-a5, a4-b4, c4-c5, c4-b4, c4-d4")),
                Arguments.of("6/1b03b02/3b01r0b01/bb2b04/1b01r02r0r0/1r0r02rbr01/1r06/6 r", parseMoves("f3-e3, d5-c5, d5-e5, g5-g4, g5-f5, g5-h5, h5-h4, h5-g5, b6-a6, b6-c6, c6-c5, c6-b6, c6-b5, c6-d6, g6-g5, g6-h6, b7-b6, b7-a7, b7-c7")),
                Arguments.of("2b03/8/8/3b0b03/2b03b01/2r03r01/2r05/6 r", parseMoves("c6-b6, c6-d6, c7-b7, c7-c6, c7-d7, g6-f6, g6-h6")),
                Arguments.of("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", parseMoves("b2-a2, b2-b3, b2-c2, b3-a3, b3-b4, b3-c3, d1-b2, d1-c3, d1-e3, d1-f2, d3-c3, d3-d4, d3-e3, e1-c2, e1-d3, e1-f3, e1-g2, e4-d4, e4-f4, g1-f1, g1-g2, g6-f6, g6-f7, g6-g7, g6-h6")),
                Arguments.of("b0b01b02/3bbb0bb2/2b03bb1/8/2b01r03/5r02/1rr1r0rr1rr1/1rr4 b", parseMoves("b1-b2, b1-c1, c1-b1, c1-c2, c1-d1, e1-d1, e1-e2, e1-f1, d2-b3, d2-c4, d2-e4, d2-f3, e2-e3, f2-d3, f2-e4, f2-g4, f2-h3, c3-b3, c3-c4, c3-d3, g3-e4, g3-f5, g3-h5, c5-b5, c5-c6, c5-d5")),
                Arguments.of("6/1b02br3/6bb1/2b0b04/2r04r0/8/1rr1r0rr1r01/6 b", parseMoves("b2-a2, b2-b3, b2-c2, g3-e4, g3-f5, g3-h5, c4-b4, c4-d4, d4-c4, d4-c5, d4-d5, d4-e4")),
                Arguments.of("2bbb0b0b0/1bbb0b0b0b0b01/8/8/8/1r01r04/2r01r0r0r01/r0r0r0r0r0r0 b", parseMoves("c2-c3, d2-d3, e2-e3, f2-f3, g2-g3, g2-h2, f1-e1, g1-f1, d2-c2, e2-d2, f2-e2, g2-f2, e1-e2, f1-f2, g1-g2, e1-f1, f1-g1, c2-d2, d2-e2, e2-f2, f2-g2, d1-c3, b2-a4, d1-e3, b2-c4, b2-d3, d1-f2")),
                Arguments.of("bb5/1bb6/bb6b0/b0r0rb5/7rb/5rr2/8/6 b", parseMoves("h3-g3, h3-h4, a4-a5, c4-a5, c4-b6, h5-g7, b1-c3, a3-b5, c4-d6, b1-d2, b2-d3, c4-e5, h5-f6, b2-a4")),
                Arguments.of("1b0b0b0b0b0/1b01bb2b01/8/3bb1b02/5rr2/2r01r03/2rr5/r0r0r0r0r0r0 b", parseMoves("c1-b1,c1-c2,c1-d1,d1-c1,d1-e1,e1-d1,e1-e2,e1-f1,f1-e1,f1-f2,f1-g1,g1-f1,g1-g2,b2-a2,b2-b3,b2-c2,d2-b3,d2-c4,d2-e4,d2-f3,g2-f2,g2-g3,g2-h2,d4-b5,d4-c6,d4-e6,d4-f5,f4-e4,f4-g4")),
                Arguments.of("b0b0b01b01/2b03b01/8/3b01b02/1b01r01r02/2br1r03/b01r02r02/2r0r0r01 r", parseMoves("f8-g8,f8-f7,f8-e8,e8-f8,e8-e7,e8-d8,d8-e8,d8-d7,d8-c8,f7-g7,f7-f6,f7-e7,c7-d7,c7-b7,e6-f6,e6-e5,e6-d6,c6-e5,c6-d4,c6-b4,c6-a5,f5-g5,f5-e5,d5-e5,d5-c5")),
                Arguments.of("b01b0b0b0b0/1b0b01b01b01/3b01b02/2b05/8/2r0r01rr2/1r04r01/r0r0r0r0r0r0 r", parseMoves("b8-b7,b8-c8,c8-b8,c8-c7,c8-d8,d8-c8,d8-d7,d8-e8,e8-d8,e8-e7,e8-f8,f8-e8,f8-f7,f8-g8,g8-f8,g8-g7,b7-a7,b7-b6,b7-c7,c6-b6,c6-c5,c6-d6,d6-c6,d6-d5,d6-e6,g7-f7,g7-g6,g7-h7,f6-d5,f6-e4,f6-g4,f6-h5")),
                Arguments.of("b01b01b01/8/2b03b01/1b06/1r01b01b02/3r04/2r03r01/4r01 r", parseMoves("f8-e8,f8-f7,f8-g8,c7-b7,c7-c6,c7-d7,g7-f7,g7-g6,g7-h7,d6-c6,d6-e6,b5-a5,b5-c5")),
                Arguments.of("b0b0b01bb1/2b0b0bbb02/5r02/3b04/4r0b02/8/2rrr01r02/r0r0r0r01r0 r", parseMoves("b8-b7,b8-c8,c8-b8,c8-d8,d8-c8,d8-e8,d8-d7,e8-d8,e8-f8,e8-e7,g8-f8,g8-g7,c7-a6,c7-b5,c7-d5,c7-e6,d7-d6,d7-e7,f7-e7,f7-g7,f7-f6,e5-d5,e5-d4,e5-e4,f3-e3,f3-e2,f3-g3")),
                Arguments.of("3b02/5r02/3r04/8/8/2b02b02/2r05/6 b", parseMoves("c6-b6,c6-d6,f6-e6,f6-g6,f6-f7,e1-d1,e1-f1,e1-e2,e1-f2")),
                Arguments.of("3b01b0/1bb1r0b03/4bb3/2b03b01/2r03r01/3rb1r02/2r0r04/r02r01r0 b", parseMoves("d6-b7,d6-c8,d6-e8,d6-f7,c4-b4,c4-d4,g4-f4,g4-h4,e3-c4,e3-d5,e3-f5,e3-g4,b2-a4,b2-c4,b2-d3,e2-f2,e1-d1,e1-d2,e1-e2,e1-f1,g1-f1,g1-g2")),
                Arguments.of("2b01b0b0/2b0bb4/8/1r06/3b0rbr0b01/3r02r01/2rr5/2r01r01 b", parseMoves("d5-c5,e5-c6,e5-d7,e5-f7,e5-g6,g5-h5,c2-b2,c2-c3,d2-b3,d2-c4,d2-e4,d2-f3,d1-c1,d1-e1,f1-e1,f1-f2,f1-g1,g1-g2,g1-f1")),
                Arguments.of("2b02bb/1bb2b03/5bb2/8/1r03r02/6r01/8/r01r01rrr0 b", parseMoves("d1-c1,d1-d2,d1-e1,g1-e2,g1-h3,b2-a4,b2-c4,b2-d3,e2-d2,e2-e3,e2-f2,f3-d4,f3-e5,f3-g5,f3-h4")),
                Arguments.of("b01bbb01b0/1b02b03/3bbr01b01/8/3rr1b0b01/8/2r01r01rr1/r0r0r01r01 r", parseMoves("b8-b7,b8-c8,c8-c7,c8-b8,c8-d8,d8-d7,d8-c8,d8-e8,f8-f7,f8-e8,f8-g8,c7-c6,c7-b7,c7-d7,e7-e6,e7-d7,e7-f7,g7-e6,g7-f5,g7-h5,d5-b4,d5-c3,d5-e3,d5-f4,e3-f3")),
                Arguments.of("b0b0b0b0b0b0/1bb3b0b01/3b04/4b03/5r02/1r02r03/2r0r02r01/r0r0r0r0r0r0 b", parseMoves("b1-c1,c1-b1,c1-c2,c1-d1,d1-c1,d1-d2,d1-e1,e1-d1,e1-e2,e1-f1,f1-e1,f1-f2,f1-g1,g1-f1,g1-g2,b2-a4,b2-c4,b2-d3,f2-e2,f2-f3,f2-g2,g2-f2,g2-g3,g2-h2,d3-c3,d3-d4,d3-e3,e4-d4,e4-e5,e4-f5,e4-f4")),
                Arguments.of("1bbb01b0b0/4b03/4rr1b01/2b02b02/5r02/1r06/3r02r01/1rrr01r01 r", parseMoves("c8-a7,c8-b6,c8-d6,c8-e7,d8-d7,d8-e8,f8-e8,f8-f7,f8-g8,d7-c7,d7-d6,d7-e7,g7-f7,g7-g6,g7-h7,b6-a6,b6-b5,b6-c6,f5-e5,f5-g5,e3-c2,e3-d1,e3-f1,e3-g2")),
                Arguments.of("b0b01b0b0b0/8/4b0b02/3br4/6b01/2rr3rb1/4rr3/r0r02r0r0 r", parseMoves("c6-e5,c8-b8,c6-b4,c8-c7,c8-d8,c6-a5,e7-d5,e7-g6,e7-f5,g8-f8,g8-g7,b8-b7,b8-c8,d4-b3,f8-f7,f8-g8,d4-c2,f8-e8,d4-f3,d4-e2")),
                Arguments.of("b0b0b0b0b0b0/8/5b01b0/5r0b01/3b04/4r0rr1rb/3r04/r0r0r0r01r0 b", parseMoves("h3-h4,c1-b1,c1-d1,e1-e2,g4-g5,g1-g2,b1-c1,d5-d6,d1-d2,f3-e3,f1-g1,f3-g3,f1-e1,h6-g8,h3-g3,c1-c2,g4-h4,e1-d1,e1-f1,g1-f1,b1-b2,d1-e1,d5-c5,d5-e6,d1-c1,d5-e5,f1-f2,h6-f7")),
                //Gruppe T: Erste Stellung fehlte ein 0
                Arguments.of("6/8/6b01/4bb3/r0r0rr4b0/3b02r01/1rr3r02/6 r", parseMoves("a5-b5, a5-a4, b5-a5, b5-b4, c5-e4, c5-a4, c5-d3, c5-b3, g6-f6, g6-h6, g6-h5, g6-g5, b7-d6, b7-a5, f7-e7, f7-g7, f7-f6")),
                Arguments.of("6/1b06/2bb1b0b02/6bb1/1r0br5/3r0rr3/8/4r0r0 b", parseMoves("b2-a2,b2-c2,b2-b3,c3-a4,c3-e4,c3-b5,c3-d5,e3-d3,e3-f3,e3-e4,f3-e3,f3-g3,f3-f4,g4-e5,g4-f6,g4-h6")),
                Arguments.of("b0b04/r0r0b02b0b0b0/2r02r0r0r0/8/8/b0b0b02b02/r0r0r02r0b0b0/4r0r0 b", parseMoves("b1-a2, b1-c1, c1-b1, c1-b2, c1-d1, c1-c2, c2-d2, f2-e2, f2-g2, f2-g3, g2-f2, g2-f3, g2-h2, g2-h3, h2-g2, h2-g3, a6-b6, a6-b7, b6-a6, b6-a7, b6-c6, b6-c7, c6-b6, c6-b7, c6-d6, f6-e6, f6-g6, g7-f8, g7-h7, h7-g7, h7-g8")),
                Arguments.of("6/3bb4/1br6/r01b0b02bb1/1rr2r0r01b0/6rb1/4rr3/6 b", parseMoves("d2-c4, d2-b3, d2-e4, d2-f3, c4-b4, c4-b5, c4-d4, c4-c5, d4-c4, d4-e4, d4-e5, d4-d5, g4-f6, g4-e5, g4-h6, h5-g5, h5-h6, g6-f8, g6-e7")),
                Arguments.of("1b0b01b0b0/3bb4/8/1r03b02/3b0rrr0b01/6r01/1r0r0r04/2r01r01 r", parseMoves("d8-c8, d8-e8, d8-d7, f8-e8, f8-g8, f8-f7, b7-a7, b7-c7, b7-b6, c7-b7, c7-d7, c7-c6, d7-c7, d7-e7, d7-d6, g6-f6, g6-h6, e5-c4, e5-g4, e5-d3, e5-f3, b4-a4, b4-c4, b4-b3")),
                Arguments.of("6/7rr/4bb1r01/8/8/b02bb3b0/8/6 r", parseMoves("g3-f3, g3-h3, g3-g2, h2-f1")),
                Arguments.of("b03b01/3b02b01/r01b05/8/8/4rb3/1r06/r03r01 b", parseMoves("b1-c1, b1-b2, f1-e1, f1-g1, f1-f2, d2-c2, d2-e2, d2-d3, g2-f2, g2-h2, g2-g3, c3-b3, c3-d3, c3-c4, e6-c7, e6-g7, e6-d8, e6-f8")),
                Arguments.of("b03b01/3bb2bb1/2bb1br3/1b06/5r02/2rr5/1r02rr3/r0r02rr1 b", parseMoves("b1-c1, b1-b2, f1-e1, f1-g1, f1-f2, d2-b3, d2-f3, d2-c4, d2-e4, g2-e3, g2-f4, g2-h4, c3-a4, c3-e4, c3-b5, c3-d5, b4-a4, b4-c4, b4-b5")),
                Arguments.of("b0b0b0b0b0b0/2bbb02bb1/4b03/8/3r04/8/1r0r01r0r0r01/r0r0r0r0r0r0 r", parseMoves("b8-c8, b8-b7, c8-b8, c8-c7, c8-d8, d8-c8, d8-d7, d8-e8, e8-d8, e8-e7, e8-f8, f8-e8, f8-f7, f8-g8, g8-f8, g8-g7, b7-a7, b7-b6, b7-c7, c7-b7, c7-c6, c7-d7, e7-d7, e7-e6, e7-f7, f7-e7, f7-f6, f7-g7, g7-f7, g7-g6, g7-h7, d5-c5, d5-d4, d5-e5")),
                Arguments.of("2b03/1b0b05/6b01/3bb2r01/3r02r01/2b05/2r03r01/3r02 b", parseMoves("d1-c1, d1-d2, d1-e1, b2-a2, b2-b3, b2-c2, c2-b2, c2-c3, c2-d2, g3-f3, g3-h3, d4-b5, d4-c6, d4-e6, d4-f5, c6-b6, c6-d6")),
                Arguments.of("b0b0b01b0b0/4b01b01/2bb2b02/8/1r01br4/4r01r01/2r02rr2/r01r01r0r0 r", parseMoves("b8-c8, b8-b7, d8-c8, d8-e8, d8-d7, f8-e8, f8-g8, g8-f8, g8-g7, c7-b7, c7-d7, c7-c6, f7-d6, f7-e5, f7-g5, f7-h6, e6-d6, e6-e5, e6-f6, g6-f6, g6-g5, g6-h6, b5-a5, b5-b4, b5-c5, d5-b4, d5-c3, d5-e3, d5-f4")),
                Arguments.of("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 r", parseMoves("c8-b8, c8-c7, c8-d7, c8-d8, b7-a5, b7-c5, b7-d6, e4-e3, e4-f4, f7-h6, f7-g5, f7-e5, f7-d6, g7-h5, g7-f5, g7-e6, e2-g1, e2-c1")),
                Arguments.of("6/1bbbbbbbbbbbb1/8/8/8/8/1rrrrrrrrrrrr1/6 b", parseMoves("b2-a4, b2-c4, b2-d3, c2-a3, c2-b4, c2-d4, c2-e3, d2-b3, d2-c4, d2-e4, d2-f3, e2-c3, e2-d4, e2-f4, e2-g3, f2-d3, f2-e4, f2-g4, f2-h3, g2-e3, g2-f4, g2-h4")),
                Arguments.of("6/8/4b03/1r01b0r03/2r02r0b01/1b02b01r01/1r06/6 b", parseMoves("b6-a6, b6-c6, d4-d5, d4-c5, e3-d3, e3-f3, e6-d6, e6-f6, e6-e7, g5-h5, d4-c4")),
                Arguments.of("b0b0b0b0b0b0/8/8/4b03/1r01b01r02/8/3r02r01/6 r", parseMoves("b5-a5, b5-c5, b5-b4, d7-c7, d7-e7, d7-d6, f5-e4, f5-e5, f5-g5, f5-f4, g7-g6, g7-h7, g7-f7")),
                Arguments.of("b0b0b0b01b0/3b04/2b01b0b0b01/2b05/3b04/1r01r01rr2/2rr1r03/r0r0r0r01r0 b", parseMoves("b1-b2, b1-c1, c1-b1, c1-c2, c1-d1, d1-c1, d1-d2, d1-e1, e1-d1, e1-e2, e1-f1, g1-f1, g1-g2, d2-c2, d2-d3, d2-e2, c3-b3, c3-c4, c3-d3, e3-d3, e3-e4, e3-f3, f3-e3, f3-f4, f3-g3, g3-f3, g3-g4, g3-h3, c4-b4, c4-c5, c4-d4, d5-c5, d5-e5")),
                Arguments.of("2bb3/5b02/8/2bb5/5rr2/8/3b03r0/7 r", parseMoves("f5-d4, f5-e3, f5-g3, f5-h4, h7-g7, h7-h6"))
                //Stellungen von X, U, W und P, nicht genutzt weil zu fehlerhaft
        );
    }


    @ParameterizedTest
    @MethodSource("provideFenStrings2")
    public  static void getPossibleMovesVariant2(String fen, Set<Move> expectedMoves) {
        int blueToMove = (fen.charAt(fen.length()-1) == 'b' ? 1 : 0);
        String init = fen.substring(0, fen.length() - 2);
        Board board = new Board();
        board.fenToBoard(init);

        Set<Move> moves = board.getPossibleMoves(blueToMove);
        moves = board.getLegalMoves(moves);
        // Find moves in the expected set but not in the actual moves
        Set<Move> finalMoves = moves;
        Set<Move> missingMoves = expectedMoves.stream()
                .filter(move -> !finalMoves.contains(move))
                .collect(Collectors.toSet());

        // Find moves in the actual set but not in the expected moves
        Set<Move> unexpectedMoves = moves.stream()
                .filter(move -> !expectedMoves.contains(move))
                .collect(Collectors.toSet());

        // Assert that both sets are empty
        assertTrue(missingMoves.isEmpty(), "Moves in the expected set but not in the actual moves: " + missingMoves + "\nExpected moves: " + expectedMoves + "\nActual moves: " +moves);
        assertTrue(unexpectedMoves.isEmpty(), "Moves in the actual set but not in the expected moves: " + unexpectedMoves + "\nExpected moves: " + expectedMoves + "\nActual moves: " +moves);

        assertEquals(expectedMoves.size(), moves.size());
        assertEquals(expectedMoves, moves);



    }

}

