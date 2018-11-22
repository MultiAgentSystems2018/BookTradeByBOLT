package BuyerBehaviours;

import ETC.BehaviourKiller;
import ETC.Book;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

import static ETC.Colours.*;

public class WaitingForResponse extends Behaviour {

    private Agent agent;
    private double bestPrice = 10000000;
    private AID bestSeller = null;
    private List<AID> receivers;
    private List<Book> bookList;
    private int receiversCounter;
    private boolean behaviourDone = false;

    public WaitingForResponse (Agent agent, DataStore ds){
        super();
        this.agent = agent;
        setDataStore(ds);
        this.receivers = (List<AID>) ds.get("receiversList");
        this.bookList = (List<Book>) ds.get("bookList");
        receiversCounter = receivers.size();
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol("bookBuying"),
                MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CANCEL),
                        MessageTemplate.MatchPerformative(ACLMessage.INFORM)));
        ACLMessage response = agent.receive(mt);

        if (response != null) {
            if (response.getPerformative() == ACLMessage.INFORM) {
                receiversCounter--;
                System.out.println("Agent " + YELLOW + agent.getLocalName() + ZERO + " said:" +
                        CYAN + "I've received the price " +
                        response.getContent() + ZERO + " from " + BLUE + response.getSender().getLocalName() + ZERO);
                double price = Double.parseDouble(response.getContent());

                if (price < bestPrice) {
                    bestSeller = response.getSender();
                    bestPrice = price;
                }
            } else if (response.getPerformative() == ACLMessage.CANCEL){
                receiversCounter--;
                System.out.println("Agent " + YELLOW + agent.getLocalName() + ZERO + " said:" + BLUE +
                        response.getSender().getLocalName() + ZERO + " hasn't got " +  PURPLE +
                        bookList.get(0).getTitle() + ZERO);

            }
        } else {
            block();
        }
        if (receiversCounter == 0) {
            behaviourDone = true;
        }
    }

    @Override
    public boolean done() {

        return behaviourDone;
    }

    @Override
    public int onEnd() {
        if (behaviourDone && bestSeller != null) {
            System.out.println("Winner is " + BLUE + bestSeller.getLocalName() + ZERO);
            SendProposal behaviour = new SendProposal(agent, getDataStore(), bestSeller, bestPrice);
            agent.addBehaviour(behaviour);
            agent.addBehaviour(new BehaviourKiller(agent,2000, behaviour));
        } else {
            System.out.println(RED  + " Seller " + agent.getLocalName() + "not found!" + ZERO);
            agent.addBehaviour(new WakerBehaviour(agent, 1000) {
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