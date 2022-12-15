package Auction;

import java.io.Serializable;
/**
 * Item class is the object type for the auction Items and contains the
 * requisite to carry out that task including a Builder and get methods.
 * Also has timer related methods for finalizing bids;
 * Namely remainingTime, setBid and resetBidTime.
 */
public class Item implements Serializable {
    private   int    auctionId;
    private   String name;
    private   String description;
    private   int    minimumBid;
    private   int    currentBid;
    private   int    bidderId;
    private   long   remainingTime;
    protected long   bidTime;
    private   int    itemId;

    /**
     * Item builds an Item when called.
     *
     * @param setName String
     * @param descriptionSet String
     * @param value int
     * @param Id int
     */
    public Item(String setName, String descriptionSet, int value, int Id) {
        description   = descriptionSet;
        name          = setName;
        minimumBid    = value;
        auctionId     = Id;
        currentBid    = minimumBid;
        bidderId      = -1;
        remainingTime = 30; //30 seconds until bid is final
        itemId        = Id;
        bidTime       = System.currentTimeMillis();
    }

    /**
     * getName returns name String
     *
     * @return name String
     */
    public String getName() {
        return name;
    }

    /**
     * getCurrentBid returns currentBid
     *
     * @return currentBid double
     */
    public int getCurrentBid() {
        return currentBid;
    }

    /**
     * getMinimumBid returns minimumBid
     *
     * @return minimumBid double
     */
    public int getMinimumBid() {
        return minimumBid;
    }

    /**
     * newBid sets new currentBid, bidderID and resets bidTime
     *
     * @param bidder String
     * @param amount double
     */
    public void newBid(int bidder, double amount) {
        this.bidderId = bidder;
        this.currentBid = (int) amount;
        resetBidTime();
    }

    /**
     * restBidTime sets new bidTime to time of current bid
     */
    public void resetBidTime() {
        bidTime = System.currentTimeMillis();
    }

    /**
     * elapsedTime sets/updates the remaining time before bid is accepted
     *
     * @param currentTime long
     */
    public void remainingTime(long currentTime) {
        remainingTime = 30 - ((currentTime-bidTime)/1000);
    }

    /**
     * setBid replace the old bidder and bid with the new ones
     *
     * @param bidder int
     * @param bid double
     */
    public void setBid(int bidder, int bid){
        this.bidderId = bidder;
        this.currentBid = bid;
        resetBidTime();
    }

    /**
     * getRemainingTime returns remaining time
     *
     * @return remainingTime long
     */
    public long getRemainingTime(){
        return remainingTime;
    }

    /**
     * getItemID returns ItemID
     *
     * @return itemID String
     */
    public int getItemID(){
        return itemId;
    }

    /**
     * getBidderID returns bidderID
     *
     * @return bidderID String
     */
    public int getBidderId(){
        return bidderId;
    }

    /**
     * getDescription returns description
     *
     * @return description String
     */
    public String getDescription() {
        return description;
    }

    /**
     * toString override returns string representation of Item
     *
     * @return String representation of Item
     */
    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", minimumBid=" + minimumBid +
                ", itemId=" + itemId +
                '}';
    }
}
