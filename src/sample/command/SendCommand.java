package sample.command;


import sample.xmodem.Xmodem;

import java.lang.invoke.MethodHandles;
import java.net.PortUnreachableException;
import java.util.logging.Logger;

public class SendCommand {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static void send(MakeCommandInt makeCommandInt) throws PortUnreachableException {
        LOGGER.info("send command: " + makeCommandInt.getCommand());
        Xmodem.sendCommand(makeCommandInt.getCommand());
    }
}
