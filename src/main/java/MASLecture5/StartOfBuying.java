package MASLecture5;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

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
    }

}