import java.sql.Time;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Item {
	private String name;
	private String price;
	private String description;
	public String currentBid;
	public ItemBider currentHolder;
	public Timer timer;
	public boolean soldOut = false;
	public boolean soldIng = false;
	public void timerStart() {
		soldIng = true;
		timer = new Timer("timerStart");
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				String outbidMessage = "replace\t" + "win\t" + "replace\t" + "replace\t" + getName() + "\t"
						+ currentBid + "\t";
				currentHolder.agentHandler.send(outbidMessage);
				soldOut = true;
			}
		}, 30000);
	}

	public Item(String n, String p) {
		name = n;
		price = p;
		description = "here is description template";
		currentBid = price;
		currentHolder = null;
	}

	public String myToString() {
		return name + "\t" + description + "\t" + price + "\t" + currentBid + "\t";
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
