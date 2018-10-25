package MASLecture5;

import jade.Boot;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import java.util.List;


public class SendProposal extends Behaviour {
    private Agent agent;
    private boolean done = false;
    private AID bestSeller;
    private double bestPrice;
    private List<Book> bookList;
    private List<AID> receiversList;
    private boolean answerReceived = false;

    public SendProposal(Agent agent, DataStore ds, AID bestSeller, double bestPrice){
        this.bestPrice = bestPrice;
        this.bestSeller = bestSeller;
        this.agent = agent;
        setDataStore(ds);
    }
    @Override
    public void onStart(){
        super.onStart();
        ACLMessage propasal = new ACLMessage(ACLMessage.PROPOSE);
        propasal.addReceiver(bestSeller);
        propasal.setContent(bestPrice+"");
        propasal.setProtocol("tradeConfirm");
        agent.send(propasal);
        bookList = (List<Book>) getDataStore().get("bookList");

        receiversList = (List<AID>) getDataStore().get("receiversList");
        ACLMessage refuse = new ACLMessage(ACLMessage.REFUSE);
        refuse.setProtocol("tradeConfirm");
        for (AID rec: receiversList){
            if (rec!=bestSeller){
                refuse.addReceiver(rec);
            }
        }
        agent.send(refuse);
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchSender(bestSeller),
                MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                        MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)
                )
        );
        ACLMessage answer = agent.receive(mt);
        if (answer != null) {
            answerReceived = true;
            if (answer.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                System.out.println(agent.getLocalName() + "I've got a confirm, i've bought a book for" + bestPrice);
                bookList.remove(0);
            } else {
                System.out.println(agent.getLocalName() + "I've got a disconfirm, i have to try again!");
                Book book = bookList.get(0);
                bookList.remove(0);
                bookList.add(book);
            }
        }
        else {
            block();
        }
    }

    @Override
    public boolean done() {
        return answerReceived;
    }

    @Override
    public int onEnd() {
        if (bookList.size() == 0){
            System.out.println(agent.getLocalName() + "I've finished a bookbuying!");
        }
        else {
            System.out.println(agent.getLocalName() + "There is still books in my list of purchase");
            agent.addBehaviour(new StartOfBuying(agent, getDataStore()));
        }
       return super.onEnd();
    }
}