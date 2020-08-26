package rddl.competition;

import rddl.EvalException;
import rddl.RDDL;
import rddl.State;
import POMDP_Solver.Atom;
//import rddl.competition.POMCP.Atom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MiniStateDataStore {
    private static Random random = new Random();
    public static int NumOfStates = 0;
    public static HashMap<Atom, HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>>> States = new HashMap<>();
    public static HashMap<Atom, HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>>> Observations = new HashMap<>();
    public static HashMap<Integer, Integer> ReadableStateID = new HashMap<>();
    public static HashMap<Atom, ArrayList<Atom>> ValidActionPerState = new HashMap<>();
    public static Atom InitStateAtom = null;
    public static Atom IllegalActionObservationAtom = null;
    public static Atom GetObservationAtom(HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> observations)
    {
        return new Atom(observations.toString().hashCode(), observations.toString());
    }

    public  static Atom[] AddStateAndObservation(MiniState s) throws EvalException {
        States.put(new Atom(s.state.toString().hashCode(), s.state.toString()), s.state);
        Observations.put(new Atom(s.observations.toString().hashCode(), s.observations.toString()), s.observations);
        ArrayList<Atom> validStateActions = ActionsDataStore.getValidStateActionIDs(s);
        ValidActionPerState.put(new Atom(s.state.toString().hashCode(), s.state.toString()), validStateActions);
        NumOfStates++;
        return new Atom[]{new Atom(s.state.toString().hashCode(), s.state.toString()), new Atom(s.observations.toString().hashCode(), s.observations.toString())};
    }

    public static ArrayList<Atom> GetStateValidActions(Atom stateHash) {
        return ValidActionPerState.get(stateHash);
    }

    public static RDDL.PVAR_INST_DEF GetStateRandomValidActions(Atom stateHash)
    {
        ArrayList<Atom> actionsHash = ValidActionPerState.get(stateHash);
        if (actionsHash.size()==0)
        {
            return null;
        }
        Integer randomActionHash = random.nextInt(actionsHash.size());
        return ActionsDataStore.GetAction(actionsHash.get(randomActionHash));
    }

    public static void InitActionsDataStore(State initState) throws EvalException {
        ActionsDataStore.InitActionsDataStore(initState);
    }

    public static Atom[] AddStateAndObservation(State s) throws EvalException {
        return AddStateAndObservation(new MiniState(s));
    }

    public  static void RemoveState(MiniState s)
    {
        States.remove(s.hashCode());
        ReadableStateID.remove(s.hashCode());
    }

    public  static HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> GetState(Atom stateHash)
    {
        return States.get(stateHash);
    }

    public  static HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> GetObservation(Atom observationHash)
    {
        return Observations.get(observationHash);
    }

    public String GetReadableStateID(int stateHash)
    {
        return ReadableStateID.containsKey(stateHash)? ReadableStateID.get(stateHash).toString() : "not in store";
    }
}
