import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

// package com.aor.numbers;

// import java.lang.reflect.Array;
// import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// import java.util.Scanner;

// import javax.swing.SingleSelectionModel;

class streetRouting {

    private static ArrayList<Junction> junctions = new ArrayList<Junction>();
    private static ArrayList<Street> streets = new ArrayList<Street>();
    private static ArrayList<Car> cars = new ArrayList<Car>();
    private static int totalJunctions;
    private static int totalStreets;
    private static int totalTime, totalCars;
    private static int initJunc;
    private static int travelDist;


    public static void main(String[] args) throws IOException {

        readInput("input.txt");
        setJunctionStreets(); //Seperate function to establish streets for each juntion since there are unidirectional streets
  
        
        // 
        for(Junction j : junctions)
            j.print();
        for(Street s : streets)
            s.print();
        System.out.println("totalJunctions : " + totalJunctions);
        System.out.println("totalStreets : " + totalStreets);
        System.out.println("totalTime : " + totalTime);
        System.out.println("totalCars : " + totalCars);
        System.out.println("initJunc : " + initJunc);

        for(int i = 0; i < totalCars; i++){
            cars.add(new Car(junctions.get(initJunc)));
        }
        for(Car c : cars)
            c.path += Integer.toString(initJunc);
        
        while(totalTime > 0){
            for(Car c : cars){
                Street bestStreet = c.junction.getStreets().get(0);
                for (Street s : c.junction.getStreets()){
                    
                    if(!s.isVisited()){
                        c.junction = s.getJ(2);
                        totalTime -= s.getTime();
                        travelDist += s.getDist();
                        s.setVisited();
                        break;
                    }
                    else{
                        if((s.getDist()/s.getTime()) > (bestStreet.getDist()/bestStreet.getTime()))
                            bestStreet = s;
                            totalTime -= s.getTime();
                            c.junction = bestStreet.getJ(2);
                    }
                    
                    
                }
                
                c.path += Integer.toString(junctions.indexOf(c.junction));
            }
        }

        for(Car c : cars)
            System.out.println(c.path);
        System.out.println(travelDist);

    }



public static void writeOutput(String fileName)   {
    // 2 //Twocarsinthefleet.
    // 1 //Firstcarstaysattheinitialjunction:
    // 0 //-theinitialjunction
    // 3 //Secondcarvisits3junctions:
    // 0 //-theinitialjunction
    // 1 //-thecarmovesfromjunction0tojunction1
    // 2 //-thecarmovesfromjunction1tojunction2


    try {
        FileWriter myWriter = new FileWriter("output.txt", true);
        BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
        // bufferedWriter.write(this.getGame().getArena().getJogador().getName() + " " + this.getGame().getArena().getJogador().getScore() + "\n");
        bufferedWriter.close();

        System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }

    }


    public static void readInput(String fileName) {
        
        try {
            File myObj = new File("input.txt");
            Scanner myReader = new Scanner(myObj);
            // 3 2 3000 2 0      |3 junctions, 2 streets, 3000 seconds, 2 cars , starting at 0
            String data = myReader.nextLine();
            String[] config = data.split(" ");
            totalJunctions = Integer.parseInt(config[0]);
            totalStreets = Integer.parseInt(config[1]);
            totalTime = Integer.parseInt(config[2]);
            totalCars = Integer.parseInt(config[3]);
            initJunc =Integer.parseInt(config[4]);

            int cycleCount = 1;

            while (myReader.hasNextLine()) {

                data = myReader.nextLine();
                // System.out.println(cycleCount);
                // System.out.println(totalJunctions);
                if(cycleCount <= totalJunctions){
                    String[] juncStr = data.split(" ");
                    Junction auxJunction = new Junction(Double.parseDouble(juncStr[0]) , Double.parseDouble(juncStr[0]));
                    junctions.add(auxJunction);

                }
                else {
                // System.out.println(data);
                //For Streets
                String[] streetStr = data.split(" ");
                // System.out.println(streetStr[0]);
                Street auxStreet = new Street(junctions.get(Integer.parseInt(streetStr[0])), junctions.get(Integer.parseInt(streetStr[1])),
                                                Integer.parseInt(streetStr[2]), Integer.parseInt(streetStr[3]), Integer.parseInt(streetStr[4]));
                streets.add(auxStreet);
                // for(Street s : streets)
                // s.print();
                }

                // 48.8582  2.2945    //Coordinates of the first junction.
                // 50.0 3.09            //Coordinates of the second junction.
                // 51.424242 3.02    //Coordinates of the third junction.
                // 0 1 1 30 250     //Street from first junction to second junction.
                // 1 2 2 45 200     //Street from second junction to third junction.

                cycleCount++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public static void setJunctionStreets() {

        for(Junction j : junctions){
            for(Street s : streets){
                // System.out.println("cycle");
                // s.print();
                
                //If direction == 1 streets are unidirectional
                if(s.getJ(1).getX() == j.getX() && s.getJ(1).getY() == j.getY() && s.getDirec() == 2)
                    j.addStreet(s);
                else if(s.getJ(1).getX() == j.getX() && s.getJ(1).getY() == j.getY() && s.getDirec() == 1)
                    j.addStreet(s);
                else if(s.getJ(2).getX() == j.getX() && s.getJ(2).getY() == j.getY() && s.getDirec() == 2)
                    j.addStreet(s);
                // else System.out.println("failing");

            }
        }

    }


}