package MASLecture5;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
//Дописать
public class BehaviourKiller extends WakerBehaviour {
    private Agent agent;
    private Behaviour behaviourToKill;

    public BehaviourKiller(Agent a, long timeout, WaitingForResponse behaviourToKill){
        super(a, timeout);
        agent = a;
        this.behaviourToKill = behaviourToKill;
    }
    @Override
    protected void onWake(){
        super.onWake();
        agent.removeBehaviour(behaviourToKill);
        System.out.println("I-ve removed the behaviour");
    }
}