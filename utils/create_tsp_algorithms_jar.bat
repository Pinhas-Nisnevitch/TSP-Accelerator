echo "Creating TSP Algoritms JAR"
if exist tsp_toolkit.jar (
    echo "Please wait.."
	mkdir tmp
	copy tsp_toolkit.jar tmp
	copy *.java tmp
	cd tmp
	javac -cp .;tsp_toolkit.jar *.java
	jar uf tsp_toolkit.jar *.class
	ren tsp_toolkit.jar "TSP Algorithms".jar
	move "TSP Algorithms".jar ../
	cd ../
	RMDIR tmp /S /Q
) else (
    echo "tsp_toolkit.jar not found"
)

