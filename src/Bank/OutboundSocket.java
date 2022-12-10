package Bank;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class OutboundSocket implements Runnable{
    protected final Socket clientSocket;
    protected final int connectionNumber;
    protected boolean stop = false;

    protected OutputStreamWriter outputStreamWriter = null;

    protected BufferedWriter bufferedWriter = null;

    protected LinkedBlockingQueue<String> outBoundMessages = new LinkedBlockingQueue<String>();

    public OutboundSocket(Socket socket, int connectNum) {
        clientSocket = socket;
        connectionNumber = connectNum;

        try {
            outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
        }
        catch (IOException e) {}

        bufferedWriter = new BufferedWriter(outputStreamWriter);
    }


    @Override
    public void run() {
        try {
            //System.out.println("Outbound socket thread live! Connection Number: " + connectionNumber);
            while (true) {
                if(stop){
                    System.out.println("Close outbound connection " + connectionNumber);
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
