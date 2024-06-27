import com.google.gson.Gson;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        while (true) {
            runClient();
        }
    }

    public static void runClient() {
        Network n = new Network();
        int player = Integer.parseInt(n.getP());
        System.out.println("You are player " + player);
        Gson gson = new Gson();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                // send "get" as a JSON to the server over the network
                String response = n.send(gson.toJson("get"));

                if (response == null) {
                    throw new ValueException("Game data is null");
                }

                // transforms JSON response to Java GameData object
                GameData game = gson.fromJson(response, GameData.class);

                // only allow input when both players are in
                if (game.bothConnected) {

                    // allow to only give input when it is your turn
                    if (player == 0 && game.player1) {
                        // not necessary while running, helpful for debug
                        System.out.println("New Board: " + game.board);

                        //TODO: add your AI getMove() call here
                        //answer must have format: start_field-end_field like E7-F7
                        //String input = scanner.nextLine();
                        // String input = myAI.getMove();

                        BitBoard.importFEN(game.board);
                        String input = BitBoard.alphaBeta(BitBoardFigures.blueToMove, 4).moveToString();
                        //BitMoves.makeMove(input, true);

                        // transforms the input move to JSON
                        String data = gson.toJson(input);

                        // Send data via network
                        n.send(data);
                    } else if (player == 1 && game.player2) {
                        //TODO: do the same here
                        System.out.println("New Board: " + game.board);

                        BitBoard.importFEN(game.board);
                        String input = BitBoard.alphaBeta(BitBoardFigures.blueToMove, 4).moveToString();
                        //BitMoves.makeMove(input, true);

                        String data = gson.toJson(input);
                        n.send(data);
                    }
                }
            } catch (Exception e) {
                System.out.println("Couldn't get game");
                break;
            }
        }
    }
}