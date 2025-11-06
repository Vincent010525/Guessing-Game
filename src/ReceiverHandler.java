// Interface to handle receiving messages from the other program and handling when both users are connected

public interface ReceiverHandler {
    void handleReceived(String message);
    void handleConnected();
}
