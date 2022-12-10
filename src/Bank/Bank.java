package Bank;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Bank {

    static RequestHandler socCmd = new RequestHandler();

    protected static String bankHostName;
    protected static int bankPort;

    //Socket & Socket Server Setup
    static int connectionNumbering = 0;
    protected static List<InboundSocket> msgIn = new ArrayList<InboundSocket>();
    protected static List<OutboundSocket> msgOut = new ArrayList<OutboundSocket>();
    protected static List<Thread> msgInThread = new ArrayList<Thread>();
    protected static List<Thread> msgOutThread = new ArrayList<Thread>();

    static LinkedBlockingQueue<String> inBoundMessages = new LinkedBlockingQueue<String>();
    static LinkedBlockingQueue<String> outBoundMessages = new LinkedBlockingQueue<String>();


    //HashMap --- accountNumber, Bank.BankAccount
    protected static HashMap<Integer, BankAccount> allAccounts = new HashMap<Integer, BankAccount>();
    //Hashmap of client's Ports, with their accountNumbers
    protected static HashMap<Integer, Integer> clientPortsWithAccNum = new HashMap<>();


    protected static HashMap<Integer, BankAccount> auctionHouses = new HashMap<>();
    protected static List<String> auctionHousesInfo = new ArrayList<>();//list of auctionHouse info


    //return "requestedListOfAH AuctionHouseName AuctionHouseAccountID AuctionHouseHostName AuctionHousePort... etc"
    public static String getAuctionHouses() {
        String info = "requestedListOfAH" + "\t";
        for (String x : auctionHousesInfo) {
            info = info + x;
        }
        info = info + "\t";

        return info;
    }

    /**
     * When a AuctionHouse wants to disconnect
     * Remove them from our list of AuctionHouses
     */
    public static void removeAhouse(String auctionName) {
        int pos = 0;
        for (String x : auctionHousesInfo) {
            if (x.contains(auctionName)) {
                auctionHousesInfo.remove(pos);
                break;
            }
            pos++;
        }
    }

    //todo close Auction house, remove auctionHouse from list

    public static void main(String[] args) {
        bankHostName = "BankHostName";
        bankPort = Integer.parseInt(args[0]);
        //bankPort = 4444;

        try {
            //auctionHouseHost = InetAddress.getLocalHost().getHostName();
            System.out.println(bankHostName + " " + InetAddress.getLocalHost().getHostName());

            //Start Server
            BankServer server = new BankServer(bankPort);
            Thread serverThread = new Thread(server);
            serverThread.start();

            //Testing
            //Scanner scanner = new Scanner(System.in);

            while (true) {
                if (inBoundMessages.size() > 0) {
                    socCmd.inBoundMSG();
                    //System.out.println("done inboundMSG");
                }
                if (outBoundMessages.size() > 0) {
                    socCmd.outBoundMSG();
                    //System.out.println("done outBoundMSG");
                }
            }
        } catch (Exception e) {
        }

    }


}
