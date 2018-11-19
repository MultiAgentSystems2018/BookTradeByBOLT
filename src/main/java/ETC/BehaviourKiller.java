package ETC;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;

public class BehaviourKiller extends WakerBehaviour {
    private Agent agent;
    private Behaviour behaviourToKill;
    private long timeout;

    public BehaviourKiller(Agent agent, long timeout, Behaviour behaviourToKill) {
        super(agent, timeout);
        this.agent = agent;
        this.timeout = timeout;
        this.behaviourToKill = behaviourToKill;
    }


    @Override
    protected void onWake() {
        super.onWake();
        agent.removeBehaviour(behaviourToKill);
    }

}