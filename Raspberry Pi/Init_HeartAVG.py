import time
import serial

def init_HeartAVG(Time, lowerThr, upperThr):
	print 'Srial PORT'
	Serial_PORT = "/dev/ttyUSB0"                            # Serial Port Address
	SensorValue = serial.Serial(Serial_PORT, 9600)          # Serial PORT Value
	SensorValue.flushInput()                                # Serial Input

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
			print '.',
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
	sumHeart = 0
	for number in range(lowerEnd + 1, upperEnd):
		#print 'Number =', number, 'Value =', initHeartValue[number]
		sumHeart = sumHeart + int(initHeartValue[number])

	avgHeart = sumHeart / (upperEnd - lowerEnd)
	print 'Sum =', sumHeart, 'Avg = ', avgHeart, 'ListNum = ', listNum

	# Result
	return avgHeart
