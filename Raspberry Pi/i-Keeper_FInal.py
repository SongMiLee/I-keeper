### HTTP POST CONNECTION
import httplib
import urllib

### ARDUINO SERIAL CONNECTION
import serial
import os

### VIOLENCE ALGORITHM
import time


### FUCTION
##################################
#   init_HeartAVG
##################################
def init_HeartAVG(countThr, lowerThr, upperThr):
	### Serial PORT OPEN
	while True :
		try :
			Serial_PORT = "/dev/ttyUSB0"                            # Serial Port Address
			SensorValue = serial.Serial(Serial_PORT, 9600)          # Serial PORT Value
			if SensorValue != None :
				SensorValue.flushInput()                            # Serial Input
				break
		except OSError :
			print "USB SERIAL ERROR"
			print "Input Your Serial!!"
			time.sleep(5)
			continue
	print "Serial OPEN"


	### Measure
	print '------ Measure ------'
	print 'Writing',
	count = 0
	countHeartThr = countThr
	initHeartValue = []
	while count < countHeartThr:			
		inputSensor = str( SensorValue.readline() )
		if " " in inputSensor :
			senToken = inputSensor.split(" ")
			sen0 = senToken[0]

			if (sen0 != "") and (sen0 != "0") :
				count = count + 1
				initHeartValue.append(sen0)
				print sen0,
				print  '[', count, ']', '.',
			else:
				print "ZERO or NONE"
		

	### Sort
	initHeartValue.sort()

	### Sort Threshold
	listNum = len(initHeartValue) 
	lowerEnd = int( round( listNum * lowerThr ) )
	upperEnd = int( round( listNum * upperThr ) )
	print ' '
	print 'listNum =', listNum
	print 'LowerEnd =', lowerEnd
	print 'UpperEnd =', upperEnd
	
	### Sum -> Average
	sumHeart = 0
	sumCount = 0
	for number in range(lowerEnd + 1, upperEnd):
		temp = int(initHeartValue[number])
		print temp, ".",
		sumHeart = sumHeart + temp
		sumCount = sumCount + 1

	if upperEnd == lowerEnd :
		avgHeart = sumHeart / 1
	else :
		avgHeart = sumHeart / sumCount
		
	print " "
	print 'Sum =', sumHeart, 'Avg = ', avgHeart, 'sumCount = ', sumCount

	### Result
	return avgHeart

##################################
#   Violence Judgement
##################################
### Variable Declaration
print "Init_HeartAVG"
standard = init_HeartAVG(15,0.1,0.9)
heartRat = 0.2
heartThr = standard * heartRat
violenceJudgement = []
beforeJudgement = []
incRate = []
sen = []
isen = []
Rate = 0
index = 0
beIndex = 0

### HTTP POST
headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}         # POST Address
conn = httplib.HTTPConnection("alert-height-91305.appspot.com")                                 # POST Connection(http:// -> Remove

### Serial Connection
while True :
	try :
		Serial_PORT = "/dev/ttyUSB0"                            # Serial Port Address
		SensorValue = serial.Serial(Serial_PORT, 9600)          # Serial PORT Value
		if SensorValue != None :
			SensorValue.flushInput()                            # Serial Input
			break
	except OSError :
		print "USB SERIAL ERROR"
		print "Input !!"
		time.sleep(5)
		continue	
print "Standard Threshold", (standard - heartThr)


