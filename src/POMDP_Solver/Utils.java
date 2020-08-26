package POMDP_Solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Utils {
    private static Random random = new Random();
    public static <O> O GetRandomMember(ArrayList<O> al)
    {
        if (al.size()==0)
        {
            return null;
        }
        Integer randomIndex = random.nextInt(al.size());
        return al.get(randomIndex);
    }

    public static <O> O GetRandomMember(O[] ar)
    {
        if (ar.length ==0)
        {
            return null;
        }
        Integer randomIndex = random.nextInt(ar.length);
        return ar[randomIndex];
    }
}
