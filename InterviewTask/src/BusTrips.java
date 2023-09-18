import java.io.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BusTrips {
    private static final String FILE_PATH_STOPS = "./data/stops.txt";
    private static final String FILE_PATH_STOP_TIMES = "./data/stop_times.txt";
    private static final String FILE_PATH_TRIPS = "./data/trips.txt";
    private static final LocalDateTime currentTime = LocalDateTime.now();


    public static void main(String[] args) {
        System.out.println(getNameOfStop(args[0]));
        nextStops(args[0], Integer.parseInt(args[1]),args[2]);
    }

    public static String getNameOfStop(String id) {
        File file = new File(FILE_PATH_STOPS);
        try(BufferedReader br = new BufferedReader((new FileReader(file)))){
            String line = br.readLine();
            while ( line != null){
                String[] busStop = line.split(",");
                if (busStop[0].equals(id)){ //checks if wanted id is the same as in the file
                    return busStop[2]; //return name of bus stop
                }
                line = br.readLine();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return "Stop not found";
    }

    public static void nextStops(String stopId, int howManyBuses, String displayOfTime){
        File file = new File(FILE_PATH_STOP_TIMES);
        TripArrivalTime[] tripArrivalTimes = new TripArrivalTime[howManyBuses]; //new array to save arrival times and stopID
        try(BufferedReader br = new BufferedReader((new FileReader(file)))){
            String line = br.readLine();
            line = br.readLine();
            while (line != null){
                String[] busStop = line.split(",");
                String arrivalTime = busStop[1]; //arrival time of the bus stop
                String tripId = busStop[0]; //name of the ID
                long minuteDifference = compareTimes(arrivalTime);
                if (busStop[3].equals(stopId) && minuteDifference < 120 && minuteDifference>0){ //checks if it's the correct ID and if it's in the correct time interval
                    TripArrivalTime t1 = new TripArrivalTime(tripId,arrivalTime,minuteDifference);
                    insertTime(tripArrivalTimes,t1);
                }
                line = br.readLine();
            }
            Map<String, List<String>> routeTimes = new HashMap<>();
            for (TripArrivalTime t : tripArrivalTimes){
                routeTimes.putIfAbsent(getRouteId(t.tripId),new ArrayList<>());
                routeTimes.get(getRouteId(t.tripId)).add(t.arrivalTime);
            }
            switch (displayOfTime){
                case "absolute":
                    for (String routeID : routeTimes.keySet()) {
                        System.out.print(routeID+": ");
                        for (String arrivalTime: routeTimes.get(routeID)){
                            System.out.print(arrivalTime.substring(0,5)+" ");
                        }
                        System.out.println();
                    }
                    break;

                case "relative":
                    for (String routeID : routeTimes.keySet()) {
                        System.out.print(routeID+": ");
                        for (String arrivalTime: routeTimes.get(routeID)){
                            long arrivalTimeInMin = compareTimes(arrivalTime);
                            System.out.print(arrivalTimeInMin+"min ");
                        }
                        System.out.println();
                    }
                    break;
                default:
                    System.out.println("Please enter valid input such as relative or absolute");
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public static long compareTimes(String time){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss");
        String[] formatedCurrentTimeBetween = dtf.format(currentTime).split(" ");
        String formatedCurrentTime = formatedCurrentTimeBetween[1]; //CURRENT TIME IN STRING
        LocalTime start = LocalTime.parse(formatedCurrentTime,DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalTime stop = LocalTime.parse(time,DateTimeFormatter.ofPattern("HH:mm:ss"));
        Duration duration  = Duration.between(start,stop);
        long minutes = duration.getSeconds()/ 60;
        return minutes;
        }

    private static void insertTime(TripArrivalTime[] array, TripArrivalTime t){
        for (int i =0; i < array.length;i++){
            if ( array[i] == null || t.arrivalTimeInMinutes < array[i].arrivalTimeInMinutes ){
                TripArrivalTime temp = array[i];
                array[i] = t;
                if (temp == null){
                    break;
                }
                t = temp;
            }
        }
        }

    private static String getRouteId(String tripId){
        File file = new File(FILE_PATH_TRIPS);
        try (BufferedReader br = new BufferedReader((new FileReader(file)))){
            String line = br.readLine();
            line = br.readLine();
            while (line != null){
                String[] array = line.split(",");
                if (array[2].equals(tripId)){
                    return array[0];
                }
                line = br.readLine();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}




class TripArrivalTime{
    String tripId;
    String arrivalTime;

    Long arrivalTimeInMinutes;

    public TripArrivalTime(String tripId, String arrivalTime, long arrivalTimeInMinutes) {
        this.tripId = tripId;
        this.arrivalTime = arrivalTime;
        this.arrivalTimeInMinutes = arrivalTimeInMinutes;
    }

}



