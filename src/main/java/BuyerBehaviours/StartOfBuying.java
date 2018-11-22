package BuyerBehaviours;

import ETC.BehaviourKiller;
import ETC.Book;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

import static ETC.Colours.PURPLE;
import static ETC.Colours.YELLOW;
import static ETC.Colours.ZERO;

public class StartOfBuying extends OneShotBehaviour {
    private Agent agent;
    private List<Book> bookList;

    public StartOfBuying(Agent agent, DataStore ds) {
        this.agent = agent;
        bookList = (List<Book>) ds.get("bookList");
        setDataStore(ds);
    }


    @Override
    public void action(){
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.setContent(bookList.get(0).getTitle()+"");
        System.out.println( "Agent " + YELLOW + agent.getLocalName() + ZERO + " said: I've sent a request for " +
                PURPLE + bookList.get(0).getTitle() + ZERO);
        List<AID> receivers = new ArrayList<AID>();
        receivers.add(new AID("Seller1", false));
        receivers.add(new AID("Seller2", false));
        receivers.add(new AID("Seller3", false));

        getDataStore().put("receiversList", receivers);
        for (AID rec: receivers){
            request.addReceiver(rec);
        }
        request.setProtocol("bookBuying");
        agent.send(request);

        WaitingForResponse behaviourToKill = new WaitingForResponse(agent, getDataStore());
        agent.addBehaviour(behaviourToKill);
        agent.addBehaviour(new BehaviourKiller(agent, 1000, behaviourToKill));

    }

}