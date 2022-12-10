package Bank;

import Bank.Bank;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankServer implements Runnable {
    static Socket socket = null;
    static ServerSocket serverSocket = null;

    BankServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
        }
    }


    @Override
    public void run() {
        try {
            while (true) {
                Bank.connectionNumbering++;
                socket = serverSocket.accept();
                InboundSocket in = new InboundSocket(socket, Bank.connectionNumbering);
                OutboundSocket out = new OutboundSocket(socket, Bank.connectionNumbering);
                Thread inThread = new Thread(in);
                Thread outThread = new Thread(out);
                inThread.start();
                outThread.start();
                Bank.msgIn.add(in);
                Bank.msgOut.add(out);
                Bank.msgInThread.add(inThread);
                Bank.msgOutThread.add(outThread);
            }
        } catch (IOException e) {
        }
    }
}
