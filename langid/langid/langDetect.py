#import goslate
import langid
import sys
import time

myfile = "/home/liwei/jdownload/fotolia/captioning.txt"
#gs = goslate.Goslate()
with open(myfile) as f:
	for line in f:
		line = line.strip('\n')
	        print langid.classify(line)	
		#print gs.detect(line)
                #time.sleep(0.01)
