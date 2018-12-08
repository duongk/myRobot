package myRobot.roboclaw;

import myRobot.utilities.crc16;
import com.pi4j.io.serial.*;
import com.pi4j.util.Console;
import java.io.IOException;
import java.util.Date;

/**
 * A Class to interface to the RoboClaw Motor controller from BasicMicro
 * using packet serial interface/commands.
 * 
 * Utilizes PI4J java libraries for IO
 * 
 * Author: Kiet Duong
 */

public class RoboClaw{
    /** Specific to device on serial bus */
    // public static final Console console = new Console();
    public final Serial serial = SerialFactory.createInstance();
    public SerialConfig config;

    public RoboClaw(){
        /**Construtor */
        // console.title("RoboClaw Interface");
        // console.promptForExit();
        serial.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                try {
                    event.getHexByteString();
                    // console.println("[HEX DATA]   " + event.getHexByteString());
                    // console.println("[ASCII DATA] " + event.getAsciiString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public byte[] loadArray(int var){
        byte[] temp = new byte[4];
        temp[3] = (byte)(var>>24 & 0xFF);
        temp[2] = (byte)(var>>16 & 0xFF);
        temp[1] = (byte)(var>>8 & 0xFF);
        temp[0] = (byte)(var & 0xFF);
        return temp;
    }
    
    public void serialOpen(){
        // create serial config object
        SerialConfig config = new SerialConfig();
        // set default serial settings (device, baud rate, flow control, etc)
        //
        // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
        // NOTE:"/dev/ttyAMA0"
        //      "/dev/ttyS0" - Raspberry Pi 3B+
        //      "/dev/ttyACM0" - USBPort may depend on port connected to
        config.device("/dev/ttyS0")
                .baud(Baud._38400)
                .dataBits(DataBits._8)
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE);

        // display connection details
        //console.box(" Connecting to: " + config.toString(),
                // " We are sending ASCII data on the serial port every 1 second.",
                // " Data received on serial port will be displayed below.");
        // open the default serial device/port with the configuration settings
        try {
            serial.open(config);
        } catch (IOException ex) {
            ex.getMessage();
            //console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        }
    }

    public void serialShutDown(){
        try{
            serial.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void readFirmware(int devAddr){
        try{
            serial.write((byte)(devAddr & 0xFF));
            serial.write((byte)(21 & 0xFF));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void m1Forward(int devAddr, int speed){
        /**
         * SEND: [Address, 0, Speed (1 Byte), CRC16 (2 Byte)]
         * RECEIVE: [0xFF]
         * Speed is 0 - 127
         * 
         */
        if(speed < 128){
        crc16 crcValue = new crc16();
            try{
                serial.write((byte)(devAddr & 0xFF));
                crcValue.crc_update((byte)(devAddr & 0xFF));
                serial.write((byte)(0 & 0xFF));
                crcValue.crc_update((byte)(0 & 0xFF));
                serial.write((byte)(speed & 0xFF));
                crcValue.crc_update((byte)(speed & 0xFF));
                int crc_msb = crcValue.crc >> 8;
                int crc_lsb = crcValue.crc & 0xFF;
                serial.write((byte)crc_msb);
                serial.write((byte)crc_lsb);
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public void m1Backward(int devAddr, int speed){
        /** 
         * SEND: [Address, 1, Speed (1 Byte), CRC16 (2 Byte)]
         * RECEIVE: [0xFF]
         * Speed is 0 - 127
         */

        if(speed < 128){
            crc16 crcValue = new crc16();
                try{
                    serial.write((byte)(devAddr & 0xFF));
                    crcValue.crc_update((byte)(devAddr & 0xFF));
                    serial.write((byte)(1 & 0xFF));
                    crcValue.crc_update((byte)(1 & 0xFF));
                    serial.write((byte)(speed & 0xFF));
                    crcValue.crc_update((byte)(speed & 0xFF));
                    int crc_msb = crcValue.crc >> 8;
                    int crc_lsb = crcValue.crc & 0xFF;
                    serial.write((byte)crc_msb);
                    serial.write((byte)crc_lsb);
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
    }

    public void m2Forward(int devAddr, int speed){
        /** 
         * SEND: [Address, 4, Speed (1 Byte), CRC16 (2 Byte)]
         * RECEIVE: [0xFF]
         * Speed is 0 - 127
         */
        if(speed < 128){
            crc16 crcValue = new crc16();
                try{
                    serial.write((byte)(devAddr & 0xFF));
                    crcValue.crc_update((byte)(devAddr & 0xFF));
                    serial.write((byte)(4 & 0xFF));
                    crcValue.crc_update((byte)(4 & 0xFF));
                    serial.write((byte)(speed & 0xFF));
                    crcValue.crc_update((byte)(speed & 0xFF));
                    int crc_msb = crcValue.crc >> 8;
                    int crc_lsb = crcValue.crc & 0xFF;
                    serial.write((byte)crc_msb);
                    serial.write((byte)crc_lsb);
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
    }

    public void m2Backward(int devAddr, int speed){
        /** 
         * SEND: [Address, 5, Speed (1 Byte), CRC16 (2 Byte)]
         * RECEIVE: [0xFF]
         * Speed is 0 - 127
         */
        if(speed < 128){
            crc16 crcValue = new crc16();
                try{
                    serial.write((byte)(devAddr & 0xFF));
                    crcValue.crc_update((byte)(devAddr & 0xFF));
                    serial.write((byte)(5 & 0xFF));
                    crcValue.crc_update((byte)(5 & 0xFF));
                    serial.write((byte)(speed & 0xFF));
                    crcValue.crc_update((byte)(speed & 0xFF));
                    int crc_msb = crcValue.crc >> 8;
                    int crc_lsb = crcValue.crc & 0xFF;
                    serial.write((byte)crc_msb);
                    serial.write((byte)crc_lsb);
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
    }

    public void m1SignedSpeed(int devAddr, int speed){
        /**
         * SEND: [ Address, 35, Speed(4 Bytes), CRC(2 Bytes)]
         * RECEIVE: [0xFF]
         * 
         */
        crc16 crcValue = new crc16();
        try{
            serial.write((byte)(devAddr & 0xFF));
            crcValue.crc_update((byte)(devAddr & 0xFF));
            serial.write((byte)35);
            crcValue.crc_update((byte)35);
            byte[] s = new byte[4];
            s = loadArray(speed);
            for (int i=3; i>=0; i--){
                serial.write(s[i]);
                crcValue.crc_update(s[i]);
            }
            int crc_msb = crcValue.crc >> 8;
            int crc_lsb = crcValue.crc & 0xFF;
            serial.write((byte)crc_msb);
            serial.write((byte)crc_lsb);
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }

    public void m2SignedSpeed(int devAddr, int speed){
        /**
         * SEND: [ Address, 36, Speed(4 Bytes), CRC(2 Bytes)]
         * RECEIVE: [0xFF]
         */
        crc16 crcValue = new crc16();
        try{
            serial.write((byte)(devAddr & 0xFF));
            crcValue.crc_update((byte)(devAddr & 0xFF));
            serial.write((byte)35);
            crcValue.crc_update((byte)36);
            byte[] s = new byte[4];
            s = loadArray(speed);
            for (int i=3; i>=0; i--){
                serial.write(s[i]);
                crcValue.crc_update(s[i]);
            }
            int crc_msb = crcValue.crc >> 8;
            int crc_lsb = crcValue.crc & 0xFF;
            serial.write((byte)crc_msb);
            serial.write((byte)crc_lsb);
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }

    public void m1m2SignedSpeed(int devAddr, int m1speed, int m2speed){
        /**
         * SEND: [ Address, 37, Speed(4 Bytes), CRC(2 Bytes)]
         * RECEIVE: [0xFF]
         */
        crc16 crcValue = new crc16();
        try{
            serial.write((byte)(devAddr & 0xFF));
            crcValue.crc_update((byte)(devAddr & 0xFF));
            serial.write((byte)37);
            crcValue.crc_update((byte)37);
            byte[] s = new byte[4];
            s = loadArray(m1speed);
            for (int i=3; i>=0; i--){
                serial.write(s[i]);
                crcValue.crc_update(s[i]);
            }
            s = loadArray(m2speed);
            for (int i=3; i>=0; i--){
                serial.write(s[i]);
                crcValue.crc_update(s[i]);
            }
            int crc_msb = crcValue.crc >> 8;
            int crc_lsb = crcValue.crc & 0xFF;
            serial.write((byte)crc_msb);
            serial.write((byte)crc_lsb);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        RoboClaw rc = new RoboClaw();
        rc.serialOpen();
        rc.readFirmware(128);
        //     Thread.sleep(1000);
        // rc.m1Forward(128, 127);
        //     Thread.sleep(1000);
        //rc.m1Forward(128, 0);
        // rc.driveForward(128, 64000);
        // rc.m1m2SignedSpeed(128, 64000, 64000);
        // Thread.sleep(10000);
        // rc.m1m2SignedSpeed(128, -64000, -64000);
        // Thread.sleep(1000);
        ;
        Thread.sleep(1000);
        // while(console.isRunning()){
        //     Thread.sleep(1000);
        // }
    }
}