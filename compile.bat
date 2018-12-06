mkdir out
cd out
del *.class
javac *.java
jar cfe ../Bil441-Connect4.jar Connect4 *.class
cd ../
java -jar Bil441-Connect4.jar
