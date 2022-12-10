package auctionhouse;

/**
 * This class holds the logic/communication of the Auction House.
 * The Auction House can communicate with the bank and an agent.
 * Each AuctionHouse creates the following objects based on private classes:
 */
public class AuctionHouse{
    private ServerSocket server; 
    //ServerSocket for agent communication
    private Socket auctionClient; 
    //Socket for bank communication
    private ObjectInputStream input;
    //stream to receive messages to Auction
    private ObjectOutputStream out; 
    //output stream to send messages
    private ItemList list; 
    //List of possible item names
    private final ArrayList<Item> catalogue = new ArrayList<>();
    //list of items currently for sale
    private final List<AgentProxy> activeAgents = new LinkedList<>();
    //List of currently connected agents.
    private final BlockingQueue<Boolean> check = new LinkedBlockingDeque<>();
    //Blocking queue used for AuctionGui communication
    private final ArrayList<String> log; 
    //log of activities/notifications
    private double balance = 0.0; 
    // the bank balance of the AuctionHouse
    private boolean run = true; 
    //boolean to keep certain threads looping
    private UUID auctionId; 
    //The id given to the AuctionHouse by the bank
    private String ip;  
    //ip address of Auction server
    private int port; 
    //port number of the Auction server

    /**
     * The constructor first connects to the bank and starts its own server.
     * The AuctionHouse object then immediately registers with the bank
     * then creates a thread to handle socket join requests to the server.
     * @param address the ip address of the bank
     * @param clientPort the port number of the bank
     * @param serverPort port number for Auction to create server
     */
    public AuctionHouse(String address, int clientPort, int serverPort){
        setupItemList();
        log = new ArrayList<>();
        try{
            log.add("Connecting to bank");
            auctionClient = new Socket();
            auctionClient.connect(
                    new InetSocketAddress(address,clientPort),);
            server = new ServerSocket(serverPort);
            Thread serverThread = new Thread(new AuctionServer());
            serverThread.start();
            out = new ObjectOutputStream(auctionClient.getOutputStream());
            setupItemList();
            try(final DatagramSocket socket = new DatagramSocket()){
                socket.connect(InetAddress.getByName("8.8.8.8"), );
                ip = socket.getLocalAddress().getHostAddress();
            }
            port = server.getLocalPort();
            NetInfo serverInfo = new NetInfo(ip,port);
            List<NetInfo> ahInfo = new LinkedList<>();
            ahInfo.add(serverInfo);
            Message register = new Message.Builder().command(Command.REGISTER_AH)
                    .netInfo(ahInfo).send(null);
            sendToBank(register);
            Thread inThread = new Thread(new AuctionIn());
            inThread.start();
            } catch(IOException u){
            check.add(false);
        }
    }

    /**
     * creates items for the auction house from the item list
     * @param needed number of items to create
     */
    private void addItems(int needed){
        while(needed > 0){
            String name = list.getRandomName();
            int random = new Random().nextInt(50);
            Item item = new Item(name, random,auctionId);
            catalogue.add(item);
            needed--;
        }
    }

    /**
     * creates catalogue for items to sell
     */
    private void setupItemList(){
        int test = new Random().nextInt(20);
        list = ItemList.createNameList("Destiny2.txt");
    }

    /**
     * This class is dedicated to handling messages sent from an specific agent.
     */
    private class AuctionIn implements Runnable {
        /**
         * loop to wait for new messages forever(until an exception is thrown).
         * Incoming messages that aren't looped (GET_AVAILABLE) are added
         * to the log.
         */
        @Override
        public void run() {
            try {
                input = new ObjectInputStream(auctionClient.getInputStream());
                while(run){
                    Message message = (Message) input.readObject();
                    Command temp = message.getCommand();
                    if(temp != Command.GET_AVAILABLE){
                        log.add("Bank: " + message);
                    }
                    processMessage(message);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e){
                try {
                    input.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private void processMessage(Message message){
            Command type = message.getCommand();
            switch(type){
                case HOLD:
                    hold(message);
                    break;
                case RELEASE_HOLD:
                    released(message);
                    break;
                case REGISTER_AH:
                    registered(message);
                    break;
                case GET_AVAILABLE:
                    bankBalance(message);
                    break;
            }
        }

        private void hold(Message message) {
            UUID bidder = message.getAccountId();
            Message.Response response = message.getResponse();
            AgentProxy temp = agentSearch(bidder);
            if (temp != null) {
                try{
                    if (response == Message.Response.SUCCESS) {
                        temp.bankSignOff.put(true);
                    }else if(response == Message.Response.INSUFFICIENT_FUNDS){
                        temp.bankSignOff.put(false);
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

        /**
         * updates the balance variable to the bank balance given by the bank,
         *
         * @param message the message with the available balance for this object
         */
        private void bankBalance(Message message) {
            balance = message.getAmount();
        }

        private void released(Message message){
            Message.Response response = message.getResponse();
            if(response == Message.Response.SUCCESS){
                log.add("release was successful");
            }else{
                log.add("release failed");
            }
        }
        private void registered(Message message){
            auctionId = message.getAccountId();
            addItems(4);
            check.add(true);
            Thread timer = new Thread(new Countdown());
            timer.setDaemon(true);
            timer.setPriority(4);
            timer.start();
        }
    }
    /**
     * This inner class is dedicated to creating AuctionProxys for each
     * socket join request.
     */
    private class AuctionServer implements Runnable{
        @Override
        public void run() {
            try{
                while(run){
                    Socket clientSocket = server.accept();
                    AgentProxy newAgent = new AgentProxy(clientSocket);
                    activeAgents.add(newAgent);
                }
            }catch (IOException e){
                run = false;
            }
        }
    }
