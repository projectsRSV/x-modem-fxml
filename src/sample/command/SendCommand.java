package sample.command;


import sample.xmodem.Xmodem;

import java.lang.invoke.MethodHandles;
import java.net.PortUnreachableException;
import java.util.logging.Logger;

public class SendCommand {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static void send(String makeCommandInt) throws PortUnreachableException {
        LOGGER.info("send command: " + makeCommandInt);
        Xmodem.sendCommand(makeCommandInt);
    }
}
