package SellerBehaviours;

import ETC.BehaviourKiller;
import ETC.Book;
import ETC.BookTitle;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

import static ETC.Colours.*;

public class WaitingForRequest extends Behaviour {

    private Agent agent;
    private boolean msgArried = false;
    private List<Book> bookList;

    public WaitingForRequest(Agent agent, DataStore ds){
        super();
        setDataStore(ds);
        this.agent = agent;
        bookList = (List<Book>) getDataStore().get("bookList");

    }
    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchProtocol("bookBuying"));
        ACLMessage request = agent.receive(mt);

        if (request!= null){
            ACLMessage answer =request.createReply();
            msgArried = true;
            BookTitle requestedBook = BookTitle.valueOf(request.getContent());

            double offeredPrice = 0;
            for (Book book:bookList){
                if (book.getTitle() == requestedBook){
                    offeredPrice = book.getPrice();
                }
            }
            if (offeredPrice > 0) {
                answer.setContent(offeredPrice + "");
                answer.setPerformative(ACLMessage.INFORM);
                System.out.println("Agent " + BLUE + agent.getLocalName() + ZERO +
                        " said:" + GREEN + "I have this book, the price is " + offeredPrice + ZERO);

            }
            else{
                System.out.println("Agent " + BLUE + agent.getLocalName() + ZERO +  " said:" + RED
                        + "I don't have this book! " + ZERO);
                answer.setPerformative(ACLMessage.CANCEL);
            }
            System.out.println(agent.getLocalName() + offeredPrice);
            agent.send(answer);
        }
        else{
            block();
        }

    }
    @Override
    public boolean done() {

        return msgArried;
    }
    @Override
    public int onEnd(){
        WaitingForProposal behaviour = new WaitingForProposal(agent, getDataStore());
        agent.addBehaviour(behaviour);
        agent.addBehaviour(new BehaviourKiller(agent, 5000, behaviour));
//        agent.addBehaviour(new WaitingForProposal(agent, getDataStore()));
        return super.onEnd();
    }
}
