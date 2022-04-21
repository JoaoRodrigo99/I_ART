import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.awt.Graphics2D;

class streetRouting {

    private static  ArrayList<Junction> junctions = new ArrayList<>();
    private static  ArrayList<Street> streets = new ArrayList<>();
    private static  ArrayList<Car> fleet = new ArrayList<>();
    private static int totalJunctions;
    private static int totalStreets;
    private static int totalTime, totalCars;
    private static int initJunction;
    private static int distanceTravelled;
    private static Random  randomGenerator = new Random();


    

    public static void main(String[] args) throws IOException {

        
        //Reset temperatures.txt if exists
        ResetTempFile("temperatures.txt");
    

        // Initializes config variables
        readInput("input.txt");
        
        
        // Separate function to establish streets for each junction since there are unidirectional streets
        setJunctionStreets();
        // try{
        // }
        // else {throw new InstantiationError("failed\n");}

        for (Junction j : junctions) j.print();
        System.out.println("totalJunctions : " + totalJunctions);
        System.out.println("totalStreets : " + totalStreets);
        System.out.println("totalTime : " + totalTime);
        System.out.println("totalCars : " + totalCars);
        System.out.println("initJunction : " + initJunction);

                // Initializes all cars paths to their starting position
                for (int i = 0; i < totalCars; ++i) {
                    Car currentCar = new Car(junctions.get(initJunction));
                    currentCar.path2.add(new SubPath(initJunction, totalTime));
                    fleet.add(currentCar);
                }
        
                // Do paths for all cars independently
        
                for(Car c: fleet)
                    c.setTime(totalTime);
        
                for (Car car : fleet) {
                    while (car.getTime() > 0) {
                        // Arbitrarily init the best available street to go through
                        ArrayList<Street> availableStreets = car.junction.getStreets();
                        Street bestStreet = availableStreets.get(randomGenerator.nextInt(availableStreets.size()));
        
                        // Stop if time's up
                        if ((car.getTime() - bestStreet.getTime()) <= 0) break;
        
                        car.reduceTime(bestStreet.getTime());
                        // delta -= bestStreet.getTime();
                        
        
                        // Set to visited and update when this car passed through this street
                        bestStreet.setVisited(fleet.indexOf(car), car.getTime());
        
                        // If current car is located at junction 1, we should go to junction 2
                        // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
                        car.junction = bestStreet.getJunction(1) == car.junction ?
                                bestStreet.getJunction(2) :
                                bestStreet.getJunction(1);
                        
                        
                        car.path2.add(new SubPath(junctions.indexOf(car.junction), car.getTime()));
                        
                    }
                }
        
        
                for (Car car : fleet){ //Print paths
                    System.out.println(fleet.indexOf(car) + ":");
                    for(SubPath sp : car.path2){
                        
                        System.out.print("  (" + sp.getJunction() +",");
                        System.out.print(sp.getTimeL() + ") ");
                    }
                    System.out.println();
                } 
        
                for (Street street : streets) distanceTravelled += street.isVisited() ? street.getDistance() : 0;
        
                System.out.println("distance traveled" + distanceTravelled);
                // System.out.println(junctions.get(1).getStreets().size());
        

                SimulatedAnnealing();
        

                for (Car car : fleet){ //Print paths
                    System.out.println(fleet.indexOf(car) + ":");
                    for(SubPath sp : car.path2){
                        
                        System.out.print("  (" + sp.getJunction() +",");
                        System.out.print(sp.getTimeL() + ") ");
                    }
                    System.out.println();
                } 
                writeOutput("output.txt");

    }

