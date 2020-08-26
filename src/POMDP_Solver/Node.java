package POMDP_Solver;

import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    public Atom NodeActionOrobservation;
    //public Node Parent;//null for Root
    public boolean IsActionNode;//if false it is an observation node
    public HashMap<Atom, Node> ActionOrObservationToChild;//for action node it will be observation IDs. for observation node it will be action IDs
    public Integer N;//Times simulation visited this node
    public Double V;//estimated value of the node
    public ArrayList<Atom> BeliefState;

    public boolean IsLeaf()
    {
        return ActionOrObservationToChild == null || ActionOrObservationToChild.size() == 0;
    }

    public Node(Node parent, boolean isActionNode, Atom actionOrobservation){
        NodeActionOrobservation = actionOrobservation;
        //Parent = parent;
        IsActionNode = isActionNode;
        ActionOrObservationToChild = null;
        N =0;
        V=0d;
        BeliefState = new ArrayList<>();
    }

    private Double getNFotUCB(Integer n)
    {
        if(n==0) return 0.00001;
        else return n+1d;
    }
    public ArrayList<Atom> GetBeliefState()
    {
        return BeliefState;
    }

    public double Get_NodeValue_Plus_UCB(Float c, Integer parentNodeN)
    {
        //double temp2 = parentNodeN == 1 ? 1 : parentNodeN;
        //double temp = N == 1 ? 0.0001: N;

        double Nn = N == 0 ? 0.00001 : N;
        double parentNodeNn = parentNodeN == 0 ? 1 : parentNodeN;
        return V+ c*Math.sqrt(Math.log(parentNodeNn)/Nn);
        //return V+ c*Math.sqrt(Math.log(getNFotUCB(parentNodeN))/getNFotUCB(N));

        //return V+ c*Math.sqrt(Math.log(temp2)/temp);
    }

    //returns Pair<Node,Boolean>: the actionOrobservation Node, Boolean stating if it is new in the tree
    public Pair<Node,Boolean> GetOrAddChildNode(Atom actionOrobservation)
    {
        //POMCP.TimeGetOrAddChildNode.Start();
        this.ActionOrObservationToChild = this.ActionOrObservationToChild == null ? new HashMap<>() : this.ActionOrObservationToChild;
        if(this.ActionOrObservationToChild.containsKey((actionOrobservation)))//observation/action already exist in children, return it
        {
            return new Pair<>(this.ActionOrObservationToChild.get(actionOrobservation), false);
        }
        if(actionOrobservation == null)//if no valid action then it is a leaf
        {
            return null;
        }
        Node newNode = new Node(this, !this.IsActionNode, actionOrobservation);
        this.ActionOrObservationToChild.put(actionOrobservation, newNode);
        //POMCP.TimeGetOrAddChildNode.Stop();
        return new Pair<>(newNode, true);

    }

    @Override
    public String toString() {
        return "N:"+N+",V:"+V+","+NodeActionOrobservation;
    }
}

