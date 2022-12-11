package Agent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketOut implements Runnable{
    protected final Socket clientSocket;
    protected final String connectionType;
    protected String locationName = "";
    protected final int accountNum;
    protected boolean stop = false;

    protected OutputStreamWriter outputStreamWriter = null;

    protected BufferedWriter bufferedWriter = null;

    protected LinkedBlockingQueue<String> outBoundMessages = new LinkedBlockingQueue<String>();

    public SocketOut(Socket socket, String connectionTYP, String location, String account) {
        clientSocket = socket;
        connectionType = connectionTYP;
        locationName = location;
        accountNum = Integer.parseInt(account);

        try {
            outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
        }
        catch (IOException e) {}

        bufferedWriter = new BufferedWriter(outputStreamWriter);
    }


    @Override
    public void run() {
        try {
            while (true) {
                if(stop){
                    clientSocket.close();
                    outputStreamWriter.close();
                    bufferedWriter.close();
                    break;
                }

                if(outBoundMessages.size() > 0){
                    String messageOut = outBoundMessages.take();
                    bufferedWriter.write(messageOut);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        }
        catch (IOException e){}
        catch (InterruptedException e) {}
    }
}
