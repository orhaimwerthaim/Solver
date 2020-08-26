package POMDP_Solver;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Atom {
    public static Atom NullAction = new Atom(0, "NullAction");
    public static Atom NullObservation = new Atom(1, "NullObservation");


    public Integer Value;
    public String Description;
    public Atom(Integer value, String desc)
    {
        Value=value;
        Description = desc;
    }


    @Override
    public String toString() {
        return Description;
    }

    @Override
    public int hashCode() {
        return Description.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Atom)) return false;
        return Description.equals(((Atom) obj).Description);
    }

    public static class StopWatch {
        public long MILLIS = 0;
        boolean IsRunning;
        Instant runStart = null;
        public int activations = 0;
        public StopWatch()
        {
            IsRunning = false;
        }

        public void Start()
        {
            activations++;
            if(runStart == null)
            {
                runStart = Instant.now();
            }
            IsRunning = true;
        }

        public void Stop()
        {
            if(runStart != null)
            {
                MILLIS += ChronoUnit.MILLIS.between(runStart,Instant.now());
                runStart = null;
            }
            IsRunning = false;
        }
    }
}
