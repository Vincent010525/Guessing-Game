// Connects the programs by having a startGame(host) method and joinGame(connect) method

import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Connector {
    private ReceiverHandler receiverHandler;
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    // Creates a new thread which assigns the server socket, as well as the writer and reader to send and receive messages
    public void startGame(int port, ReceiverHandler receiverHandler) {
        this.receiverHandler = receiverHandler;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.ISO_8859_1), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Platform.runLater(() -> receiverHandler.handleConnected());
                receive();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    // Creates a new thread which assigns the socket, as well as the writer and reader to send and receive messages
    public void joinGame(String host, int port, ReceiverHandler receiverHandler) {
        this.receiverHandler = receiverHandler;
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.ISO_8859_1), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Platform.runLater(() -> receiverHandler.handleConnected());
                receive();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    // Creates a new thread which reads received messages
    private void receive() {
        new Thread(() -> {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    String tmp_message = message;
                    Platform.runLater(() -> receiverHandler.handleReceived(tmp_message));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    // Sends message to the connected program
    public void sendMessage(String message) {
        writer.println(message);
    }
}
