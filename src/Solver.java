import JavaSim2POMCP.POMCP.JavaGeneratos.Generated.MicroStateObservation;
import JavaSim2POMCP.POMCP.JavaGeneratos.Generated.MicroState_Gate;
import JavaSim2POMCP.POMCP.JavaGeneratos.fixed.JavaSimulatorUtils;
import JavaSim2POMCP.POMCP.JavaGeneratos.fixed.MiniStateDataStore ;
import POMDP_Solver.*;
import Solver_middleware.SendToROS;
import rddl.competition.RDDL_Simulator;
import util.Pair;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class Solver {
    static JavaSim2POMCP.POMCP.JavaGeneratos.fixed.MiniStateDataStore javaStore;
    static rddl.competition.MiniStateDataStore rddlStore;

    private static String RDDL_Dir = null;
    private static String RDDL_InitialStateDir = null;
    private static String RDDL_Instance = "created_plps_instance";
    private static String RDDL_InitialStateInstance = "initial_state_instance";
    private static int ILLEGAL_ACTION_PENALTY = -400;
    private static float ucb_c = 100.0f;
    private static int planningTime = 1000;
    private static boolean IsJavaSimulator = true;
    private static int NUMBER_OF_PARTICLES = 40;
    private static boolean usingROS = true;
    //args[0]=directory of RDDL2POMCP.RDDL2POMCP.RDDL2POMCP.rddl files
    //args[1]=instance name
    //arg[2]= if exists then it will not send TCP messages to ROS

    private static void LoadArgs(String[] args)
    {
        RDDL_InitialStateDir = GetArgByType("rddl_initial_state_dir", args);
        RDDL_Dir = GetArgByType("rddl_dir", args);
        IsJavaSimulator = RDDL_Dir == null;

        String sRDDL_Instance = GetArgByType("rddl_instance", args);
        if(sRDDL_Instance != null)
        {
            RDDL_Instance = sRDDL_Instance;
        }

        String sRDDL_InitialStateInstance = GetArgByType("rddl_initial_state_instance", args);
        if(sRDDL_InitialStateInstance != null)
        {
            RDDL_InitialStateInstance = sRDDL_InitialStateInstance;
        }


        String sUsingROS = GetArgByType("use_ros", args);
        if(sUsingROS != null)
        {
            usingROS = sUsingROS.toLowerCase().equals("true");
        }

        String sIllegalActionPenalty = GetArgByType("illegal_action_penalty", args);
        if(sIllegalActionPenalty != null)
        {
            ILLEGAL_ACTION_PENALTY = -Math.abs(Integer.parseInt(sIllegalActionPenalty));
        }

        String sMinNumOfParticles = GetArgByType("min_num_of_particles", args);
        if(sMinNumOfParticles != null)
        {
            NUMBER_OF_PARTICLES = Math.abs(Integer.parseInt(sMinNumOfParticles));
        }

        String sUCBc = GetArgByType("ucb_c", args);
        if(sUCBc != null)
        {
            ucb_c = Math.abs(Integer.parseInt(sUCBc));
        }


        String sPlanningTime = GetArgByType("planning_time", args);
        if(sPlanningTime != null)
        {
            planningTime = Math.abs(Integer.parseInt(sPlanningTime));
        }
    }

    public static void main(String[] args) {
        //System.out.println("args("+args.length+"):");
        //Arrays.stream(args).forEach(x-> System.out.println(x));

        LoadArgs(args);

        POMCP_Gate domainAndProblem = null;
        try {
            //NewServer initStateGen = new NewServer(args[0], "init_instance"/*args[1]*/, null);
            if(IsJavaSimulator) {
                domainAndProblem = new MicroState_Gate();
            }
            else
            {
                RDDL_Simulator initStateGen = RDDL_InitialStateDir == null ? null :
                        new RDDL_Simulator(RDDL_InitialStateDir, RDDL_InitialStateInstance/*args[1]*/, null, true);
                domainAndProblem = new RDDL_Simulator(RDDL_Dir, RDDL_Instance, initStateGen, false);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        POMCP pomcp = new POMCP(ucb_c, NUMBER_OF_PARTICLES, domainAndProblem, ILLEGAL_ACTION_PENALTY);

        try
        {
            String observationTemplate  = null;

            int totalScore = 0;
            ArrayList<String> realHist = new ArrayList<>();
            Atom realState = usingROS ? null : Utils.GetRandomMember(domainAndProblem.GetPossibleInitialStates(1));
            if(!usingROS) System.out.println("state:\n" +  realState.Description + "\n\n\n");
            while(true) {
                Atom actionHash = pomcp.Search(planningTime);
                if (actionHash == null || actionHash.toString().contains("no_action")) {
                    System.out.println("Plan Finished");
                    return;
                }

                System.out.println("---------------------------------------------------------------------------");
                //System.out.println("ACTION:" + ActionsDataStore.GetAction(actionHash).toString());
                System.out.println("ACTION:" + actionHash.toString());
                System.out.println("Time:" + Instant.now() + "\n");

                String msg = actionHash.toString()
                        .replace('(', ',')
                        .replace(")", "")
                        .replace(";", "")
                        .replace(" ", "");


                Pair<POMCP_Gate.GeneratorResult, Boolean> nextRealState = null;
                Atom observationReceived = null;

                if (usingROS) {

                    String ROS_ObservationResponse = SendToROS.SendROSMessage(msg);
                    //ROS response format- '<observationVar1>:<observationVar1Value>,<observationVar2>:<observationVar2Value>...'

                    observationReceived = domainAndProblem.GetObservationFromROS_Response(ROS_ObservationResponse);
                } else {
                    nextRealState = domainAndProblem.Generator(realState, actionHash);
                    if(nextRealState._o2) {
                        observationReceived = nextRealState._o1.Observation;
                    }
                    else
                    {
                        observationReceived = domainAndProblem.GetIllegalActionObservation();
                    }
                }
                boolean llegalAction = (usingROS) ? observationReceived.Description.contains("invalid_action_observation:true") : nextRealState._o2;

                realHist.add("Action: " + actionHash.toString());
                if (!usingROS) realHist.add("State: " + realState.toString());

                if (llegalAction) {
                    realHist.add("Observation: " + observationReceived.toString());
                }

                if(!usingROS) totalScore +=nextRealState._o1.Reward;

                if(!usingROS) realState = nextRealState._o1.NextState;
                if(!usingROS && !nextRealState._o2)
                {
                    totalScore += ILLEGAL_ACTION_PENALTY;
                    if(!usingROS) realHist.add("REWARD: " + ILLEGAL_ACTION_PENALTY);
                    realHist.add("Illegal action was executed");
                    System.out.println("Illegal action, state was not changed");
                }
                else {
                    if(!usingROS) realHist.add("REWARD: " + nextRealState._o1.Reward);
                    System.out.println("OBSERVATION (after action): " + (IsJavaSimulator ? javaStore.GetObservation(observationReceived) : rddlStore.GetObservation(observationReceived)));
                    System.out.println("Time:" + Instant.now() + "\n");
                    if(!usingROS) System.out.println("Next REAL STATE: " + (IsJavaSimulator ? javaStore.GetState(realState) : rddlStore.GetState(realState)));
                    if(!usingROS) System.out.println("REAL REWARD: " + nextRealState._o1.Reward);

                }
                pomcp.PruneAfterRealActionByActionAndObservationAndFillBefiefeState(actionHash, observationReceived);
                if (!usingROS && llegalAction && domainAndProblem.IsGoalReached(observationReceived))//observationReceived.toString().contains("goal_reached={[]=true}"))
                {
                    realHist.add("final State: " + realState.toString());
                    realHist.add("Total REWARD: " + totalScore);
                    System.out.println("goal reached, simulation ended");
                    //return;
                    System.out.println("****************************************************************");
                    for (String s2:realHist
                    ) {
                        System.out.println(s2);
                    }
                    return;
                }
                if(!usingROS) realHist.add("");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            int i = 33;
        }

    }

    private static String GetArgByType(String type, String[] args)
    {
        if (Arrays.stream(args).filter(x-> x.startsWith(type + "=")).count() == 0) return null;
        String var = Arrays.stream(args).filter(x-> x.startsWith(type + "=")).toArray()[0].toString();
        return var.substring(type.length()+1);
    }


}
