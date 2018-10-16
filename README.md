# TSP-Accelerator
Algorithm accelerator for the travelling salesman problem

------------------------------------------------------------------------------------------------------------------------------
- [More about TSP-Accelerator](#more-about-tsp-accelerator)
- [Getting Started](#getting-started)
- [Create New TSP Solver](#create-new-tsp-solver)
- [The Team](#the-team)
------------------------------------------------------------------------------------------------------------------------------
![](TSPAccelerator.gif)







## More about TSP-Accelerator
TSP Accelerator is a java implementation of the paper: "TODO"



## Getting Started

```
git clone --recursive https://github.com/Pinhas-Nisnevitch/TSP-Accelerator/
cd TSP-Accelerator
ant -f "TSP Accelerator" -Dnb.internal.action.name=rebuild clean jar
```
Double-Click on: /TSP-Accelerator/TSP Accelerator/dist/TSP_Accelerator.jar  

# install dependencies  
**git:** https://git-scm.com/book/en/v2/Getting-Started-Installing-Git  
**java jdk:** https://www.oracle.com/technetwork/java/javase/downloads/index.html  
**ant:** https://ant.apache.org/manual/install.html  


## Create New TSP Solver

1) Write your TSP algorithm using this template:

  ```
  import Main.Cost;
  import java.util.ArrayList;
  import TSP_Algorithms.TSP_Algorithm;


  public class YOUR_TSP_ALGORITHM extends TSP_Algorithm{
      private final static String ALGORITHM_NAME = "YOUR TSP ALGORITHM NAME";


      public YOUR_TSP_ALGORITHM(Cost cost_func, ArrayList<Integer> points) {
          super(cost_func, points, ALGORITHM_NAME);
          int [] tour = new int[points.size()];
          // YOUR CODE HERE MAKE SURE TO UPDATE THE "tour" ARRAY BEFORE THE "setTour(tour)" COMMAND.
          setTour(tour);
      }

  }
  ```
  and save as "YOUR_TSP_ALGORITHM.java"
  
  See for example: https://github.com/Pinhas-Nisnevitch/TSP-Accelerator/blob/master/utils/NearestNeighbor.java

2) save your java file in: TSP-Accelerator/utils/
3) For Linux/macOS:  
   ```source create_tsp_algorithms_jar.sh```  
   For Windows:  
   "TODO"  
4) Load the new JAR file ("TSP-Accelerator/utils/TSP Algorithms.jar") into the program. (File -> Load Algorithm)  
   or (Shift + A).  

# Accelerating the "Nearest Neighbor" algorithm with 100k points:
![](monalisa.png)

## The Team

TSP-Accelerator is currently maintained by [Pinhas Nisnevitch](https://github.com/Pinhas-Nisnevitch)
major contributions coming from Dr. Lee-Ad Gottlieb , Omer Levin and Dina Ankonina.
