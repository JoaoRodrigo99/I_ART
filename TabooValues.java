// import java.util.ArrayList;

// public class TabooValues {
    
//     private ArrayList<ArrayList<Junction>> junctionsList = new ArrayList<>();
//     private ArrayList<ArrayList<Street>> streetsList = new ArrayList<>();
//     private ArrayList<ArrayList<Car>> fleetsList = new ArrayList<>();
//     private boolean[] isR;

//     public TabooValues(){
//         junctionsList = new ArrayList<>();
//         streetsList = new ArrayList<>();
//         fleetsList = new ArrayList<>();
//     }

//     public int getSize(){
//         return junctionsList.size();
//     }

//     public void addLists(ArrayList<Junction> junctions, ArrayList<Street> streets, ArrayList<Car> cars){

//         ArrayList<Junction> newJ = new ArrayList<>();
//         for(Junction j : junctions){
//             newJ.add(new Junction(j.getX(), j.getY()))
//         }


//     }

//     public boolean isRepeated(ArrayList<Car> fleetIn){
        
//         boolean ret = false;
//         int b = 0;
//         for(ArrayList<Car> singleFleet : fleetsList){
//             isR[b] = true;
//             b++;
//         }

//         b = 0;
//         for(ArrayList<Car> singleFleet : fleetsList){
//             //Check fleets
//             for(int i = 0; i < singleFleet.size(); i++){
//                 if(singleFleet.get(i).path2.size() != fleetIn.get(i).path2.size())
//                     isR[b] = false;
//                 //Check paths
//                 for(int p = 0; p < singleFleet.get(i).path2.size(); p++){
//                     if(singleFleet.get(i).path2.get(p).getJunction() == fleetIn.get(i).path2.get(p).getJunction()){

//                     }
//                     else{
//                         isR[b] = false;;
//                     }
//                 }

//             }
//             b++;
//         }

//         for(ArrayList<Car> singleFleet : fleetsList){
//             if(isR[b] == true)
//                 ret = true;
            
//         }

//         return ret;
//     }
// }
