package myRobot.utilities;

/*
 * CRC16-XMODEM Class 
 * Author: Kiet Duong
 * This class is to calculate the CRC16 for sending to RoboClaw 
 * motor controller
 * 
 * References: http://forums.ionmc.com/viewtopic.php?t=174
 * 
 * See main for usage:
 * 
 * */

public class crc16{

    public int crc;

    public crc16(){
        // Constructor
        crc = 0;
    }

    public void crc_clear(){
        crc = 0;
    }

    public void crc_update(int data){
        int i;
        // Converts to unsigned int
        data = data & 0xFFFF;
        crc = crc ^((short)data << 8);
        for ( i = 0; i < 8; i++){
            if ((crc & 0x8000) != 0){
                crc = (crc << 1) ^ 0x1021;
            } else {
                crc = crc << 1;
            }
        }
        // Convert to unsigned int
        crc = crc & 0xFFFF;
    }
 
    public static void main(String[] args) {
        //CRC of 0x80 0x00 0x20 should be 1F 38
        crc16 test = new crc16();
        test.crc_clear();
        System.out.println(test.crc);
        test.crc_update(0x80);
        System.out.println(test.crc);
        test.crc_update(0x00);
        System.out.println(test.crc);
        test.crc_update(0x20);
        System.out.println(test.crc);
        System.out.println("CRC MSB: " + Integer.toHexString(test.crc >> 8));
        System.out.println("CRC LSB: " + Integer.toHexString(test.crc & 0xFF));
     }
}