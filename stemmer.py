import sys
import nltk
from nltk.tokenize import wordpunct_tokenize 

myargs = sys.argv
myfile = myargs[1]
path = "/home/haoruo/TextCleanUp/"



stemmer = nltk.PorterStemmer()
with open(path + myfile, 'r') as fp:
    for line in fp:
        #tokens = wordpunct_tokenize(line)
        tokens = line.split()
        res = ""
        for token in tokens:
            res = res + stemmer.stem(token) + " "
        print res
                
