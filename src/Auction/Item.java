import java.sql.Time;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

public class Item{
	
	//Variables for item information.
	String itemName= "";
	int itemID;
    	float startingValue = 0f;
    	float currentBid = 0f;
    	
    	//Variables to currently highest bidder.
    	String highestBidder = "";
    	int bidderAccountNumber = 0;
    	int highestBidderConnectionID = 0;

    	//Variables for timing 30 seconds.
    	long startTime = 0;
    	long currentTime = 0;
    	long timeElapsed;
    	    
	public boolean soldOut = false;
	public boolean soldIng = false;
	public void startTime() {
		soldIng = true;
		timer = new Timer("startTime");
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				String acceptBidMessage = "connectionNumber\t" + "bid\t" 						+"accepted";
				currentHolder.agentHandler.send(acceptBidMessage);
				accept = true;
			}
			public void run() {
				String outBidMessage = "connectionNumber\t" + "outbid\t" + "Auction.auctionName\t" + "Auction.auctionAccountNum\t" + item + "\t"
						+ yourBid + "\t";
				currentHolder.agentHandler.send(outbidMessage);
				lostBid = true;
			}
		}, 30000);
	}
    
    /**
     * Helper method to send an agent they won the bid.
     */
    protected void winner(){
        socCmd.winner(String.valueOf(highestBidderConnectionID), itemName,
                String.valueOf(currentBid));
    }
    
	public Item(String n, String p) {
		name = n;
		price = p;
		description = "here is description template";
		currentBid = price;
		currentHolder = null;
	}

	public String myToString() {
		return itemName + "\t" + itemID + "\t" + startingValue + "\t" + currentBid + "\t";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
    
}