    @SuppressWarnings("unchecked")
    public static void SimulatedAnnealing(){

        //Empty streets
        ArrayList<Street> EmptyStreets = (ArrayList<Street>) streets.clone();
        

        double temperature = 60;
        // double prob = 0.95;

        
        while(temperature > 0)  {
            //Creates copys of original solution
            // ArrayList<Junction> junctionsAux = (ArrayList<Junction>) junctions.clone();
            // ArrayList<Street> streetsAux = (ArrayList<Street>) EmptyStreets.clone();
            // ArrayList<Car> fleetAux = (ArrayList<Car>) fleet.clone();

                        //Creates copys of original solution
            ArrayList<Junction> junctionsAux = new ArrayList<>();
            ArrayList<Street> streetsAux = new ArrayList<>();
            ArrayList<Car> fleetAux = new ArrayList<>();

            for(Junction j: junctions){
                junctionsAux.add(j);
            }
            for(Street s: EmptyStreets){
                streetsAux.add(s);
            }
            for(Car c: fleet){
                fleetAux.add(c);
            }

            writeTempNumbers("temperatures.txt", Integer.toString(distanceTravelled));

            System.out.println("INITAL PATHS");
            for (Car car : fleet){ //Print paths
                System.out.println(fleet.indexOf(car) + ":");
                for(SubPath sp : car.path2){
                    
                    System.out.print("  (" + sp.getJunction() +",");
                    System.out.print(sp.getTimeL() + ") ");
                }
                System.out.println();
            
            } 
            System.out.println("(Current) Distance Travalled : " + distanceTravelled);

            for(Street s : streetsAux)
                s.unVisit();

            //Pick a random CAR 
            int car2Choose = randomGenerator.nextInt(fleetAux.size() );
            Car auxCar = fleetAux.get(car2Choose);

            //Pick a random JUNCTION
            int junc2Choose = randomGenerator.nextInt(auxCar.path2.size() );

            //Checks if a change can be made in that junction, if not select another junction(the came after the randomly picked). If no junction can be changed, move to another car
            while(true){
                if(junc2Choose > auxCar.path2.size() -1){
                    car2Choose = randomGenerator.nextInt(fleetAux.size() );
                    auxCar = fleetAux.get(car2Choose);
                    junc2Choose = randomGenerator.nextInt(auxCar.path2.size() );
                }

                //If the junction has another street than car can go another way
                if(junctionsAux.get( auxCar.path2.get(junc2Choose).getJunction() ).getStreets().size() > 1)
                    break;
                else{
                    junc2Choose++;
                }
            }

            

            //Repor valores do Carro desde a junction escolhido, isto é cortar o path depois dessa junction
            List<SubPath> tempPathAux = auxCar.path2.subList(0, junc2Choose);
            ArrayList<SubPath> tempPath = new ArrayList<SubPath>();
            for(SubPath sp : tempPathAux)
                tempPath.add(sp);

        
            System.out.println(auxCar);
            System.out.println("junc : " + junc2Choose);

            //Set remaining time && and currentJunction 
            if(junc2Choose > 0){
                auxCar.setTime(tempPath.get(tempPath.size()-1).getTimeL());
                auxCar.setJunction( junctionsAux.get(tempPath.get(tempPath.size()-1).getJunction()) );
            } 
            else {
                auxCar.setTime(totalTime);
                auxCar.setJunction(junctionsAux.get(0));
            }

            auxCar.setPath(tempPath);
            
            //Form car new Path
            
            while (auxCar.getTime() > 0) {
                // Arbitrarily init the random available street to go through
                ArrayList<Street> availableStreets = auxCar.junction.getStreets();
                Street bestStreet = availableStreets.get(randomGenerator.nextInt(availableStreets.size()));

                // Stop if time's up
                if ((auxCar.getTime() - bestStreet.getTime())< 0) break;

                auxCar.reduceTime(bestStreet.getTime());
                // Set to visited and update when this car passed through this street
                // bestStreet.setVisited(fleet.indexOf(auxCar), auxCar.getTime());

                // If current car is located at junction 1, we should go to junction 2
                // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
                auxCar.junction = bestStreet.getJunction(1) == auxCar.junction ?
                        bestStreet.getJunction(2) :
                        bestStreet.getJunction(1);
                
                
                auxCar.path2.add(new SubPath(junctionsAux.indexOf(auxCar.junction), auxCar.getTime()));
            }

            //Check for which streets were visited
            for(Car c : fleetAux){
                int prevJ = -1;
                for(SubPath sp : c.path2){
                    if(prevJ == -1)
                        prevJ = sp.getJunction();
                    else{
                        for(Street s : streetsAux){
                            if((junctionsAux.indexOf(s.getJunction(1)) == prevJ) && ( junctionsAux.indexOf((s.getJunction(2))) == sp.getJunction()))
                                s.setVisited(fleetAux.indexOf(c), sp.getTimeL());
                            else if((junctionsAux.indexOf(s.getJunction(2)) == prevJ) && ( junctionsAux.indexOf((s.getJunction(1))) == sp.getJunction()) && s.getDirection() == 2)
                                s.setVisited(fleetAux.indexOf(c), sp.getTimeL());
                        }
                        prevJ = sp.getJunction();
                    }
                    
                }
            }              

            System.out.println("Comparing cases\n");


            //COMPARAR CASOS
            int distanceTravelled2 = 0;
            for (Street street : streetsAux) distanceTravelled2 += street.isVisited() ? street.getDistance() : 0;
            System.out.println("(neighboor)distance traveled : " + distanceTravelled2);

            int difDistance = distanceTravelled2 - distanceTravelled;

            if(distanceTravelled2 >= distanceTravelled){
                System.out.println("Was a better solution");
                distanceTravelled = distanceTravelled2;
                junctions.clear();
                streets.clear();
                fleet.clear();
                junctions = (ArrayList<Junction>) junctionsAux.clone();
                streets = (ArrayList<Street>) streetsAux.clone();
                fleet = (ArrayList<Car>) fleetAux.clone();
            }
            else{
                System.out.println("\nWorse Case ...");
                double rNum = randomGenerator.nextDouble();
                double probab = Math.exp(difDistance/temperature) ; //PROBABILIDADE (0-100%)
                System.out.println("difDist = " + difDistance + "\n" + "Temp : " + temperature);
                System.out.println("Random Double : " + rNum + "\n"+ "Probability" + probab +" ->(e^difDist/Temp)");
                System.out.println("Rnum <= probab");
                
                if(rNum <= probab){ //Probability hit - Assume worst case
                    System.out.println("PICKED WORSe CASE    \n");
                    distanceTravelled = distanceTravelled2;
                    junctions.clear();
                    streets.clear();
                    fleet.clear();
                    junctions = (ArrayList<Junction>) junctionsAux.clone();
                    streets = (ArrayList<Street>) streetsAux.clone();
                    fleet = (ArrayList<Car>) fleetAux.clone();
                }
            }


            temperature -= 0.25;
            System.out.println("-----------------------------------");
        }
    }

