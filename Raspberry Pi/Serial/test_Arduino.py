import socket
import serial
import os


### Variable
#Server_HOST = '104.155.212.106'		# Server IP
#Server_PORT = 9999			# Server PORT
Serial_PORT = "/dev/ttyUSB0"		# Serial PORT
#Server_Socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
SensorValue = serial.Serial(Serial_PORT, 9600)
SensorValue.flushInput()

print('Arduino Start')


#Server_Socket.connect(('104.155.212.106',9998))
#Server_Socket.connect((Server_HOST, Server_PORT))
#print('Socket Connect')


### Server Send Message
# \n -> Python String
#print('Before Message')

while True:
	inputSensor = str( SensorValue.readline() )
	sendSensor = "NULL"

	### SensorValue Slice
	if ","  in inputSensor :
		senToken = inputSensor.split(",")
		sen0 = senToken[0]
		sen1 = senToken[1]
		sen2 = senToken[2]
		sen3 = senToken[3]
		sendSensor = '_touchsensor=' + sen0 + '=' + sen1 + '=' + sen2 + '=' + sen3 + '\n'

	os.system('clear')
#	Server_Socket.send(sendSensor)
	print("SEND\t" + sendSensor + "\tINPUT\t" + inputSensor, end="\t")
	print("")
	#recive_data = Server_Socket.recv(1024)
	#print("Received\t", recive_data)
	

#print('After Message')


#print('Before Data')
#recive_data = Server_Socket.recv(1024)
#print('After Data')

#Server_Socket.close()

#print('Received', repr(recive_data))
