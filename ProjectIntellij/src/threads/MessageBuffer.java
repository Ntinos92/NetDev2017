package threads;

import java.util.ArrayList;
import java.util.List;

public class MessageBuffer {
    static List<String> Entoles2 = null;
    static int end = 1;
    static double statistics = 0.0;
    static double statsSuccess = 0.0;
    static double statsCounter = 0.0;
    public MessageBuffer(){
        MessageBuffer.Entoles2 = new ArrayList<>();
    }
}
