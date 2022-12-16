
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
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
			auctionName = Inet4Address.getLocalHost().getHostName() + String.valueOf(port);
			connectToBank("localhost", 4444);
			createAccount();
			start();
			initAuction();
			startingFunds = 100;
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

	public void initAuction() // function which add Item object to the array when server starts running
	{
		ItemToBid.add(new Item("Iphone 14s", "20"));
		ItemToBid.add(new Item("c++ primer", "20"));
		ItemToBid.add(new Item("mac book", "40"));
		ItemToBid.add(new Item("airpods", "30"));
		// ItemToBid.add(new Item("beats", "25"));
		// ItemToBid.add(new Item("Iphone 13s", "20"));
		// ItemToBid.add(new Item("unix programming", "20"));
		// ItemToBid.add(new Item("huawei mate 40", "40"));
		// ItemToBid.add(new Item("oneplus 7", "30"));
		// ItemToBid.add(new Item("earbuds pro", "25"));
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
		AuctionServer server = null;
		if (args.length != 1)
			System.out.println("Usage: java AuctionServer port");
		else
			server = new AuctionServer(Integer.parseInt(args[0]));
	}

}
