import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Network {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private final String server = "localhost";
    //port on which the server.py is running, add to Network constructor if necessary
    private final int port = 5555;
    private String p;

    public Network() {
        try {
            client = new Socket(server, port);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            p = readPlayerNumber(); //alternative for in.readLine();
            //p = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readPlayerNumber() {
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

    public String getP() {
        return p;
    }

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


