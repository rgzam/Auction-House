public class ItemBider {
    public String agentName;
    public String agentAccount;
    public ServerHandler agentHandler;

    public ItemBider(String AgentName, ServerHandler agentHandler) {
        this.agentName = AgentName;
        this.agentHandler = agentHandler;
    }

    public ItemBider(String AgentName,String agentAccount, ServerHandler agentHandler) {
        this.agentName = AgentName;
        this.agentHandler = agentHandler;
        this.agentAccount = agentAccount;
    }
}