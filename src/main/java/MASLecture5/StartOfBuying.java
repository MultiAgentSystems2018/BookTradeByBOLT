package MASLecture5;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class StartOfBuying extends OneShotBehaviour {
    private Agent agent;
    private List<Book> bookList;

    public StartOfBuying(Agent agent, DataStore ds) {
        this.agent = agent;
        bookList = (List<Book>) ds.get("booklist");
    }


    @Override
    public void action(){
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        System.out.println(agent.getLocalName() + "I've sent a request");
        request.setContent(bookList.get(0).getTitle()+"");
        List<AID> reseivers = new ArrayList<AID>();
        reseivers.add(new AID("Seller1", false));
        reseivers.add(new AID("Seller2", false));
        reseivers.add(new AID("Seller3", false));

        getDataStore().put("receiverList", reseivers);

        for (AID rec: reseivers){
            request.addReceiver(rec);
        }
        request.setProtocol("bookBuying");
        agent.send(request);

        WaitingForResponse behaviourToKill = new WaitingForResponse(agent, getDataStore());
        agent.addBehaviour(behaviourToKill);
        agent.addBehaviour(new BehaviourKiller(agent, 20000, behaviourToKill));

    }

}