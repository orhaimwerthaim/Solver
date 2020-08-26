package POMDP_Solver;
import JavaSim2POMCP.POMCP.JavaGeneratos.Generated.MicroStateObservation;
import JavaSim2POMCP.POMCP.JavaGeneratos.fixed.MiniStateDataStore;
import util.Pair;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

public class POMCP {
    Float _gamma;
    Float _c;
    Float _threshold;
    Integer _numberOfParticles;
    Integer _horizon;
    POMCP_Gate _gate;
    Node _rootNode;
    int illegalActionPenalty;
    int SimulatorActivationCount;
    //public static StopWatch TimeGetOrAddChildNode = new StopWatch();

    public POMCP(Float c, Integer numberOfParticles, POMCP_Gate gate, int _illegalActionPenalty)
    {
        illegalActionPenalty = _illegalActionPenalty;
        _gate = gate;
        _gamma = gate.GetDiscount();
        _c = c == null ? 1f : c;
        _horizon = gate.GetHorizon();
        _threshold = (float)Math.pow(_gamma, gate.GetHorizon()) - 0.000001f;
        _numberOfParticles = numberOfParticles == null ? 50 : numberOfParticles;
        _rootNode = new Node(null,false, null);
    }

    public void PruneAfterRealActionByActionAndObservationAndFillBefiefeState(Atom realActionThatWasExecuted, Atom realObservationReceived) throws Exception {
        ArrayList<Atom> priorBH = _rootNode.BeliefState;
        Node hao;
        Node ha = _rootNode.ActionOrObservationToChild.get(realActionThatWasExecuted);
        if(ha.ActionOrObservationToChild != null && ha.ActionOrObservationToChild.containsKey(realObservationReceived)) {
            hao = ha.ActionOrObservationToChild.get(realObservationReceived);
        }
        else
        {
            hao = ha.GetOrAddChildNode(realObservationReceived)._o1;//if no such child observation than create the node
        }
        _rootNode = hao;//we pruned the tree by action and observation

        System.out.println("Time:"+ Instant.now()+",filling belief state, num of BH states:" + hao.BeliefState.size());

        while(hao.BeliefState.size() < _numberOfParticles)
        {
            Atom state = Utils.GetRandomMember(priorBH);
            POMCP_Gate.GeneratorResult genResult = _gate.Generator(state, realActionThatWasExecuted)._o1;
            SimulatorActivationCount++;
            if(genResult.Observation.equals(realObservationReceived))
            {
                hao.BeliefState.add(genResult.NextState);
            }
        }
        System.out.println("Time:"+ Instant.now()+",finished filling belief state, num of BH states:" + hao.BeliefState.size());
    }

    //returns action id/hash code
    public Atom Search(int milliSecondsTimeout) throws Exception {
        Instant previous = Instant.now();
        Instant current = Instant.now();
        Atom possibleCurrentState = null;
        int numOfSimulations = 0;
        SimulatorActivationCount = 0;
        int numOfRoolouts = 0;
        do {
            numOfSimulations++;
            if(_rootNode.GetBeliefState().size() == 0)
            {
                _rootNode.BeliefState = _gate.GetPossibleInitialStates(100);
            }
            possibleCurrentState = Utils.GetRandomMember(_rootNode.GetBeliefState());
            Simulate(possibleCurrentState, _rootNode, 0, _rootNode.IsLeaf());
            current = Instant.now();
        }while (ChronoUnit.MILLIS.between(previous,current) < milliSecondsTimeout);
//        System.out.println("TotalTime:\n GetNextState("+TimeGtNextState.MILLIS+"),activation:"+TimeGtNextState.activations+" \n"+
//                "TimeGetValidAction("+ TimeGetValidAction.MILLIS+"),activation:"+TimeGetValidAction.activations+" \n" +
//                //"TimeGetOrAddChildNode("+ TimeGetOrAddChildNode.MILLIS+"),activation:"+TimeGetOrAddChildNode.activations+" \n" +
//                "TimeTotalSearchTime("+ TimeTotalSearchTime.MILLIS+") \n" +
//                "NotInNextOrGetAction("+ (TimeTotalSearchTime.MILLIS - TimeGetValidAction.MILLIS - TimeGtNextState.MILLIS)+")\n " +
//                        "InNextOrGetAction("+ (TimeGetValidAction.MILLIS + TimeGtNextState.MILLIS)+")\n" );
        //select the action that led to highest value node
   //     Painter.PaintTree(_rootNode, 6);
        Atom selectedAction = null;
        double maxValue = -Double.MAX_VALUE;
        HashMap.Entry<Integer,Node> maxChildNode = null;
        for (HashMap.Entry<Atom,Node> childNode:
        _rootNode.ActionOrObservationToChild.entrySet()) {
            if(childNode.getValue().V > maxValue && childNode.getValue().N > 0){
                selectedAction = childNode.getKey();
                maxValue = childNode.getValue().V;
            }
        }
        System.out.println("Number of Simulations in POMCP search: "+numOfSimulations+"\n");
        System.out.println("Number of Simulator activations in POMCP search: "+SimulatorActivationCount+"\n");
        return selectedAction;
    }

