package Bank;

import Bank.Bank;

import java.util.HashMap;

public class BankAccount {

    protected String accName;//Account Name
    private int accNum;//Account Number
    private float balance = 0;
    private String hostName;
    private int port;

    // NameOfAuction Item, amount
    private HashMap<String, Transaction> pendingFunds = new HashMap<>();

    /**
     * Create Bank.BankAccount with the given parameters
     */
    public BankAccount(String name, float initialBalance, String hostName, int port) {
        this.accName = name;
        accNum = (int) (Math.random() * (99998 + 1) + 10000); //Generate Random 5-digit account number
        deposit(initialBalance);
        this.hostName = hostName;
        this.port = port;
    }

    public float getBalance() {
        return balance;
    }

    public int getAccNum() {
        return accNum;
    }

    protected void deposit(float amount) {
        this.balance = this.balance + amount;
    }

    /**
     * Take out said-amount of money from the account
     */
    protected void withdraw(float amount) {
        if (amount < this.balance) {
            this.balance = this.balance - amount;
        } else {
            System.out.println("Insufficient Funds for ACCNUM# " + getAccNum());
        }
    }

    /**
     * Transfer given amount from accNum1 to accNum2
     */
    protected static void transfer(int accNum1, int accNum2, float amount) {
        Bank.allAccounts.get(accNum1).withdraw(amount);
        Bank.allAccounts.get(accNum2).deposit(amount);
        //Not in use
    }

    /**
     * Complete Bank.Transaction
     * Agent won item, he must pay for it
     * Upend Agent's funds, and deposit that money into the AuctionHouse's Account
     */
    protected void compTransaction(int accNum, String auctionItem) {
        float amount = pendingFunds.get(auctionItem).getPrice();
        int accNum2 = pendingFunds.get(auctionItem).getAuctionHouseID();
        Bank.allAccounts.get(accNum2).deposit(amount);//deposit amount into seller's account
        pendingFunds.remove(auctionItem);//remove item from pendingFunds
        //Bank.Bank.allAccounts.get(accNum).
    }

    /**
     * Remove auctionItem bid on, from PendingFunds and put funds back into account
     */
    protected void releasePendFunds(String auctionItem) {
        deposit(pendingFunds.get(auctionItem).getPrice());//Put funds back into account
        pendingFunds.remove(auctionItem);//remove item from pendingFunds
    }

    /**
     * Return true/false if the client's account has any pending funds
     */
    protected boolean hasPendingFunds(int accNum) {
        if (pendingFunds.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add auctionItem bid on to pendingFunds and remove funds from account
     * "pendFunds agentAccountNumber PendAmount AuctionHouseAccountID ItemName"
     */
    protected void addPendingFund(String auctionItem, float amount, int auctionHouseID) {
        if (amount <= balance) {
            Transaction t = new Transaction(amount, auctionHouseID, auctionItem);
            pendingFunds.put(auctionItem, t);//Create and add a pendingFund
            withdraw(amount);//Remove funds from account
        } else {
            System.out.println("Insufficient Funds for ACCNUM#" + getAccNum());
        }

    }


    @Override
    public String toString() {
        return "\t"+"[name=" + accName + ", accNum= " + accNum + ", balance=" + balance + "]";
    }

    public static void main(String[] args) {
    }
}
