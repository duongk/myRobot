package myRobot.imu;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import com.pi4j.platform.PlatformAlreadyAssignedException;

/**
 * IMU600 Class
 * 
 * Public Data: accelX, accelY, accelZ, gyroX, gyroY, gyroZ
 * 
 * Public Method: readIMU - will update public data with current readings.
 * 
 * Update IMU_ADDR as appropriate - may want to change as construtor argument
 * 
 */

public class IMU6001{
    private static final byte IMU_ADDR            = 0x68; // 6050 Address
    private static final byte INT_STATUS          = 0x3A;
    private static final byte GYRO_CONFIG         = 0x1B;
    private static final byte ACCEL_CONFIG        = 0x1C;
    private static final byte INT_ENABLE          = 0x38;
    private static final byte ACCEL_XOUT_H        = 0x3B;
    private static final byte ACCEL_XOUT_L        = 0x3C;
    private static final byte ACCEL_YOUT_H        = 0x3D;
    private static final byte ACCEL_YOUT_L        = 0x3E;
    private static final byte ACCEL_ZOUT_H        = 0x3F;
    private static final byte ACCEL_ZOUT_L        = 0x40;
    private static final byte TEMP_OUT_H          = 0x41;
    private static final byte TEMP_OUT_L          = 0x42;
    private static final byte GYRO_XOUT_H         = 0x43;
    private static final byte GYRO_XOUT_L         = 0x44;
    private static final byte GYRO_YOUT_H         = 0x45;
    private static final byte GYRO_YOUT_L         = 0x46;
    private static final byte GYRO_ZOUT_H         = 0x47;
    private static final byte GYRO_ZOUT_L         = 0x48;
    private static final byte PWR_MGMT_1          = 0x6B; // Device defaults to the SLEEP mode
    private static final byte ACCEL_FS            = 0x10; //FS 2g, 4g, 8g, 16g : 0x00, 0x08, 0x10, 0x18
    private static final byte GYRO_FS             = 0x00; // FS 250, 500, 1000, 2000 : 0x00, 0x08, 0x10, 0x18
    private static final int ACCEL_SENS           = 4096; // FS 2g, 4g, 8g, 16g : 16384, 8192, 4096, 2048
    private static final float GYRO_SENS          = 65.5f; // FS 250, 500, 1000, 2000 : 131, 65.5, 32.8, 16.4
    private static final float DT                 = 0.004f;
    
    private static int arrayPointer    = 0;
    private float[] accXMedian = new float[20];
    private float[] accYMedian = new float[20];
    private float[] accZMedian = new float[20];
    private int accelOffsetX = 0;
    private int accelOffsetY = 0;
    private int accelOffsetZ = 0;
    private int gyroOffsetX  = 0;
    private int gyroOffsetY  = 0;
    private int gyroOffsetZ  = 0;

    private static I2CBus i2c = null;
    private static I2CDevice device = null;   

    public float accelX = 0;
    public float accelY = 0;
    public float accelZ = 0;
    public float gyroX  = 0;
    public float gyroY  = 0;
    public float gyroZ  = 0;

    public IMU6001() throws InterruptedException{  
        /** Constructor - vaeriable to use once instance is created */
        try {
            i2c = I2CFactory.getInstance(I2CBus.BUS_1);
            device = i2c.getDevice(IMU_ADDR);
            device.write(PWR_MGMT_1, (byte)0x00); // Power up IMU
            device.write(ACCEL_CONFIG, ACCEL_FS); 
            device.write(GYRO_CONFIG, GYRO_FS);
            device.write(INT_ENABLE, (byte)0x01); // Enable Interupt - 1 when sensor data is ready
        } catch (IOException | UnsupportedBusNumberException e) {
            System.out.println("Caught Exception: " + e.toString());
        }
        getOffset();

    }
    
