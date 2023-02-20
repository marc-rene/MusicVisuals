
@echo off
echo:
echo |set /p="Compiling Stuff..."
javac -classpath "./;./lib/minim.jar;./lib/core.jar" -d bin src/ie/tudublin/*.java src/example/*.java
echo  DONE! :D

echo Now We're Running Stuff!
echo:
java -classpath "./bin;./lib/minim.jar;./lib/core.jar;./lib/jsminim.jar;./lib/mp3spi1.9.5.jar;./lib/tritonus_share.jar;./lib/tritonus_aos.jar;./lib/jl1.0.1.jar;./lib/sqlite-jdbc-3.23.1.jar;lib/jogl-all.jar;lib/gluegen-rt.jar;lib/gluegen-rt-natives-windows-amd64.jar;lib/gluegen-rt-natives-windows-i586.jar;lib/jogl-all-natives-windows-amd64.jar;lib/jogl-all-natives-windows-i586.jarlib/jogl-all.jar;lib/gluegen-rt.jar;lib/gluegen-rt-natives-windows-amd64.jar;lib/gluegen-rt-natives-windows-i586.jar;lib/jogl-all-natives-windows-amd64.jar;lib/jogl-all-natives-windows-i586.jar" ie.tudublin.Main