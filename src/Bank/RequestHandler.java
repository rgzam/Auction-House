package Bank;

import Bank.Bank;

public class RequestHandler {
    private static String connectionNumber;

    protected static void inBoundMSG() {
        while (Bank.inBoundMessages.size() > 0) {
            try {
                String msg = Bank.inBoundMessages.take();
                String[] action = msg.split("\t");
                connectionNumber = action[0];
                handleRequest(removeHeader(msg));
                //System.out.println("handled request-->"+removeHeader(msg));

            } catch (InterruptedException e) {
            }
        }
    }

    protected static void outBoundMSG() {
        while (Bank.outBoundMessages.size() > 0) {
            try {
                String msg = Bank.outBoundMessages.take();
                System.out.println("Outbound msg: " + msg);
                String[] sendTo = msg.split("\t");

                for (int i = 0; i < Bank.msgOut.size(); i++) {
                    int connNum = Bank.msgOut.get(i).connectionNumber;
                    if (sendTo[0].equals(String.valueOf(connNum))) {
                        System.out.println("send to: " + sendTo[0] + " connectionNumber " + connNum);
                        Bank.msgOut.get(i).outBoundMessages.add(removeHeader(msg));
                        break;
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Remove connection Number from message
     */
    protected static String removeHeader(String message) {
        String[] stringSplit = message.split("\t");
        String returnString = "";
        for (int i = 1; i < stringSplit.length; i++) {
            if (i != stringSplit.length - 1) {
                returnString = returnString + stringSplit[i] + "\t";
            } else {
                returnString = returnString + stringSplit[i];
            }
        }
        return returnString;
    }


    //Split message into an array of Strings/commands
    private static String[] spliceMsg(String msgIn) {
        return msgIn.split("[\t]");
    }

    /**
     * Given a set-command
     * if that first command meets any of the switch cases
     * The command is handled
     */
    public static void handleRequest(String msgIn) {
        String[] msg = spliceMsg(msgIn);

        switch (msg[0]) {
            case "createAccount":
                //createAccount client clientName initialBalance hostName port
                createAccount(msg[1], msg[2], Integer.parseInt(msg[3]), msg[4], Integer.parseInt(msg[5]));
                break;
            case "requestListOfAH":
                //requestListOfAH
                Bank.outBoundMessages.add(connectionNumber + "\t" + Bank.getAuctionHouses());
                break;
            case "requestCurrentBalance":
                //requestCurrentBalance accNum
                //String msgOut = "requestedCurrentBalance " + Bank.Bank.allAccounts.get(Integer.parseInt(msg[1]));
                String msgOut = "requestedCurrentBalance"+"\t"+Bank.allAccounts.get(Integer.parseInt(msg[1])).getBalance();
                Bank.outBoundMessages.add(connectionNumber + "\t" + msgOut);
                break;
            case "checkPending":
                //checkPending accNum
                String messageOut = Bank.allAccounts.get
                        (Integer.parseInt(msg[1])).hasPendingFunds(Integer.parseInt(msg[1])) + "";
                Bank.outBoundMessages.add(connectionNumber + "\t" + messageOut);
                break;
            case "pendFunds":
                //"pendFunds agentAccountNumber PendAmmount AuctionHouseAccountID ItemName"
                int accountNum = Integer.parseInt(msg[1]);
                float price = Float.parseFloat(msg[2]);
                int auctionHouseID = Integer.parseInt(msg[3]);
                String auctionItem = msg[4];

                Bank.allAccounts.get(accountNum).addPendingFund(auctionItem, price, auctionHouseID);
                break;
            case "unpendFunds":
                //unpendFunds accNum auctionItem
                //String name2 = Bank.Bank.allAccounts.get(Integer.parseInt(msg[1])).accName;
                Bank.allAccounts.get(Integer.parseInt(msg[1])).releasePendFunds(msg[4]);//release funds method
                break;
            case "completePurchase":
                //"completePurchase accountNumber auctionItem"
                Bank.allAccounts.get(Integer.parseInt(msg[1])).compTransaction(Integer.parseInt(msg[1]), msg[2]);
                float balance = Bank.allAccounts.get(Integer.parseInt(msg[1])).getBalance();

                Bank.outBoundMessages.add(connectionNumber + "\t" + "completePurchase" + "\t" + balance);
                break;

        }

    }

    /**
     * Create a new Bank.Bank Account with the given parameters
     * client/AH clientName initialBalance hostName port
     * Ex.
     */
    public static void createAccount(String client, String name, float initialBalance, String hostName, int port) {
        BankAccount newAcc = new BankAccount(name, initialBalance, hostName, port);//Create new Bank.BankAccount

        if (client.compareTo("AuctionHouse") == 0) {
            Bank.auctionHouses.put(newAcc.getAccNum(), newAcc);///Store auctionHouse account info
            String ahInfo = client + "\t" + name + "\t" + newAcc.getAccNum() + "\t" + hostName + "\t" + port + "\t";
            Bank.auctionHousesInfo.add(ahInfo);//Add AuctionHouse connection Info
        }

        Bank.allAccounts.put(newAcc.getAccNum(), newAcc);//Store agent's new Account info

        //Store the clients connectionNumber as key, and their account number
        Bank.clientPortsWithAccNum.put(Integer.parseInt(connectionNumber), newAcc.getAccNum());

        //Create outBound Message
        Bank.outBoundMessages.add(connectionNumber + "\t" + "createdAccount" + "\t" + newAcc.getAccNum());
    }

}
