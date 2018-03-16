package sample.command;


import sample.xmodem.Xmodem;

import java.net.PortUnreachableException;

public class SendCommand {

    public static void send(MakeCommandInt makeCommandInt) throws PortUnreachableException {
//        Xmodem.read(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 5120);
        Xmodem.sendCommand(makeCommandInt.getCommand());
    }
}
