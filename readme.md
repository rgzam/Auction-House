# Project 5
## Name: Ricardo Gonzales
## Name: Shi Ge
## Name: Junyi Zheng

## Bank
In order to start the Bank you first need to run the Bank.jar
 and it takes an input argument which will be the PortNumber
 ex: java -jar Bank.jar (portnumber)
 Commands for bank:
-requestListOfAh
-checkPending
-unpendFunds
-pendFunds
-createAccount
-completePurchase
The bank will display messages that will show if it's been 
recieved or sent to ther desntination.
If an agent/clients disconnects, the bank will make sure that their
ports are closed on their side of the program.
If the auctionhouse gets disconnect the bank will update their info
when the agent/client's ask for the updated list
In order disconnect from the bank you must input the command "bye".

## Auction House
To start the auctionhouse you must enter the following command:
java -jar AuctionHouse.jar AuctionHouse 4222 localhost 4223 items1.txt
The Items list must be in the same path as the jar in order take as an input argument.
If you want to run the auctionhouse we first need the bank to already be running so we can connect to it.
The AuctionHouse will display a message saying that it's receiving and sending.
In order to shut off the auctionhouse you need to type in "q" in the console.
If any bid's are occuring it will wait then shut down.

## Agent
To start the Agent you must enter the following command:
java -jar Agent.jar BankHost BankPort AgentName InitialFund
ex: java -jar Agent.jar BankHost 4222 Chris 1000
In order for the agent to work the agent must be connected to the bank
When running the agent it display options that are available to the user.
In order to Bid you will need to enter a 1 digit number that is listed in the
auction house. the display option is friendly and will output the given things 
such as refresh which is "c", check for bank balance enter "b" show a list of items
you have won "s", and if you want to quit the program you must enter "q".
