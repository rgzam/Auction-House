package Agent;

public class MessageHandler implements Runnable{
    AgentSocketCommands socCmd = Agent.socCmd;

    @Override
    public void run() {
        while(true){
            if(Agent.inBoundMessages.size() > 0){
                socCmd.inBoundMSG();
            }
            if(Agent.outBoundMessages.size() > 0){
                socCmd.outBoundMSG();
            }
        }
    }
}
