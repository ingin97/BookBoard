package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    /*
     *  Klassen som holder styr på brukeren som er innlogget.
     *  Variabler:
     *      _bruker - Innlogget brukernavn
     *      _rolle - Roller en bruker har. Liste på formen: [[EmneID,Rolle][EmneID,Rolle]..]
     *      _aktivtEmne - Aktivt emne
     *  Metoder:
     *      checkLogin(username, password)
     *          -Sjekker om brukernavn og passord stemmer overens med det som ligger i databasen
     *      booking(String dato, String tidspunkt, String studass)
     *          -Booker for gjeldende bruker
     *      addStudassPåSal(String dato, String tidspunkt, String emneid, String varighet)
     *          -Legger til tid på sal for studass og sjekker at dette ligger innenfor oppgitt saltid.
     *      checkTime(String før, String etter)
     *          -Sjekker hva som er først av tidspunkt
     *      addSaltid(String dato, String fra, String til, String emneid, String tid)
     *          -Legger til saltid
     */


    public static String _bruker = "";
    public static String _aktivtEmne = "";
    public static String _aktivRolle = "";
    public static ArrayList<ArrayList<String>> _rolle = new ArrayList<ArrayList<String>>();

    public static String Input(String what) {
        BufferedReader br = null;
        String input = "";
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter " + what + ":");
            input = br.readLine();
            return input;
        }catch (IOException e){
            System.err.println("Your input was invalid: "+e.getMessage());
        }
        return input;
    }
    //Sjekker om brukernavn og passord stemmer overens med det som ligger i databasen
    public static boolean checkLogin(String username, String password) {

        if(DatabaseController.checkLogin(username,password)){
            System.out.println("Login success!");
            ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getBruker(username);
            if (dbOutput.isEmpty()){
                System.err.println("Enda ikke lagt til en rolle til et fag.");
                _bruker = username;
                _aktivRolle = "";
                _aktivtEmne = "";
                _rolle = new ArrayList<ArrayList<String>>();

            }else{
                String rolle = "Rolle: ";
                ArrayList<ArrayList<String>> roller = new ArrayList<ArrayList<String>>();
                for (HashMap<String,ArrayList<String>> set : dbOutput) {
                    for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                        _bruker = entry.getKey();
                        ArrayList<String> values = entry.getValue();
                        ArrayList<String> r = new ArrayList<String>();
                        r.add(values.get(2));
                        r.add(values.get(1));
                        roller.add(r);
                    }
                }
                for (ArrayList<String> role : roller){
                    for (String r : role){
                        rolle += r + ": ";
                    }
                    rolle += "| ";
                }
                _rolle = roller;

                System.out.println(rolle);
                DatabaseController.rsToString(dbOutput);
            }
            return true;
        }
        else {
            System.out.println("Login failed!");
            return false;
        }
    }
    //Oppdaterer rollene til brukeren
    public static boolean updateRoller() {
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getBruker(_bruker);
        if (dbOutput.isEmpty()) {
            return false;

        } else {
            ArrayList<ArrayList<String>> roller = new ArrayList<ArrayList<String>>();
            for (HashMap<String, ArrayList<String>> set : dbOutput) {
                for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                    _bruker = entry.getKey();
                    ArrayList<String> values = entry.getValue();
                    ArrayList<String> r = new ArrayList<String>();
                    r.add(values.get(2));
                    r.add(values.get(1));
                    roller.add(r);
                }
            }
            _rolle = roller;
            return true;
        }
    }
    //Om det ikke allerede finst en booking så booker den tiden som er sendt inn
    public static boolean booking(String dato, String tidspunkt, String studass){
        ArrayList<HashMap<String,ArrayList<String>>> booking = DatabaseController.getUnikBooking(dato, tidspunkt, studass);
        if (!booking.isEmpty()){
            return false;
        }
        ArrayList<HashMap<String,ArrayList<String>>> sps = DatabaseController.getUnikStudassPåSal(dato, _aktivtEmne, tidspunkt, studass);

        return DatabaseController.addBooking(DatabaseController.getBookingID(), _bruker, dato, tidspunkt, studass, _aktivtEmne);
    }
    //Legger til tid på sal for studass og sjekker at dette ligger innenfor oppgitt saltid.
    public static boolean addStudassPåSal(String dato, String tidspunkt, String varighet) {
        ArrayList<HashMap<String,ArrayList<String>>> dbOutput = DatabaseController.getSaltid(dato, _aktivtEmne);
        int lengde = Integer.parseInt(varighet);
        boolean ok = false;
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> val = entry.getValue();
                int intervall = Integer.parseInt(val.get(2));
                while(lengde >= intervall){
                    if (Check.checkTime(val.get(0), tidspunkt) && Check.checkTime(tidspunkt, val.get(1))){
                        ok = DatabaseController.addStudassPåSal(dato, tidspunkt, _aktivtEmne, _bruker, intervall);

                        lengde -= intervall;
                        int hh = Integer.parseInt(tidspunkt.substring(0,2));
                        int mm = Integer.parseInt(tidspunkt.substring(3));
                        mm += intervall;
                        if (mm >= 60){
                            mm = mm-60;
                            hh += 1;
                        }
                        if (mm == 0){
                            tidspunkt = hh + ":00";
                        }else {
                            tidspunkt = hh + ":" + mm;
                        }

                    }else {
                        break;
                    }
                }

            }
        }
        return ok;
    }
    // Sjekker at før faktisk er før etter

    //Legger til saltid
    public static boolean addSaltid(String dato, String fra, String til, String tid) {
        return (DatabaseController.addSaltid(dato, fra, til, _aktivtEmne, Integer.parseInt(tid) , _bruker));
    }
}
