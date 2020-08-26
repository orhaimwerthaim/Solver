package POMDP_Solver;

import util.Pair;

import java.util.ArrayList;

public interface POMCP_Gate {
    public int GetHorizon();

    public float GetDiscount();

    public Atom GetObservationFromROS_Response(String rosResponse);

    public ArrayList<Atom> GetPossibleInitialStates(int numOfStates) throws Exception;

    public ArrayList<Atom> GetStateActions(Atom state) throws Exception;

    public Atom GetIllegalActionObservation();

    public boolean IsGoalReached(Atom observation);

    public Pair<GeneratorResult, Boolean> Generator(Atom state, Atom action) throws Exception;

    public class GeneratorResult {
        public Atom NextState;
        public Atom Observation;
        public double Reward;

        public GeneratorResult(Atom nextState, Atom observation, double reward) {
            NextState = nextState;
            Observation = observation;
            Reward = reward;
        }
    }
}
