
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ServerHandler extends Thread {
	private AuctionServer server = null;
	private Socket socket = null;
	private int ID = -1;

	private Thread thread;
	private DataInputStream streamIn = null;
	private DataOutputStream streamOut = null;

	public ServerHandler(AuctionServer server, Socket socket) {
		super();
		this.server = server;
		this.socket = socket;
		this.ID = socket.getPort();

	}

	public void send(String msg) {
		try {
			streamOut.writeUTF(msg);
			streamOut.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public int getID() {
		return ID;
	}

	public void run() {
		System.out.println("Server Thread " + ID + " running.");
		this.thread = new Thread(this);
		while (true) {
			try {
				// house id, item id, description, minimum bid and current bid.
				String message = streamIn.readUTF();
				String[] splitRes = message.split("\t");
				if (splitRes.length > 1 && splitRes[1].equals("currentAuctions")) {
					StringBuffer allItems = new StringBuffer();
					for (Item item : server.ItemToBid) {
						allItems.append(server.auctionName + "\t" + item.myToString());
					}
					streamOut.writeUTF(allItems.toString());
				} else if (splitRes.length > 1 && splitRes[1].equals("bid")) {
					String holderName = splitRes[3];
					String agentAccountNumber = splitRes[4];
					// requestCurrentBalance
					String requestCurrentBalanceMessage = "requestCurrentBalance" + "\t" + agentAccountNumber;
					server.bankOutput.writeUTF(requestCurrentBalanceMessage);
					String requestCurrentBalanceRes = server.bankInput.readUTF();
					String[] requestCurrentBalanceResSplit = requestCurrentBalanceRes.split("\t");
					float currentBalance = Float.parseFloat(requestCurrentBalanceResSplit[2]);
					//
					String holderBidAmount = splitRes[5];
					if (Float.parseFloat(holderBidAmount) > currentBalance) {
						// current balance is not enough

						return;
					}
					Item currItem = null;
					for (int i = 0; i < server.ItemToBid.size(); ++i) {
						if (server.ItemToBid.get(i).getName().equals(holderName)) {
							currItem = server.ItemToBid.get(i);
							break;
						}
					}
					assert (currItem != null);
					String outbidMessage = "replace\t" + "outbid\t" + "replace\t" + "replace\t" + currItem.getName()
							+ "\t" + currItem.currentBid + "\t";
					if (currItem.currentHolder != null) {
						// unpend before holder
						String unpendMessage = "unpendFunds" + "\t" + agentAccountNumber + "\t" + "replace\t"
								+ "replace\t" + server.auctionName;
						server.bankOutput.writeUTF(unpendMessage);

						currItem.currentHolder.agentHandler.send(outbidMessage);
					}
					currItem.currentHolder = new ItemBider(holderName, server.clientsKeeper.get(holderName));
					// "pendFunds agentAccountNumber PendAmmount AuctionHouseAccountID ItemName"
					String pendFundsMsg = "pendFunds\t" + agentAccountNumber + "\t" + holderBidAmount + "\t"
							+ server.accountNumber + "\t" + server.auctionName;
					server.bankOutput.writeUTF(pendFundsMsg);
					currItem.currentBid = holderBidAmount;
					currItem.timer.cancel();
					currItem.timerStart();
				}
				int pause = (int) (Math.random() * 3000);
				Thread.sleep(pause);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public void open() throws IOException {
		streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	}

	public void close() throws IOException {
		if (socket != null)
			socket.close();

		if (streamIn != null)
			streamIn.close();

		if (streamOut != null)
			streamOut.close();
	}
}