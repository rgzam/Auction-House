package Agent;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Agent {
    static AgentSocketCommands socCmd = new AgentSocketCommands();

    static String agentName = "";
    static String bankHost = "";
    static String bankPort = "";
    static String startingFunds = "";
    static int agentAccountNumber = 0;
    static boolean pendingBids = false;

    static List<String> itemsWon = new ArrayList<String>();

    static LinkedBlockingQueue<String> inBoundMessages = new LinkedBlockingQueue<String>();
    static LinkedBlockingQueue<String> outBoundMessages = new LinkedBlockingQueue<String>();
    static LinkedBlockingQueue<String> myBalance = new LinkedBlockingQueue<String>();
    static LinkedBlockingQueue<String> itemsForAuction = new LinkedBlockingQueue<String>();

    protected static List<String> currentItems = new ArrayList<String>();
    protected static List<SocketIn> in = new ArrayList<SocketIn>();
    protected static List<SocketOut> out = new ArrayList<SocketOut>();
    protected static List<Thread> socketInThread = new ArrayList<Thread>();
    protected static List<Thread> socketOutThread = new ArrayList<Thread>();


    public static void main(String[] args) {
        bankHost = args[0];
        bankPort = args[1];
        agentName = args[2];
        startingFunds = args[3];
        System.out.println("Hello " + agentName + "!");
        Scanner scanner = new Scanner(System.in);

        try{
            MessageHandler messageHandler = new MessageHandler();
            Thread threadMessageHandler = new Thread(messageHandler);
            threadMessageHandler.start();

            //connect to bank
            socCmd.connectToSever(bankHost, bankPort, "bank", "bank", "1");
            socCmd.createBankAccount();

            while(true){
                socCmd.requestAHs();
                listItemsForAuction();
                textOptions();
                String action = scanner.nextLine();

                if(isInteger(action) && !currentItems.isEmpty()){
                    int bidOnItem = Integer.parseInt(action);
                    if(bidOnItem >= 1 && bidOnItem <= currentItems.size()){
                        System.out.println("How much would you like to bid?");
                        String bidAmount = scanner.nextLine();
                        if(isFloat(bidAmount)){
                            bidOnItem--;
                            socCmd.requestBalance();
                            while (myBalance.size() == 0){}
                            try {
                                float balance = Float.parseFloat(myBalance.take());
                                if(Float.parseFloat(bidAmount) <= balance){
                                    socCmd.makeBid(bidOnItem, Float.parseFloat(bidAmount));
                                }
                                else{
                                    System.out.println("Sorry you don't have enough money in your account to bid that much!\n" +
                                            "Your current balance is: $" + String.format("%.2f", balance));
                                }
                            } catch (InterruptedException e) {}
                        }
                    }
                    else{
                        System.out.println("Sorry that's not a correct option try again!");
                    }
                }
                else if(action.equals("c")){
                    socCmd.requestAHs();
                }
                else if(action.equals("b")){
                    requestBalance();
                }

                else if(action.equals("s")){
                    itemsWon();
                }
                else if(action.equals("q")){
                     checkPending();
                }
                else{
                    System.out.println("Sorry that's not a correct option try again!");
                }
            }
        }
        catch (Exception e){}
    }

    protected static void textOptions(){
        System.out.println("\nTo bid on an item type a number for the item.\n" +
                "If There are no items listed please try one of the following:\n" +
                "[c]-check for new auction houses or new items.\n" +
                "[b]-check your bank account balance.\n" +
                "[s]-show a list of items you have won.\n" +
                "[q]-to quit the program.");
    }

    protected static void requestBalance(){
        socCmd.requestBalance();
        while (myBalance.size() == 0){}
        try {
            float balance = Float.parseFloat(myBalance.take());
            System.out.println("Your current Bank Balance: $" + String.format("%.2f", balance));
        } catch (InterruptedException e) {}
    }

    protected static void checkPending(){
        socCmd.checkPending();
        long delay = 0;
        while(delay <= 1200000000L){delay++;}
        if(!pendingBids){
            quit();
        }
        else if (pendingBids){
            System.out.println("Sorry you can't currently quit you have a pending bid, try again later!");
        }
    }

    protected static void itemsWon(){
        if(itemsWon.isEmpty()){
            System.out.println("You current have not won any bids.");
        }
        else{
            System.out.println("Here are the items you've won!");
            for(String item: itemsWon){
                System.out.println(item);
            }
        }
    }

    protected static void listItemsForAuction(){
        currentItems.clear();
        socCmd.requestCurrentAuctions();
        int numberOfAHConnections = 0;
        int location = 1;
        int itemNum = 1;
        long checkAgain = 0;
        int attempt = 1;
        for(int i = 0; i < out.size(); i++){
            if(!out.get(i).connectionType.equals("bank")){
                if(socketOutThread.get(i).isAlive()){
                    numberOfAHConnections++;
                }
            }
        }
        while (itemsForAuction.size() != numberOfAHConnections){
            if(attempt == 4){
                break;
            }
            else if(attempt >= 2){
                socCmd.requestAHs();
            }
            if(checkAgain >= 1000000000L){
                currentItems.clear();
                socCmd.requestCurrentAuctions();
                checkAgain = 0;
                attempt++;
            }
            checkAgain++;
        }
        while (!itemsForAuction.isEmpty()){
            try {
                String items = itemsForAuction.take();
                String[] itemList = items.split("\t");
                String item = "";
                for(int i = 2; i < itemList.length; i++){
                    if(location == 1){
                        item = itemNum + ".\t";
                    }
                    if(location < 5) {
                        item = item + itemList[i] + "\t";
                    }

                    else if(location == 5){
                        item = item + itemList[i];
                        currentItems.add(item);
                        location = 0;
                        item = "";
                        itemNum++;
                    }
                    location++;

                }
            }
            catch (InterruptedException e){}
        }
        System.out.println("\n\nCurrent Items For Auction:");
        for(int i = 0; i < currentItems.size(); i++){
            System.out.println(currentItems.get(i));
        }
    }

    protected static boolean isInteger(String string){
        int value;
        if(string == null || string.equals("")){
            return false;
        }
        try{
            value = Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException e){}
        return false;
    }


    protected static boolean isFloat(String string){
        float value;
        if(string == null || string.equals("")){
            return false;
        }
        try{
            value = Float.parseFloat(string);
            return true;
        }
        catch (NumberFormatException e){}
        return false;
    }


    protected static void quit(){
        for(int i = 0; i < out.size(); i++){
            out.get(i).outBoundMessages.add("bye");
        }
        long delay = 0;
        while(delay <= 1200000000L){delay++;}
        System.out.println("Bye " + agentName + "!");
        System.exit(0);
    }
}
