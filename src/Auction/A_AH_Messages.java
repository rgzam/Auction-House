package Auction;

import Auction.Item;

import java.io.Serializable;
import java.util.ArrayList;

public class A_AH_Messages implements Serializable {
    private final A_AH_MTopic     topic;       //what kind of message is it
    private final ArrayList<Item> auctionList; //list of items in the auction
    private final int             bid;         //bid value
    private final int             accountId;   //Client ID from first contact with Auction
    private final int             itemId;      //The item identification
    private final String          itemName;    //Item name

    /**
     * AgentAuctionMessage Builder
     */
    public static class Builder {
        private A_AH_MTopic     topic       = null;
        private ArrayList<Item> auctionList = null;
        private int             bid         = 0;
        private int             accountId   = -1;
        private int             itemId      = -1;
        private String          name        = null;

        /**
         * accountId sets the accountId
         *
         * @param accountId the int being set
         * @return returns the builder
         */
        public Builder accountId(int accountId) {
            this.accountId = accountId;
            return this;
        }

        /**
         * sets the catalogue of the message
         *
         * @param auctionList ArrayList<Item>
         * @return builder
         */
        public Builder auctionList(ArrayList<Item> auctionList) {
            this.auctionList = auctionList;
            return this;
        }

        /**
         * bid sets the message's bid
         *
         * @param bid int
         * @return builder
         */
        public Builder bid(int bid) {
            this.bid = bid;
            return this;
        }

        /**
         * build returns builder as a message
         *
         * @return AgentAuctionMessage Builder
         */
        public A_AH_Messages build() {
            return new A_AH_Messages(this);
        }

        /**
         * itemId sets the message's itemID
         *
         * @param itemID int
         * @return builder
         */
        public Builder itemId(int itemID) {
            this.itemId = itemID;
            return this;
        }

        /**
         * name sets the message's item name
         *
         * @param name Builder
         * @return builder
         */
        public Builder name(String name){
            this.name = name;
            return this;
        }

        /**
         * newBuilder returns a new builder
         *
         * @return returns the created builder
         */
        public static Builder newBuilder() {
            return new Builder();
        }

        /**
         * topic sets the message's topic
         *
         * @param topic enum
         * @return builder
         */
        public Builder topic(A_AH_MTopic topic) {
            this.topic = topic;
            return this;
        }
    }

    /**
     * getTopic returns the message's topic
     *
     * @return returns the AMType type
     */
    public A_AH_MTopic getTopic(){
        return topic;
    }

    /**
     * AgentAuctionMessages Constructor for messages
     *
     * @param builder builder
     */
    public A_AH_Messages(Builder builder) {
        this.topic       = builder.topic;
        this.auctionList = builder.auctionList;
        this.bid        = builder.bid;
        this.itemId      = builder.itemId;
        this.accountId   = builder.accountId;
        this.itemName = builder.name;
    }

    /**
     * getId returns message's accountId
     *
     * @return accountId int
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * getAuctionList returns the list of items for sale
     *
     * @return auctionList ArrayList
     */
    public ArrayList<Item> getAuctionList() {
        return auctionList;
    }

    /**
     * getBid returns message's bid
     *
     * @return bid int
     */
    public int getBid() {
        return bid;
    }

    /**
     * getItem returns message's itemID
     *
     * @return itemID int
     */
    public int getItem() {
        return itemId;
    }

    /**
     * getName returns the message's name
     *
     * @return name String
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * toString overrides Java toString and converts message to a string
     *
     * @return message String
     */
    @Override
    public String toString() {
        String message = "{ ";
        if(topic != null) {
            message = message + "topic: "+topic;
        }
        if(auctionList != null) {
            message = message + ", catalogue: "+auctionList;
        }
        if(bid != 0) {
            message = message+", bid:"+bid;
        }
        if(itemId != -1) {
            message = message+", itemId"+ itemId;
        }
        if(accountId != -1) {
            message = message+", accountId"+accountId;
        }
        message = message + " }";
        return message;
    }

    /**
     * Enums to let the Agent/Auction House know the topic when
     * sending/receiving messages.
     */
    public enum A_AH_MTopic {
        BID,        //client submits bid on item
        DEREGISTER, //client de-registers from auction house
        OUTBID,     //Auction tells agent they were outbid
        REJECT,    //Auction tells agent their bid was refused
        REGISTER,   //agent registers with auction
        SUCCESS,    //Auction tells agent the bid was accepted
        UPDATE,     //Auction updates auctionList for client
        WINNER      //Auction tells agent they won
    }
}
