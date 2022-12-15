package Auction;

import Auction.A_AH_Messages;
import Auction.ConnectionReqs;
import Auction.Item;
import Auction.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * AH_AgentThread built to allow multithreading of the agents.
 * each agent assigned to an instance of this class.
 */
public class AH_AgentThread extends Thread {
    public static boolean running = true;
    A_AH_Messages message;
    protected static Socket agentSocket;
    private static ObjectInputStream agentIn;
    private static ObjectOutputStream agentOut;
    int agentId;

    /**
     * Constructor for an AgentReqs. Takes socket from AuctionHouseServer,
     * opens in and out streams for it and begins communication.
     *
     * @param socket the accepted socket from the server variable
     */
    public AH_AgentThread(Socket socket) throws IOException {
        agentSocket = socket;
        agentOut = new ObjectOutputStream(socket.getOutputStream());
        agentOut.flush();
        agentIn = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * The run method is dedicating to reading message from an agent.
     * The method also adds the incoming message to the log
     */
    @Override
    public void run() {
        do {
            try{
                message = (A_AH_Messages) agentIn.readObject();
                A_AH_Messages.A_AH_MTopic topic = message.getTopic();
                if(topic != A_AH_Messages.A_AH_MTopic.UPDATE) {
                    System.out.println("From a client: " + message);
                }
                if(topic == A_AH_Messages.A_AH_MTopic.DEREGISTER) {
                    System.out.println(
                            "deregister requested by " + message.getAccountId());
                    running = false;
                }
                switch(topic) {
                    case BID:
                        AgentActions.bid(message);
                        break;
                    case REGISTER:
                        agentId = message.getAccountId();
                        AgentActions.register();
                        break;
                    case DEREGISTER:
                        agentShutdown();
                        break;
                    case UPDATE:
                        AgentActions.update();
                        break;
                }
            } catch (IOException|ClassNotFoundException e) {
                agentShutdown();
                message = null;
            }
        } while(message != null && running);// && running);
    }

    /**
     * This method is given an AuctionMessage and writes/sends it to
     * agentSocket.
     *
     * @param message the message being sent
     */
    static void sendOut(A_AH_Messages message) {
        try{
            if(message.getTopic() != A_AH_Messages.A_AH_MTopic.UPDATE) {
                System.out.println("To Agent: " + message);
            }
            agentOut.reset();
            agentOut.writeObject(message);
        } catch(IOException e) {
            agentShutdown();
        }
    }

    /**
     * agentShutdown calls deregister to send message and closes
     * associated sockets.
     */
    static void agentShutdown() {
        AgentActions.deRegister();
        try {
            agentOut.reset();
            if(!agentSocket.isClosed()){
                agentOut.close();
                agentIn.close();
                agentSocket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * shutdown closes all sockets and streams. It then calls agentsShutdown
     * to signal the stop of all threads and closes the sockets.
     */
    public void shutdown() {
        try {
            running = false;
            ConnectionReqs serverInfo = AuctionServer.reqs;
            List<ConnectionReqs> ahInfo = new LinkedList<>();
            ahInfo.add(serverInfo);
            Message deregister = new Message.Builder()
                    .command(Message.Command.DEREGISTER)
                    .connectionReqs(ahInfo)
                    .senderId(Auction.AuctionServer.getAuctionId());
            BankActions.sendToBank(deregister);
            //out.writeObject(deregister);
            while(!AuctionServer.activeAgents.isEmpty()){
                AuctionServer.activeAgents.get(0).message = null;
                AuctionServer.activeAgents.get(0).agentShutdown();
            }
            agentOut.close();
            agentIn.close();
            AuctionServer.auctionSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * winner sends message to
     *
     * @param item Item
     */
    public void winner(Item item) {
        A_AH_Messages winner = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_Messages.A_AH_MTopic.WINNER)
                .accountId(agentId)
                .itemId(item.getItemID())
                .name(item.getName())
                .bid(item.getCurrentBid())
                .build();
        sendOut(winner);
    }
}
