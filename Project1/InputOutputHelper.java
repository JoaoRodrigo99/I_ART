import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class InputOutputHelper {
  public static void writeOutput(String fileName) {

    try {
      FileWriter myWriter = new FileWriter(fileName, false);
      BufferedWriter bufferedWriter = new BufferedWriter(myWriter);

      bufferedWriter.write(StreetRouting.getFleet().size() + "\n");
      for (Car c : StreetRouting.getFleet()) {
        bufferedWriter.write(c.getPath2().size() + "\n");
        for (int i = 0; i < c.getPath2().size(); i++) {
          bufferedWriter.write(c.getPath2().get(i).getJunction() + "\n");
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

    for (int i = 0; i < StreetRouting.getFleet().size(); i++) {
      try {
        FileWriter myWriter = new FileWriter(fileName + i + ".dat", false);
        BufferedWriter bufferedWriter = new BufferedWriter(myWriter);

        bufferedWriter.write("# " + i + "\n");
        bufferedWriter.write("#x1 y1 x2 y2 id\n");

        ArrayList<SubPath> sp = StreetRouting.getFleet().get(i).getPath2();
        for (int j = 0; j < sp.size() - 1; j += 2) {
          bufferedWriter.write(StreetRouting.getJunctions().get(sp.get(j).getJunction()).getX() + " " + StreetRouting.getJunctions().get(sp.get(j).getJunction()).getY() + " ");
          bufferedWriter.write(StreetRouting.getJunctions().get(sp.get(j + 1).getJunction()).getX() + " " + StreetRouting.getJunctions().get(sp.get(j + 1).getJunction()).getY() + " " + sp.get(j).getJunction() + " " + sp.get(j + 1).getJunction() + "\n");
        }

        bufferedWriter.close();
        System.out.println("Successfully wrote to the data file.");
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
    }
  }

  public static void writeCurrentPath(String algorithm, Car currentCar, int carNumber, int iterationNumber) {

    try {
      String filename = "paths/car" + carNumber + "/" + algorithm + "/" + "iteration-" + iterationNumber;
      FileWriter myWriter = new FileWriter(filename + ".dat", false);
      BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
      bufferedWriter.write("#x1 y1 x2 y2 id\n");

      ArrayList<SubPath> sp = currentCar.getPath2();
      for (int j = 0; j < sp.size() - 1; j += 1) {
        bufferedWriter.write(StreetRouting.getJunctions().get(sp.get(j).getJunction()).getX() + " " + StreetRouting.getJunctions().get(sp.get(j).getJunction()).getY() + " " + StreetRouting.getJunctions().get(sp.get(j + 1).getJunction()).getX() + " " + StreetRouting.getJunctions().get(sp.get(j + 1).getJunction()).getY() + " " + sp.get(j).getJunction() + " " + sp.get(j + 1).getJunction() + "\n");
      }

      bufferedWriter.close();
      System.out.println("Successfully wrote to the data file.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  public static void writeGraphDat(String fileName) {

    try {
      FileWriter myWriter = new FileWriter(fileName, false);
      BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
      bufferedWriter.write("#x1 y1 x2 y2 id\n");

      for (Street street : StreetRouting.getStreets()) { //Print paths
        if (street.getDirection() == 1)
          bufferedWriter.write(street.getJunction(1).getX() + " " + street.getJunction(1).getY() + " " + street.getJunction(2).getX() + " " + street.getJunction(2).getY() + " " + StreetRouting.getJunctions().indexOf(street.getJunction(1)) + " " + StreetRouting.getJunctions().indexOf(street.getJunction(2)) + "\n");
        else {
          bufferedWriter.write(street.getJunction(1).getX() + " " + street.getJunction(1).getY() + " " + street.getJunction(2).getX() + " " + street.getJunction(2).getY() + " " + StreetRouting.getJunctions().indexOf(street.getJunction(1)) + " " + StreetRouting.getJunctions().indexOf(street.getJunction(2)) + "\n");
          bufferedWriter.write(street.getJunction(2).getX() + " " + street.getJunction(2).getY() + " " + street.getJunction(1).getX() + " " + street.getJunction(1).getY() + " " + StreetRouting.getJunctions().indexOf(street.getJunction(2)) + " " + StreetRouting.getJunctions().indexOf(street.getJunction(1)) + "\n");
        }
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
      StreetRouting.setTotalJunctions(Integer.parseInt(config[0]));
      StreetRouting.setTotalStreets(Integer.parseInt(config[1]));
      StreetRouting.setTotalTime(Integer.parseInt(config[2]));
      StreetRouting.setTotalCars(Integer.parseInt(config[3]));
      StreetRouting.setInitJunction(Integer.parseInt(config[4]));

      int cycleCount = 1;
      while (myReader.hasNextLine()) {
        data = myReader.nextLine();
        if (cycleCount <= StreetRouting.getTotalJunctions()) {
          String[] junctionStr = data.split(" ");
          StreetRouting.getJunctions().add(new Junction(Double.parseDouble(junctionStr[0]), Double.parseDouble(junctionStr[1])));
        } else {
          String[] streetStr = data.split(" ");
          StreetRouting.getStreets().add(new Street(StreetRouting.getJunctions().get(Integer.parseInt(streetStr[0])), StreetRouting.getJunctions().get(Integer.parseInt(streetStr[1])), Integer.parseInt(streetStr[2]), Integer.parseInt(streetStr[3]), Integer.parseInt(streetStr[4])));
        }

        ++cycleCount;
      }

      myReader.close();

    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

  }
}
