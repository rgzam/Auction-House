package Bank;

public class Transaction {
    //Keep Track of what item is pending to be purchased
    private String itemName;
    private float price;
    private int auctionHouseID;//account number of Auction House

    //"pendFunds agentAccountNumber PendAmmount AuctionHouseAccountID ItemName"
    public Transaction(float price, int auctionHouseID, String itemName) {
        this.itemName = itemName;
        this.price = price;
        this.auctionHouseID = auctionHouseID;
    }

    public String getItemName() {
        return itemName;
    }

    public float getPrice() {
        return price;
    }

    public int getAuctionHouseID() {
        return auctionHouseID;
    }

    //"pendFunds 100039 38.29 100043 Knife"
    //agentAccountNumber PendAmmount AuctionHouseAccountID ItemName"
    public String getItem() {
        return getPrice() + getAuctionHouseID() + getItemName();
    }


}