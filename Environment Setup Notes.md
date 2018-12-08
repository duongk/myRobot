# Readme.md #

## Requirements on Raspberry Pi ##

Install pi4j

curl -s get.pi4j.com | sudo bash

This will install in /opt/pi4j/lib.

[PI4J Project](http://pi4j.com/index.html)

## Wiringpi Install ##

<http://wiringpi.com/download-and-install/>

## To Compile Java Program ##

Need to install the pi4j libs and include in classpath.  These programs will only run on raspberry pi.  Because no GPIO pins etc on PC/Mac

PI4J libs are installed on Google Drive to sync accross multiple PC/Mac

Set env path:

e.g set PI4J_HOME="G:\Google_Drive\pi4j1_1"

## OSX ##

export PI4J_HOME=/Users/kduong/Google_Drive/pi4j1_1/

In vscode terminal:

javac -classpath %PI4J_HOME%\lib\* -d . HelloWorld.java
javac -cp %PI4J_HOME%\lib\* -d . HelloWorld.java

javac -cp "$PI4J_HOME/libl/*:." -d . HelloWorld.java

if there are spaces in directory path the quotes are needed

javac -cp "%PI4J_HOME%\lib\*" -d . HelloWorld.java

## To Transfer files to Raspberry Pi ##

Right click on file and select "SFTP: Upload"

## To run progams on raspberry PI ##

1. Open another terminal in VC Code
2. ssh pi@192.168.0.137
3. pw: fr0gg3r1
4. java - command to run

> java -Dpi4j.linking=dynamic -classpath '.:classes:*:classes:/opt/pi4j/lib/*' GpioInputExample

In the above, 'GpioInputExample' is the program to run.
> java -Dpi4j.linking=dynamic -classpath '.:classes:*:classes:/opt/pi4j/lib/*' [Program To Run]

## minIMUv9 Address ##

pi@raspberrypi:~ $ i2cdetect -y 1
    0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
00:          -- -- -- -- -- -- -- -- -- -- -- -- --
10: -- -- -- -- -- -- -- -- -- -- -- -- -- -- 1e --
20: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
30: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
40: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
50: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
60: -- -- -- -- -- -- -- -- -- -- -- 6b -- -- -- --
70: -- -- -- -- -- -- -- --
pi@raspberrypi:~ $

## Testing minimuv9 ##

Githu of utility <https://github.com/DavidEGrayson/minimu9-ahrs>

Run 'minimu9-ahrs --mode raw' will wake up imu.  Noticed that after a few days imu goes to sleep.  Need to determine commands to way/power up

## PI4J version 1.2 ##

Need to install the 1.2 SNAPSHOT version of the PI4J libraries for the exmaples show on GIThub.

   90  cd Downloads/
   91  wget <http://get.pi4j.com/download/pi4j-1.2-SNAPSHOT.deb>
   92  sudo dpkg -i pi4j-1.2-SNAPSHOT.deb

Download and extracted the SNAPSHOT version to the GoogleDrive dir.

NOTE:  left the home dir pointed to "G:\Google_Drive\pi4j1_1" just overwrote the lib directory with newer 1.2 jar files.

## RoboClaw Settings In EEPROM ##

Control Mode - Packet Serial
PWM Mode - signed magnitude

Serial - Address 128
Serial - Baud 38400

Motor 1 P: 5395.02784
Motor 1 I: 289.13957
Motor 1 D: 0.00
QPPS: 10750

Motor 2 P: 4451.61752
Motor 2 I: 209.16047
Motor 2 D: 0.00
QPPS: 11074

## Good Online CRC Calculator for RoboClaw ##

<http://forums.ionmc.com/viewtopic.php?t=174>
<>
<https://www.lammertbies.nl/comm/info/crc-calculation.html>
Roboclaw uses CRC16_XMODEM

Polynomial = 0x1021