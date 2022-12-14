package Auction;

import shared.A_AH_Messages;
import shared.A_AH_Messages.A_AH_MTopic;
import shared.Items.Item;
import shared.Message;

/**
 * AgentActions contains the methods that directly involve interacting with the
 * agent.
 */
public class AgentActions {
    private static int    itemID;
    private static int    bidderId;
    private static String name;

    /**
     * This method grabs the itemID, bidderId, name, and amount of the
     * item to bid on. First it checks if the item is still for sale, then
     * checks if the bid amount is above the minimum/current bid. Then it
     * requests the bank to hold the bidded funds and waits for a response.
     * After receiving the response, it then decides whether to reject or
     * accept the bid.
     *
     * @param message The message with AMType BID
     */
    static void bid(A_AH_Messages message) {
        itemID   = message.getItem();
        bidderId = message.getAccountId();
        name     = message.getItemName();
        int newBid = message.getBid();
        Item bidItem = itemSearch(itemID);
        if(bidItem == null) {
            reject(itemID,name);
            return;
        }
        int minimumBid = bidItem.getMinimumBid();
        int currentBid = bidItem.getCurrentBid();
        if( currentBid < minimumBid) {
            currentBid = minimumBid;
        }
        if((newBid >= currentBid) && (bidderId != -1) ||
                (newBid > currentBid) && (bidderId == -1)) {
            System.out.println("Bid amount sufficient" + message);
            Message requestBlock = new Message.Builder()
                .command(Message.Command.BLOCK)
                .accountId(bidderId)
                .balance(newBid)
                .senderId(bidderId);
            //requests the hold.
            Message response = BankActions.sendToBank(requestBlock);
            if(response != null && response.getResponse() == Message.Response.SUCCESS) {
                System.out.println("Block request Success" + response);
                A_AH_Messages accept = A_AH_Messages.Builder.newBuilder()
                        .topic(A_AH_MTopic.SUCCESS)
                        .itemId(message.getItem())
                        .name(name)
                        .build();
                AH_AgentThread.sendOut(accept);
                int oldBidder = bidItem.getBidderId();
                if(oldBidder != -1) {
                    System.out.println("1'st bid on Item OutBid Success");
                    release(oldBidder, currentBid);
                    outBid(oldBidder);
                    bidItem.resetBidTime();
                }
                System.out.println("OutBid processed");
                bidItem.resetBidTime();
                bidItem.setBid(bidderId, newBid);
                accept(bidItem.getItemID(), bidItem.getName());
            } else {
                assert response != null;
                if(response.getResponse() == Message.Response.FAILURE) {
                    System.out.println("Block request Failure" + response);
                    A_AH_Messages accept = A_AH_Messages.Builder.newBuilder()
                            .topic(A_AH_MTopic.REJECT)
                            .itemId(message.getItem())
                            .name(name)
                            .auctionList(CountDown.getAuctionList())
                            .build();
                    AH_AgentThread.sendOut(accept);
                    reject(itemID,name);
                }
            }
        } else {
            reject(itemID,name);
        }
    }

    /**
     * Once a bid by an Agent is accepted, this method lets the agent
     * know their bid was accepted. The message also contains the updated
     * catalogue
     *
     * @param item int of the item bid on
     * @param name String/name of the item bid on
     */
    private static void accept(int item, String name){
        System.out.println("Return bid SUCCESS" + item);
        A_AH_Messages accept = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_MTopic.SUCCESS)
                .itemId(item)
                .name(name)
                .build();
        AH_AgentThread.sendOut(accept);
    }

    /**
     * outBid replaces the new bid/bidder with the new ones and
     * tells the old bidder they were outbid.
     *
     * @param oldBidder int ID
     */
    private static void outBid(int oldBidder){
        AH_AgentThread agent = AuctionServer.agentSearch(oldBidder);
        A_AH_Messages outbid = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_MTopic.OUTBID)
                .itemId(itemID)
                .name(name)
                .accountId(bidderId)
                .build();
        assert agent != null;
        AH_AgentThread.sendOut(outbid);
    }

    /**
     * register lets the connected agent know they successfully
     * registered and sends the auctionList.
     */
    static void register() {
        A_AH_Messages reply = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_MTopic.REGISTER)
                .accountId(AuctionServer.getAuctionId())
                .auctionList(CountDown.getAuctionList())
                .build();
        AH_AgentThread.sendOut(reply);
    }

    /**
     * This method creates the message with the updated catalogue
     * and passes it to sendOut to send to the agent
     */
    static void update() {
        A_AH_Messages update = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_Messages.A_AH_MTopic.UPDATE)
                .auctionList(CountDown.getAuctionList())
                .build();
        AH_AgentThread.sendOut(update);
    }

    /**
     * creates message to agent about shutdown then shuts down agent port.
     * sending message left to AH_AgentThread.
     */
    static void deRegister() {
        A_AH_Messages shutDown = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_Messages.A_AH_MTopic.DEREGISTER)
                .build();
        AH_AgentThread.sendOut(shutDown);
    }

    /**
     * Lets the bidder know its bid was rejected due to various reasons
     * (not enough funds, bid not high enough, etc.)
     * @param itemId The int of the item bid on
     * @param name the name of the item
     */
    private static void reject(int itemId, String name) {
        System.out.println("Bid rejected");
        A_AH_Messages reject = A_AH_Messages.Builder.newBuilder()
                .topic(A_AH_MTopic.REJECT)
                .name(name)
                .itemId(itemId)
                .build();
        AH_AgentThread.sendOut(reject);
    }

    /**
     * itemSearch searches the auctionList for an item of matching id
     *
     * @param itemId the int of the item being searched
     * @return returns the item searched, or null if item isn't found
     */
    private static Item itemSearch(int itemId) {
        for(Item item: CountDown.getAuctionList()) {
            if(item.getItemID() == itemId) {
                return item;
            }
        }
        return null;
    }

    /**
     * requests the bank to release the hold of (amount) amount on account
     * id
     * @param id the account(bidder) having their funds released
     * @param amount the amount requested to release
     */
    private static synchronized void release(int id, int amount) {
        Message unBlock = new Message.Builder()
                .command(Message.Command.UNBLOCK)
                .accountId(id)
                .balance(amount)
                .senderId(0);
        BankActions.sendToBank(unBlock);
    }
}
