package Bank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Listener implements Runnable{

    protected final Socket agentSocket;

    protected static InputStreamReader inputStreamReader = null;
    protected static BufferedReader bufferedReader = null;

    Listener(Socket socket){
        agentSocket = socket;

    }

    @Override
    public void run() {
        try{
            //Connect to server

            //From input server
            inputStreamReader = new InputStreamReader(agentSocket.getInputStream());
            //Buffer for IO.
            bufferedReader = new BufferedReader(inputStreamReader);

            while (true){
                //Read message from server
                System.out.println("From server: " + bufferedReader.readLine());
            }
        }
        catch (Exception e) {
        }

    }
}
