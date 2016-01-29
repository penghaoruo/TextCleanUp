dataDir=data:data/
CLASSPATH=".:bin:$dataDir"
nice java -Xmx10g -cp ${CLASSPATH} edu.illinois.cs.haoruo.langvision.textfilter.RefineText
