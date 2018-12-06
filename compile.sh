mkdir out
find out/ -name \*.class -type f -delete
javac *.java -d out
cd out
jar cfe ../Bil441-Connect4.jar Connect4 *.class
cd ../
java -jar Bil441-Connect4.jar
