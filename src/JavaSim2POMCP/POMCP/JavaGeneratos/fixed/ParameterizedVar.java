package JavaSim2POMCP.POMCP.JavaGeneratos.fixed;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ParameterizedVar<Value>
{
    public ArrayList<String> params;
    public Value value;

    public ParameterizedVar(ArrayList<String> _params, Value _value)
    {
        params=_params;
        value=_value;
    }

    public ParameterizedVar() {
    }

    @Override
    public String toString() {
        String sParams = params.size() == 0 ? "" :
                "(" + params.stream().collect(Collectors.joining(",")) + ")";
        return sParams + "=" + value;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ParameterizedVar)) return false;
        ParameterizedVar o1 = (ParameterizedVar)o;
        return this.toString().equals(o1.toString());
//
//        if (this.value.equals(o1.value)) return false;
//        if(this.params.size()!= o1.params.size()) return false;
//        for(int i=0;i<this.params.size();i++)
//        {
//            if(!this.params.get(i).equals(o1.params.get(i))) return false;
//        }
//        return true;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
