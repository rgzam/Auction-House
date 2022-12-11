package Agent;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class AgentSocketCommands {

    protected static void inBoundMSG(){
        while(Agent.inBoundMessages.size() > 0){
            try{
                String msg = Agent.inBoundMessages.take();
                String[] action = msg.split("\t");
                if(action[1].equals("createdAccount")){
                    Agent.agentAccountNumber = Integer.parseInt(action[2]);
                    System.out.println("Agent Account Number is: " + Agent.agentAccountNumber);
                }
                else if(action[1].equals("requestedCurrentBalance")){
                    Agent.myBalance.add(action[2]);
                }
                else if(action[1].equals("true") || action[1].equals("false")){
                    Agent.pendingBids = Boolean.parseBoolean(action[1]);
                }
                else if(action[1].equals("requestedListOfAH")){
                    if(action.length > 2){
                        connectToAH(msg);
                    }
                }

                else if(action[1].equals("currentAuctions")){
                    if(action.length > 2){
                        Agent.itemsForAuction.add(msg);
                    }
                }
                else if(action[1].equals("bid")){
                    if(action[2].equals("auctionClosed")){
                        System.out.println("Sorry the auction for that item was closed!");
                    }
                    else{
                        System.out.println("Your bid was " + action[2] + "!");
                    }
                }

                else if(action[1].equals("outbid")){
                    System.out.println("You've been outbid on " + action[4] +
                    ", "+ action[5] +" has been returned to your account.");
                }

                else if(action[1].equals("win")){
                    System.out.println("Congratulations you won your bid on: " + action[4]);
                    transferFunds(action[4]);
                    Agent.itemsWon.add(action[4]);
                }
                else if(action[1].equals("completePurchase")){
                    System.out.println("Your current balance after your latest bid win is: $" + String.format("%.2f", Float.parseFloat(action[2])));
                }
            }
            catch (InterruptedException e) {}
        }

    }

    protected static void outBoundMSG(){
        while(Agent.outBoundMessages.size() > 0){
            try{
                String msg = Agent.outBoundMessages.take();
                String [] sendTo = msg.split("\t");
                for(int i = 0; i < Agent.out.size(); i++){
                    int connectNum = Agent.out.get(i).accountNum;
                    if(sendTo[0].equals(String.valueOf(connectNum))){
                        Agent.out.get(i).outBoundMessages.add(removeHeader(msg));
                        break;
                    }
                }

            }
            catch (InterruptedException e){}
        }
    }

    protected static String removeHeader(String message){
        String[] stringSplit = message.split("\t");
        String returnString = "";
        for(int i = 1; i < stringSplit.length; i++){
            if(i != stringSplit.length-1){
                returnString = returnString +  stringSplit[i] + "\t";
            }
            else{
                returnString = returnString +  stringSplit[i];
            }
        }
        return  returnString;
    }

    protected static void connectToSever(String host, String port, String connectionType, String locationName, String accountNumber){
        try {
            Socket socket = new Socket(host, Integer.parseInt(port));
            SocketIn msgIn = new SocketIn(socket, connectionType, locationName, accountNumber);
            SocketOut msgOut = new SocketOut(socket, connectionType, locationName, accountNumber);
            Thread inThread = new Thread(msgIn);
            Thread outThread = new Thread(msgOut);
            inThread.start();
            outThread.start();
            Agent.in.add(msgIn);
            Agent.out.add(msgOut);
            Agent.socketInThread.add(inThread);
            Agent.socketOutThread.add(outThread);
        }
        catch (IOException e) {}
    }

    protected static void createBankAccount() {
        try {
            String createAcct = "1" + "\t"
                    + "createAccount" + "\t"
                    + "Agent" + "\t"
                    + Agent.agentName + "\t"
                    + Agent.startingFunds + "\t"
                    + InetAddress.getLocalHost().getHostName()+ "\t"
                    + "1234";
        Agent.outBoundMessages.add(createAcct);
        }
        catch (UnknownHostException e) {}
    }

    protected static void requestBalance(){
        String reqBal = "1" + "\t"
                    + "requestCurrentBalance" + "\t"
                    + Agent.agentAccountNumber;
        Agent.outBoundMessages.add(reqBal);
    }

    protected static void checkPending(){
        String chkPnd = "1" + "\t"
                + "checkPending" + "\t"
                + Agent.agentAccountNumber;
        Agent.outBoundMessages.add(chkPnd);
    }

    protected static void requestAHs(){
        String reqAH = "1" + "\t"
                + "requestListOfAH";
        Agent.outBoundMessages.add(reqAH);
        long delay = 0;
        while(delay <= 1200000000L){delay++;}
    }

    protected static void requestCurrentAuctions(){
        for(int i = 0; i < Agent.out.size(); i++){
            if(!Agent.out.get(i).connectionType.equals("bank")){
                String reqAuctions = Agent.out.get(i).accountNum + "\t"
                                     + "currentAuctions";
                Agent.outBoundMessages.add(reqAuctions);
            }
        }
    }


    protected static void connectToAH(String message){
        boolean alreadyConnected = false;
        int locationInSplit = 1;
        String connectionType = "";
        String ahName = "";
        String ahAcctNum = "";
        String ahHost = "";
        String ahPort = "";

        String[] ahSplit = message.split("\t");
        for(int i = 2; i < ahSplit.length; i++){
            if(locationInSplit == 1){
                connectionType = ahSplit[i];
            }
            if(locationInSplit == 2){
                ahName = ahSplit[i];
            }
            if(locationInSplit == 3){
                ahAcctNum = ahSplit[i];
            }
            if(locationInSplit == 4){
                ahHost = ahSplit[i];
            }
            if(locationInSplit == 5) {
                ahPort = ahSplit[i];
                for(int j = 0; j < Agent.in.size(); j++){
                    if(Agent.in.get(j).accountNum == Integer.parseInt(ahAcctNum)){
                        alreadyConnected = true;
                        break;
                    }
                }
                if(!alreadyConnected){
                    System.out.println("You are connected to Auction House: " + ahName);
                    connectToSever(ahHost, ahPort, connectionType, ahName, ahAcctNum);
                }
                alreadyConnected = false;
                locationInSplit = 0;
            }
            locationInSplit++;
        }
    }

    protected static void makeBid(int item, float amount){
        String auctionItem = Agent.currentItems.get(item);
        String [] itemSplit = auctionItem.split("\t");
        String bid = itemSplit[1] + "\t"
                     + "bid" + "\t"
                     + itemSplit[3] + "\t"
                     + Agent.agentName + "\t"
                     + Agent.agentAccountNumber + "\t"
                     + String.valueOf(amount);
        Agent.outBoundMessages.add(bid);
    }

    protected static void transferFunds(String item) {
        String transfer = "1" + "\t"
                         + "completePurchase" + "\t"
                         + Agent.agentAccountNumber + "\t"
                         + item;
        Agent.outBoundMessages.add(transfer);
    }
}
