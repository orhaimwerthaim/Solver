package JavaSim2POMCP.POMCP.JavaGeneratos.fixed;

import JavaSim2POMCP.POMCP.JavaGeneratos.Generated.MicroState;
import JavaSim2POMCP.POMCP.JavaGeneratos.Generated.MicroStateState;
import POMDP_Solver.Atom;
import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionsDataStore {
    private static HashMap<Atom, Pair<String,String[]>> IdToActions = new HashMap<>();

    private static HashMap<Integer, Integer> ReadableActionID = new HashMap<>();

    private static ArrayList<Pair<String,String[]>>  AllPossibleActions = null;

    public static void InitActionsDataStore() throws Exception {
        if(AllPossibleActions != null)return;
        AllPossibleActions = MicroState.GetAllPossibleActions();

        int NumOfActions = 0;
        for (Pair<String,String[]> action: AllPossibleActions
        ) {
            String actionDesc = MicroState.ActionToString(action);
            IdToActions.put(new Atom(actionDesc.hashCode(), actionDesc), action);
            ReadableActionID.put(actionDesc.hashCode(), NumOfActions);
            NumOfActions++;
        }
    }

    public static Atom GetActionAtom(Pair<String,String[]> action)
    {
        String actionDesc = MicroState.ActionToString(action);
        return  new Atom(actionDesc.hashCode(), actionDesc);
    }

    public  static Pair<String,String[]> GetAction(Atom actionHash)
    {
        return IdToActions.get(actionHash);
    }


    public String GetReadableStateID(int actionHash)
    {
        return ReadableActionID.containsKey(actionHash)? ReadableActionID.get(actionHash).toString() : "not in store";
    }


    public ArrayList<Pair<String,String[]>> getValidStateActions(MicroStateState s) throws Exception {
        return MicroState.GetStateActions(s);
    }

    public static ArrayList<Atom>  getValidStateActionIDs(MicroStateState ms) throws Exception {
        ArrayList<Atom> actionIDs = new ArrayList<>();
        for (Pair<String,String[]> action: MicroState.GetStateActions(ms)) {
            actionIDs.add(GetActionAtom(action));
        }
        return actionIDs;
    }
}
