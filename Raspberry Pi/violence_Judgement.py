import time
import serial
import time
import serial

def init_HeartAVG(Time, lowerThr, upperThr):
	print 'Srial PORT'
	Serial_PORT = "/dev/ttyUSB0"                            # Serial Port Address
	SensorValue = serial.Serial(Serial_PORT, 9600)          # Serial PORT Value
	SensorValue.flushInput()                                # Serial Input
	inputSensor = str( SensorValue.readline() )
        
	print 'Start Time'
	startTime = time.time()
	continueTime = Time

	initHeartValue = [0]
        
	##########################  
	# TEST
	##########################
	#listHeart = [10,9,8,7,6,5,4,3,2,1]
	#lenListHeart = len(listHeart)
	#
	#print 'Len =', (lenListHeart)
	#for number in range(0, lenListHeart):
	#	initHeartValue.append(listHeart[number])
	#	print 'Number =', number, 'Init HeartValue =', initHeartValue[number+1]

		# Save Heart
	print 'Start While'
	count = 0
	endTime = time.time()
	while (endTime - startTime) <= continueTime:
		if count == 0 :
			print 'Writing',
		else:
			print sen0, '.',
			#print sen0
		count = count + 1
		inputSensor = str( SensorValue.readline() )
		senToken = inputSensor.split(" ")
		sen0 = senToken[0]
		initHeartValue.append(sen0)
		endTime = time.time()

	# Sort
	initHeartValue.sort()
	listNum = len(initHeartValue)
	print ' '
	print 'listNum =', listNum
	lowerEnd = int( round( listNum * lowerThr ) )
	print 'LowerEnd =', lowerEnd
	upperEnd = int( round( listNum * upperThr ) )
	print 'UpperEnd =', upperEnd
		
	# Sum -> Average
	print "SUM"
	sumHeart = 0
	for number in range(lowerEnd + 1, upperEnd):
		#print 'Number =', number, 'Value =', initHeartValue[number]
		temp = int(initHeartValue[number])
		print temp, ".",
		sumHeart = sumHeart + int(initHeartValue[number])

	if upperEnd == lowerEnd :
		avgHeart = sumHeart / 1
	else :
		avgHeart = sumHeart / (upperEnd - lowerEnd)
	print " "
	print 'Sum =', sumHeart, 'Avg = ', avgHeart, 'ListNum = ', listNum

	# Result
	return avgHeart



def violence_Judgement(standardHeart, heartRat):
	standard = standardHeart
	heartThr = standard * heartRat
	violenceJudgement = []
	beforeJudgement = []
	index = 0
	beIndex = 0
	
	print 'Srial PORT'
	Serial_PORT = "/dev/ttyUSB0"                            # Serial Port Address
	SensorValue = serial.Serial(Serial_PORT, 9600)          # Serial PORT Value
	SensorValue.flushInput()                                # Serial Input

	inputSensor = str( SensorValue.readline() )
        
	print "STD Thr", (standard + heartThr)
	while True :
		inputSensor = str( SensorValue.readline() )
		if " "  not in inputSensor :
			print "NOT Sensing"
			continue
		senToken = inputSensor.split(" ")
		sen0 = senToken[0]
		sen1 = senToken[1]
		sen2 = senToken[2]
		sen3 = senToken[3]
		sen4 = senToken[4]
		print("Pressure1\tPressure2\tPressure3\tPressure4\tHeartbeat")
		print("'" + sen1 + "'\t\t'" + sen2 + "'\t\t'" + sen3 + "'\t\t'" + sen4 + "'\t\t'" + sen0 + "'")
		sen0 = int(sen0)
		sen1 = int(sen1)
		sen2 = int(sen2)
		sen3 = int(sen3)
		sen4 = int(sen4)
		
		### Judgement
		# Stable
		if sen0 != 0 :					
			thr = (standard - heartThr)
			print thr, sen0
			# A. Touch Violence Judgement
			if sen1 >= 850 or sen2 >= 850 or sen3 >= 850 or sen4 >= 850 :
				#if 'A' in beforeJudgement and beIndex == 2 :
				#	violenceJudgement.append('A')
				#	index = index + 1
				#	print '#A', 
				violenceJudgement.append('A')
				beIndex = beIndex + 1
				print 'A', 
			# B. Heartbeat Fast Judgement
			elif thr > sen0:
				violenceJudgement.append('B')
				beIndex = beIndex + 1
				print 'B',
			# E. NOT Touch Violence Judgement
			else:
				violenceJudgement.append('E')
				beIndex = beIndex + 1
				print 'E',
		
		# NOT Stable
		elif sen0 == 0:
			# C. Touch Violence Judgement
			if sen1 >= 850 or sen2 >= 850 or sen3 >= 850 or sen4 >= 850 :
				violenceJudgement.append('C')
				beIndex = beIndex + 1
				print 'C',
				
			# D. NOT Touch Violence Judgement
			else :
				violenceJudgement.append('D')
				beIndex = beIndex + 1
				print 'D',

		# Violence Judgement
		#if index == 3 : # Number 4
		#	print "Violence Judgement"
			
		### Init
		#if beIndex == 3 : # Number 3
		#	print "Before Judge", beforeJudgement
		#	beforeJudgement = []
		#	beIndex = 0
		#	print ' '
		#	print 'Before INIT'
			
		#if index == 4 : # Number 4
		#	print "Violence Judge", violenceJudgement
		#	violenceJudgement = []
		#	index = 0
		#	print ' '
		#	print 'Judgement INIT'
		#	continue
                #else :
		#	print ' '
		#	continue
		
		if len(violenceJudgement) == 2 :
			if violenceJudgement[0] == 'E' and violenceJudgement[1] == 'E':
				print "POP"
				violenceJudgement.pop()
                             
                ### Judgement
		if "E" in violenceJudgement and ("A" in violenceJudgement or "C" in violenceJudgement) and "D" in violenceJudgement :
			violenceJudgement = []
			print "---------------------------------------------VIOLENCE"
			
		
	return 0
