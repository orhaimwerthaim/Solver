package rddl.competition;
import java.util.*;

import org.apache.commons.math3.random.RandomDataGenerator;
import rddl.EvalException;
import rddl.RDDL;
import rddl.RDDL.*;
import rddl.State;
//import rddl.competition.POMCP.Atom;
//import rddl.competition.POMCP.POMCP_Gate;
import POMDP_Solver.POMCP_Gate;
import POMDP_Solver.Atom;
import util.Pair;


public class RDDL_Simulator implements POMCP_Gate {
    DOMAIN domain;
    State state = new State();
    RDDL rddl;
    INSTANCE instance;
    NONFLUENTS nonFluents;
    RandomDataGenerator rand;
    int NumOfInitialStateSamples = 20;
    public ArrayList<HashMap<PVAR_NAME,HashMap<ArrayList<LCONST>,Object>> > GeneratedInitialStates;
    ArrayList<Atom> possibleInitialStates;
    boolean IsInitialStateGenerator;
    public RDDL_Simulator(String rddlFilesDirectory, String InstanceName, RDDL_Simulator initialStateGenerator, boolean isInitStateGenerator) throws EvalException {
        IsInitialStateGenerator = isInitStateGenerator;

        rddl = new RDDL(rddlFilesDirectory);
        rand = new RandomDataGenerator();
        if (!rddl._tmInstanceNodes.containsKey(InstanceName)) {
            System.out.println("Instance name '" + InstanceName + "' not found.");
            return;
        }

        initializeState(rddl, InstanceName);

        state.init(domain._hmObjects, nonFluents != null ? nonFluents._hmObjects : null, instance._hmObjects,
                domain._hmTypes, domain._hmPVariables, domain._hmCPF,
                instance._alInitState, nonFluents == null ? new ArrayList<PVAR_INST_DEF>() : nonFluents._alNonFluents, instance._alNonFluents,
                domain._alStateConstraints, domain._alActionPreconditions, domain._alStateInvariants,
                domain._exprReward, instance._nNonDefActions);
        if (!IsInitialStateGenerator) {
            State stateOuterUse = new State();
            stateOuterUse.init(domain._hmObjects, nonFluents != null ? nonFluents._hmObjects : null, instance._hmObjects,
                    domain._hmTypes, domain._hmPVariables, domain._hmCPF,
                    instance._alInitState, nonFluents == null ? new ArrayList<PVAR_INST_DEF>() : nonFluents._alNonFluents, instance._alNonFluents,
                    domain._alStateConstraints, domain._alActionPreconditions, domain._alStateInvariants,
                    domain._exprReward, instance._nNonDefActions);

            //set obsrv_illegal_action value to true
               for (Map.Entry<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>, Object>> entry : stateOuterUse._observ.entrySet()) {
                    if(entry.getKey()._sPVarName.equals("obsrv_illegal_action"))
                    {
                        HashMap<ArrayList<RDDL.LCONST>, Object> value = new HashMap<>();
                        value.put(new ArrayList<RDDL.LCONST>(),true);
                        stateOuterUse._observ.put(entry.getKey(), value);
                    }
                }
            MiniStateDataStore.InitActionsDataStore(stateOuterUse);
            Atom[] stateData = MiniStateDataStore.AddStateAndObservation(state);

            MiniStateDataStore.InitStateAtom = stateData[0];
            MiniStateDataStore.IllegalActionObservationAtom = stateData[1];
        }
        InitPossibleInitialState(initialStateGenerator);
    }

    public Pair<GeneratorResult, Boolean> ComputeNextStateAndReward(HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> _state, ArrayList<PVAR_INST_DEF> actions, RandomDataGenerator _rand)
            throws EvalException {
        state._state = _state;
        if(!state.checkStateActionConstraints(actions, false)) {
            MiniState ms = new MiniState(state);
            Atom stateA = new Atom(ms.state.toString().hashCode(), ms.state.toString());
            return new Pair(new GeneratorResult(stateA, MiniStateDataStore.IllegalActionObservationAtom, 0), false);
        }
//clear next state for next state.computeNextState();

        for (HashMap<ArrayList<LCONST>,Object> nextStateValue:
                state._nextState.values()) {
            nextStateValue.clear();
        }
//clear intermediate fluents for next state.computeNextState();
        for (PVAR_NAME p : state._interm.keySet())
            state._interm.get(p).clear();


        state.computeNextState(actions, _rand);

        double immediate_reward = ((Number)domain._exprReward.sample(new HashMap<LVAR,LCONST>(),
                state, rand)).doubleValue();
        Atom[] nextStateData = MiniStateDataStore.AddStateAndObservation(new MiniState(state, true));
        return new Pair(new GeneratorResult(nextStateData[0], nextStateData[1], immediate_reward), true);
    }

    /*public static void main(String[] args) throws EvalException{
        new NewServer(args[0], args[1]);
    }*/

