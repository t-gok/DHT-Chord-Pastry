
# type, nodeid, msgid, x, y
# 0, 12, 1, 10, 20 // create a node 12 at (10,20)
# 1, 12, 2         // kill a node  
# 2, 12, 3, 18     // search 18 at 12

import sys, random
import numpy as np
numNodes = int(sys.argv[1])
numCmds = int(sys.argv[2])

x_r = 1000;
y_r = 1000;
mid = 1

# first create a few nodes with predefined fingers #
nodescreated = [1]
for i in range(0,5):
	nodeid = random.randint(1,2**15-1)
	while(nodeid in nodescreated):
		nodeid = random.randint(1,2**15-1)
	nodescreated.append(nodeid)


nodescreated.sort()

temp = nodescreated + (np.array(nodescreated)+2**15).tolist()
#print temp

for node in nodescreated:
	fingers = []
	nodex = random.randint(0,1000)
	nodey = random.randint(0,1000)

	for i in range(1,16):
		val = (node + 2**(i-1))%(2**15)
		for j in range(0,len(temp)):
			if(val <= temp[j]):
				fingers.append(temp[j]%2**15)
				break

	C = "0,"+str(node)+","+str(mid)+","+str(nodex)+","+str(nodey)+","
	for i in range(0,15):
		C = C + str(fingers[i]) + ","

	predecessor = nodescreated[nodescreated.index(node) - 1]
	C = C + str(predecessor)
	print C
	mid += 1 






while(mid < numCmds):
	val = random.random()
	if(len(nodescreated)<numNodes):
		if val<= 0.6:
			nodeid = random.randint(1,2**15-1)
			while(nodeid in nodescreated):
				nodeid = random.randint(1,2**15-1)

			nodescreated.append(nodeid)	
			cmdtype = 0
			mid+=1
			nodex = random.randint(0,1000)
			nodey = random.randint(0,1000)

			print str(cmdtype)+","+str(nodeid)+","+str(mid)+","+str(nodex)+","+str(nodey)
		else:
			index = random.randint(0,len(nodescreated)-1)
			key = random.randint(1,2**15-1)
			mid += 1

			print "2,"+str(nodescreated[index])+","+str(mid)+","+str(key)
	else:
		index = random.randint(0,len(nodescreated)-1)
		key = random.randint(1,2**15-1)
		mid += 1

		print "2,"+str(nodescreated[index])+","+str(mid)+","+str(key)	

while len(nodescreated)<numNodes:
	nodeid = random.randint(1,2**15-1)
	while(nodeid in nodescreated):
		nodeid = random.randint(1,2**15-1)

	nodescreated.append(nodeid)	
	cmdtype = 0
	mid+=1
	nodex = random.randint(0,1000)
	nodey = random.randint(0,1000)

	print str(cmdtype)+","+str(nodeid)+","+str(mid)+","+str(nodex)+","+str(nodey)

for i in range(numCmds):
	index = random.randint(0,len(nodescreated)-1)
	key = random.randint(1,2**15-1)
	mid += 1

	print "2,"+str(nodescreated[index])+","+str(mid)+","+str(key)	







	










