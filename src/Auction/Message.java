package Auction;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {
    private final Command              command;
    private final int                  balance;
    private final int                  senderId;
    private final int                  accountId;
    private final String               accountName;
    private final String []            arguments;
    private final List<ConnectionReqs> connectionReqs;
    private final Response             response;

    /**
     * Builder inner class implements builder pattern
     */
    public static class Builder {
        private Command              command        = null;
        private int                  balance        = 0;
        private int                  senderId       = -1;
        private int                  accountId      = -1;
        private String               accountName    = null;
        private String []            arguments      = null;
        private List<ConnectionReqs> connectionReqs = null;
        private Response             response       = null;

        /**
         * accountId sets the message's accountID
         *
         * @param accountId int
         * @return Builder
         */
        public Builder accountId(int accountId) {
            this.accountId = accountId;
            return this;
        }

        /**
         * command sets the message's command
         *
         * @param command Enum Command
         * @return Builder
         */
        public Builder command(Command command) {
            this.command = command;
            return this;
        }

        /**
         * connectionReqs sets the message's connectionReqs
         *
         * @param serverSpecs List<ConnectionReqs>
         * @return Builder
         */
        public Builder connectionReqs(List<ConnectionReqs> serverSpecs) {
            this.connectionReqs = serverSpecs;
            return this;
        }

        /**
         * cost sets the message's amount
         *
         * @param cost Double
         * @return Builder
         */
        public Builder balance(int cost) {
            this.balance = cost;
            return this;
        }

        /**
         * response sets the message's response
         *
         * @param response enum Response
         * @return Builder
         */
        public Builder response(Response response) {
            this.response = response;
            return this;
        }

        /**
         * accountName sets name and returns Builder object
         *
         * @param name
         * @return Builder
         */
        public Builder accountName(String name) {
            this.accountName = name;
            return this;
        }

        /**
         * General Payload of strings
         * @param args
         * @return
         */
        public Builder arguments(String [] args){
            this.arguments = args;
            return this;
        }

        /**
         * senderId sets the message's senderId and return a Message object.
         *
         * @param senderId UUID
         * @return Message
         */
        public Message senderId(int senderId) {
            this.senderId = senderId;
            return new Message(this);
        }

        public Message nullId(){
            return new Message(this);
        }
    }

    /**
     * Message constructor that takes a builder object.
     *
     * @param builder Builder
     */
    private Message(Builder builder) {
        this.accountId      = builder.accountId;
        this.balance        = builder.balance;
        this.senderId       = builder.senderId;
        this.accountName    = builder.accountName;
        this.arguments      = builder.arguments;
        this.command        = builder.command;
        this.connectionReqs = builder.connectionReqs;
        this.response       = builder.response;
    }

    /**
     * Get the UUID of the target.
     *
     * @return UUID
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * getBalance returns balance
     *
     * @return balance Double
     */
    public int getBalance() {
        return balance;
    }

    /**
     * getResponse returns response
     *
     * @return response enum
     */
    public Response getResponse() {
        return response;
    }

    /**
     * getSender returns sender
     *
     * @return senderId int
     */
    public int getSenderId() {
        return senderId;
    }


    public String[] getArguments() {
        return arguments;
    }

    /**
     * getAccountName returns accountName
     *
     * @return accountName String
     */
    public String getAccountName(){ return accountName; }

    /**
     * getCommand returns command
     *
     * @return command enum
     */
    public Command getCommand() {
        return command;
    }

    /**
     * getConnectionReqs returns connectionReqs
     *
     * @return connectionReqs List<ConnectionReqs>
     */
    public List<ConnectionReqs> getConnectionReqs() {
        return connectionReqs;
    }

    /**
     * toString override returns a string representation of Message.
     *
     * @return String
     */
    @Override
    public String toString() {
        String message = "{";
        if (balance != 0)        message += ("amount=" + balance);
        if (senderId != -1)         message += ("\n\t\tsender=" + senderId);
        if (accountId != -1)        message += ("\n\t\taccountId=" + accountId);
        if (command != null)        message += ("\n\t\tcommand=" + command);
        if (response != null)       message += ("\n\t\tresponse=" + response);
        if (connectionReqs != null) message += ("\n\t\tnetInfo=" + connectionReqs);
        return message + "}";
    }

    /**
     * Response is an Enum representing responses from the bank.
     *      SUCCESS   => transaction successful
     *      FAILURE   => General Failuer
     *      OVERDRAFT => insufficient funds
     */
    public enum Response {
        SUCCESS,
        OVERDRAFT,
        FAILURE
    }

    /**
     * Command is an Enum for message commands.
     *      BLOCK           => Block an amount on an accountId.
     *      DEPOSIT         => Deposit an amount into the senderId account.
     *      DEREGISTER      => Deregister an account.
     *      GETBALANCE      => Get available balance from the accountId.
     *      REGISTER        => Register an account.
     *      TRANSFER        => Transfer an amount from senderId to accountId.
     *      UNBLOCK         => Release an amount from an accountId.
     */
    public enum Command {
        LOGIN,
        OPENACCOUNT,
        REGISTERHOUSE,
        GETHOUSES,
        BLOCK,
        DEPOSIT,
        DEREGISTER,
        GETBALANCE,
        REGISTER,
        TRANSFER,
        UNBLOCK,
    }
}
