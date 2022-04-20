import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class streetRouting {

    private static  ArrayList<Junction> junctions = new ArrayList<>();
    private static  ArrayList<Street> streets = new ArrayList<>();
    private static  ArrayList<Car> fleet = new ArrayList<>();
    private static int totalJunctions;
    private static int totalStreets;
    private static int totalTime, totalCars;
    private static int initJunction;
    private static int distanceTravelled;


    public static void main(String[] args) throws IOException {

        // Initializes config variables
        readInput("input.txt");

        
        // Separate function to establish streets for each junction since there are unidirectional streets
        setJunctionStreets();
        ArrayList<Street> EmptyStreets = (ArrayList<Street>) streets.clone();

        for (Junction j : junctions) j.print();
        System.out.println("totalJunctions : " + totalJunctions);
        System.out.println("totalStreets : " + totalStreets);
        System.out.println("totalTime : " + totalTime);
        System.out.println("totalCars : " + totalCars);
        System.out.println("initJunction : " + initJunction);

        // Initializes all cars paths to their starting position
        for (int i = 0; i < totalCars; ++i) {
            Car currentCar = new Car(junctions.get(initJunction));
            currentCar.path += Integer.toString(initJunction);
            fleet.add(currentCar);
        }

        // Do paths for all cars independently
        // boolean probe = false;
        int delta = totalTime;
        Random randomGenerator = new Random();

        for(Car c: fleet)
            c.setTime(totalTime);

        for (Car car : fleet) {
            while (car.getTime() > 0) {
                // Arbitrarily init the best available street to go through
                ArrayList<Street> availableStreets = car.junction.getStreets();
                Street bestStreet = availableStreets.get(randomGenerator.nextInt(availableStreets.size()));

                // Stop if time's up
                car.reduceTime(bestStreet.getTime());
                // delta -= bestStreet.getTime();
                if (car.getTime() <= 0) break;

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


        for (Car car : fleet){
            System.out.println(fleet.indexOf(car) + ":");
            for(SubPath sp : car.path2){
                
                System.out.println(sp.getJunction());
                System.out.println(sp.getTimeL());
            }
           
        } 
        for (Street street : streets) distanceTravelled += street.isVisited() ? street.getDistance() : 0;

        System.out.println(distanceTravelled);
        System.out.println(junctions.get(1).getStreets().size());

        writeOutput("output.txt");

        //Creates copys of original solution
        ArrayList<Junction> junctionsAux = (ArrayList<Junction>) junctions.clone();
        ArrayList<Street> streetsAux = (ArrayList<Street>) EmptyStreets.clone();
        ArrayList<Car> fleetAux = (ArrayList<Car>) fleet.clone();
        

        int temperature = 1000;
        double prob = 0.95;

        while(temperature > 0)  {


            //Pick which car to change path
            int car2Choose = randomGenerator.nextInt(fleetAux.size() - 1);
            Car auxCar = fleetAux.get(car2Choose);
            int junc2Choose = randomGenerator.nextInt(auxCar.path.length() - 1);

            //Checks if a change can be made, if not select another junction
            while(true){
                if(junc2Choose > auxCar.path.length() -1){
                    car2Choose = randomGenerator.nextInt(fleetAux.size() - 1);
                    auxCar = fleetAux.get(car2Choose);
                    junc2Choose = randomGenerator.nextInt(auxCar.path.length() - 1);
                }

                if(junctionsAux.get( Integer.parseInt(auxCar.path.charAt(junc2Choose) + "") ).getStreets().size() > 1)
                    break;
                else{
                    junc2Choose++;
                }
            }
            //FALTA SE N HOUVER NENHUMA JUNCTION POSSIVEL DE SER ALTERADA

            //Repor valores do Carro desde junc2choose
            ArrayList<SubPath> tempPath = (ArrayList<SubPath>) auxCar.path2.subList(0, junc2Choose);
            //Set remaining time
            auxCar.setTime(tempPath.get(tempPath.size()-1).getTimeL());
            auxCar.setPath(tempPath);
            
            //Form car new Path
            
            while (auxCar.getTime() > 0) {
                // Arbitrarily init the best available street to go through
                ArrayList<Street> availableStreets = auxCar.junction.getStreets();
                Street bestStreet = availableStreets.get(randomGenerator.nextInt(availableStreets.size()));

                // Stop if time's up
                auxCar.reduceTime(bestStreet.getTime());
                // delta -= bestStreet.getTime();
                if (auxCar.getTime() <= 0) break;

                // Set to visited and update when this car passed through this street
                bestStreet.setVisited(fleet.indexOf(auxCar), auxCar.getTime());

                // If current car is located at junction 1, we should go to junction 2
                // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
                auxCar.junction = bestStreet.getJunction(1) == auxCar.junction ?
                        bestStreet.getJunction(2) :
                        bestStreet.getJunction(1);
                
                
                auxCar.path2.add(new SubPath(junctions.indexOf(auxCar.junction), auxCar.getTime()));
            }

            //Check visited streets
            for(Car c : fleetAux){
                int prevJ = -1;
                for(SubPath sp : c.path2){
                    if(prevJ == -1)
                        prevJ = sp.getJunction();
                    else{
                        for(Street s : streetsAux){
                            if((junctionsAux.indexOf(s.getJunction(1)) == prevJ) && ( junctionsAux.indexOf((s.getJunction(2))) == sp.getJunction()))
                                s.setVisited(fleetAux.indexOf(c), 0);
                            else if((junctionsAux.indexOf(s.getJunction(2)) == prevJ) && ( junctionsAux.indexOf((s.getJunction(1))) == sp.getJunction()) && s.getDirection() == 2)
                                s.setVisited(fleetAux.indexOf(c), 0);
                        }
                    }
                    
                }
            }

            //COMPARAR CASOS
            int distanceTravelled2 = 0;
            for (Street street : streetsAux) distanceTravelled2 += street.isVisited() ? street.getDistance() : 0;

            int difDistance = distanceTravelled2 - distanceTravelled;

            if(distanceTravelled2 > distanceTravelled){
                junctions = (ArrayList<Junction>) junctionsAux.clone();
                streets = (ArrayList<Street>) streetsAux.clone();
                fleet = (ArrayList<Car>) fleetAux.clone();
            }
            else{
                // randomGenerator.nextInt(1);
                // Math.exp(-difDistance/temperature) PROBABILIDADE
            }


        }






    }

    public static void writeOutput(String fileName) {
        try {
            FileWriter myWriter = new FileWriter("output.txt", false);
            BufferedWriter bufferedWriter = new BufferedWriter(myWriter);

            bufferedWriter.write(fleet.size() + "\n");
            for (Car c : fleet) {
                bufferedWriter.write(c.numberJunctions() + "\n");
                for (int i = 0; i < c.path.length(); i++) {
                    bufferedWriter.write(c.path.charAt(i) + "\n");
                }
            }

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


}