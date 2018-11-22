package BuyerBehaviours;

import ETC.Book;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

import static ETC.Colours.*;


public class SendProposal extends Behaviour {
    private Agent agent;
    private AID bestSeller;
    private double bestPrice;
    private List<Book> bookList;
    private List<AID> receiversList;
    private boolean answerReceived = false;
    private int receiversCounter;


    public SendProposal(Agent agent, DataStore ds, AID bestSeller, double bestPrice){
        this.bestPrice = bestPrice;
        this.bestSeller = bestSeller;
        this.agent = agent;
        this.receiversList = (List<AID>) ds.get("receiversList");
        receiversCounter = receiversList.size();
        this.bookList = (List<Book>) ds.get("bookList");
        setDataStore(ds);
    }
    @Override
    public void onStart(){
        super.onStart();

        ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
        proposal.addReceiver(bestSeller);
        proposal.setContent(bestPrice+"");
        proposal.setProtocol("trade");
        System.out.println(bestSeller.getLocalName()+ "  proposed");
        agent.send(proposal);
        ACLMessage refuse = new ACLMessage(ACLMessage.REFUSE);
        refuse.setProtocol("trade");
        for (AID rec: receiversList){
//            if (rec!=bestSeller){
            if (!rec.equals(bestSeller)){
                System.out.println(rec.getLocalName() + "   refused");
                refuse.addReceiver(rec);
            }
        }
        agent.send(refuse);
    }

    @Override
    public void action() {
//        System.out.println(bookList.get(0).getTitle());
        MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchProtocol("trade"),
                MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                        MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)));
        ACLMessage answer = agent.receive(mt);
        if (answer != null) {
            if (answer.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {

                System.out.println("Agent " + YELLOW + agent.getLocalName() + ZERO +
                        " said:" + GREEN + "I've got a confirm from " + BLUE + answer.getSender().getLocalName()
                        + ZERO + ", and bought a " + PURPLE + bookList.get(0).getTitle() + ZERO + " for " + bestPrice);
//                bookList.remove(0);
//                getDataStore().put("bookList", bookList);
                receiversCounter--;

            } else if (answer.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                System.out.println("Agent " + YELLOW + agent.getLocalName() + ZERO +
                        " said:"  + RED +"I've got a disconfirm from " + BLUE + answer.getSender().getLocalName()
                        + ZERO);
                receiversCounter--;

            }
        }
        else {
            block();
        }
        if (receiversCounter == 0){
            answerReceived = true;
        }

    }

    @Override
    public boolean done() {

        return answerReceived;
    }

    @Override
    public int onEnd() {
        if (bookList.size() != 0) {
            Book book = bookList.get(0);
            bookList.remove(0);
//            bookList.add(book);
        }
        if (bookList.size() == 0){
            System.out.println("Agent " + YELLOW + agent.getLocalName() + ZERO +  " said:" +
                    GREEN + "I've finished bookbuying!");
        }
        else {
            System.out.println("Agent " + YELLOW + agent.getLocalName() + ZERO +
                    " said:" + CYAN + "There are still books in my list of purchase" + ZERO);
            System.out.println("----------------------------------------------------");
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            agent.addBehaviour(new StartOfBuying(agent, getDataStore()));
        }
        return super.onEnd();
    }
}
