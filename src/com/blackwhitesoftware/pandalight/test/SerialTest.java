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
            final SerialPort serialPort = new SerialPort("COM24");
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
            serialPort.writeBytes(new byte[]{0x65, 0x00, 0x00, 0x00, 0x65});
            Thread.sleep(500);
            System.out.println("sending acknowledge for packet 0x00");
            serialPort.writeBytes(new byte[]{0x66, 0x00, 0x66});
            Thread.sleep(2000);
            System.out.println("sending 100KB bitfile");
            serialPort.writeBytes(new byte[]{
                    0x65, 0x01,
                    0x04,                    // payload length
                    0x40,                    // command
                    0x00,                    // bitfile index
                    0x01, (byte) 0x90, 0x00, // bitfile size
                    (byte) 0x3B              // checksum
            });

            for (int packetNumber = 2; packetNumber < 2 + 400; packetNumber++) {
                if (paused) {
                    System.out.println("paused sending");
                    while (paused)
                        Thread.sleep(100);
                    System.out.println("resumed sending");
                }

                System.out.println("sending packet " + packetNumber);
                int checksum = 0x65 + packetNumber + 0xFF;
                serialPort.writeBytes(new byte[]{0x65, (byte) (packetNumber & 0xFF), (byte) 0xFF});

                for (int i = 0; i < 256; i++) {
                    byte data = (byte) (((packetNumber - 2) & 0x07) << 5);
                    data |= i & 0x1F;

                    // 3 bit packet number + 5 bit counter
                    serialPort.writeByte(data);
                    checksum += data;
                }

                serialPort.writeByte((byte) (checksum & 0xFF));
            }
            Thread.sleep(2000);

            serialPort.closePort();
        } catch (InterruptedException | SerialPortException e) {
            e.printStackTrace();
        }
    }
}
