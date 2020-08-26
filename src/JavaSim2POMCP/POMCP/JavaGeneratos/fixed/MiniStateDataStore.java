package JavaSim2POMCP.POMCP.JavaGeneratos.fixed;

import JavaSim2POMCP.POMCP.JavaGeneratos.Generated.MicroState;
import JavaSim2POMCP.POMCP.JavaGeneratos.Generated.MicroStateObservation;
import JavaSim2POMCP.POMCP.JavaGeneratos.Generated.MicroStateState;
import POMDP_Solver.Atom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MiniStateDataStore {
    private static Random random = new Random();
    public static int NumOfStates = 0;
    public static HashMap<Atom, MicroStateState> States = new HashMap<>();
    public static HashMap<Atom, MicroStateObservation> Observations = new HashMap<>();
    public static HashMap<Integer, Integer> ReadableStateID = new HashMap<>();
    public static HashMap<Atom, ArrayList<Atom>> ValidActionPerState = new HashMap<>();


    public  static Atom[] AddStateAndObservation(MicroState s) throws Exception {
        return AddStateAndObservation(s.state, s.observation);
    }

    public  static Atom[] AddStateAndObservation(MicroStateState state, MicroStateObservation observation) throws  Exception {
        if(Observations.size() == 0)
        {
            MicroStateObservation invalidActionObservation = new MicroStateObservation(true);
            Atom observationA =  new Atom(invalidActionObservation.toString().hashCode(), invalidActionObservation.toString());
            Observations.put(observationA, invalidActionObservation);
        }

        Atom stateA =  new Atom(state.toString().hashCode(), state.toString());
        if(!States.containsKey(stateA)) {
            States.put(stateA, state);
            ArrayList<Atom> validStateActions = ActionsDataStore.getValidStateActionIDs(state);
            ValidActionPerState.put(stateA, validStateActions);
            NumOfStates++;
        }

        Atom observationA =  new Atom(observation.toString().hashCode(), observation.toString());
        if(!Observations.containsKey(observationA)) {
            Observations.put(observationA, observation);
        }

        return new Atom[]{stateA, observationA};
    }

    public static Atom GetStateAtom(MicroStateState ms)
    {
        return new Atom(ms.toString().hashCode(), ms.toString());
    }

    public static Atom GetObservationAtom(MicroStateObservation obs)
    {
        return new Atom(obs.toString().hashCode(), obs.toString());
    }

    public static ArrayList<Atom> GetStateValidActions(Atom stateHash) throws Exception {
        ArrayList<Atom> actions = ValidActionPerState.getOrDefault(stateHash, null);
        if(actions == null)throw new Exception();
        return actions;
    }

    public  static MicroStateState GetState(Atom stateHash)
    {
        return States.get(stateHash);
    }

    public  static MicroStateObservation GetObservation(Atom observationHash)
    {
        return Observations.get(observationHash);
    }

    public String GetReadableStateID(int stateHash)
    {
        return ReadableStateID.containsKey(stateHash)? ReadableStateID.get(stateHash).toString() : "not in store";
    }
}
