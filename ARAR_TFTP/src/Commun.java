import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Brandon on 15/05/2017.
 */
public class Commun {

    public static int firstAvailablePort(int departurePort, int arrivalPort){
        if(departurePort > arrivalPort || departurePort > 65535 || arrivalPort > 65535)
            return 0;
        boolean isUsed;
        DatagramSocket ds = null;
        for(int i = departurePort; i <= arrivalPort; ++i) {
            isUsed = false;
            try {
                ds = new DatagramSocket(i);
            }
            catch(SocketException ex) {
                isUsed = true;
            }
            if(!isUsed) {
                ds.close();
                return i;
            }
        }
        return 0;
    }

}
