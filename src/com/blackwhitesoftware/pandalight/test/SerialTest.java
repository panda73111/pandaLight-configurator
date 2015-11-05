package com.blackwhitesoftware.pandalight.test;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Formatter;
import java.util.TooManyListenersException;

/**
 * Created by hudini on 05.11.2015.
 */
public class SerialTest {
    public static void main(String[] pArgs) throws TooManyListenersException {
        try

        {
            CommPortIdentifier identifier = CommPortIdentifier.getPortIdentifier("COM24");
            CommPort port = identifier.open("SerialTest", 10);
            if (!(port instanceof SerialPort)) {
                System.out.println("the target device is no serial port");
            }

            SerialPort serialPort = (SerialPort) port;
            serialPort.setSerialPortParams(
                    115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            InputStream in = serialPort.getInputStream();
            OutputStream out = serialPort.getOutputStream();

            serialPort.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(SerialPortEvent serialPortEvent) {
                    System.out.println("Event: " + serialPortEvent + " " + serialPortEvent.getEventType());
                }
            });
            serialPort.notifyOnDataAvailable(true);
            serialPort.notifyOnBreakInterrupt(true);
            serialPort.notifyOnCarrierDetect(true);
            serialPort.notifyOnCTS(true);
            serialPort.notifyOnDSR(true);
            serialPort.notifyOnFramingError(true);
            serialPort.notifyOnOutputEmpty(true);
            serialPort.notifyOnOverrunError(true);
            serialPort.notifyOnParityError(true);
            serialPort.notifyOnRingIndicator(true);

            out.write(new byte[] {0x65, 0x00, 0x00, 0x00, 0x65});
            out.flush();

            byte[] buffer = new byte[16];
            int readCount = in.read(buffer);
            System.out.println("read " + readCount + " bytes");
            System.out.println(bytesToHex(buffer));

            Thread.sleep(1000);

            serialPort.close();
        } catch (NoSuchPortException | PortInUseException | IOException | UnsupportedCommOperationException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, 0, bytes.length);
    }

    private static String bytesToHex(byte[] bytes, int offset, int length) {
        Formatter formatter = new Formatter();
        for (int i = offset; i < length; i++) {
            formatter.format("%02x", bytes[i]);
        }
        return formatter.toString();
    }
}
