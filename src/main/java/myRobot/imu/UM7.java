package myRobot.imu;

import com.pi4j.io.serial.*;
import com.pi4j.util.Console;
import com.sun.org.apache.regexp.internal.recompile;
import java.io.IOException;
import java.util.Date;

public class UM7{
    public final byte DREG_PITCH_ROLL = 0x70;
    public final byte DREG_YAW        = 0x71; 
    
    public final Serial serial = SerialFactory.createInstance();
    public final SerialConfig config = new SerialConfig(); 
    public byte [] receivedBytes = new byte[11];  

    public UM7(){
        // *** Constructor ***//
        serial.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {
                try {
                    receivedBytes = event.getBytes();
                    System.out.println("[HEX DATA]   " + event.getHexByteString());
                    //System.out.println("[ASCII DATA] " + event.getAsciiString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
    }
    
    public void serialOpen(){
        
        config.device("/dev/ttyUSB1")
        .baud(Baud._115200)
        .dataBits(DataBits._8)
        .parity(Parity.NONE)
              .stopBits(StopBits._1)
              .flowControl(FlowControl.NONE);
              
        try {
            serial.open(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public void getPitchRoll(){
        byte [] tx_data = new byte[7];
        int checksum = 0;
        tx_data[0] = 's';
        tx_data[1] = 'n';
        tx_data[2] = 'p';
        tx_data[3] = 0x00;
        tx_data[4] = 0x70;
        for (int i = 0; i<5; i++){
            checksum += tx_data[i];
        }
        tx_data[5] = (byte)(checksum >> 8 & 0xFF);
        tx_data[6] = (byte)(checksum & 0xFF);

        for(int i = 0; i<7; i++){
            try {
                serial.write(tx_data[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }    
    }

    public static void main(String[] args)throws InterruptedException {
        //** Main Loop */
        double roll  = 0;
        double pitch = 0;
        UM7 um = new UM7();
        um.serialOpen();
        while(true){
            um.getPitchRoll();
            // roll = ((um.receivedBytes[5] << 8) | um.receivedBytes[6]) / 91.0222;
            // pitch = ((um.receivedBytes[7] << 8) | um.receivedBytes[8]) / 91.0222;
            // // for(int i =5; i<9; i++){
            // //     System.out.println(um.receivedBytes[i] & 0xFF);
            // // }
            // System.out.println("Roll: " + roll + " Pitch: " + pitch + "\r");
            for(int i =0; i<11; i++){
                System.out.println(um.receivedBytes[i] & 0xFF);
            }
            Thread.sleep(100);
            
        }
        
    }
}