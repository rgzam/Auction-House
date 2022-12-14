package Auction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CountDown class maintains and creates the auction item list.
 * It also is responsible for the timeout conditions for winning and the logic
 * needed to transfer an item, and otherwise determine and process a winning
 * bid.
 */
public class CountDown implements Runnable {
    private static ObjectOutputStream dBOut;
    private static ObjectInputStream dBIn;
    public static ArrayList<Item> auctionList = new ArrayList<>();
    private static final ArrayList<Item> auctionHistory = new ArrayList<>();

    /**
     * getAuctionList returns the auctionList
     *
     * @return auctionList ArrayList<Item>
     */
    public static ArrayList<Item> getAuctionList() {
        return auctionList;
    }
    /**
     * run started at list creation and at the addition of each new
     * item to the list. Starts time on that item and manages method
     * calls to determine winner and swap in new items.
     */
    @Override
    public void run() {
        CountDown.addItems(3);
        while(AH_AgentThread.running) {
            try {
                int i = 0;
                ArrayList<Item> auctionList = getAuctionList();
                int size = auctionList.size();
                int needed =  3 - auctionList.size();
                if(needed > 0){
                    addItems(needed);
                }
                //change to while
                while(i < size) {
                    Item listItem = auctionList.get(i);
                    long currentTime = System.currentTimeMillis();
                    listItem.remainingTime(currentTime);
                    long timeLeft = listItem.getRemainingTime();
                    if (timeLeft <= 0) {
                        itemResult(listItem);
                    }
                    i++;
                    size = auctionList.size();
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * After the time expires on an Item for sale. This method checks if
     * there was any bidders and sends the WINNER message to that bidder.
     * Also releases the hold on the bidders amount.
     *
     * @param item the item being checked
     */
    private void itemResult(Item item) {
        int bidder = item.getBidderId();
        AH_AgentThread agent = AuctionServer.agentSearch(bidder);
        if (bidder != -1) {
            Message release = new Message.Builder()
                    .command(Message.Command.TRANSFER)
                    .balance(item.getCurrentBid())
                    .accountId(bidder)
                    .senderId(AuctionServer.auctionId);
            Message response = BankActions.sendToBank(release);
            assert response != null;
            if(response.getResponse() == Message.Response.SUCCESS) {
                System.out.println("Item Transferred");
                agent.winner(item);
            } else {
                System.out.println("Item Transfer failed");
            }
            auctionList.remove(item);
        }
    }

    /**
     * addItems gets Items from database to replenish the list after a list item
     * times out and is removed or transferred upon a winning bid.
     *
     * @param needed int number of list items that need be gotten
     */
    static void addItems(int needed) {
        List<ConnectionReqs> reqsList = new ArrayList<>();
        reqsList.add(AuctionServer.reqs);
        Socket dBSocket = null;
        while(needed > 0) {
            Item item = null;
            Random random = new Random();
            int randInt = random.nextInt(200);
            DBMessage dBMessage = null;
            try {
                dBSocket = new Socket("localHost", 6002);
                dBOut = new ObjectOutputStream(dBSocket.getOutputStream());
                dBOut.flush();
                dBIn = new ObjectInputStream(dBSocket.getInputStream());
                dBMessage = new DBMessage.Builder()
                        .command(DBMessage.Command.GET)
                        .table(DBMessage.Table.ITEM)
                        .accountId(randInt)
                        .build();
                dBOut.writeObject(dBMessage);
                System.out.println("listening...");
                DBMessage response = (DBMessage) dBIn.readObject();
                item = (Item) response.getPayload();
                System.out.println(item.toString());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(!auctionHistory.contains(item)) {
                System.out.println(dBMessage.toString());
                auctionList.add(item);
                auctionHistory.add(item);
                needed--;
            }
        }
    }
}
