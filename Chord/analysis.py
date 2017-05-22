import sys
import os
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.ticker as ticker
# FIRST MERGE ALL THE LOG FILES #
os.system('rm Combined.log')
os.system('cat *.log > Combined.log')

# ITERATE OVER ALL THE LINE #
f = open('Combined.log','r')
lines = f.readlines()
f.close()


hops = {}

for line in lines:
	tokens = line.strip().split(":")
	if(len(tokens) != 1):
		mid = int(tokens[0])
		if(mid not in hops):
			hops[mid] = 1
		else:
			hops[mid] += 1


avg_hops = 0.0
H = hops.keys()
Histogram_List = []
for h in H:
	avg_hops += hops[h]
	Histogram_List.append(hops[h])
avg_hops = avg_hops/len(H)

print "Avg Hops: ",avg_hops


Histogram = {}
for h in H:
	no = hops[h]
	if(no not in Histogram):
		Histogram[no] = 1
	else:
		Histogram[no] += 1

fig, ax = plt.subplots()
# Be sure to only pick integer tick locations.
for axis in [ax.xaxis, ax.yaxis]:
    axis.set_major_locator(ticker.MaxNLocator(integer=True))


plt.hist(np.array(Histogram_List),bins=np.arange(1,10)-0.5)
#plt.xticks([1,2,3,4,5])
plt.title("Histogram")
plt.xlabel("Number of Hops")
plt.ylabel("Frequency")

plt.show()

TTT = Histogram.keys()
for key in TTT:
	print key," ",Histogram[key]
#os.system('rm Combined.log')



# TRACE THE PATH OF A MESSAGE #
path = {}

for line in lines:
	tokens = line.strip().split(":")
	if(len(tokens) != 1):
		mid = int(tokens[0])
		src = int(tokens[1])
		if mid not in path:
			path[mid] = [src]
		else:
			path[mid] = path[mid] + [src]

search_key = {}
f = open(sys.argv[1])
cmds = f.readlines()
f.close
nodesCreated = []

for cmd in cmds:
	tokens = cmd.strip().split(",")
	if(tokens[0].strip() == '2'):
		mid = int(tokens[-2])
		skey = int(tokens[-1])
		search_key[mid] = skey
	elif(tokens[0] == '0'):
		nodesCreated.append(int(tokens[1]))



	