    public static void writeOutput(String fileName) {
        try {
            FileWriter myWriter = new FileWriter("output.txt", false);
            BufferedWriter bufferedWriter = new BufferedWriter(myWriter);

            bufferedWriter.write(fleet.size() + "\n");
            for (Car c : fleet) {
                bufferedWriter.write(c.path2.size() + "\n");
                for (int i = 0; i < c.path2.size(); i++) {
                    bufferedWriter.write(c.path2.get(i).getJunction() + "\n");
                }
                bufferedWriter.write("-\n");
            }

            bufferedWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public static void writeTempNumbers(String fileName, String toWrite) {  
        
        try {
            FileWriter myWriter = new FileWriter("temperatures.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(myWriter);

            // bufferedWriter.write(fleet.size() + "\n");
            bufferedWriter.write(toWrite + "\n");
             

            bufferedWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public static void ResetTempFile(String fileName) {  
        
        try {
            FileWriter myWriter = new FileWriter("temperatures.txt", false);
            BufferedWriter bufferedWriter = new BufferedWriter(myWriter);

            // bufferedWriter.write(fleet.size() + "\n");
            bufferedWriter.write("\n");
             

            bufferedWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public static void readInput(String fileName) {

        try {
            File myObj = new File("newinput.txt");
            Scanner myReader = new Scanner(myObj);

            String data = myReader.nextLine();
            String[] config = data.split(" ");
            totalJunctions = Integer.parseInt(config[0]);
            totalStreets = Integer.parseInt(config[1]);
            totalTime = Integer.parseInt(config[2]);
            totalCars = Integer.parseInt(config[3]);
            initJunction = Integer.parseInt(config[4]);

            int cycleCount = 1;
            while (myReader.hasNextLine()) {
                data = myReader.nextLine();
                if (cycleCount <= totalJunctions) {
                    String[] junctionStr = data.split(" ");
                    junctions.add(new Junction(Double.parseDouble(junctionStr[0]), Double.parseDouble(junctionStr[1])));
                } else {
                    String[] streetStr = data.split(" ");
                    streets.add(new Street(junctions.get(Integer.parseInt(streetStr[0])), junctions.get(Integer.parseInt(streetStr[1])), Integer.parseInt(streetStr[2]), Integer.parseInt(streetStr[3]), Integer.parseInt(streetStr[4])));
                }

                ++cycleCount;
            }

            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    
    }



    public static void setJunctionStreets() {

        for (Junction j : junctions) {
            for (Street s : streets) {
                // If direction == 1 streets are unidirectional
                if (s.getJunction(1).getX() == j.getX() && s.getJunction(1).getY() == j.getY() && s.getDirection() == 2)
                    j.addStreet(s);
                else if (s.getJunction(1).getX() == j.getX() && s.getJunction(1).getY() == j.getY() && s.getDirection() == 1)
                    j.addStreet(s);
                else if (s.getJunction(2).getX() == j.getX() && s.getJunction(2).getY() == j.getY() && s.getDirection() == 2)
                    j.addStreet(s);
            }
        }

    }

//End code
}