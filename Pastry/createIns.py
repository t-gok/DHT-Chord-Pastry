
# type, nodeid, msgid, x, y
# 0, 12, 1, 10, 20 // create a node 12 at (10,20)
# 1, 12, 2         // kill a node  
# 2, 12, 3, 18     // search 18 at 12

import sys, random
numNodes = int(sys.argv[1])
numCmds = int(sys.argv[2])

x_r = 1000;
y_r = 1000;
mid = 1

nodescreated = [1]
print "0,1,1,1,1"


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
		elif val>=0.96:
			index = random.randint(0,len(nodescreated)-1)
			mid += 1
			print "1,"+str(nodescreated[index])+","+str(mid)
			del(nodescreated[index])

		else:
			index = random.randint(0,len(nodescreated)-1)
			key = random.randint(1,2**15-1)
			mid += 1

			print "2,"+str(nodescreated[index])+","+str(mid)+","+str(key)
	else:
		# if val>=0.999 and len(nodescreated)>10:
		# 	index = random.randint(0,len(nodescreated)-1)
		# 	mid += 1
		# 	print "1,"+str(nodescreated[index])+","+str(mid)+","+str(len(nodescreated))
		# 	del(nodescreated[index])
		# else:	
		index = random.randint(0,len(nodescreated)-1)
		key = random.randint(1,2**15-1)
		mid += 1

		print "2,"+str(nodescreated[index])+","+str(mid)+","+str(key)	

# while len(nodescreated)<numNodes:
# 	nodeid = random.randint(1,2**15-1)
# 	while(nodeid in nodescreated):
# 		nodeid = random.randint(1,2**15-1)

# 	nodescreated.append(nodeid)	
# 	cmdtype = 0
# 	mid+=1
# 	nodex = random.randint(0,1000)
# 	nodey = random.randint(0,1000)

# 	print str(cmdtype)+","+str(nodeid)+","+str(mid)+","+str(nodex)+","+str(nodey)

# for i in range(numCmds):
# 	index = random.randint(0,len(nodescreated)-1)
# 	key = random.randint(1,2**15-1)
# 	mid += 1

# 	print "2,"+str(nodescreated[index])+","+str(mid)+","+str(key)	







	










