import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class AuctionServer implements Runnable {
	public HashMap<String, ServerHandler> clientsKeeper = new HashMap<>();
	private ServerSocket server = null;
	private Thread thread = null;
	private int auctionPort;
	private static Timer timer;
	private Socket bankSocket = null;
	DataInputStream bankInput = null;
	DataOutputStream bankOutput = null;
	public ArrayList<Item> ItemToBid = new ArrayList<Item>();
	public String auctionName;
	private int startingFunds;
	public String accountNumber;

	public AuctionServer(int port) {
		try {
			server = new ServerSocket(port);
			auctionPort = port;
            		auctionHouseHost = InetAddress.getLocalHost().getHostName();
			connectToBank("localhost", 4444);
			createAccount();
			start();
			initAuction();
			startingFunds = 1000;
		} catch (IOException ioe) {
			System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());

		}
	}

	public void connectToBank(String serverName, int serverPort) {
		try {
			bankSocket = new Socket(serverName, serverPort);
			bankInput = new DataInputStream(bankSocket.getInputStream());
			bankOutput = new DataOutputStream(bankSocket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createAccount() {
		try {
			String createAcct = "createAccount" + "\t"
					+ "AuctionHouse" + "\t"
					+ auctionName + "\t"
					+ startingFunds + "\t"
					+ InetAddress.getLocalHost().getHostName() + "\t"
					+ String.valueOf(auctionPort);
			bankOutput.writeUTF(createAcct);
			String createAcctRes = bankInput.readUTF();
			String []createAcctResSplit = createAcctRes.split("\t");
			this.accountNumber = createAcctResSplit[3];
			System.out.println("already");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// String message =
	}

	public void addRandomItemsForAuction // function which add Item object to the array when server starts running
	{	
	  int randNum = ThreadLocalRandom.current().
	  	nextInt(0, auctionItems.size()-1;
	
        Item item = new Item(auctionItems.get(randNum), itemNum);
        itemNum++;
        Thread threadItem = new Thread(item);
        
        currentAuctions.add(item);
        liveAuctions.add(threadItem);

        auctionItems.remove(randNum);
	}

	public void run() {
		for (; thread != null;) {
			try {
				System.out.println("Waiting for a client ...");
				joinUser(server.accept());
				Thread.sleep(3000);

			} catch (IOException ioe) {
				stop();
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		thread = null;
	}

	private void joinUser(Socket socket) {
		try {
			DataInputStream joinIn = new DataInputStream(socket.getInputStream());
			String newUserName = joinIn.readUTF();
			System.out.println("Client join: " + socket);
			ServerHandler newUser = new ServerHandler(this, socket);
			clientsKeeper.put(newUserName, newUser);
			newUser.open();
			newUser.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		auctionHouseName = args[0];
        auctionHousePort = Integer.parseInt(args[1]);
        bankHost = args[2];
        bankPort = Integer.parseInt(args[3]);
        ingestConfigFile(args[4]);

        //Print verification on startup.
        System.out.println("auction house name: " + auctionHouseName);
        System.out.println("auction house port: " + auctionHousePort);
        System.out.println("bank host: " + bankHost);
        System.out.println("bank port: " + bankPort);
        System.out.println("auction items list size: " + auctionItems.size());
	}

}
