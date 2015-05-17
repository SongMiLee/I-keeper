import socket
import serial
import os


### Variable
Server_HOST = '104.155.212.106'		# Server IP
Server_PORT = 9999			# Server PORT
Serial_PORT = "/dev/ttyUSB0"		# Serial PORT
Server_Socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
SensorValue = serial.Serial(Serial_PORT, 9600)
SensorValue.flushInput()

print('Client Start')


#Server_Socket.connect(('104.155.212.106',9998))
Server_Socket.connect((Server_HOST, Server_PORT))
print('Socket Connect')


### Server Send Message
# \n -> Python String
print('Before Message')

while True:
	inputSensor = str( SensorValue.readline() )

	### SensorValue Split
	if ","  in inputSensor :
		senToken = inputSensor.split(",")
		sen0 = senToken[0]
		sen1 = senToken[1]
		sen2 = senToken[2]
		sen3 = senToken[3]
		sendSensor = '_touchsensor=' + sen0 + '=' + sen1 + '=' + sen2 + '=' + sen3 + '\n'
		Server_Socket.send(sendSensor)
		os.system('clear')
		print("Pressure1\tPressure2\tPressure3\tVarSensor\t")
		print(sen0 + "\t\t" +  sen1 + "\t\t" +  sen2 + "\t\t" + sen3)

#	print("INPUT\t" + inputSensor)
#	print("")


	#print("Received\t", recive_data)
	

#print('After Message')


#print('Before Data')
#recive_data = Server_Socket.recv(1024)
#print('After Data')

Server_Socket.close()

#print('Received', repr(recive_data))
