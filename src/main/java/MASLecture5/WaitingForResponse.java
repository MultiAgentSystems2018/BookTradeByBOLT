package MASLecture5;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

public class WaitingForResponse extends Behaviour {

    private Agent agent;
    private double bestPrice = 10000000;
    private AID bestSeller = null;
    private List<AID> receivers;
    private int receiversCounter;
    private boolean behaviourDone = false;

    public WaitingForResponse (Agent agent, DataStore ds){
        super();
        this.agent = agent;
        setDataStore(ds);
        this.receivers = (List<AID>) ds.get("receiversList");
        receiversCounter = receivers.size();
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol("bookBuying"),
                MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CANCEL),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM)));
        ACLMessage response = agent.receive(mt);

        if (response != null) {
            receiversCounter--;
            if (response.getPerformative() == ACLMessage.INFORM) {
                System.out.println(agent.getLocalName() + " I've received the price " + response.getContent());
                double price = Double.parseDouble(response.getContent());

                if (price < bestPrice) {
                    bestSeller = response.getSender();
                    bestPrice = price;
                }
                if (receiversCounter == 0) {
                    behaviourDone = true;
                }
            } else {
                System.out.println(agent.getLocalName() + " I've received disconfirm");
            }
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return behaviourDone;
    }

    @Override
    public int onEnd() {
            if (behaviourDone && bestSeller != null) {
                System.out.println("Winner is " + bestSeller);
                Behaviour beh = new SendProposal(agent, getDataStore(), bestSeller, bestPrice);
                agent.addBehaviour(beh);
                agent.addBehaviour(new BehaviourKiller(agent, 5000, beh));
            } else {
                System.out.println(agent.getLocalName() + " Seller not found!");
                agent.addBehaviour(new WakerBehaviour(agent, 5000) {
                    @Override
                    protected void onWake() {
                        super.onWake();
                        agent.addBehaviour(new StartOfBuying(agent, getDataStore()));
                    }
                });
            }
                return super.onEnd();
            }
        }