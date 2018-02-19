package sample.xmodem.commands;


import sample.xmodem.Xmodem;

import java.net.PortUnreachableException;

public class SendCommand {

    public void send(MakeCommandInt makeCommandInt) throws PortUnreachableException {
//        try {
            Xmodem.sendCommandUart(makeCommandInt.getCommand());
//                if (Xmodem.DEBUG) System.out.println("in setThread: " + makeCommandInt.getCommand());
//        } catch (PortUnreachableException e) {
//            if (Xmodem.DEBUG) System.out.println("portUnreachableException");
//            throw new PortUnreachableException();
//        } catch (NullPointerException e) {
//            if (Xmodem.DEBUG) System.out.println("nullPointerException");
//        }
    }
}
