package Auction;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * Auction server is the main class and contains the main method.
 * It connects to the bank and then moves into a loop where it recieves
 * connections from the agents.
 */
public class AuctionServer {
    public static int bankPort;
    public static String bankIp;
    public static int dBPort;
    public static String dBIp;
    private static String name;
    static int port;
    public static int auctionId;
    static ServerSocket auctionSocket;
    public static ConnectionReqs reqs;
    static Message message;
    private static boolean running = true;
    static List<AH_AgentThread> activeAgents = new LinkedList<>();

    /**
     * try to connect to bank and receives
     * connections to auction house from agents within a while loop.
     * The argument in order are Auction port int, bank port int,
     * bank ip address String, bank name String
     *
     * @param args String[]
     * @throws IOException for command input and
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 4) {
            System.out.println("Arg number wrong.");
            System.exit(1);
        }
        String ip;
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }

        //Auction port
        port = Integer.parseInt(args[0]);
        //bank port value
        bankPort = Integer.parseInt(args[1]);
        //bank ip address
        bankIp = args[2];
        //bank name
        name = args[3];

        reqs = new ConnectionReqs(ip, port);
        List<ConnectionReqs> reqsList = new ArrayList<>();
        reqsList.add(reqs);
        message = BankActions.getActive().registerBank(reqsList , name);
        auctionSocket = new ServerSocket(port);
        System.out.println("listening...");

        while(running) {
            try {
                    Socket clientSocket = auctionSocket.accept();
                    //processess inputs
                    AH_AgentThread at = new AH_AgentThread(clientSocket);
                    activeAgents.add(at);
                    at.start();
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }

    public static int getAuctionId() {
        return auctionId;
    }

    /**
     * searches for agent in active agentlist of AH_AgentThreads
     * pulls out Id to find correct one.
     *
     * @param id int
     * @return AH_AgentThread
     */
    static AH_AgentThread agentSearch(int id) {
        for(AH_AgentThread agent: activeAgents) {
            if(agent.agentId == id) {
                return agent;
            }
        }
        return null;
    }
}
