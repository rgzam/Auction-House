package Auction;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class DBMessage implements Serializable {
    private final Command command;
    private final Table table;
    private final int                  senderId;
    private final int                  accountId;
    private final String []            arguments;
    private final Message.Response     response;
    private final Object               payload;

    public static class Builder{
        private  Command             command   = null;
        private  Table               table     = null;
        private  int                 senderId  = -1;
        private  int                 accountId = -1;
        private  String []           arguments = null;
        private  Message.Response    response  = null;
        private  Object              payload   = null;


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

        public Builder table(Table table){
            this.table = table;
            return this;
        }

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
         * Id of the sender for security
         *
         * @param id Enum Command
         * @return Builder
         */
        public Builder senderId(int id) {
            this.senderId = id;
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


        public Builder payload(Object payload){
                this.payload = payload;
                return this;
        }


        /**
         * response sets the message's response
         *
         * @param response enum Response
         * @return Builder
         */
        public Builder response(Message.Response response) {
            this.response = response;
            return this;
        }

        public DBMessage build(){
            return new DBMessage(this);
        }
    }

    private DBMessage(Builder builder){
        this.accountId = builder.accountId;
        this.senderId = builder.senderId;;
        this.arguments = builder.arguments;
        this.command = builder.command;
        this.payload = builder.payload;
        this.response = builder.response;
        this.table = builder.table;
    }

    public Command getCommand() {
        return command;
    }

    public Table getTable() {
        return table;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String[] getArguments() {
        return arguments;
    }

    public Message.Response getResponse() {
        return response;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "DBMessage{" +
                "command=" + command +
                ", table=" + table +
                ", senderId=" + senderId +
                ", accountId=" + accountId +
                ", arguments=" + Arrays.toString(arguments) +
                ", response=" + response +
                ", payload=" + payload +
                '}';
    }

    /**
     * Command is an Enum for message commands.
     *      GET             => Returns a Single Entry
     *      GET_COLLECTION  => Returns a group of entries
     *      UPDATE          => Updates a table
     *      CREATE          => Creates an entry
     */
    public enum Command {
        GET,
        PUT,
        UPDATE
    }

    public enum  Table{
        ITEM,
        CLIENT
    }
}
