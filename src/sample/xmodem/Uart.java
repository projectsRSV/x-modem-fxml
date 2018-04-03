package sample.xmodem;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.net.PortUnreachableException;
import java.util.logging.Logger;

public class Uart {
    private final static Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final static int BAUD_RATE = 115200;
    private final static int NUMBER_DATA_BITS = 8;
    private final static int NUMBER_STOP_BITS = 1;
    private final static int NUMBER_PARITY_BITS = 0;

    private ObservableList<SerialPort> portListString = FXCollections.observableArrayList();
    private SerialPort port;

    public Uart() {
        for (SerialPort port : SerialPort.getCommPorts()) {
//            if (port.getDescriptivePortName().matches(".*AT Port 9000.*") ||
//                    port.getDescriptivePortName().matches(".*Modem 9000.*"))
            if (port.getDescriptivePortName().contains("AT Port 9000") ||
                    port.getDescriptivePortName().contains("Modem 9000"))
                portListString.add(port);
        }
        LOGGER.info("find ports: " + portListString.size());
    }

    public void setPort(SerialPort port) {
        this.port = port;
    }

    public SerialPort getPort() {
        return port;
    }

    public void openPortDevice() throws PortUnreachableException {
        port.openPort();
        if (!port.isOpen()) throw new PortUnreachableException();
        port.setComPortParameters(BAUD_RATE, NUMBER_DATA_BITS, NUMBER_STOP_BITS, NUMBER_PARITY_BITS);
        LOGGER.info("port is opened: " + port.getDescriptivePortName());
    }

    public ObservableList<SerialPort> getPortListString() {
        return portListString;
    }
}
