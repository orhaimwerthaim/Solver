package rddl.competition;
import POMDP_Solver.Atom;
import POMDP_Solver.POMCP;
import POMDP_Solver.POMCP_Gate;
import util.Pair;

import java.time.Instant;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RunRDDL_POMCP {
    /*private static int ILLEGAL_ACTION_PENALTY = -400;
    //args[0]=directory of rddl files
    //args[1]=instance name
    //arg[2]= if exists then it will not send TCP messages to ROS
    private static int NUMBER_OF_PARTICLES = 40;


    public static void main(String[] args) {
        boolean usingROS = args.length < 3;
        RDDL_Simulator domainAndProblem = null;
        try {
            RDDL_Simulator initStateGen = new RDDL_Simulator(args[0], "init_instance", null);
            domainAndProblem = new RDDL_Simulator(args[0], args[1], initStateGen);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        POMCP pomcp = new POMCP(100.0f, NUMBER_OF_PARTICLES, domainAndProblem, ILLEGAL_ACTION_PENALTY);

        try
        {
            String observationTemplate  = null;

            int totalScore = 0;
            ArrayList<String> realHist = new ArrayList<>();
            Atom realState = usingROS ? null : Utils.GetRandomMember(domainAndProblem.possibleInitialStates);
            if(!usingROS) System.out.println("state:\n" + MiniStateDataStore.GetState(realState) + "\n\n\n");
            while(true) {
                Atom actionHash = pomcp.Search(1000);
                if(actionHash == null || actionHash.toString().contains("no_action"))
                {
                    System.out.println("Plan Finished");
                    return;
                }

                System.out.println("---------------------------------------------------------------------------");
                System.out.println("ACTION:" + ActionsDataStore.GetAction(actionHash).toString());
                System.out.println("Time:" + Instant.now() + "\n");

                String msg = actionHash.toString()
                        .replace('(', ',')
                        .replace(")", "")
                        .replace(";", "")
                        .replace(" ", "");


                Pair<POMCP_Gate.GeneratorResult, Boolean> nextRealState = null;
                Atom observationReceived = null;

                if(observationTemplate == null)
                {
                    observationTemplate = domainAndProblem.Generator(domainAndProblem.possibleInitialStates.get(0), actionHash)._o1.Observation.toString();
                    observationTemplate = observationTemplate.replaceAll("[0-9]+","0");
                }

                if(usingROS)
                {

                    String ROS_ObservationResponse = SendToROS.SendROSMessage(msg);
                    //ROS response format- '<observationVar1>:<observationVar1Value>,<observationVar2>:<observationVar2Value>...'
                    String currentStringObservation = observationTemplate;
                    String[] observations = ROS_ObservationResponse.split(",");
                    for(int i = 0; i < observations.length;i++)
                    {
                        String[] bits = observations[i].split(":");
                        currentStringObservation = InsertValueToObservation(currentStringObservation, bits[0], bits[1]);
                    }
                    observationReceived = new Atom(currentStringObservation.hashCode(), currentStringObservation);
                }
                else
                {
                    nextRealState = domainAndProblem.Generator(realState, actionHash);
                    observationReceived = nextRealState._o1.Observation;
                }

                realHist.add("Action: " + actionHash.toString());
                if(!usingROS) realHist.add("State: " + realState.toString());


                realHist.add("Observation: " + observationReceived.toString());

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
                    System.out.println("OBSERVATION (after action): " + MiniStateDataStore.GetObservation(observationReceived));
                    System.out.println("Time:" + Instant.now() + "\n");
                    if(!usingROS) System.out.println("Next REAL STATE: " + MiniStateDataStore.GetState(realState));
                    if(!usingROS) System.out.println("REAL REWARD: " + nextRealState._o1.Reward);
                    pomcp.PruneAfterRealActionByActionAndObservationAndFillBefiefeState(actionHash, observationReceived);
                }
                if (!usingROS && observationReceived.toString().contains("goal_reached={[]=true}"))
                {
                    realHist.add("final State: " + realState.toString());
                    realHist.add("Total REWARD: " + totalScore);
                    System.out.println("goal reached, simulation ended");
                    //return;
                    System.out.println("****************************************************************");
                    for (String s:realHist
                         ) {
                        System.out.println(s);
                    }
                    return;
                }
                if(!usingROS) realHist.add("");
//////////////////////////////////////////////////////
////These lines are for Testing with a simulation of real world:
//                POMCP_Gate.GeneratorResult nextRealState = domainAndProblem.Generator(realState, actionHash);
//                realState = nextRealState.NextState;
//                System.out.println("OBSERVATION (after action): "+MiniStateDataStore.GetObservation(nextRealState.Observation));
//                System.out.println("Time:"+ Instant.now()+"\n");
//                System.out.println("Next REAL STATE: "+MiniStateDataStore.GetState(realState));
//                System.out.println("REAL REWARD: "+nextRealState.Reward);
//                pomcp.PruneAfterRealActionByActionAndObservationAndFillBefiefeState(actionHash, nextRealState.Observation);
//////////////////////////////////////////////////////
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            int i = 33;
        }
    }
    private static String InsertValueToObservation(String currentObservation, String observationVariableName, String value)
    {
        if(value.equals("") || value.equals("-1")) return currentObservation;

        Pattern p = Pattern.compile(observationVariableName);
        Matcher m = p.matcher(currentObservation );
        m.find();
        String second  = currentObservation.substring(m.start()).replaceFirst("0",value);
        String result = currentObservation.substring(0, m.start()) + second;
        return result;
    }*/
}
