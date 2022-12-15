package Auction;

import java.io.Serializable;

public class ConnectionReqs implements Serializable {
    private int    port;
    private String ip;
    private String name;

    /**
     * ConnectionReqs Builder
     *
     * @param ip String ip address
     * @param port int port number
     */
    public ConnectionReqs(String ip, int port) {
        this.ip   = ip;
        this.port = port;
    }

    /**
     * getIp returns ip address
     *
     * @return ip String ip address
     */
    public String getIp() {
        return ip;
    }

    /**
     * getPort returns port number
     *
     * @return port int value
     */
    public int getPort() {
        return port;
    }

    /**
     * getName returns connection/server's name
     *
     * @return name String
     */
    public String getName() {
        return name;
    }

    /**
     * setName takes string name and sets local name value
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * toString override method gives string value of ConnectionReqs
     *
     * @return String conversion of ConnectionReqs
     */
    @Override
    public String toString() {
        return "ServerSpecs{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", name= " + name +
                '}';
    }
}
