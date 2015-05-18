import serial
import os

port = "/dev/ttyUSB0"
serialFromArduino = serial.Serial(port, 9600)
serialFromArduino.flushInput()

while True:
        input_s = serialFromArduino.readline()
        input = str(input_s)
        os.system('clear')
	print(input) 
