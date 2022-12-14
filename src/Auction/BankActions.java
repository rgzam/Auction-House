package Auction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Bank Actions class contains the methods directly pertaining to bank
 * communication/operation with regards to the auction house.
 */
public class BankActions {
    private static BankActions active;
    public static BankActions getActive() {
        if(active == null) {
            active = new BankActions();
        }
        return active;
    }

    /**
     * registerBank creates message for bank registration, processes response
     * to get database ip address and port number before calling CountDown
     * on a separate thread. Returns message response and prints connection
     * success message.
     *
     * @param reqsList List<ConnectionReqs> String ip and int port
     * @param name String
     * @return Message bank message class
     */
    public Message registerBank(List<ConnectionReqs> reqsList, String name) {
        Message message = new Message.Builder().command(Message.Command.REGISTERHOUSE)
                .connectionReqs(reqsList).accountName(name).nullId();
        Message response = sendToBank(message);
        assert response != null;
        AuctionServer.auctionId = response.getAccountId();
        List<ConnectionReqs> dBReqs = response.getConnectionReqs();
        ConnectionReqs reqs = dBReqs.get(0);
        AuctionServer.dBPort = reqs.getPort();
        AuctionServer.dBIp = reqs.getIp();
        CountDown count = new CountDown();
        Thread timer = new Thread(count);
        timer.start();
        if(response.getResponse() == Message.Response.SUCCESS) {
            System.out.println("Connection Success");
        }
        return response;
    }

    /**
     * sendToBank opens a socket and sends a message to the bank, then accepts
     * the response.
     *
     * @param message Message
     * @return Message response from bank
     */
    public static Message sendToBank(Message message) {
        try {
            Socket bankSocket = new Socket(AuctionServer.bankIp, AuctionServer.bankPort);
            ObjectOutputStream out = new ObjectOutputStream(bankSocket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(bankSocket.getInputStream());
            out.writeObject(message);
            return (Message) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
