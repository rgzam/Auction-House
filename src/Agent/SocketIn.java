package Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketIn implements Runnable{
    protected final Socket clientSocket;
    protected final String connectionType;
    protected String locationName = "";
    protected final int accountNum;

    protected InputStreamReader inputStreamReader = null;

    protected BufferedReader bufferedReader = null;

    public SocketIn(Socket socket, String connectionTYP, String location, String account) {
        clientSocket = socket;
        connectionType = connectionTYP;
        locationName = location;
        accountNum = Integer.parseInt(account);

        try {
            inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
        }
        catch (IOException e) {}

        bufferedReader = new BufferedReader(inputStreamReader);
    }

    private void closeSocket(){
        try {
            System.out.println("Close connection to " + locationName);
            clientSocket.close();
            inputStreamReader.close();
            bufferedReader.close();
            for (int i = 0; i < Agent.out.size(); i++) {
                if(Agent.out.get(i).accountNum == accountNum){
                    Agent.out.get(i).stop = true;
                }
            }
        }
        catch(IOException e){}
    }

    @Override
    public void run() {
        try {
            while (true) {

                //Read in
                String messageFromClient = bufferedReader.readLine();
                if(messageFromClient == null){
                    closeSocket();
                    break;
                }
                else if (messageFromClient.equalsIgnoreCase("bye")) {
                    closeSocket();
                    break;
                }
                else{
                    String placeInBoundQueue = String.valueOf(accountNum) + "\t" +messageFromClient;
                    //System.out.println("From Server: " + placeInBoundQueue);
                    Agent.inBoundMessages.add(placeInBoundQueue);
                }
            }
        }
        catch (IOException e){}
    }
}