    private static void bubbleSort(float[] arr){
        /** Sort Array */
        int n = arr.length;
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (arr[j] > arr[j+1])
                {
                    // swap temp and arr[i]
                    float temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
    }

    private static int[] convertoInt(byte[] data){
        int l      = data.length;
        int[] axis = new int[l/2];
        for(int i=0; i<l/2; i++){
            axis[i] = (int)(data[i*2] << 8 | data[(i*2)+1]);
        }
        return axis;
    }

    private void readIMU(){
        /** Reads IMU Registers for RAW gyro and accelerometer values */
        byte[] data     = new byte[14];
        int[] rtrnData  = new int[7];
        Boolean notRDY  = true;
        int statusReg = 0;
        try{
            while(notRDY) {
                statusReg = device.read(INT_STATUS);
                // System.out.println(statusReg);
                if(statusReg==0x01){
                    /** Start at ACCEL_XOUT_H and read 14 bytes */
                    device.read(ACCEL_XOUT_H, data, 0, 14); 
                    notRDY = false;
                } else {
                    Thread.sleep(1);
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Caught Exception: " + e.toString());
        }
        rtrnData = convertoInt(data);

        accelX  = (float)(rtrnData[0]);
        accelY  = (float)(rtrnData[1]);
        accelZ  = (float)(rtrnData[2]);
        gyroX   = (float)(rtrnData[4]);
        gyroY   = (float)(rtrnData[5]);
        gyroZ   = (float)(rtrnData[6]);

    }

    private void getOffset() throws InterruptedException{
        /** Caculate offset based on average of samples */
        int samples = 2000;
        for (int i=0; i< samples; i++){
            long lStartTime = System.currentTimeMillis();
            readIMU();
            accelOffsetX += accelX;
            accelOffsetY += accelY;
            accelOffsetZ += accelZ;
            gyroOffsetX  += gyroX;
            gyroOffsetY  += gyroY;
            gyroOffsetZ  += gyroZ;
            long lEndTime = System.currentTimeMillis();
            while((lEndTime - lStartTime) < (DT*1000)){
                Thread.sleep(1);
                lEndTime = System.currentTimeMillis();
            }
        }

        accelOffsetX /= samples;
        accelOffsetY /= samples;
        accelOffsetZ /= samples;
        gyroOffsetX  /= samples;
        gyroOffsetY  /= samples;
        gyroOffsetZ  /= samples;

    }

    private void getIMUValues(){
        /** Median filter for acceleration values */
        int samples = 20;
        readIMU();
        if (arrayPointer < samples){
            accXMedian [arrayPointer] = accelX; // Load Raw X Acceleration Value
            accYMedian [arrayPointer] = accelY; // Load Raw Y Acceleration Value
            accZMedian [arrayPointer] = accelZ; // Load Raw Z Acceleration Value
            arrayPointer += 1;
        } else {
            arrayPointer = 0;                   // Start at Beginning
            accXMedian [arrayPointer] = accelX;
            accYMedian [arrayPointer] = accelY;
            accZMedian [arrayPointer] = accelZ;
            arrayPointer += 1;
        }

        bubbleSort(accXMedian);
        bubbleSort(accYMedian);
        bubbleSort(accZMedian);

        gyroX -= gyroOffsetX;
        gyroY -= gyroOffsetY;
        gyroZ -= gyroOffsetZ;

        accelX = accXMedian[samples/2];
        accelY = accYMedian[samples/2];
        accelZ = accZMedian[samples/2];

        if(accelX > ACCEL_SENS)  { accelX = ACCEL_SENS; }
        if(accelX < -ACCEL_SENS) { accelX = -1*ACCEL_SENS;}
        // accelX -= accelOffsetX;
        // accelY -= accelOffsetY;
        // accelZ -= accelOffsetZ;


    }




    public static void main(String[] args) throws InterruptedException{
        boolean set_gyro_angles = false;
        float PI = 3.142f;
        double atanValue = 0;
        double acc_pitch = 0;
        double acc_roll = 0;
        double gyro_pitch = 0;
        double pitch_output = 0;
        double roll_output = 0;
        double pitch = 0;
        double roll = 0;
        IMU6001 imu = new IMU6001();
        while(true){
            long lStartTime = System.currentTimeMillis();
            imu.getIMUValues();
            atanValue = Math.atan2(imu.accelX, imu.accelZ)*57.29578;  // pitch - revolve around the Y axis
            acc_pitch  = Math.asin(imu.accelX / ACCEL_SENS) * 57.29578;
            gyro_pitch += (imu.gyroY * DT / GYRO_SENS );
            pitch_output = .98 * gyro_pitch + .02 * acc_pitch;
           
       
            System.out.print(gyro_pitch + ",  " + acc_pitch + ",  " + pitch_output + ", " +
                             imu.accelX + ",    " + imu.accelY + ",    " + imu.accelZ + ",    " +
                             imu.gyroX + ",    " + imu.gyroY + ",    " + imu.gyroZ + ",  " +
                             "\r");
            long lEndTime = System.currentTimeMillis();
            while((lEndTime - lStartTime) < (DT*1000)){
                Thread.sleep(1);
                lEndTime = System.currentTimeMillis();
            }
        }
    }
}



