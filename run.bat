@echo off
echo Compiling Java files...
javac -cp "sqlite-jdbc-3.49.1.0.jar" *.java

echo Running program...
java -cp "sqlite-jdbc-3.49.1.0.jar;." Main

echo Cleaning up class files...
del /Q *.class

pause