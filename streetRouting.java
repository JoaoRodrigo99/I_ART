import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class streetRouting {

    private static final ArrayList<Junction> junctions = new ArrayList<>();
    private static final ArrayList<Street> streets = new ArrayList<>();
    private static final ArrayList<Car> fleet = new ArrayList<>();
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
        boolean probe = false;
        int delta = totalTime;
        Random randomGenerator = new Random();
        do {
            for (Car car : fleet) {
                // Arbitrarily init the best available street to go through
                ArrayList<Street> availableStreets = car.junction.getStreets();
                Street bestStreet = availableStreets.get(randomGenerator.nextInt(availableStreets.size()));

                // Stop if time's up
                delta -= bestStreet.getTime();
                if (delta <= 0) break;

                // Set to visited and update when this car passed through this street
                bestStreet.setVisited(fleet.indexOf(car), delta);

                // If current car is located at junction 1, we should go to junction 2
                // Otherwise, go to junction 1 (it's implied that we are currently in junction 2)
                car.junction = bestStreet.getJunction(1) == car.junction ?
                        bestStreet.getJunction(2) :
                        bestStreet.getJunction(1);
                car.path += Integer.toString(junctions.indexOf(car.junction));

                probe = car.equals(fleet.get(fleet.size() - 1));
            }

        } while (!probe);

        for (Car car : fleet) System.out.println(car.path);
        for (Street street : streets) distanceTravelled += street.isVisited() ? street.getDistance() : 0;

        System.out.println(distanceTravelled);
        System.out.println(junctions.get(1).getStreets().size());

        writeOutput("output.txt");
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