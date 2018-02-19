package sample.uart;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.xmodem.Xmodem;

import java.net.PortUnreachableException;

public class Uart {
    private final static int BAUD_RATE = 115200;
    private final static int NUMBER_DATA_BITS = 8;
    private final static int NUMBER_STOP_BITS = 1;
    private final static int NUMBER_PARITY_BITS = 0;

    private ObservableList<SerialPort> portListString = FXCollections.observableArrayList();
    private SerialPort port;

    public Uart() {
        for (SerialPort port : SerialPort.getCommPorts()) {
//            if (port.getDescriptivePortName().matches(".*AT Port.*"))
                portListString.add(port);
        }
        if (Xmodem.DEBUG) System.out.println("end of searching uart ports");
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
        if (Xmodem.DEBUG) System.out.println("port is opened: " + port.getDescriptivePortName());
    }

    public ObservableList<SerialPort> getPortListString() {
        return portListString;
    }
}
