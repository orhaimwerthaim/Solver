package rddl.competition;

import rddl.EvalException;
import rddl.RDDL;
import rddl.State;
import rddl.competition.MiniState;
//import rddl.competition.POMCP.Atom;
import POMDP_Solver.Atom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActionsDataStore {
    private static HashMap<Atom, RDDL.PVAR_INST_DEF> IdToActions = new HashMap<>();
    private static HashMap<RDDL.PVAR_INST_DEF, Atom> ActionsToID = new HashMap<>();

    private static HashMap<Integer, Integer> ReadableActionID = new HashMap<>();

    private static ArrayList<RDDL.PVAR_INST_DEF> AllPossibleActions;
    private static State StateTemplate;

    private static RDDL.PVAR_INST_DEF NullAction = new RDDL.PVAR_INST_DEF("NullAction", null, new ArrayList<RDDL.LCONST>());

    public static void InitActionsDataStore(State initState) throws EvalException {
        StateTemplate = initState;
        AllPossibleActions = GetAllActions(initState);

        int NumOfActions = 0;
        for (RDDL.PVAR_INST_DEF action: AllPossibleActions
        ) {
            IdToActions.put(new Atom(action.toString().hashCode(), action.toString()), action);
            ActionsToID.put(action, new Atom(action.toString().hashCode(), action.toString()));
            ReadableActionID.put(action.toString().hashCode(), NumOfActions);
            NumOfActions++;
        }
    }

    public  static RDDL.PVAR_INST_DEF GetAction(Atom actionHash)
    {
        return IdToActions.get(actionHash);
    }

    public  static Integer GetActionId(RDDL.PVAR_INST_DEF action)
    {
        return ActionsToID.get(action).Value;
    }

    public String GetReadableStateID(int actionHash)
    {
        return ReadableActionID.containsKey(actionHash)? ReadableActionID.get(actionHash).toString() : "not in store";
    }

    public static ArrayList<RDDL.PVAR_INST_DEF> GetAllActions(State s) throws EvalException {
        ArrayList<RDDL.PVAR_INST_DEF> actions = new ArrayList<RDDL.PVAR_INST_DEF>();
        if (s == null) return actions;
        for (int j = 0; j < s._alActionNames.size(); j++) {
            // Get a random action
            RDDL.PVAR_NAME p = s._alActionNames.get(j);
            RDDL.PVARIABLE_DEF pvar_def = s._hmPVariables.get(p);
            // Get term instantations for that action and select *one*
            ArrayList<ArrayList<RDDL.LCONST>> inst = s.generateAtoms(p);
            for (int i = 0; i < inst.size(); i++) {
                ArrayList<RDDL.LCONST> terms = inst.get(i);
                Object value = true;
                actions.add(new RDDL.PVAR_INST_DEF(p._sPVarName, value, terms));
            }
        }
        return actions;
    }

    public ArrayList<RDDL.PVAR_INST_DEF> getValidStateActions(State s) throws EvalException {
        ArrayList<RDDL.PVAR_INST_DEF> validActions = new ArrayList<>();
        for (RDDL.PVAR_INST_DEF action: AllPossibleActions) {
            if(s.checkStateActionConstraints(new ArrayList<>(List.of(action)), false))
            {
                validActions.add(action);
            }
        }
        return validActions;
    }

    public static ArrayList<Atom>  getValidStateActionIDs(MiniState ms) throws EvalException {
        StateTemplate._state = ms.state;
        ArrayList<Atom> actionIDs = new ArrayList<>();
        ArrayList<RDDL.PVAR_INST_DEF> validActions = new ArrayList<>();
        for (RDDL.PVAR_INST_DEF action: AllPossibleActions) {
            if(StateTemplate.checkStateActionConstraints(new ArrayList<>(List.of(action)), false))
            {
                actionIDs.add(ActionsToID.get(action));
            }
        }
        return actionIDs;
    }
}
