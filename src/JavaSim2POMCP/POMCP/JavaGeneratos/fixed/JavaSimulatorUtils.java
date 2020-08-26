package JavaSim2POMCP.POMCP.JavaGeneratos.fixed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSimulatorUtils {
    public static String InsertValueToObservation(String currentObservation, String observationVariableName, String value)
    {
        if(value.equals("") || value.equals("-1")) return currentObservation;

        switch (value)
        {
            case "true":
                value = "1";
                break;
            case "false":
                value = "0";
                break;
            case "invalid":
                value = "true";
                break;
            case "success_false":
                value = "1";
                break;
            case "success_true":
                value = "2";
                break;
            default:break;
        }
        Pattern p = Pattern.compile("obsrv_"+observationVariableName);
        Matcher m = p.matcher(currentObservation );
        m.find();
        String second  = currentObservation.substring(m.start()).replaceFirst("0",value);
        String result = currentObservation.substring(0, m.start()) + second;
        return result;
    }
}
