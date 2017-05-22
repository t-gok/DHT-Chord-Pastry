import sys
import os
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.ticker as ticker
# FIRST MERGE ALL THE LOG FILES #
# os.system('rm Combined.log')
os.system('cat '+sys.argv[1]+'/*.log > Combined.log')

# ITERATE OVER ALL THE LINE #
f = open('Combined.log','r')
lines = f.readlines()
f.close()


hops = {}

for line in lines:
	tokens = line.strip().split(",")
	if(len(tokens) != 1):
		if('Search' in tokens[0]):
			mid = int(tokens[1])

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


plt.hist(np.array(Histogram_List),bins=np.arange(1,20)-0.5)
#plt.xticks([1,2,3,4,5])
plt.title("Histogram")
plt.xlabel("Number of Hops")
plt.ylabel("Frequency")


plt.show()


#os.system('rm Combined.log')


# f1 = open(sys.argv[1],'r')
# xy = {}
# srcs = {}

# for l in f1:
# 	if l.strip().split(',')[0]=='0':
# 		l = l.strip().split(',')
# 		xy[int(l[1])] = (int(l[3]),int(l[4]))
# 	elif l.strip().split(',')[0]=='2':	
# 		l = l.strip().split(',')
# 		srcs[int(l[2])] = xy[int(l[1])]

# f1.close()


# # TRACE THE PATH OF A MESSAGE #
# NodeId = []
# search_key = {}
# path = {}
# routes = {}

# for line in lines:
# 	tokens = line.strip().split(",")
# 	if(len(tokens) != 1):
# 		if('Search' in tokens[0]):
# 			mid = int(tokens[1])
# 			src = int(tokens[2])
# 			dest = int(tokens[3])
# 			skey = int(tokens[6])

# 			if mid not in path:
# 				path[mid] = [(src,dest)]
# 			else:
# 				path[mid] = path[mid] + [(src,dest)]

# 			search_key[mid] = skey

# CALCULATE THE PATHS FOR EACH MID #
# messages = path.keys()[:1000]
# for m in messages:
# 	route = [0]
# 	tmp = {}
# 	for (src,dest) in path[m]:
# 		tmp[dest] = src

# 	dest = 0
# 	while(True):
# 		if(dest in tmp):
# 			route = [tmp[dest]] + route
# 			dest = tmp[dest]
# 		else:
# 			break

# 	routes[m] = route

# def ed((x1,y1),(x2,y2)):
# 	return ((x2-x1)**2 + (y2-y1)**2)**(0.5)

# def distance(mid,r):
# 	if(len(r)<=1):
# 		return (0,0)

# 	d = 0
# 	dest = (0,0)
# 	for (p1,p2) in r:
# 		if p2!=0:
# 			d += ed(xy[p1],xy[p2])
# 		else:
# 			dest = xy[p1]	

# 	return (d,ed(srcs[mid],dest))	

# d1 = 0
# d2 = 0

# for key in path.keys():
# 	d11, d12 = distance(key,path[key])
# 	d1 += d11
# 	d2 += d12


# print d1,d2, d1/d2	