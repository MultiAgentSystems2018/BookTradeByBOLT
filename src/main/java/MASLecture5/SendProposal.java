package MASLecture5;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
// Дописать
public class SendProposal extends Behaviour {
    private Agent agent;
    private boolean done = false;

    public SendProposal(Agent agent, DataStore ds,  ){
        super();
        this.agent = agent;
        setDataStore(ds);
    }

    @Override
    public void action() {

    }
    @Override
    public boolean done() {
        return false;
    }
}
