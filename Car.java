public class Car {
    
    public Junction junction;
    public String path;

    public Car(Junction j)  {
        junction = j;
        path = "";
    }

    public int numberJunctions()    {
        // int n = 0;
        // String aux = new String();
        
        // for(int i = 0; i < path.length() ; i++){
        //     if( aux.contains( path.subSequence(i,i+1))){
        //         //Already counted that junction
                
        //     }
        //     else{
        //         n++;
        //         aux +=path.subSequence(i,i+1);
        //     }
        // }


        return path.length() ;
    }

}