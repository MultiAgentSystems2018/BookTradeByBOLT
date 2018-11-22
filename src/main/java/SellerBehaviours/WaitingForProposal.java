package SellerBehaviours;

import ETC.BehaviourKiller;
import SellerBehaviours.WaitingForRequest;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static ETC.Colours.*;

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

        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol("trade"), MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                MessageTemplate.MatchPerformative(ACLMessage.REFUSE)));
        ACLMessage msg = agent.receive(mt);
        if (msg != null){
            done = true;
            if (msg.getPerformative() == ACLMessage.PROPOSE){
                ACLMessage accept = msg.createReply();
                System.out.println("Agent " + BLUE + agent.getLocalName() + ZERO + " said:" + GREEN +
                        " Accepting trade from " + YELLOW + "Buyer" + ZERO);
                accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                agent.send(accept);
            }else if(msg.getPerformative() == ACLMessage.REFUSE){
                ACLMessage ref = msg.createReply();
                System.out.println("Agent " + BLUE + agent.getLocalName() + ZERO + " said:" + RED +
                        " Refusing trade from " + YELLOW + "Buyer" + ZERO);
                ref.setPerformative(ACLMessage.REJECT_PROPOSAL);
                agent.send(ref);
            }
            else {
                block();
            }
        }
    }
    @Override
    public boolean done() {
        return done;
    }
    @Override
    public int onEnd() {
        WaitingForRequest behaviour = new WaitingForRequest(agent, getDataStore());
        agent.addBehaviour(behaviour);
        agent.addBehaviour(new BehaviourKiller(agent, 2000, behaviour));
        //  agent.addBehaviour(new WaitingForRequest(agent, getDataStore()));
        return super.onEnd();
    }
}
