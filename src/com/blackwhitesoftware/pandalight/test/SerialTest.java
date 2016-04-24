package com.blackwhitesoftware.pandalight.test;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by hudini on 05.11.2015.
 */
public class SerialTest {
    public static void main(String[] pArgs) throws SerialPortException {
        try

        {
            final SerialPort serialPort = new SerialPort("COM24");
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            serialPort.setFlowControlMode(
                    SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

            serialPort.setEventsMask(SerialPortEvent.RXCHAR);
            serialPort.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(SerialPortEvent serialPortEvent) {
                    try {
                        System.out.println("new bytes: " + serialPort.readHexString());
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread.sleep(2000);
            System.out.println("sending SYSINFO command");
            serialPort.writeBytes(new byte[]{0x65, 0x00, 0x00, 0x00, 0x65});
            Thread.sleep(500);
            System.out.println("sending acknowledge for packet 0x00");
            serialPort.writeBytes(new byte[]{0x66, 0x00, 0x66});
            Thread.sleep(2000);

            serialPort.closePort();
        } catch (InterruptedException | SerialPortException e) {
            e.printStackTrace();
        }
    }
}
