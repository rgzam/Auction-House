package Bank;

import Bank.Bank;

import java.io.*;
import java.net.Socket;

public class InboundSocket implements Runnable {
    protected final Socket clientSocket;
    protected final int connectionNumber;

    protected InputStreamReader inputStreamReader = null;

    protected BufferedReader bufferedReader = null;

    public InboundSocket(Socket socket, int connectNum) {
        clientSocket = socket;
        connectionNumber = connectNum;

        try {
            inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
        } catch (IOException e) {
        }

        bufferedReader = new BufferedReader(inputStreamReader);
    }


    @Override
    public void run() {
        try {
            //System.out.println("Inbound socket thread live! Connection Number: " + connectionNumber);
            while (true) {

                //Read in
                String messageFromClient = bufferedReader.readLine();
                if (messageFromClient == null) {
                    close();
                    break;
                } else if (messageFromClient.equalsIgnoreCase("bye")) {
                    int accNum = Bank.clientPortsWithAccNum.get(connectionNumber);
                    String name = Bank.allAccounts.get(accNum).accName;
                    if (Bank.clientPortsWithAccNum.containsKey(connectionNumber)) {
                        Bank.clientPortsWithAccNum.remove(connectionNumber);
                        Bank.removeAhouse(name);//Remove auctionHouse from listOfAuctionHouse Info
                    }
                    close();
                    break;
                } else {
                    String placeInBoundQueue = String.valueOf(connectionNumber) + "\t" + messageFromClient;
                    System.out.println("From Client: " + placeInBoundQueue);
                    Bank.inBoundMessages.add(placeInBoundQueue);
                }
            }
        } catch (IOException e) {
        }

    }

    private void close() throws IOException {
        System.out.println("Close inbound connection " + connectionNumber);
        clientSocket.close();
        inputStreamReader.close();
        bufferedReader.close();
        for (int i = 0; i < Bank.msgOut.size(); i++) {
            if (Bank.msgOut.get(i).connectionNumber == connectionNumber) {
                Bank.msgOut.get(i).stop = true;
            }
        }//
    }
}
