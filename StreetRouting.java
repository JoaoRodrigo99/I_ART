import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class StreetRouting extends InputOutputHelper {
  private static final Random randomGenerator = new Random();
  private static ArrayList<Junction> junctions = new ArrayList<>();
  private static ArrayList<Street> streets = new ArrayList<>();
  private static ArrayList<Car> fleet = new ArrayList<>();
  private static int totalJunctions;
  private static int totalStreets;
  private static int totalTime;
  private static int totalCars;
  private static int initJunction;
  private static int distanceTravelled;

  public static void main(String[] args) throws IOException {

    boolean run = true;
    while (true) {

      // Initializes config variables
      readInput("input.txt");

      // Separate function to establish streets for each junction since there are unidirectional streets
      setJunctionStreets();

      // for (Junction j : junctions) j.print();
      System.out.println("total Junctions : " + getTotalJunctions());
      System.out.println("total Streets : " + getTotalStreets());
      System.out.println("total Time : " + getTotalTime());
      System.out.println("total Cars : " + getTotalCars());
      System.out.println("initial Junction : " + getInitJunction());

      // Initializes all cars paths to their starting position
      for (int i = 0; i < getTotalCars(); ++i) {
        Car currentCar = new Car(getJunctions().get(getInitJunction()));
        currentCar.getPath2().add(new SubPath(getInitJunction(), getTotalTime()));
        getFleet().add(currentCar);
      }

      // Do paths for all cars independently
      for (Car c : getFleet())
        c.setTime(getTotalTime());

      for (Car car : getFleet()) {
        while (car.getTime() > 0) {
          // Arbitrarily init the best available street to go through
          ArrayList<Street> availableStreets = car.getJunction().getStreets();
          Street bestStreet = availableStreets.get(getRandomGenerator().nextInt(availableStreets.size()));

          // Stop if time's up
          if ((car.getTime() - bestStreet.getTime()) <= 0) break;

          car.reduceTime(bestStreet.getTime());

          // Set to visited and update when this car passed through this street
          bestStreet.setVisited(getFleet().indexOf(car), car.getTime());

          // If current car is located at junction 1, we should go to junction 2
          // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
          car.setJunction(bestStreet.getJunction(1) == car.getJunction() ? bestStreet.getJunction(2): bestStreet.getJunction(1));

          car.getPath2().add(new SubPath(getJunctions().indexOf(car.getJunction()), car.getTime()));
        }
      }

      for (Street street : getStreets())
        setDistanceTravelled(getDistanceTravelled() + (street.isVisited() ? street.getDistance(): 0));

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
      while (input.charAt(0) != '0' && input.charAt(0) != '1' && input.charAt(0) != '2' && input.charAt(0) != '3' && input.charAt(0) != '4' && input.charAt(0) != 'q') {
        input = terminalInput.readLine();
      }

      int option;
      //Decision considering input option
      if (input.charAt(0) == 'q') {
        break;
      } else {
        option = Integer.parseInt(input);

        switch (option) {
          case 1 -> System.out.println("\nGenerating random paths...\n");
          case 2 -> SimulatedAnnealing();
          case 3 -> TabooSearch();
          case 4 -> GreedyApproach();
        }
      }

      System.out.println("Final Paths : \n");
      pathPrinter();
      System.out.println("distance traveled : " + getDistanceTravelled());

      writeOutput("output.txt");
      writeGraphDat("paths/graph.dat");

      switch (option) {
        case 2 -> writePathsDat("paths/finalPaths/simulatedAnnealingRandom/path");
        case 3 -> writePathsDat("paths/finalPaths/tabooSearch/path");
        case 4 -> writePathsDat("paths/finalPaths/simulatedAnnealingGreedy/path");
      }

      System.out.println("Press Enter key to continue...");
      terminalInput.readLine();

      //RESET VALUES
      setJunctions(new ArrayList<>());
      setStreets(new ArrayList<>());
      setFleet(new ArrayList<>());
      setTotalJunctions(0);
      setTotalStreets(0);
      setTotalTime(0);
      setTotalCars(0);
      setInitJunction(0);
      setDistanceTravelled(0);
    }
  }

  private static void pathPrinter() {
    for (Car car : getFleet()) { //Print paths
      System.out.println(getFleet().indexOf(car) + ":");
      for (SubPath sp : car.getPath2()) {

        System.out.print("  (" + sp.getJunction() + ",");
        System.out.print(sp.getTimeL() + ") ");
      }
      System.out.println();
    }
  }

  @SuppressWarnings("unchecked")
  public static void SimulatedAnnealing() {

    //Reset temperatures.txt if exists
    ResetTempFile("temperatures.txt");

    //Empty streets
    ArrayList<Street> EmptyStreets = (ArrayList<Street>) getStreets().clone();

    double temperature = 60;

    while (temperature > 0) {

      //Creates copys of original solution
      ArrayList<Junction> junctionsAux = new ArrayList<>(getJunctions());
      ArrayList<Street> streetsAux = new ArrayList<>(EmptyStreets);
      ArrayList<Car> fleetAux = new ArrayList<>(getFleet());

      writeTempNumbers("temperatures.txt", Integer.toString(getDistanceTravelled()));

      //Print Current path of choice
      System.out.println("INITAL PATHS");
      pathPrinter();
      System.out.println("(Current) Distance Travelled : " + getDistanceTravelled());

      for (Street s : streetsAux)
        s.unVisit();

      //Pick a random CAR
      int car2Choose = getRandomGenerator().nextInt(0, fleetAux.size());
      Car auxCar = fleetAux.get(car2Choose);

      //Pick a random JUNCTION
      int junc2Choose = getRandomGenerator().nextInt(auxCar.getPath2().size());

      //Checks if a change can be made in that junction, if not select another junction(the came after the randomly picked). If no junction can be changed, move to another car
      while (true) {
        if (junc2Choose > auxCar.getPath2().size() - 1) {
          car2Choose = getRandomGenerator().nextInt(fleetAux.size());
          auxCar = fleetAux.get(car2Choose);
          junc2Choose = getRandomGenerator().nextInt(auxCar.getPath2().size());
        }

        //If the junction has another street than car can go another way
        if (junctionsAux.get(auxCar.getPath2().get(junc2Choose).getJunction()).getStreets().size() > 1) break;
        else {
          ++junc2Choose;
        }
      }

      //Repor valores do Carro desde a junction escolhido, isto é cortar o path depois dessa junction
      List<SubPath> tempPathAux = auxCar.getPath2().subList(0, junc2Choose);
      ArrayList<SubPath> tempPath = new ArrayList<SubPath>(tempPathAux);

      //Set remaining time && and currentJunction
      setCurrentTimeAndJunction(junctionsAux, auxCar, junc2Choose, tempPath);

      // Form car new Path
      while (auxCar.getTime() > 0) {
        // Arbitrarily init the random available street to go through
        ArrayList<Street> availableStreets = auxCar.getJunction().getStreets();
        Street bestStreet = availableStreets.get(getRandomGenerator().nextInt(availableStreets.size()));

        // Stop if time's up
        if ((auxCar.getTime() - bestStreet.getTime()) < 0) break;

        auxCar.reduceTime(bestStreet.getTime());

        // If current car is located at junction 1, we should go to junction 2
        // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
        auxCar.setJunction(bestStreet.getJunction(1) == auxCar.getJunction() ? bestStreet.getJunction(2): bestStreet.getJunction(1));

        auxCar.getPath2().add(new SubPath(junctionsAux.indexOf(auxCar.getJunction()), auxCar.getTime()));
        writeCurrentPath("simulatedAnnealingRandom", auxCar, car2Choose, auxCar.getPath2().size() - 2);
      }

      visitedCheck(junctionsAux, streetsAux, fleetAux);

      // COMPARAR CASOS
      // System.out.println("Comparing cases\n");
      int distanceTravelled2 = 0;
      for (Street street : streetsAux) distanceTravelled2 += street.isVisited() ? street.getDistance(): 0;
      System.out.println("(neighboor)distance traveled : " + distanceTravelled2);

      int difDistance = distanceTravelled2 - getDistanceTravelled();

      if (distanceTravelled2 >= getDistanceTravelled()) {
        System.out.println("Was a better solution");
        replaceOriginalWithCandidate(junctionsAux, streetsAux, fleetAux, distanceTravelled2);
      } else {
        System.out.println("\nWorse Case ...");
        double randomNumber = getRandomGenerator().nextDouble();
        double probability = Math.exp(difDistance / temperature); //PROBABILIDADE (0-100%)

        if (randomNumber <= probability) { //Probability hit - Assume worst case
          System.out.println("PICKED WORST CASE\n");
          replaceOriginalWithCandidate(junctionsAux, streetsAux, fleetAux, distanceTravelled2);
        }
      }

      temperature -= 0.25;
      System.out.println("-----------------------------------");
    }
  }

  public static void replaceOriginalWithCandidate(ArrayList<Junction> junctionsAux, ArrayList<Street> streetsAux, ArrayList<Car> fleetAux, int distanceTravelled2) {
    setDistanceTravelled(distanceTravelled2);
    getJunctions().clear();
    getStreets().clear();
    getFleet().clear();
    setJunctions((ArrayList<Junction>) junctionsAux.clone());
    setStreets((ArrayList<Street>) streetsAux.clone());
    setFleet((ArrayList<Car>) fleetAux.clone());
  }

  private static void visitedCheck(ArrayList<Junction> junctionsAux, ArrayList<Street> streetsAux, ArrayList<Car> fleetAux) {
    // Check for which streets were visited
    for (Car c : fleetAux) {
      int prevJ = -1;
      for (SubPath sp : c.getPath2()) {
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
  }

  @SuppressWarnings("unchecked")
  public static void GreedyApproach() {
    // Empty streets
    ArrayList<Street> EmptyStreets = (ArrayList<Street>) getStreets().clone();

    // Number of tries that failed to find a better path
    double numberOfTries = 0;

    while (true) {
      // Creates a copy of original solution
      ArrayList<Junction> junctionsAux = new ArrayList<>(getJunctions());
      ArrayList<Street> streetsAux = new ArrayList<>(EmptyStreets);
      ArrayList<Car> fleetAux = new ArrayList<>(getFleet());

      // Print Current path of choice
      System.out.println("INITAL PATHS");
      pathPrinter();
      System.out.println("(Current) Distance Travalled: " + getDistanceTravelled());

      for (Street s : streetsAux)
        s.unVisit();

      //Pick a random CAR
      int car2Choose = getRandomGenerator().nextInt(fleetAux.size());
      Car auxCar = fleetAux.get(car2Choose);

      //Pick a random JUNCTION
      int junc2Choose = getRandomGenerator().nextInt(auxCar.getPath2().size());

      //Checks if a change can be made in that junction, if not select another junction(the came after the randomly picked). If no junction can be changed, move to another car
      while (true) {
        if (junc2Choose > auxCar.getPath2().size() - 1) {
          car2Choose = getRandomGenerator().nextInt(fleetAux.size());
          auxCar = fleetAux.get(car2Choose);
          junc2Choose = getRandomGenerator().nextInt(auxCar.getPath2().size());
        }

        //If the junction has another street than car can go another way
        if (junctionsAux.get(auxCar.getPath2().get(junc2Choose).getJunction()).getStreets().size() > 1) break;
        else {
          junc2Choose++;
        }
      }

      //Repor valores do Carro desde a junction escolhido, isto é cortar o path depois dessa junction
      List<SubPath> tempPathAux = auxCar.getPath2().subList(0, junc2Choose);
      ArrayList<SubPath> tempPath = new ArrayList<SubPath>(tempPathAux);

      //Set remaining time && and currentJunction
      setCurrentTimeAndJunction(junctionsAux, auxCar, junc2Choose, tempPath);

      //Form car new Path
      while (auxCar.getTime() > 0) {
        // Arbitrarily init the random available street to go through
        ArrayList<Street> availableStreets = auxCar.getJunction().getStreets();
        Street bestStreet = availableStreets.get(getRandomGenerator().nextInt(availableStreets.size()));

        // Stop if time's up
        if ((auxCar.getTime() - bestStreet.getTime()) < 0) break;

        auxCar.reduceTime(bestStreet.getTime());

        // If current car is located at junction 1, we should go to junction 2
        // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
        auxCar.setJunction(bestStreet.getJunction(1) == auxCar.getJunction() ? bestStreet.getJunction(2): bestStreet.getJunction(1));

        writeCurrentPath("simulatedAnnealingGreedy", auxCar, car2Choose, auxCar.getPath2().size() - 2);
        auxCar.getPath2().add(new SubPath(junctionsAux.indexOf(auxCar.getJunction()), auxCar.getTime()));
      }

      // Check for which streets were visited
      visitedCheck(junctionsAux, streetsAux, fleetAux);

      //COMPARAR CASOS
      int distanceTravelled2 = 0;
      for (Street street : streetsAux) distanceTravelled2 += street.isVisited() ? street.getDistance(): 0;
      System.out.println("(neighboor) Distance traveled: " + distanceTravelled2);

      int difDistance = distanceTravelled2 - getDistanceTravelled();

      if (distanceTravelled2 >= getDistanceTravelled()) {
        System.out.println("Was a better solution");
        replaceOriginalWithCandidate(junctionsAux, streetsAux, fleetAux, distanceTravelled2);
        numberOfTries = 0;
      } else {
        numberOfTries++;
      }

      if (numberOfTries == 10) break;

      System.out.println("-----------------------------------");
    }
  }

  @SuppressWarnings("unchecked")
  public static void TabooSearch() {
    // Stopping criteria
    double sumDist = 0;
    double sumTime = 0;
    for (Street s : getStreets()) {
      sumDist += s.getDistance();
      sumTime += s.getTime();
    }
    double avgDist = sumDist / getStreets().size();
    double avgTime = sumTime / getStreets().size();

    // Average number of streets that can visited in given time
    double avgStreetVis = getTotalTime() / avgTime;

    // Combinations possible C(streets.size() , avgStreetVis)
    double totalPossPaths = factorial(getStreets().size()) / (factorial(Math.floor(getStreets().size() - avgStreetVis)) * factorial((avgStreetVis - 1)));
    System.out.println(totalPossPaths);

    // Empty streets
    ArrayList<Street> EmptyStreets = (ArrayList<Street>) getStreets().clone();
    ArrayList<ArrayList<Junction>> junctionsTaboo = new ArrayList<>();
    ArrayList<ArrayList<Street>> streetsTaboo = new ArrayList<>();
    ArrayList<ArrayList<Car>> fleetTaboo = new ArrayList<>();

    junctionsTaboo.add(getJunctions());
    streetsTaboo.add(getStreets());
    fleetTaboo.add(getFleet());

    // System.out.println(totalPossPaths + " >=" + junctionsTaboo.size());
    if (totalPossPaths > 1000) totalPossPaths = 1000;
    for (Car auxCar: (ArrayList<Car>) fleetTaboo.get(0).clone()) {
      // Create copies of the original solution
      ArrayList<Junction> junctionsAux = (ArrayList<Junction>) junctionsTaboo.get(junctionsTaboo.size()-1).clone();
      ArrayList<Street> streetsAux = (ArrayList<Street>) streetsTaboo.get(junctionsTaboo.size()-1).clone();
      ArrayList<Car> fleetAux = (ArrayList<Car>) fleetTaboo.get(junctionsTaboo.size()-1).clone();

      System.out.println("(Current) Distance Travelled: " + getDistanceTravelled());
      for (Street s : streetsAux) s.unVisit();

      for (int junc2Choose = 0; junc2Choose < auxCar.getPath2().size() - 1; ++junc2Choose) {
        if (!(junctionsAux.get(auxCar.getPath2().get(junc2Choose).getJunction()).getStreets().size() > 1)) continue;

        // Repor valores do Carro desde a junction escolhido, isto é cortar o path depois dessa junction
        List<SubPath> tempPathAux = auxCar.getPath2().subList(0, junc2Choose);
        ArrayList<SubPath> tempPath = new ArrayList<>(tempPathAux);

        // Set remaining time && and currentJunction
        setCurrentTimeAndJunction(junctionsAux, auxCar, junc2Choose, tempPath);

        //Form car new Path
        while (auxCar.getTime() > 0) {
          // Arbitrarily init the random available street to go through
          ArrayList<Street> availableStreets = auxCar.getJunction().getStreets();
          Street bestStreet = availableStreets.get(getRandomGenerator().nextInt(availableStreets.size()));
          int maxStreetDistance = bestStreet.getDistance();
          for (Street street: availableStreets) {
            if (street.getDistance() > maxStreetDistance && !street.isVisited()) {
              maxStreetDistance = street.getDistance();
              bestStreet = street;
            }
          }

          // Stop if time's up
          if ((auxCar.getTime() - bestStreet.getTime()) < 0) break;

          auxCar.reduceTime(bestStreet.getTime());

          // If current car is located at junction 1, we should go to junction 2
          // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
          auxCar.setJunction(bestStreet.getJunction(1) == auxCar.getJunction() ? bestStreet.getJunction(2): bestStreet.getJunction(1));

          //Add new junction to path
          auxCar.getPath2().add(new SubPath(junctionsAux.indexOf(auxCar.getJunction()), auxCar.getTime()));
        }

        // Check for which streets were visited
        visitedCheck(junctionsAux, streetsAux, fleetAux);

        //COMPARAR CASOS
        int candidateDistanceTravelled = 0;
        for (Street street : streetsAux) candidateDistanceTravelled += street.isVisited() ? street.getDistance(): 0;
        System.out.println("(neighboor) Distance traveled: " + candidateDistanceTravelled);

        boolean checkRepetition = fleetTaboo.stream().allMatch(fleetTabooSingle -> fleetTabooSingle.equals(fleetAux));
        System.out.println(checkRepetition);
        if (!checkRepetition && candidateDistanceTravelled >= getDistanceTravelled()) {
          System.out.println("Was a better solution");
          setDistanceTravelled(candidateDistanceTravelled);
          getJunctions().clear();
          getStreets().clear();
          getFleet().clear();

          ArrayList<Junction> toAddJ = (ArrayList<Junction>) junctionsAux.clone();
          ArrayList<Street> toAddS = (ArrayList<Street>) streetsAux.clone();
          ArrayList<Car> toAddC = (ArrayList<Car>) fleetAux.clone();

          junctionsTaboo.add(toAddJ);
          streetsTaboo.add(toAddS);
          fleetTaboo.add(toAddC);
        } else {
          System.out.println("\nWorse Case ...");
          if (candidateDistanceTravelled >= getDistanceTravelled()) { //Probability hit - Assume worst case
            System.out.println("PICKED AN WORSE CASE BUT BETTER DISTANCE\n");
            setDistanceTravelled(candidateDistanceTravelled);
            getJunctions().clear();
            getStreets().clear();
            getFleet().clear();

            junctionsTaboo.add((ArrayList<Junction>) junctionsAux.clone());
            streetsTaboo.add((ArrayList<Street>) streetsAux.clone());
            fleetTaboo.add((ArrayList<Car>) fleetAux.clone());
          }
        }

        System.out.println("-----------------------------------");
      }
    }

    double finalDistanceTravelled = 0;
    double prevDist = 0;
    int indexStreet = 0;
    for (ArrayList<Street> list : streetsTaboo) {
      for (Street street : list) finalDistanceTravelled += street.isVisited() ? street.getDistance(): 0;
      System.out.println(finalDistanceTravelled);

      if (finalDistanceTravelled > prevDist) {
        prevDist = finalDistanceTravelled;
        indexStreet = streetsTaboo.indexOf(list);
      }
      finalDistanceTravelled = 0;
    }

    getJunctions().clear();
    getStreets().clear();
    getFleet().clear();
    setJunctions((ArrayList<Junction>) junctionsTaboo.get(indexStreet).clone());
    setStreets((ArrayList<Street>) streetsTaboo.get(indexStreet).clone());
    setFleet((ArrayList<Car>) fleetTaboo.get(indexStreet).clone());

  }

  public static void setCurrentTimeAndJunction(ArrayList<Junction> junctionsAux, Car auxCar, int junc2Choose, ArrayList<SubPath> tempPath) {
    if (junc2Choose > 0) {
      auxCar.setTime(tempPath.get(tempPath.size() - 1).getTimeL());
      auxCar.setJunction(junctionsAux.get(tempPath.get(tempPath.size() - 1).getJunction()));
    } else {
      auxCar.setTime(getTotalTime());
      auxCar.setJunction(junctionsAux.get(0));
    }

    auxCar.setPath(tempPath);
  }

  public static double factorial(double n) {
    if (n == 1) {
      return n;
    }
    return n * factorial(n - 1);
  }

  public static void setJunctionStreets() {

    for (Junction j : getJunctions()) {
      for (Street s : getStreets()) {
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

  public static Random getRandomGenerator() {
    return randomGenerator;
  }

  public static ArrayList<Junction> getJunctions() {
    return junctions;
  }

  public static void setJunctions(ArrayList<Junction> junctions) {
    StreetRouting.junctions = junctions;
  }

  public static ArrayList<Street> getStreets() {
    return streets;
  }

  public static void setStreets(ArrayList<Street> streets) {
    StreetRouting.streets = streets;
  }

  public static ArrayList<Car> getFleet() {
    return fleet;
  }

  public static void setFleet(ArrayList<Car> fleet) {
    StreetRouting.fleet = fleet;
  }

  public static int getTotalJunctions() {
    return totalJunctions;
  }

  public static void setTotalJunctions(int totalJunctions) {
    StreetRouting.totalJunctions = totalJunctions;
  }

  public static int getTotalStreets() {
    return totalStreets;
  }

  public static void setTotalStreets(int totalStreets) {
    StreetRouting.totalStreets = totalStreets;
  }

  public static int getTotalTime() {
    return totalTime;
  }

  public static void setTotalTime(int totalTime) {
    StreetRouting.totalTime = totalTime;
  }

  public static int getTotalCars() {
    return totalCars;
  }

  public static void setTotalCars(int totalCars) {
    StreetRouting.totalCars = totalCars;
  }

  public static int getInitJunction() {
    return initJunction;
  }

  public static void setInitJunction(int initJunction) {
    StreetRouting.initJunction = initJunction;
  }

  public static int getDistanceTravelled() {
    return distanceTravelled;
  }

  public static void setDistanceTravelled(int distanceTravelled) {
    StreetRouting.distanceTravelled = distanceTravelled;
  }
}