package JavaSim2POMCP.POMCP.JavaGeneratos.Generated;

public class MicroState_backup {
 /*   public int reward;
    static HashSet<String> robot;
    static HashSet<String> obj;
    static HashSet<String> discrete_location;
    static HashSet<String> floor;

    public static MicroStateIntermediate interm = new MicroStateIntermediate();

    public static ArrayList<ParameterizedVar<Boolean>> at_floor;
    public MicroStateObservation observation;
    public MicroStateState state;

    static Random rand = new Random();

    static
    {
        robot = new HashSet<>();
        robot.add("armadillo");

        obj = new HashSet<>();
        obj.add("can");
        obj.add("can2");

        discrete_location = new HashSet<>();
        discrete_location.add("robot_lab");
        discrete_location.add("office");
        discrete_location.add("hallway");
        discrete_location.add("out_of_reach_warehouse");

        floor = new HashSet<>();
        floor.add("f1");
        floor.add("f2");
        floor.add("f3");

        at_floor = new ArrayList<>();

        for (String i0: discrete_location)
        {
            for (String i1: floor)
            {
                ArrayList<String> a1 =
                        new ArrayList<String>(Arrays.asList(i0, i1));
                boolean value = (i0.equals("robot_lab") && i1.equals("f2")) ? true : (i0.equals("office") && i1.equals("f2")) ? true : (i0.equals("hallway") && i1.equals("f2")) ? true : (i0.equals("out_of_reach_warehouse") && i1.equals("f3")) ? true : false;

                ParameterizedVar<Boolean> p = new ParameterizedVar<Boolean>(a1, value);
                at_floor.add(p);
            }
        }
    }

    //Pair<String:'var name',String:'var type, can be bool/int'>

    private int GetRandomOption()
    {
        float[] options = new float[]{0.37f,0.40f,0.23f};
        Random rand = new Random();
        float fRand = rand.nextFloat();
        float current = 0.0f;
        for(int i=0;;i++)
        {
            if(current <= fRand && fRand <= options[i] + current)
            {
                return i;
            }
            current += options[i];
        }
    }
    public MicroState_backup(boolean initState) {
        state = new MicroStateState();
        observation = new MicroStateObservation();

        if (initState) {
            int numOfOptions = 3;
            int selectedOpt= numOfOptions > 0 ? GetRandomOption() : -1;
            state.near = new ArrayList<>();

            for (String i0: robot)
            {
                for (String i1: discrete_location)
                {
                    ArrayList<String> a1 =
                            new ArrayList<String>(Arrays.asList(i0, i1));
                    boolean value = ((selectedOpt == 0) && i0.equals("armadillo") && i1.equals("office")) ? true : ((selectedOpt == 1) && i0.equals("armadillo") && i1.equals("robot_lab")) ? true : ((selectedOpt == 2) && i0.equals("armadillo") && i1.equals("robot_lab")) ? true : false;

                    ParameterizedVar<Boolean> p = new ParameterizedVar<Boolean>(a1, value);
                    state.near.add(p);
                }
            }
            state.hand_empty = new ArrayList<>();

            for (String i0: robot)
            {
                ArrayList<String> a1 =
                        new ArrayList<String>(Arrays.asList(i0));
                boolean value = true;

                ParameterizedVar<Boolean> p = new ParameterizedVar<Boolean>(a1, value);
                state.hand_empty.add(p);
            }
            state.pickable = new ArrayList<>();

            for (String i0: obj)
            {
                ArrayList<String> a1 =
                        new ArrayList<String>(Arrays.asList(i0));
                boolean value = (i0.equals("can")) ? true : false;

                ParameterizedVar<Boolean> p = new ParameterizedVar<Boolean>(a1, value);
                state.pickable.add(p);
            }
            state.object_at = new ArrayList<>();

            for (String i0: obj)
            {
                for (String i1: discrete_location)
                {
                    ArrayList<String> a1 =
                            new ArrayList<String>(Arrays.asList(i0, i1));
                    boolean value = (i0.equals("can2") && i1.equals("out_of_reach_warehouse")) ? true : ((selectedOpt == 0) && i0.equals("can") && i1.equals("robot_lab")) ? true : ((selectedOpt == 1) && i0.equals("can") && i1.equals("office")) ? true : ((selectedOpt == 2) && i0.equals("can") && i1.equals("robot_lab")) ? true : false;

                    ParameterizedVar<Boolean> p = new ParameterizedVar<Boolean>(a1, value);
                    state.object_at.add(p);
                }
            }
        }
    }

    public static MicroState NextState(MicroStateState state, ArrayList<Pair<String,String[]>> actions)
    {
        Atom sA = MiniStateDataStore.GetStateAtom(state);
        ArrayList<Atom> actionsA=null;
        try {
            actionsA = MiniStateDataStore.GetStateValidActions(sA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Atom actionA = ActionsDataStore.GetActionAtom(actions.get(0));
        if(!actionsA.contains(actionA)) {
            int i = 4;
        }


        MicroState next = new MicroState(false);
        MicroStateIntermediate interm = CalcIntermediate(state, actions);
        next.state = CalcNextStateVariables(state, interm, actions);
        next.observation = CalcObservation(state, interm, next.state, actions);
        next.reward = CalcReward(state, interm, next.observation, next.state, actions);
        return next;
    }

    public static ArrayList<Pair<String,String[]>> GetAllPossibleActions() {
        ArrayList<Pair<String, String[]>> actions = new ArrayList<>();

        ArrayList<Pair<String, String[]>> validActions = new ArrayList<>();
        for (String rob :
                robot) {
            for (String location :
                    discrete_location) {
                for (String object :
                        obj) {
                    actions.add(new Pair<>("observe_can", new String[]{rob, location, object}));
                }
            }
        }

        for (String location1 :
                discrete_location) {
            for (String location2 :
                    discrete_location) {
                if (location1.equals(location2)) continue;
                ParameterizedVar<Boolean> at_loc1 =
                        at_floor.stream()
                                .filter(a -> a.value &&
                                        a.params.get(0).equals(location1)).findFirst().get();
                ParameterizedVar<Boolean> at_loc2 =
                        at_floor.stream()
                                .filter(a -> a.value &&
                                        a.params.get(0).equals(location2)).findFirst().get();
                if (!at_loc1.params.get(1).equals(at_loc2.params.get(1))) continue;//not in same floor
                for (String rob :
                        robot) {
                    actions.add(new Pair<>("move_to_point", new String[]{rob, at_loc1.params.get(0), at_loc2.params.get(0), at_loc1.params.get(1)}));
                }

                for (String rob :
                        robot) {
                    for (String location :
                            discrete_location) {
                        for (String object :
                                obj) {
                            actions.add(new Pair<>("pick", new String[]{rob, location, object}));
                        }
                    }
                }

            }
        }
        return actions;
    }

    private static ArrayList<Pair<String,String[]>> GetValidStateObserveActions(MicroStateState ms)
    {
        ArrayList<Pair<String,String[]>> validActions = new ArrayList<>();
        for (ParameterizedVar<Boolean> var:
                ms.near) {
            if(!var.value)continue;
            String rob = var.params.get(0);
            String location = var.params.get(1);
            for (String o:
                    obj) {
                validActions.add(new Pair<>("observe_can", new String[]{rob, location,o}));
            }
        }
        return validActions;
    }

    private static ArrayList<Pair<String,String[]>> GetValidStateMoveActions(MicroStateState ms)
    {
        ArrayList<Pair<String,String[]>> validActions = new ArrayList<>();
        for (String robot1:
                robot) {
            for (String discrete_location3 :
                    discrete_location) {
                for (String floor4 :
                        floor) {
                    for (String discrete_location2 :
                            discrete_location) {
                        if (
                                (ms.near.stream().filter(x-> x.value.equals(true) && x.params.get(0).equals(robot1) && x.params.get(1).equals(discrete_location3)).count() > 0)
                                        && (at_floor.stream().filter(x-> x.value.equals(true) && x.params.get(0).equals(discrete_location3) && x.params.get(1).equals(floor4)).count() > 0)
                                        && (at_floor.stream().filter(x-> x.value.equals(true) && x.params.get(0).equals(discrete_location2) && x.params.get(1).equals(floor4)).count() > 0)
                                        && (ms.near.stream().filter(x-> x.value.equals(false) && x.params.get(0).equals(robot1) && x.params.get(1).equals(discrete_location2)).count() > 0)
                        )
                        {
                            validActions.add(new Pair<>("move_to_point", new String[]{robot1, discrete_location3, discrete_location2, floor4}));
                        }
                    }
                }
            }
        }


//        for (String rob:
//                robot) {
//            for (ParameterizedVar<Boolean>  nearO:
//                    ms.near) {
//                if(!nearO.value || !nearO.params.get(0).equals(rob))continue;
//                for (ParameterizedVar<Boolean>  at_floorO:
//                        at_floor) {
//                    if(!at_floorO.value || !at_floorO.params.get(0).equals(nearO.params.get(1)))continue;
//                    String robotLoc = nearO.params.get(1);
//                    String robotFloor = at_floorO.params.get(1);
//                    ArrayList<String> sameFloorLocations = new ArrayList<>();
//
//                    for (ParameterizedVar<Boolean>  at_floor2O:
//                            at_floor) {
//                        if(!at_floor2O.value ||
//                                !at_floor2O.params.get(1).equals(robotFloor) ||
//                                at_floor2O.params.get(0).equals(robotLoc)) continue;
//                        validActions.add(new Pair<>("move_to_point", new String[]{rob, robotLoc,at_floor2O.params.get(0), robotFloor}));
//                    }
//                }
//            }
//        }
        return validActions;
    }
    private static ArrayList<Pair<String,String[]>> GetValidStatePickActions(MicroStateState ms)
    {
        ArrayList<Pair<String,String[]>> validActions = new ArrayList<>();
        for (String rob:
                robot) {
            for (ParameterizedVar<Boolean>  hand_emptyO:
                    ms.hand_empty) {
                if(!hand_emptyO.value)continue;
                if(rob.equals(hand_emptyO.params.get(0)))
                {
                    for (ParameterizedVar<Boolean>  nearO:
                            ms.near) {
                        if(!nearO.value)continue;
                        if(nearO.params.get(0).equals(rob))
                        {
                            String robotLocation = nearO.params.get(1);
                            for (ParameterizedVar<Boolean>  object_atO:
                                    ms.object_at) {
                                if(!object_atO.value)continue;
                                if(object_atO.params.get(1).equals(robotLocation))
                                {
                                    String object = object_atO.params.get(0);
                                    for (ParameterizedVar<Boolean>  pickableO:
                                            ms.pickable) {
                                        if(!pickableO.value)continue;
                                        if(pickableO.params.get(0).equals(object))
                                        {
                                            validActions.add(new Pair<>("pick", new String[]{rob, robotLocation, object}));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return validActions;
    }

    public static ArrayList<Pair<String,String[]>> GetStateActions(MicroStateState ms) {
        ArrayList<Pair<String, String[]>> validActions = new ArrayList<>();
        validActions.addAll(GetValidStateObserveActions(ms));
        validActions.addAll(GetValidStateMoveActions(ms));
        validActions.addAll(GetValidStatePickActions(ms));

        return validActions;
    }

    public static String ActionToString(Pair<String,String[]> action)
    {
        String params = action._o2.length == 0 ? "" : ("(" +
                Arrays.stream(action._o2)
                        .collect(Collectors.joining(",")) + ")");
        return action._o1 + params;
    }

    private static int CalcReward(MicroStateState state, MicroStateIntermediate interm, MicroStateObservation observation, MicroStateState next, ArrayList<Pair<String,String[]>> actions)
    {
        int reward = 0;
        if(observation.goal_reached)
        {
            reward +=400;
        }

        for (Pair<String,String[]> action : actions) {
            if(action._o1.equals("observe_can"))
                reward +=-3;
        }

        for (Pair<String,String[]> action : actions) {
            if(action._o1.equals("move_to_point"))
                reward +=-20;
        }


        for (Pair<String,String[]> action : actions) {
            if(action._o1.equals("pick"))
                reward +=200;
        }
        return reward;
    }

    private static MicroStateObservation CalcObservation(MicroStateState state, MicroStateIntermediate interm, MicroStateState next, ArrayList<Pair<String,String[]>> actions)
    {
        MicroStateObservation observation = new MicroStateObservation();
        observation.goal_reached = getGoal_reached(state, interm, next, actions);
        observation.obsrv_observe_can = getObsrv_observe_can(state, interm, next, actions);
        observation.obsrv_move_to_point = getObsrv_move_to_point(state, interm, next, actions);
        observation.obsrv_pick = getObsrv_pick(state, interm, next, actions);
        return observation;
    }

    private static int getObsrv_move_to_point(MicroStateState state, MicroStateIntermediate interm, MicroStateState next, ArrayList<Pair<String,String[]>> actions)
    {
        return interm.success_move_to_point ? 1:0;
    }

    private static int getObsrv_pick(MicroStateState state, MicroStateIntermediate interm, MicroStateState next, ArrayList<Pair<String,String[]>> actions)
    {
        return interm.success_pick ? 1:0;
    }

    private static int getObsrv_observe_can(MicroStateState state, MicroStateIntermediate interm, MicroStateState next, ArrayList<Pair<String,String[]>> actions)
    {
        if(!interm.success_observe_can)return 0;
        int result = 0;
        result += interm.success_observe_can ? 1 : 0;
        boolean exists = false;
        for (Pair<String,String[]> action : actions) {
            if (!action._o1.equals("observe_can")) continue;
            String can = action._o2[2];
            String location = action._o2[1];

            if (interm.success_observe_can) {
                for (ParameterizedVar<Boolean> object_atO :
                        next.object_at) {
                    if (object_atO.params.get(0).equals(can) &&
                            object_atO.params.get(1).equals(location)) {
                        if (!object_atO.value) {
                            break;
                        } else {
                            result += rand.nextFloat() < 0.95 ? 1 : 0;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static MicroStateIntermediate CalcIntermediate(MicroStateState currentState, ArrayList<Pair<String,String[]>> actions) {
        MicroStateIntermediate interm = new MicroStateIntermediate();
        interm.success_pick = getIntsuccess_pick(actions, currentState);
        interm.success_move_to_point = getIntsuccess_move_to_point(actions, currentState);
        interm.success_observe_can = getIntsuccess_observe_can(actions, currentState);
        return interm;
    }

    private static boolean getIntsuccess_observe_can(ArrayList<Pair<String,String[]>> actions, MicroStateState currentState) {
        for (Pair<String,String[]> action : actions) {
            if(!action._o1.equals("observe_can"))continue;

            return rand.nextFloat() < 0.99;
        }
        return false;
    }

    private static boolean getIntsuccess_move_to_point(ArrayList<Pair<String,String[]>> actions, MicroStateState currentState) {
        for (Pair<String,String[]> action : actions) {
            if(!action._o1.equals("move_to_point"))continue;

            return rand.nextFloat() < 0.93;
        }
        return false;
    }

    private static boolean getIntsuccess_pick(ArrayList<Pair<String,String[]>> actions, MicroStateState currentState) {
        for (Pair<String,String[]> action : actions) {
            if(!action._o1.equals("pick"))continue;

            return rand.nextFloat() < 1.0;
        }
        return false;
    }

    private static MicroStateState CalcNextStateVariables(MicroStateState state, MicroStateIntermediate interm, ArrayList<Pair<String,String[]>> actions)
    {
        MicroStateState next = new MicroStateState();
        for (ParameterizedVar<Boolean> object_atO:
                state.object_at) {
            boolean o = getNextObjectAt(object_atO, actions, state, interm);
            next.object_at.add(new ParameterizedVar<Boolean>(object_atO.params, o));
        }

        for (ParameterizedVar<Boolean> pickableO:
                state.pickable) {
            boolean o = getNextPickable(pickableO, actions, state, interm);
            next.pickable.add(new ParameterizedVar<Boolean>(pickableO.params, o));
        }

        for (ParameterizedVar<Boolean> nearO:
                state.near) {
            boolean o = getNextNear(nearO, actions, state, interm);
            next.near.add(new ParameterizedVar<Boolean>(nearO.params, o));
        }

        for (ParameterizedVar<Boolean> hand_emptyO:
                state.hand_empty) {
            boolean o = getNextHandEmptyO(hand_emptyO, actions, state, interm);
            next.hand_empty.add(new ParameterizedVar<Boolean>(hand_emptyO.params, o));
        }
        return next;
    }

    private static boolean getNextHandEmptyO(ParameterizedVar<Boolean> var, ArrayList<Pair<String,String[]>> actions, MicroStateState state, MicroStateIntermediate interm) {
        String robot_1 = var.params.get(0);
        if (interm.success_pick) {
            for (Pair<String,String[]> action : actions) {
                if(!action._o1.equals("pick"))continue;
                if (action._o2[0].equals(robot_1))
                    return false;
            }
        }
        return var.value;
    }


    private static boolean getGoal_reached(MicroStateState state, MicroStateIntermediate interm, MicroStateState next, ArrayList<Pair<String,String[]>> actions)
    {
        boolean exists = false;
        for (ParameterizedVar<Boolean> nearO:
                next.near) {
            if(!nearO.value)continue;
            if(nearO.params.get(1).equals("robot_lab"))
            {
                exists=true;
                break;
            }
        }
        if(!exists)return false;

        for (ParameterizedVar<Boolean> hand_emptyO:
                next.hand_empty) {
            if(!hand_emptyO.value) return true;
        }
        return false;
    }

    private static boolean getNextNear(ParameterizedVar<Boolean> var, ArrayList<Pair<String,String[]>> actions, MicroStateState state, MicroStateIntermediate interm) {
        String robot_1 = var.params.get(0);
        String discrete_location_2 = var.params.get(1);
        if (interm.success_move_to_point) {
            for (Pair<String,String[]> action : actions) {
                if(!action._o1.equals("move_to_point"))continue;
                if (action._o2[1].equals(discrete_location_2))
                    return false;
            }
        }
        if (interm.success_move_to_point) {
            for (Pair<String,String[]> action : actions) {
                if(!action._o1.equals("move_to_point"))continue;
                if (action._o2[2].equals(discrete_location_2))
                    return true;
            }
        }
        return var.value;
    }

    private static boolean getNextPickable(ParameterizedVar<Boolean> var, ArrayList<Pair<String,String[]>> actions, MicroStateState state, MicroStateIntermediate interm) {
        String obj_1 = var.params.get(0);
        if (interm.success_pick) {
            for (Pair<String,String[]> action : actions) {
                if(!action._o1.equals("pick"))continue;
                if (action._o2[0].equals(obj_1))
                    return false;
            }
        }
        return var.value;
    }

    private static boolean getNextObjectAt(ParameterizedVar<Boolean> var, ArrayList<Pair<String,String[]>> actions, MicroStateState state, MicroStateIntermediate interm) {
        String obj_1 = var.params.get(0);
        String discrete_location_2 = var.params.get(1);
        if (interm.success_pick) {
            for (Pair<String,String[]> action : actions) {
                if(!action._o1.equals("pick"))continue;
                if (action._o2[1].equals(discrete_location_2) &&
                        action._o2[2].equals(obj_1))
                    return true;
            }
        }
        return var.value;
    }
*/}
