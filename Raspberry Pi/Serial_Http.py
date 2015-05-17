# -*- coding: utf-8 -*-
import httplib
import urllib
import serial
import os
import time

##################################
#       Python Defination
##################################
def average(values):
        if len(values) == 0:
                return None

        result = sum(values, 0.0) / len(values)
        return result

def init_HeartAVG(startTime, Time):
        endTime = time.time()
        continueTime = Time
        initHeartValue = [0,1]
        listHeart = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]
        
        for number in listHeart:
        #while (endTime - startTime) <= continueTime:
                #inputSensor = str( SensorValue.readline() )
                #senToken = inputSensor.split(" ")
                #sen0 = senToken[0]
                initHeartValue.append(listHeart[number])
                endTime = time.time()

        # Sort
        initHeartValue.sort()
        listNum = len(init_HeartValue)
        lowerEnd = round( listNum / 10 )
        upperEnd = round( listNum / 90 )
                
        # Sum -> Average
        for number in range(lowerEnd, upperEnd):
                print 'Value = ' + initHeartValue[number]
                sumHeart = initHeartValue[number]

        avgHeart = sumHeart / listNum

        return avgHeart



print('Before PORT CONNECTION')
##################################
#       HTTP POST CONNECTION SETUP
##################################
headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}         # POST Address
conn = httplib.HTTPConnection("alert-height-91305.appspot.com")                                 # POST Connection(http:// -> Remove)


print('Before USB SERIAL CONNECTION')
##################################
#       USB SERIAL CONNECTION SETUP
##################################
Serial_PORT = "/dev/ttyUSB0"                            # Serial Port Address
SensorValue = serial.Serial(Serial_PORT, 9600)          # Serial PORT Value
SensorValue.flushInput()                                # Serial Input


print('Before ALGORITHM')
##################################
#                      ALGORITHM
##################################
while True:
        #print('Before Sensor Read')
        inputSensor = str( SensorValue.readline() )

        start_time = time.time()
        
                
        
        
        ### SensorValue Split
        if " "  in inputSensor :
                senToken = inputSensor.split(" ")
                sen0 = senToken[0]
                sen1 = senToken[1]
                sen2 = senToken[2]
                sen3 = senToken[3]
                sen4 = senToken[4]
                
                print('Before Senser Input')
                params = {'Mode' : 'TouchSensor', 'id' : '1st', 'R1' : sen0, 'R2' : sen1, 'L1' : sen2, 'L2' : sen3}
                params = urllib.urlencode(params)
                conn.request("POST", "/hello", params, headers)
                response = conn.getresponse()
                data = response.read()

                time.sleep(2)
                
                print("Pressure1\tPressure2\tPressure3\tPressure4\tDataState")
                print(sen1 + "\t\t" +  sen2 + "\t\t" +  sen3 + "\t\t" + sen4 + "\t\t" + data)
                #print(sen1 + "\t\t" +  sen2 + "\t\t" +  sen3 + "\t\t" + sen4 + "\t\t" + sen0)


        end_time = time.time()

        print end_time - start_time
        
print('CONNECTION END')         
                
conn.close()