    private   double Simulate(Atom state, Node history_ho, int depth, Boolean notInTree) throws Exception {
        if (depth > _horizon || Math.pow(_gamma, depth) < _threshold)
        {
            return 0;
        }

        ArrayList<Atom> validActions = _gate.GetStateActions(state);
        FillHistory_ho_NodeWithChildActionsNodes(history_ho, state, validActions);

        if(notInTree) return Rollout(state, history_ho, depth);

        Atom action = null;

        Pair<POMCP_Gate.GeneratorResult, Boolean> gen = null;

        if(validActions == null || validActions.isEmpty())//when it is a leaf node
        {
            history_ho.GetOrAddChildNode(Atom.NullAction);
            action = Atom.NullAction;
            gen = new Pair<POMCP_Gate.GeneratorResult, Boolean>(new POMCP_Gate.GeneratorResult(state, Atom.NullObservation, 0), true);
        }
        else
        {
            action = GetMaxChildNodeActionByUCB(history_ho, state);
            gen = _gate.Generator(state,action);
            SimulatorActivationCount++;
        }
        POMCP_Gate.GeneratorResult genResult = gen._o1;

        if(!gen._o2)
        {
            genResult.Reward = illegalActionPenalty;
            genResult.NextState = state;
            genResult.Observation = MiniStateDataStore.GetObservationAtom(new MicroStateObservation(true));
        }
        Node ha = history_ho.ActionOrObservationToChild.get(action);
        Pair<Node, Boolean> res = ha.GetOrAddChildNode(genResult.Observation);
        Node hao = res._o1;
        double reward = genResult.Reward + _gamma*Simulate(genResult.NextState, hao, depth + 1, res._o2);
        history_ho.BeliefState.add(state);

        history_ho.N++;
        ha.N++;
        ha.V = ha.V + (reward - ha.V)/ha.N ;

        return reward;
    }

    private  Atom GetMaxChildNodeActionByUCB(Node history_ho, Atom state) throws Exception {
        Atom selectedAction = null;
        double maxUCB = -Double.MAX_VALUE;
        for (HashMap.Entry<Atom, Node> childNode :
                history_ho.ActionOrObservationToChild.entrySet()) {


            double nodeUCB = childNode.getValue().Get_NodeValue_Plus_UCB(this._c, history_ho.N);
            if (nodeUCB > maxUCB && !childNode.getKey().equals(Atom.NullAction)) {
                maxUCB = nodeUCB;
                selectedAction = childNode.getKey();
            }
        }
        return selectedAction;
    }

    private void FillHistory_ho_NodeWithChildActionsNodes(Node history_ho, Atom state, ArrayList<Atom> stateValidActions) throws Exception {
        stateValidActions = stateValidActions != null ? stateValidActions : _gate.GetStateActions(state);
        if (history_ho.ActionOrObservationToChild == null && (stateValidActions == null || stateValidActions.size() == 0)) {
            history_ho.GetOrAddChildNode(Atom.NullAction);//mark it is leaf
        } else {
            for (Atom action :
                    stateValidActions) {
                history_ho.GetOrAddChildNode(action);
            }
        }
    }

    private double Rollout(Atom state, Node history_ho, int depth) throws Exception {
        FillHistory_ho_NodeWithChildActionsNodes(history_ho, state, null);
        if (_horizon < depth || Math.pow(_gamma, depth) < _threshold || history_ho.ActionOrObservationToChild.size() == 0)
        {
            return 0;
        }
        ArrayList<Atom> valiActions = _gate.GetStateActions(state);
        if(valiActions == null || valiActions.isEmpty())//when it has no valid actions
        {
            return 0;
        }

        Atom action = (Atom) Utils.GetRandomMember(history_ho.ActionOrObservationToChild.keySet().toArray());

        history_ho.GetOrAddChildNode(action);
        Node ha = history_ho.ActionOrObservationToChild.get(action);
        POMCP_Gate.GeneratorResult genResult = _gate.Generator(state, action)._o1;
        Node hao = ha.GetOrAddChildNode(genResult.Observation)._o1;
        return genResult.Reward + _gamma*Rollout(genResult.NextState, hao, depth+1);
    }
}
