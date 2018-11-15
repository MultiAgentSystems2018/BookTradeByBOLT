package MASLecture5;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class WaitingForProposal extends Behaviour {

    private Agent agent;
    private boolean done = false;

    public WaitingForProposal(Agent agent, DataStore ds){
        super();
        this.agent = agent;
        setDataStore(ds);
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol("tradeConfirm"),
                MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE)));
        ACLMessage msg = agent.receive(mt);
        if (msg != null){
            done = true;
            if (msg.getPerformative() == ACLMessage.PROPOSE){
                ACLMessage accept = msg.createReply();
                accept.setContent("Confirming trade from " + agent.getLocalName());
                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                agent.send(accept);
            }else if(msg.getPerformative() == ACLMessage.REFUSE){
                ACLMessage refprop = msg.createReply();
                refprop.setContent("Refusing trade from " + agent.getLocalName());
                refprop.setPerformative(ACLMessage.REJECT_PROPOSAL);
                agent.send(refprop);
            }
            else {
                block();
            }
        }
    }
    @Override
    public boolean done() {

        return false;
    }
}