    PVAR_INST_DEF getAction(String name, Object value, ArrayList<String> args) {
        ArrayList<LCONST> lcArgs = new ArrayList<LCONST>();
        for (String argName : args) {
            lcArgs.add(new RDDL.OBJECT_VAL(argName));
        }
        return new PVAR_INST_DEF(name, value, lcArgs);
    }


    void initializeState(RDDL rddl, String requestedInstance) {
        instance = rddl._tmInstanceNodes.get(requestedInstance);
        nonFluents = null;
        if (instance._sNonFluents != null) {
            nonFluents = rddl._tmNonFluentNodes.get(instance._sNonFluents);
        }
        domain = rddl._tmDomainNodes.get(instance._sDomain);
        if (nonFluents != null && !instance._sDomain.equals(nonFluents._sDomain)) {
            try {
                throw new Exception("Domain name of instance and fluents do not match: " +
                        instance._sDomain + " vs. " + nonFluents._sDomain);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    @Override
    public int GetHorizon() {
        return instance._nHorizon;
    }

    @Override
    public float GetDiscount() {
        return (float) instance._dDiscount;
    }

    @Override
    public Atom GetObservationFromROS_Response(String rosResponse) {
        throw new UnsupportedOperationException();
    }


    private void InitPossibleInitialState(RDDL_Simulator initialStateGenerator) throws EvalException {
        possibleInitialStates = new ArrayList<>();
        if (IsInitialStateGenerator) {
            GeneratedInitialStates = new ArrayList<>();
            ArrayList<RDDL.PVAR_INST_DEF> actions = ActionsDataStore.GetAllActions(state);

            for (int i = 0; i < NumOfInitialStateSamples; i++) {
                state = new State();
                state.init(domain._hmObjects, nonFluents != null ? nonFluents._hmObjects : null, instance._hmObjects,
                        domain._hmTypes, domain._hmPVariables, domain._hmCPF,
                        instance._alInitState, nonFluents == null ? new ArrayList<PVAR_INST_DEF>() : nonFluents._alNonFluents, instance._alNonFluents,
                        domain._alStateConstraints, domain._alActionPreconditions, domain._alStateInvariants,
                        domain._exprReward, instance._nNonDefActions);

                state.computeNextState(actions, rand);
                GeneratedInitialStates.add(state._nextState);
            }
        }else {
            if(initialStateGenerator == null)
            {
                MiniState ms = new MiniState(state._state);
                possibleInitialStates.add(MiniStateDataStore.AddStateAndObservation(ms)[0]);
            }
            else {
                for (HashMap<PVAR_NAME, HashMap<ArrayList<LCONST>, Object>> s :
                        initialStateGenerator.GeneratedInitialStates) {
                    MiniState ms = new MiniState(s);
                    possibleInitialStates.add(MiniStateDataStore.AddStateAndObservation(ms)[0]);
                }
            }
        }
    }

    @Override
    public ArrayList<Atom> GetPossibleInitialStates(int numOfInitialStateSamples) throws EvalException {
        int toIndex = possibleInitialStates.size() > numOfInitialStateSamples ? numOfInitialStateSamples :possibleInitialStates.size();
        return  new ArrayList<Atom>(possibleInitialStates.subList(0,toIndex));
    }

    @Override
    public ArrayList<Atom> GetStateActions(Atom state) {
        return MiniStateDataStore.GetStateValidActions(state);
    }

    @Override
    public Atom GetIllegalActionObservation() {
        return MiniStateDataStore.IllegalActionObservationAtom;
    }

    @Override
    public boolean IsGoalReached(Atom observation) {
        boolean result = false;
        HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> _observ = MiniStateDataStore.GetObservation(observation);
        for (Map.Entry<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>, Object>> entry : _observ.entrySet()) {
            if(entry.getKey()._sPVarName.equals("goal_reached"))
            {
                for (Map.Entry<ArrayList<RDDL.LCONST>, Object> entryVal :
                        entry.getValue().entrySet()) {
                    result = (Boolean) entryVal.getValue();
                }
            }
        }
        return result;
    }

    @Override
    public Pair<GeneratorResult, Boolean> Generator(Atom state, Atom action) throws EvalException {
        HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> actualState = MiniStateDataStore.GetState(state);

        ArrayList<PVAR_INST_DEF> actions = new ArrayList<PVAR_INST_DEF>(List.of(ActionsDataStore.GetAction(action)));
        return ComputeNextStateAndReward(actualState, actions, rand);
    }
}
 //   name: pick
 //  ArrayList<String> args:  size = 3 ['armadillo,can,robo_lab']
//  ArrayList<LCONST> lcArgs:  size = 3 ['$armadillo,$can,$robo_lab']      lcArgs.add(new RDDL.OBJECT_VAL(arg));
//String pvalue == 'true'