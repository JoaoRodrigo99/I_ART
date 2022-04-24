import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class streetRouting {
    private static final Random randomGenerator = new Random();
    private static ArrayList<Junction> junctions = new ArrayList<>();
    private static ArrayList<Street> streets = new ArrayList<>();
    private static ArrayList<Car> fleet = new ArrayList<>();
    private static int totalJunctions;
    private static int totalStreets;
    private static int totalTime, totalCars;
    private static int initJunction;
    private static int distanceTravelled;

    public static void main(String[] args) throws IOException {

        boolean run = true;
        while (true) {


            // Initializes config variables
            readInput("input.txt");


            // Separate function to establish streets for each junction since there are unidirectional streets
            setJunctionStreets();
            // try{
            // }
            // else {throw new InstantiationError("failed\n");}

            // for (Junction j : junctions) j.print();
            System.out.println("total Junctions : " + totalJunctions);
            System.out.println("total Streets : " + totalStreets);
            System.out.println("total Time : " + totalTime);
            System.out.println("total Cars : " + totalCars);
            System.out.println("initial Junction : " + initJunction);

            // Initializes all cars paths to their starting position
            for (int i = 0; i < totalCars; ++i) {
                Car currentCar = new Car(junctions.get(initJunction));
                currentCar.path2.add(new SubPath(initJunction, totalTime));
                fleet.add(currentCar);
            }

            // Do paths for all cars independently

            for (Car c : fleet)
                c.setTime(totalTime);

            for (Car car : fleet) {
                while (car.getTime() > 0) {
                    // Arbitrarily init the best available street to go through
                    ArrayList<Street> availableStreets = car.junction.getStreets();
                    Street bestStreet = availableStreets.get(randomGenerator.nextInt(availableStreets.size()));

                    // Stop if time's up
                    if ((car.getTime() - bestStreet.getTime()) <= 0) break;

                    car.reduceTime(bestStreet.getTime());

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


            // for (Car car : fleet){ //Print paths
            //     System.out.println(fleet.indexOf(car) + ":");
            //     for(SubPath sp : car.path2){

            //         System.out.print("  (" + sp.getJunction() +",");
            //         System.out.print(sp.getTimeL() + ") ");
            //     }
            //     System.out.println();
            // }

            for (Street street : streets) distanceTravelled += street.isVisited() ? street.getDistance() : 0;

            // System.out.println("distance traveled : " + distanceTravelled);
            // System.out.println(junctions.get(1).getStreets().size());
            System.out.println(factorial(4));
            System.out.println("Which algorthim do you want? \n");
            System.out.println("1. Random (Only 1 iteration - path randomly generated as starting point of every algorthim\n");
            System.out.println("2. Simulated Annealing (Temperature = 60, cooling(per cycle) = -0.25\n");
            System.out.println("3. Taboo Search \n");
            System.out.println("4. Greedy aproach (only changes solution if it is better) \n"); //If it has failed to improve for 10 times breaks
            System.out.println("q. Quit");

            BufferedReader terminalInput = new BufferedReader(new InputStreamReader(System.in));

            //Validating input option
            String input = terminalInput.readLine();
            while (input.charAt(0) != '0' && input.charAt(0) != '1' && input.charAt(0) != '2' && input.charAt(0) != '3'
                    && input.charAt(0) != '4' && input.charAt(0) != 'q') {
                input = terminalInput.readLine();
            }

            //Decision considering input option
            if (input.charAt(0) == 'q') {
                break;
            } else {
                int option = Integer.parseInt(input);

                switch (option) {
                    case 1 -> System.out.println("\nGenerating random paths...\n");
                    case 2 -> SimulatedAnnealing();
                    case 3 -> TabooSearch();
                    case 4 -> GreedyApproach();
                }
            }


            System.out.println("Final Paths : \n");
            for (Car car : fleet) { //Print paths
                System.out.println(fleet.indexOf(car) + ":");
                for (SubPath sp : car.path2) {

                    System.out.print("  (" + sp.getJunction() + ",");
                    System.out.print(sp.getTimeL() + ") ");
                }
                System.out.println();
            }
            System.out.println("distance traveled : " + distanceTravelled);

            writeOutput("output.txt");
            writeGraphDat("graph.dat");
            writePathsDat("path");

            System.out.println("Press Enter key to continue...");
            terminalInput.readLine();

            //RESET VALUES
            junctions = new ArrayList<>();
            streets = new ArrayList<>();
            fleet = new ArrayList<>();
            totalJunctions = 0;
            totalStreets = 0;
            totalTime = 0;
            totalCars = 0;
            initJunction = 0;
            distanceTravelled = 0;

        }//END WHILE

    }

    @SuppressWarnings("unchecked")
    public static void SimulatedAnnealing() {

        //Reset temperatures.txt if exists
        ResetTempFile("temperatures.txt");

        //Empty streets
        ArrayList<Street> EmptyStreets = (ArrayList<Street>) streets.clone();

        double temperature = 60;

        while (temperature > 0) {

            //Creates copys of original solution
            ArrayList<Junction> junctionsAux = new ArrayList<>();
            ArrayList<Street> streetsAux = new ArrayList<>();
            ArrayList<Car> fleetAux = new ArrayList<>();

            junctionsAux.addAll(junctions);
            streetsAux.addAll(EmptyStreets);
            fleetAux.addAll(fleet);

            writeTempNumbers("temperatures.txt", Integer.toString(distanceTravelled));

            //Print Current path of choice
            System.out.println("INITAL PATHS");
            for (Car car : fleet) { //Print paths
                System.out.println(fleet.indexOf(car) + ":");
                for (SubPath sp : car.path2) {

                    System.out.print("  (" + sp.getJunction() + ",");
                    System.out.print(sp.getTimeL() + ") ");
                }
                System.out.println();

            }
            System.out.println("(Current) Distance Travalled : " + distanceTravelled);

            for (Street s : streetsAux)
                s.unVisit();

            //Pick a random CAR 
            int car2Choose = randomGenerator.nextInt(fleetAux.size());
            Car auxCar = fleetAux.get(car2Choose);

            //Pick a random JUNCTION
            int junc2Choose = randomGenerator.nextInt(auxCar.path2.size());

            //Checks if a change can be made in that junction, if not select another junction(the came after the randomly picked). If no junction can be changed, move to another car
            while (true) {
                if (junc2Choose > auxCar.path2.size() - 1) {
                    car2Choose = randomGenerator.nextInt(fleetAux.size());
                    auxCar = fleetAux.get(car2Choose);
                    junc2Choose = randomGenerator.nextInt(auxCar.path2.size());
                }

                //If the junction has another street than car can go another way
                if (junctionsAux.get(auxCar.path2.get(junc2Choose).getJunction()).getStreets().size() > 1) break;
                else {
                    junc2Choose++;
                }
            }


            //Repor valores do Carro desde a junction escolhido, isto é cortar o path depois dessa junction
            List<SubPath> tempPathAux = auxCar.path2.subList(0, junc2Choose);
            ArrayList<SubPath> tempPath = new ArrayList<SubPath>();
            tempPath.addAll(tempPathAux);

            //Print random choices
            // System.out.println(auxCar);
            // System.out.println("junc : " + junc2Choose);

            //Set remaining time && and currentJunction 
            if (junc2Choose > 0) {
                auxCar.setTime(tempPath.get(tempPath.size() - 1).getTimeL());
                auxCar.setJunction(junctionsAux.get(tempPath.get(tempPath.size() - 1).getJunction()));
            } else {
                auxCar.setTime(totalTime);
                auxCar.setJunction(junctionsAux.get(0));
            }

            auxCar.setPath(tempPath);

            // Form car new Path
            while (auxCar.getTime() > 0) {
                // Arbitrarily init the random available street to go through
                ArrayList<Street> availableStreets = auxCar.junction.getStreets();
                Street bestStreet = availableStreets.get(randomGenerator.nextInt(availableStreets.size()));

                // Stop if time's up
                if ((auxCar.getTime() - bestStreet.getTime()) < 0) break;

                auxCar.reduceTime(bestStreet.getTime());

                // If current car is located at junction 1, we should go to junction 2
                // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
                auxCar.junction = bestStreet.getJunction(1) == auxCar.junction ? bestStreet.getJunction(2) : bestStreet.getJunction(1);


                auxCar.path2.add(new SubPath(junctionsAux.indexOf(auxCar.junction), auxCar.getTime()));
            }

            // Check for which streets were visited
            for (Car c : fleetAux) {
                int prevJ = -1;
                for (SubPath sp : c.path2) {
                    if (prevJ == -1) prevJ = sp.getJunction();
                    else {
                        for (Street s : streetsAux) {
                            if ((junctionsAux.indexOf(s.getJunction(1)) == prevJ) && (junctionsAux.indexOf((s.getJunction(2))) == sp.getJunction()))
                                s.setVisited(fleetAux.indexOf(c), sp.getTimeL());
                            else if ((junctionsAux.indexOf(s.getJunction(2)) == prevJ) && (junctionsAux.indexOf((s.getJunction(1))) == sp.getJunction()) && s.getDirection() == 2)
                                s.setVisited(fleetAux.indexOf(c), sp.getTimeL());
                        }
                        prevJ = sp.getJunction();
                    }

                }
            }

            // COMPARAR CASOS
            // System.out.println("Comparing cases\n");
            int distanceTravelled2 = 0;
            for (Street street : streetsAux) distanceTravelled2 += street.isVisited() ? street.getDistance() : 0;
            System.out.println("(neighboor)distance traveled : " + distanceTravelled2);

            int difDistance = distanceTravelled2 - distanceTravelled;

            if (distanceTravelled2 >= distanceTravelled) {
                System.out.println("Was a better solution");
                distanceTravelled = distanceTravelled2;
                junctions.clear();
                streets.clear();
                fleet.clear();
                junctions = (ArrayList<Junction>) junctionsAux.clone();
                streets = (ArrayList<Street>) streetsAux.clone();
                fleet = (ArrayList<Car>) fleetAux.clone();
            } else {
                System.out.println("\nWorse Case ...");
                double rNum = randomGenerator.nextDouble();
                double probab = Math.exp(difDistance / temperature); //PROBABILIDADE (0-100%)
                // System.out.println("difDist = " + difDistance + "\n" + "Temp : " + temperature);
                // System.out.println("Random Double : " + rNum + "\n"+ "Probability" + probab +" ->(e^difDist/Temp)");
                // System.out.println("Rnum <= probab");

                if (rNum <= probab) { //Probability hit - Assume worst case
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

    @SuppressWarnings("unchecked")
    public static void GreedyApproach() {


        //Empty streets
        ArrayList<Street> EmptyStreets = (ArrayList<Street>) streets.clone();

        double numbertrys = 0; //Trys that failed to find a better path

        while (true) {

            //Creates copys of original solution
            ArrayList<Junction> junctionsAux = new ArrayList<>();
            ArrayList<Street> streetsAux = new ArrayList<>();
            ArrayList<Car> fleetAux = new ArrayList<>();

            junctionsAux.addAll(junctions);
            streetsAux.addAll(EmptyStreets);
            fleetAux.addAll(fleet);


            //Print Current path of choice
            System.out.println("INITAL PATHS");
            for (Car car : fleet) { //Print paths
                System.out.println(fleet.indexOf(car) + ":");
                for (SubPath sp : car.path2) {

                    System.out.print("  (" + sp.getJunction() + ",");
                    System.out.print(sp.getTimeL() + ") ");
                }
                System.out.println();

            }
            System.out.println("(Current) Distance Travalled : " + distanceTravelled);

            for (Street s : streetsAux)
                s.unVisit();

            //Pick a random CAR
            int car2Choose = randomGenerator.nextInt(fleetAux.size());
            Car auxCar = fleetAux.get(car2Choose);

            //Pick a random JUNCTION
            int junc2Choose = randomGenerator.nextInt(auxCar.path2.size());

            //Checks if a change can be made in that junction, if not select another junction(the came after the randomly picked). If no junction can be changed, move to another car
            while (true) {
                if (junc2Choose > auxCar.path2.size() - 1) {
                    car2Choose = randomGenerator.nextInt(fleetAux.size());
                    auxCar = fleetAux.get(car2Choose);
                    junc2Choose = randomGenerator.nextInt(auxCar.path2.size());
                }

                //If the junction has another street than car can go another way
                if (junctionsAux.get(auxCar.path2.get(junc2Choose).getJunction()).getStreets().size() > 1) break;
                else {
                    junc2Choose++;
                }
            }


            //Repor valores do Carro desde a junction escolhido, isto é cortar o path depois dessa junction
            List<SubPath> tempPathAux = auxCar.path2.subList(0, junc2Choose);
            ArrayList<SubPath> tempPath = new ArrayList<SubPath>();
            tempPath.addAll(tempPathAux);

            //Print random choices
            // System.out.println(auxCar);
            // System.out.println("junc : " + junc2Choose);

            //Set remaining time && and currentJunction
            if (junc2Choose > 0) {
                auxCar.setTime(tempPath.get(tempPath.size() - 1).getTimeL());
                auxCar.setJunction(junctionsAux.get(tempPath.get(tempPath.size() - 1).getJunction()));
            } else {
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
                if ((auxCar.getTime() - bestStreet.getTime()) < 0) break;

                auxCar.reduceTime(bestStreet.getTime());

                // If current car is located at junction 1, we should go to junction 2
                // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
                auxCar.junction = bestStreet.getJunction(1) == auxCar.junction ? bestStreet.getJunction(2) : bestStreet.getJunction(1);


                auxCar.path2.add(new SubPath(junctionsAux.indexOf(auxCar.junction), auxCar.getTime()));
            }

            //Check for which streets were visited
            for (Car c : fleetAux) {
                int prevJ = -1;
                for (SubPath sp : c.path2) {
                    if (prevJ == -1) prevJ = sp.getJunction();
                    else {
                        for (Street s : streetsAux) {
                            if ((junctionsAux.indexOf(s.getJunction(1)) == prevJ) && (junctionsAux.indexOf((s.getJunction(2))) == sp.getJunction()))
                                s.setVisited(fleetAux.indexOf(c), sp.getTimeL());
                            else if ((junctionsAux.indexOf(s.getJunction(2)) == prevJ) && (junctionsAux.indexOf((s.getJunction(1))) == sp.getJunction()) && s.getDirection() == 2)
                                s.setVisited(fleetAux.indexOf(c), sp.getTimeL());
                        }
                        prevJ = sp.getJunction();
                    }

                }
            }

            // System.out.println("Comparing cases\n");


            //COMPARAR CASOS
            int distanceTravelled2 = 0;
            for (Street street : streetsAux) distanceTravelled2 += street.isVisited() ? street.getDistance() : 0;
            System.out.println("(neighboor)distance traveled : " + distanceTravelled2);

            int difDistance = distanceTravelled2 - distanceTravelled;

            if (distanceTravelled2 >= distanceTravelled) {
                System.out.println("Was a better solution");
                distanceTravelled = distanceTravelled2;
                junctions.clear();
                streets.clear();
                fleet.clear();
                junctions = (ArrayList<Junction>) junctionsAux.clone();
                streets = (ArrayList<Street>) streetsAux.clone();
                fleet = (ArrayList<Car>) fleetAux.clone();

                numbertrys = 0;
            } else {
                numbertrys++;
            }

            if (numbertrys == 10)
                break;


            System.out.println("-----------------------------------");
        }
    }

    @SuppressWarnings("unchecked")
    public static void TabooSearch() {

        //Reset temperatures.txt if exists
        // ResetTempFile("temperatures.txt");

        //Stopping criteria
        double sumDist = 0;
        double sumTime = 0;
        for (Street s : streets) {
            sumDist += s.getDistance();
            sumTime += s.getTime();
        }
        double avgDist = sumDist / streets.size();
        double avgTime = sumTime / streets.size();

        //Average number of streets that can visited in given time
        double avgStreetVis = totalTime / avgTime;

        //Combinations possible C(streets.size() , avgStreetVis)
        double totalPossPaths = factorial(streets.size()) / (factorial(Math.floor(streets.size() - avgStreetVis)) * factorial((avgStreetVis - 1)));
        // System.out.println("street size : " + streets.size());
        // System.out.println("avgStreetvis :" + avgStreetVis);
        // System.out.println(factorial((int) Math.floor(streets.size() - avgStreetVis)));
        // System.out.println(factorial((int) (avgStreetVis-1)));
        System.out.println(totalPossPaths);
        //Average max distance travelled
        double avgStreetVisDist = avgDist * avgStreetVis;

        //Empty streets
        ArrayList<Street> EmptyStreets = (ArrayList<Street>) streets.clone();

        double temperature = 60;
        ArrayList<ArrayList<Junction>> junctionsTaboo = new ArrayList<>();
        ArrayList<ArrayList<Street>> streetsTaboo = new ArrayList<>();
        ArrayList<ArrayList<Car>> fleetTaboo = new ArrayList<>();

        junctionsTaboo.add(junctions);
        streetsTaboo.add(streets);
        fleetTaboo.add(fleet);

        // System.out.println(totalPossPaths + " >=" + junctionsTaboo.size());
        if (totalPossPaths > 1000)
            totalPossPaths = 1000;
        while (10 >= junctionsTaboo.size()) {

            //Creates copys of original solution
            ArrayList<Junction> junctionsAux = new ArrayList<>();
            ArrayList<Street> streetsAux = new ArrayList<>();
            ArrayList<Car> fleetAux = new ArrayList<>();

            junctionsAux.addAll(junctionsTaboo.get(junctionsTaboo.size() - 1));
            streetsAux.addAll(streetsTaboo.get(streetsTaboo.size() - 1));
            fleetAux.addAll(fleetTaboo.get(fleetTaboo.size() - 1));


            // writeTempNumbers("temperatures.txt", Integer.toString(distanceTravelled));

            //Print Current path of choice
            // System.out.println("INITAL PATHS");
            // for (Car car : fleet){ //Print paths
            //     System.out.println(fleet.indexOf(car) + ":");
            //     for(SubPath sp : car.path2){

            //         System.out.print("  (" + sp.getJunction() +",");
            //         System.out.print(sp.getTimeL() + ") ");
            //     }
            //     System.out.println();

            // }
            System.out.println("(Current) Distance Travalled : " + distanceTravelled);

            for (Street s : streetsAux)
                s.unVisit();

            //Pick a random CAR
            int car2Choose = randomGenerator.nextInt(fleetAux.size());
            Car auxCar = fleetAux.get(car2Choose);

            //Pick a random JUNCTION
            int junc2Choose = randomGenerator.nextInt(auxCar.path2.size());

            //Checks if a change can be made in that junction, if not select another junction(the came after the randomly picked). If no junction can be changed, move to another car
            while (true) {
                if (junc2Choose > auxCar.path2.size() - 1) {
                    car2Choose = randomGenerator.nextInt(fleetAux.size());
                    auxCar = fleetAux.get(car2Choose);
                    junc2Choose = randomGenerator.nextInt(auxCar.path2.size());
                }

                //If the junction has another street than car can go another way
                if (junctionsAux.get(auxCar.path2.get(junc2Choose).getJunction()).getStreets().size() > 1)
                    break;
                else {
                    junc2Choose++;
                }
            }


            //Repor valores do Carro desde a junction escolhido, isto é cortar o path depois dessa junction
            List<SubPath> tempPathAux = auxCar.path2.subList(0, junc2Choose);
            ArrayList<SubPath> tempPath = new ArrayList<SubPath>();
            tempPath.addAll(tempPathAux);

            //Print random choices
            // System.out.println(auxCar);
            // System.out.println("junc : " + junc2Choose);

            //Set remaining time && and currentJunction
            if (junc2Choose > 0) {
                auxCar.setTime(tempPath.get(tempPath.size() - 1).getTimeL());
                auxCar.setJunction(junctionsAux.get(tempPath.get(tempPath.size() - 1).getJunction()));
            } else {
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
                if ((auxCar.getTime() - bestStreet.getTime()) < 0) break;

                auxCar.reduceTime(bestStreet.getTime());

                // If current car is located at junction 1, we should go to junction 2
                // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
                auxCar.junction = bestStreet.getJunction(1) == auxCar.junction ?
                        bestStreet.getJunction(2) :
                        bestStreet.getJunction(1);

                //Add new junction to path
                auxCar.path2.add(new SubPath(junctionsAux.indexOf(auxCar.junction), auxCar.getTime()));
            }

            //Check for which streets were visited
            for (Car c : fleetAux) {
                int prevJ = -1;
                for (SubPath sp : c.path2) {
                    if (prevJ == -1)
                        prevJ = sp.getJunction();
                    else {
                        for (Street s : streetsAux) {
                            if ((junctionsAux.indexOf(s.getJunction(1)) == prevJ) && (junctionsAux.indexOf((s.getJunction(2))) == sp.getJunction()))
                                s.setVisited(fleetAux.indexOf(c), sp.getTimeL());
                            else if ((junctionsAux.indexOf(s.getJunction(2)) == prevJ) && (junctionsAux.indexOf((s.getJunction(1))) == sp.getJunction()) && s.getDirection() == 2)
                                s.setVisited(fleetAux.indexOf(c), sp.getTimeL());
                        }
                        prevJ = sp.getJunction();
                    }

                }
            }

            // System.out.println("Comparing cases\n");


            //COMPARAR CASOS
            int distanceTravelled2 = 0;
            for (Street street : streetsAux) distanceTravelled2 += street.isVisited() ? street.getDistance() : 0;
            System.out.println("(neighboor)distance traveled : " + distanceTravelled2);

            int difDistance = distanceTravelled2 - distanceTravelled;

            boolean checkRepetition = isRepeated(fleetTaboo, fleetAux);
            System.out.println(checkRepetition);
            if (!checkRepetition) {
                if (distanceTravelled2 >= distanceTravelled) {
                    System.out.println("Was a better solution");
                    distanceTravelled = distanceTravelled2;
                    junctions.clear();
                    streets.clear();
                    fleet.clear();

                    ArrayList<Junction> toAddJ = (ArrayList<Junction>) junctionsAux.clone();
                    ArrayList<Street> toAddS = (ArrayList<Street>) streetsAux.clone();
                    ArrayList<Car> toAddC = (ArrayList<Car>) fleetAux.clone();

                    junctionsTaboo.add(toAddJ);
                    streetsTaboo.add(toAddS);
                    fleetTaboo.add(toAddC);


                    // junctions = (ArrayList<Junction>) junctionsAux.clone();
                    // streets = (ArrayList<Street>) streetsAux.clone();
                    // fleet = (ArrayList<Car>) fleetAux.clone();
                } else {
                    System.out.println("\nWorse Case ...");
                    // double rNum = randomGenerator.nextDouble();
                    // double probab = Math.exp(difDistance/temperature) ; //PROBABILIDADE (0-100%)
                    // System.out.println("difDist = " + difDistance + "\n" + "Temp : " + temperature);
                    // System.out.println("Random Double : " + rNum + "\n"+ "Probability" + probab +" ->(e^difDist/Temp)");
                    // System.out.println("Rnum <= probab");

                    if (distanceTravelled2 >= distanceTravelled * 0.9) { //Probability hit - Assume worst case
                        System.out.println("PICKED WORSe CASE    \n");
                        distanceTravelled = distanceTravelled2;
                        junctions.clear();
                        streets.clear();
                        fleet.clear();

                        junctionsTaboo.add((ArrayList<Junction>) junctionsAux.clone());
                        streetsTaboo.add((ArrayList<Street>) streetsAux.clone());
                        fleetTaboo.add((ArrayList<Car>) fleetAux.clone());

                        // junctions = (ArrayList<Junction>) junctionsAux.clone();
                        // streets = (ArrayList<Street>) streetsAux.clone();
                        // fleet = (ArrayList<Car>) fleetAux.clone();
                    }
                }
            }

            // if(!junctionsTaboo.contains(junctionsAux))
            // junctionsTaboo.add(junctionsAux);
            // if(!streetsTaboo.contains(streetsAux))
            // streetsTaboo.add((ArrayList<Street>) streetsAux.clone());
            // if(!fleetTaboo.contains(fleetAux))
            // fleetTaboo.add((ArrayList<Car>) fleetAux.clone());

            // junctionsTaboo.add(new ArrayList<Junction>());
            // for(Junction j: (ArrayList<Junction>) junctionsAux.clone()){
            //     junctionsTaboo.get(junctionsTaboo.size() -1).add(j);
            // }
            // double tempSum = 0;
            // streetsTaboo.add(new ArrayList<Street>());
            // for(Street s: (ArrayList<Street>) streetsAux.clone()){
            //     tempSum += s.isVisited() ? s.getDistance() : 0;
            //     streetsTaboo.get(junctionsTaboo.size() -1).add(s);
            // }
            // System.out.println("dist : " + tempSum);

            // fleetTaboo.add(new ArrayList<Car>());
            // for(Car c: (ArrayList<Car>) fleetAux.clone()){
            //     fleetTaboo.get(junctionsTaboo.size() -1).add(c);
            // }


            // temperature -= 0.25;
            System.out.println("-----------------------------------");
        }

        double travlledDIstFInal = 0;
        double prevDist = 0;
        int indexStreet = 0;
        for (ArrayList<Street> list : streetsTaboo) {
            for (Street street : list) travlledDIstFInal += street.isVisited() ? street.getDistance() : 0;
            System.out.println(travlledDIstFInal);

            if (travlledDIstFInal > prevDist) {
                prevDist = travlledDIstFInal;
                indexStreet = streetsTaboo.indexOf(list);
            }
            travlledDIstFInal = 0;
        }

        junctions.clear();
        streets.clear();
        fleet.clear();
        junctions = (ArrayList<Junction>) junctionsTaboo.get(indexStreet).clone();
        streets = (ArrayList<Street>) streetsTaboo.get(indexStreet).clone();
        fleet = (ArrayList<Car>) fleetTaboo.get(indexStreet).clone();


    }

    public static double factorial(double n) {

        if (n == 1)
            return n;
        else
            return n * factorial(n - 1);
    }

    public static boolean isRepeated(ArrayList<ArrayList<Car>> fleetsList, ArrayList<Car> fleetIn) {
        boolean[] isR = new boolean[20000];
        boolean ret = false;
        int b = 0;
        for (ArrayList<Car> singleFleet : fleetsList) {
            isR[b] = true;
            b++;
        }

        b = 0;
        for (ArrayList<Car> singleFleet : fleetsList) {
            //Check fleets
            for (int i = 0; i < singleFleet.size(); i++) {
                if (singleFleet.get(i).path2.size() != fleetIn.get(i).path2.size())
                    isR[b] = false;
                //Check paths
                for (int p = 0; p < singleFleet.get(i).path2.size(); p++) {
                    if (singleFleet.get(i).path2.get(p).getJunction() == fleetIn.get(i).path2.get(p).getJunction()) {

                    } else {
                        isR[b] = false;
                    }
                }

            }
            b++;
        }

        for (ArrayList<Car> singleFleet : fleetsList) {
            if (isR[b]) ret = true;
        }

        return ret;
    }

    public static void writeOutput(String fileName) {

        try {
            FileWriter myWriter = new FileWriter(fileName, false);
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

    public static void writePathsDat(String fileName) {

        for (int i = 0; i < fleet.size(); i++) {
            try {
                FileWriter myWriter = new FileWriter(fileName + i + ".dat", false);
                BufferedWriter bufferedWriter = new BufferedWriter(myWriter);

                bufferedWriter.write("# " + i + "\n");

                ArrayList<SubPath> sp = fleet.get(i).path2;
                for (int j = 0; j < sp.size() / 2; j += 2) {
                    bufferedWriter.write(junctions.get(sp.get(j).getJunction()).getX() + " " + junctions.get(sp.get(j).getJunction()).getY() + " " + sp.get(j).getJunction() + "\n");
                    bufferedWriter.write(junctions.get(sp.get(j + 1).getJunction()).getX() + " " + junctions.get(sp.get(j + 1).getJunction()).getY() + " " + sp.get(j + 1).getJunction() + "\n");
                }
                bufferedWriter.write("\n");

                bufferedWriter.close();
                System.out.println("Successfully wrote to the data file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }

    public static void writeGraphDat(String fileName) {

        try {
            FileWriter myWriter = new FileWriter(fileName, false);
            BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
            int streetCounter = 0;
            for (Street street : streets) { //Print paths
                bufferedWriter.write(street.getJunction(1).getX() + " " + street.getJunction(1).getY() + " " + streetCounter + "\n");
                bufferedWriter.write(street.getJunction(2).getX() + " " + street.getJunction(2).getY() + " " + streetCounter + "\n");
                //bufferedWriter.write("\n");
                ++streetCounter;
            }

            bufferedWriter.close();
            System.out.println("Successfully wrote to the data file.");
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
}