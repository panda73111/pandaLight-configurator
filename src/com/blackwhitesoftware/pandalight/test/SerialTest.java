package com.blackwhitesoftware.pandalight.test;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by hudini on 05.11.2015.
 */
public class SerialTest {
    private static boolean paused = false;

    public static void main(String[] pArgs) throws SerialPortException {
        try
        {
            final SerialPort serialPort = new SerialPort("COM7");
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            serialPort.setFlowControlMode(
                    SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

            serialPort.setEventsMask(SerialPortEvent.RXCHAR | SerialPortEvent.CTS);
            serialPort.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(SerialPortEvent serialPortEvent) {
                    if (serialPortEvent.isCTS()) {
                        System.out.println("CTS event: " + serialPortEvent.getEventValue());
                        paused = serialPortEvent.getEventValue() == 0;
                        return;
                    }

                    try {
                        System.out.println("new bytes: " + serialPort.readHexString());
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread.sleep(2000);
            System.out.println("sending SYSINFO command");
            serialPort.writeBytes(new byte[]{0x00});
            Thread.sleep(2000);
            System.out.println("sending 400KB bitfile");
            serialPort.writeBytes(new byte[]{
                    0x40,             // command
                    0x00,             // bitfile index
                    0x06, 0x40, 0x00, // bitfile size
            });

            for (int i = 0; i < 400 * 1024; i++) {
                byte data = (byte)(i & 0xFF);
                serialPort.writeByte(data);
            }
            Thread.sleep(2000);

            serialPort.closePort();
        } catch (InterruptedException | SerialPortException e) {
            e.printStackTrace();
        }
    }
}
