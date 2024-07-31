import com.google.gson.Gson;
import java.util.Scanner;
import BitBoard.BitBoardFigures;
import BitBoard.BitBoard;

/**
 * The Client class is responsible for communicating with the server and managing the game state.
 * It continuously runs a loop to send and receive data from the server, and updates the game board accordingly.
 */
public class Client {

    /**
     * Main method to run the client application. It continuously calls runClient method in an infinite loop.
     *
     * @param args command-line arguments
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        while (true) {
            runClient();
        }
    }

    /**
     * Runs the client, handles communication with the server, and manages the game state.
     */
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
                        System.out.println("New Board.Board: " + game.board);

                        BitBoard.importFEN(game.board);
                        String input = BitBoard.alphaBeta(BitBoardFigures.blueToMove).moveToString();
                        //BitMoves.makeMove(input, true);

                        // transforms the input move to JSON
                        String data = gson.toJson(input);

                        // Send data via network
                        n.send(data);
                    } else if (player == 1 && game.player2) {
                        System.out.println("New Board.Board: " + game.board);

                        BitBoard.importFEN(game.board);
                        String input = BitBoard.alphaBeta(BitBoardFigures.blueToMove).moveToString();
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