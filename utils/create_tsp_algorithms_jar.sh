
#!/bin/sh
echo "Creating TSP Algoritms JAR"
if [[ ! -f tsp_toolkit.jar ]]; then
	echo "tsp_toolkit.jar not found"
else
	echo "Please wait.."
	mkdir tmp
	cp tsp_toolkit.jar tmp/tsp_toolkit.jar
	cp -r *.java tmp/
	cd tmp
	javac -cp .:tsp_toolkit.jar *.java
	jar uf tsp_toolkit.jar *.class
	mv tsp_toolkit.jar ../"TSP Algorithms".jar
	cd ../
	rm -rf tmp
	echo "Done!"
	
fi