package POMDP_Solver;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class SendToROS {
    public static String SendROSMessage(String msg) throws IOException {
        BufferedReader inFromServer = null;
        OutputStreamWriter osw = null;
        Socket connection = null;
        String responseFromROS = null;
        try {
            InetAddress address = InetAddress.getByName("localhost");
            connection = new Socket(address, 1770);
            System.out.println("RDDL client initialized");

            BufferedOutputStream bos = new BufferedOutputStream(connection.
                    getOutputStream());
            osw = new OutputStreamWriter(bos, "US-ASCII");

            inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            osw.write(msg + '\0');
            osw.flush();
            responseFromROS = inFromServer.readLine();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            connection.close();
            osw.close();
            inFromServer.close();
            return responseFromROS;
        }

    }
}
