package sample.xmodem.commands;


import java.text.SimpleDateFormat;
import java.util.Date;

public enum Commands implements MakeCommandInt{
    SET_ALL {
        @Override
        public String getCommand() {
            return "AT+SET_OP=";
        }

        @Override
        public boolean isValid() {
            return false;
        }
    },
    SET_FREQ {
        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public String getCommand() {
            return "AT+3G_FREQS_ADD=";
        }
    },
    READ_FREQ {
        @Override
        public String getCommand() {
            return "AT+3G_FREQS_GET";
        }

        @Override
        public boolean isValid() {
            return false;
        }
    },
    READ_MAIN {
        @Override
        public String getCommand() {
            return "AT+GET_OP";
        }

        @Override
        public boolean isValid() {
            return false;
        }
    },
    REBOOT {
        @Override
        public String getCommand() {
            return "AT+REBOOT";
        }

        @Override
        public boolean isValid() {
            return false;
        }
    },
    SET_CLOCK {
        @Override
        public String getCommand() {
            SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd,HH:mm:ss");
            Date date = new Date();
            String stringTime = formatter.format(date);
            return "AT+CCLK=" + "\"" + stringTime + "\"";
        }

        @Override
        public boolean isValid() {
            return false;
        }
    },
    REFRESH {
        @Override
        public String getCommand() {
            return "AT+GET_OP=REFRESH";
        }

        @Override
        public boolean isValid() {
            return false;
        }
    },
    DEL_FREQ {
        @Override
        public String getCommand() {
            return "AT+3G_FREQS_DEL=1";
        }

        @Override
        public boolean isValid() {
            return false;
        }
    };
}