while True :
	### Reading of the sensor values
	os.system('clear')
	inputSensor = str( SensorValue.readline() )
	senToken = inputSensor.split(" ")
	for i in range(5): 
		sen.append( senToken[i] )
	
	### It will read the value of the sensor case of cast error
	try:
		### String to Int
		for i in range(5):
			isen.append( int(sen[i]) )
	
		if isen[0] != 0 :
			incRate.append( isen[0] )
	except ValueError:
		print "------Value Error"
		continue
		
		
	violenceJudge = "FALSE"
	
	### Case Judgement
	### - Stable
	if isen[0] >= 400 :					
		thr = (standard - heartThr)

		### - A. Touch Violence Judgement
		if isen[1] >= 750 or isen[2] >= 750 or isen[3] >= 750 or isen[4] >= 750 :
			violenceJudgement.append('A')
			beIndex = beIndex + 1
			print 'A' 
		### - B. Heartbeat Fast Judgement
		elif thr > isen[0]:
			violenceJudgement.append('B')
			beIndex = beIndex + 1
			print 'B'
		### - E. NOT Touch Violence Judgement
		else:
			violenceJudgement.append('E')
			beIndex = beIndex + 1
			print 'E'
	### - NOT Stable
	elif isen[0] <= 400:
		### - C. Touch Violence Judgement
		if isen[1] >= 750 or isen[2] >= 750 or isen[3] >= 750 or isen[4] >= 750 :
			violenceJudgement.append('C')
			beIndex = beIndex + 1
			print 'C'	
		### - D. NOT Touch Violence Judgement
		else :
			violenceJudgement.append('D')
			beIndex = beIndex + 1
			print 'D'

	### Duplication Remove
	lenList = len(violenceJudgement)
	if lenList >= 2 :
		if violenceJudgement[beIndex-2] == 'B' and violenceJudgement[beIndex-1] == 'B':
			print "B POP"
			beIndex = beIndex - 1
			violenceJudgement.pop()
		elif violenceJudgement[beIndex-2] == 'D' and violenceJudgement[beIndex-1] == 'D':
			print "D POP"
			beIndex = beIndex - 1
			violenceJudgement.pop()
		elif violenceJudgement[beIndex-2] == 'E' and violenceJudgement[beIndex-1] == 'E':
			print "E POP"
			beIndex = beIndex - 1
			violenceJudgement.pop()
	print "LIST ------> ", violenceJudgement
	print "List INDEX", beIndex
	lenList = len(violenceJudgement)

	### Situation Index Count
	index = [0, 0, 0, 0, 0, 0]
	count = [0, 0, 0, 0, 0]
	for i in range(lenList) :
		if violenceJudgement[i] == 'A' :
			count[0] = count[0] + 1
			index[0] = i	
		elif violenceJudgement[i] == 'B' :
			index[1] = i
			count[1] = count[1] + 1
		elif violenceJudgement[i] == 'C' :
			count[2] = count[2] + 1
			index[2] = i
		elif violenceJudgement[i] == 'D' :
			index[3] = i
			count[3] = count[3] + 1
		elif violenceJudgement[i] == 'E' :
			index[4] = i
			count[4] = count[4] + 1
			if index[5] == 0:
				index[5] == i

		### Situation PRINT
		if i == (lenList - 1) :
			print "\tIndex -------> ",
			print index
			print "\tCount -------> ",
			print count
		
		### VIOLENCE Judgement
		### Violence ( E < A < D  < B or E )
		if ( ( index[5] < index[0] ) and (index[0] < index[3]) and ((index[3] < index[1]) or (index[3] < index[4])) ) and ( count[0] != 0 ) :
			print "\t\t\t--------------------VIOLENCE--------------------"
			violenceJudge = "TRUE"
			violenceJudgement = []
			beIndex = 0
		### Violence ( E < C < D  < B or E )
		elif ( ( index[5] < index[2] ) and (index[2] < index[3]) and ((index[3] < index[1]) or (index[3] < index[4])) ) and ( count[2] != 0 ) :
			print "\t\t\t--------------------VIOLENCE--------------------"
			violenceJudge = "TRUE"
			violenceJudgement = [] 
			beIndex = 0
		### Not Violence
		else:
			violenceJudge = "FALSE"

	### First the end of the list
	if ( len( violenceJudgement ) - 1 ) == index[4] and count[4] != 0:
		violenceJudgement = []
		count = [0, 0, 0, 0, 0]
		beIndex = 1
		violenceJudgement.append("E")
	elif ( len( violenceJudgement ) - 1 ) == index[1] and count[1] != 0:
		violenceJudgement = []
		count = [0, 0, 0, 0, 0]
		beIndex = 1
		violenceJudgement.append("B")
		
	### (Situation A and C) > 2 -> Error -> Remove
	if count[0] > 2 or count[2] > 2 :
		violenceJudgement = []
		beIndex = 0
	elif ( count[0] < 2 or count[1] < 2 ) and i == (lenList - 1):
		count = [0, 0, 0, 0, 0]

	### (B or D or E) In the last case, initialization
	if count[1] == lenList or count[3] == lenList or count[4] == lenList:
		violenceJudgement = []
		beIndex = 0
			
	### Send of the heart rate increase and decrease
	incSum = 0
	increasingRate = 0.0
	if len(incRate) == 5 :
		incAvg = sum(incRate, 0.0) / 5
		increasingRate = ( ((incAvg - standard) / standard * 100 ) * -1 + 100 )
		Rate = int(increasingRate)
		incRate = []
		params = {'Mode' : 'Sensor', 'id' : '1st', 'left1' : sen[1], 'left2' : sen[2], 'right1' : sen[3], 'right2' : sen[4], 'psensor' : Rate, 'violence' : violenceJudge}
	else :
		params = {'Mode' : 'Sensor', 'id' : '1st', 'left1' : sen[1], 'left2' : sen[2], 'right1' : sen[3], 'right2' : sen[4], 'psensor' : Rate, 'violence' : violenceJudge}
	
	### HTTP POST Connection and Responsee
	params = urllib.urlencode(params)
	conn.request("POST", "/hello", params, headers)
	response = conn.getresponse()
	data = response.read()

	### When Violence Case is an error, Retransmitted until it receives the OK
	while data == "error" and violenceJudge == "TRUE" :
		conn.request("POST", "/hello", params, headers)
		response = conn.getresponse()
		data = response.read()
	print("'" + sen[1] + "'\t\t'" + sen[2] + "'\t\t'" + sen[3] + "'\t\t'" + sen[4] + "'\t\t'" + str(Rate) + "'\t\t'" + violenceJudge + "'\t\t'" + data + "'")
	
	### Initialization of the sensor values
	sen = []
	isen = []