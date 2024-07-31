import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The Network class manages the connection and communication with the game server.
 * It handles sending and receiving data over a socket connection, typically used to interact with a game server.
 */
class Network {
    Socket client;
    BufferedReader in;
    PrintWriter out;
    String p;

    /**
     * Constructs a Network object and establishes a connection to the game server.
     * Initializes the input and output streams for communication.
     */
    public Network() {
        try {
            String server = "localhost";
            //port on which the server.py is running, add to Network constructor if necessary
            int port = 5555;
            client = new Socket(server, port);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            p = readPlayerNumber(); //alternative for in.readLine();
            //p = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the player number or identifier from the server.
     * This method waits for a single character response from the server.
     *
     * @return the player number or identifier as a String
     */
    String readPlayerNumber() {
        char value;
        String tmp = "";
        try {
            value = (char) in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tmp += (char) value;
        return tmp;
    }

    /**
     * Reads the response from the server until a closing '}' character is encountered.
     * This method handles multiline responses or responses that do not end with a newline.
     *
     * @return the response from the server as a String
     */
    private String readResponse() {
        int value;
        String tmp = "";
        while (true) {
            try {
                value = in.read();
                tmp += (char) value;
                if ((char) value == '}') {
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return tmp;
    }

    /**
     * Gets the player number assigned to this client.
     *
     * @return the player number as a String
     */
    public String getP() {
        return p;
    }

    /**
     * Sends data to the server and reads the response.
     *
     * @param data the data to send to the server
     * @return the response from the server
     */
    public String send(String data) {
        try {
            out.println(data);
            return readResponse(); //alternative for in.readLine();
            //return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


