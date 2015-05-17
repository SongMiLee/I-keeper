import time

def init_HeartAVG(startTime, Time, lowerThr, upperThr):
	endTime = time.time()
	continueTime = Time

	initHeartValue = [0]
	listHeart = [10,9,8,7,6,5,4,3,2,1]
	lenListHeart = len(listHeart)

	#print 'Len =', (lenListHeart)
	#for number in range(0, lenListHeart):
	#	initHeartValue.append(listHeart[number])
		#print 'Number =', number, 'Init HeartValue =', initHeartValue[number+1]
	while (endTime - startTime) <= continueTime:
		inputSensor = str( SensorValue.readline() )
		senToken = inputSensor.split(" ")
		sen0 = senToken[0]
		initHeartValue.append(listHeart[number])
		endTime = time.time()

	# Sort
	initHeartValue.sort()
	listNum = len(initHeartValue)
	lowerEnd = int( round( listNum * lowerThr ) )
	#print 'LowerEnd =', lowerEnd
	upperEnd = int( round( listNum * upperThr ) )
	#print 'UpperEnd =', upperEnd
		
	# Sum -> Average
	sumHeart = 0
	for number in range(lowerEnd + 1, upperEnd):
		#print 'Number =', number, 'Value =', initHeartValue[number]
		sumHeart += initHeartValue[number]

	avgHeart = sumHeart / listNum
	#print 'Sum =', sumHeart, 'Avg = ', avgHeart, 'ListNum = ', listNum
	return avgHeart
