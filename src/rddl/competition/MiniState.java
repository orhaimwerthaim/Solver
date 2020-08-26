package rddl.competition;

import com.google.gson.reflect.TypeToken;
import rddl.RDDL;
import rddl.State;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MiniState {
    public static Type type = new TypeToken<HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>>>(){}.getType();
    public  HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> state;
    public  HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> observations;

    public MiniState()
    {
        state = new HashMap<>();
        observations = new HashMap<>();
    }
    public MiniState(State s) {
        extractStateAndObservationCopy(s._state, s._observ);
    }

    public MiniState(State s, boolean fromNextState)  {
        if(fromNextState) {
            extractStateAndObservationCopy(s._nextState, s._observ);
        }
        else{
            extractStateAndObservationCopy(s._state, s._observ);
        }
    }

    public MiniState(HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> state)
    {
        extractStateAndObservationCopy(state, null);
    }

    public void extractStateAndObservationCopy(
            HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> stateVars, HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>,Object>> observation) {

        state = new HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>, Object>>();
        observations = new HashMap<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>, Object>>();
        for (Map.Entry<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>, Object>> entry : stateVars.entrySet()) {
            HashMap<ArrayList<RDDL.LCONST>, Object> value = new HashMap<>();
            for (Map.Entry<ArrayList<RDDL.LCONST>, Object> entryVal :
                    entry.getValue().entrySet()) {
                value.put(new ArrayList<RDDL.LCONST>(entryVal.getKey()), entryVal.getValue());
            }
            state.put(entry.getKey(), value);
        }

        if (observation != null) {
            for (Map.Entry<RDDL.PVAR_NAME, HashMap<ArrayList<RDDL.LCONST>, Object>> entry : observation.entrySet()) {
                HashMap<ArrayList<RDDL.LCONST>, Object> value = new HashMap<>();
                for (Map.Entry<ArrayList<RDDL.LCONST>, Object> entryVal :
                        entry.getValue().entrySet()) {
                    value.put(new ArrayList<RDDL.LCONST>(entryVal.getKey()), entryVal.getValue());
                }
                observations.put(entry.getKey(), value);
            }
        }
    }

    public int hashCode() {
        return state.toString().hashCode();
    }

}
