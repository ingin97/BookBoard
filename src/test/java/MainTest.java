/*import org.junit.jupiter.api.Test;
 */


import static org.junit.Assert.*;

import app.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainTest {
    /*
     * Hoved testklasse. Tester ved bruk av JUnit.
     */

    private static String brukernavn = "truls";
    private static String passord = "123";
    private static String emne = "ABC1234";

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dato = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat tidspunkt = new SimpleDateFormat("HH-mm");

    @org.junit.BeforeClass
    public static void opprettEmne() {
        try {
            DatabaseController.addEmne(emne, "Test emne");
            ArrayList<HashMap<String, ArrayList<String>>> user = DatabaseController.getBruker("Truls");
            for (HashMap<String,ArrayList<String>> set : user) {
                for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                    String key = entry.getKey();
                    assertEquals(brukernavn, key);
                }
            }
        }catch(Exception e){
            fail();
        }
    }

    @org.junit.Before
    public void opprettBruker() {
        UserManager._aktivtEmne= emne;
        UserManager._bruker=brukernavn;
        try {
            DatabaseController.addBruker(brukernavn, "Truls", passord);
            ArrayList<HashMap<String, ArrayList<String>>> user = DatabaseController.getBruker("Truls");
            for (HashMap<String,ArrayList<String>> set : user) {
                for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                    String key = entry.getKey();
                    assertEquals(brukernavn, key);
                }
            }
        }catch(Exception e){
            fail();
        }
    }
    @org.junit.After
    public void slettBruker() {
        try {
            assertTrue(DatabaseController.deleteBruker(brukernavn));
        }catch(Exception e){
            fail();
        }
    }
    @org.junit.AfterClass
    public static void slettEmne() {
        try {
            assertTrue(DatabaseController.deleteEmne(emne));
        }catch(Exception e){
            fail();
        }
    }
    @org.junit.Test
    public void sjekkSjekker() {
        assertTrue(Check.checkDato("2019-03-02"));
        assertFalse(Check.checkDato("2019-03-"));
        assertTrue(Check.checkTidspunkt("12:30"));
        assertFalse(Check.checkTidspunkt("1200"));
        assertTrue(Check.checkTime("12:00", "13:00"));
        assertFalse(Check.checkTime("12:00", "11:00"));
        assertTrue(Check.future("2030-01-01"));
        assertFalse(Check.future("1970-01-01"));
    }
    @org.junit.Test
    public void sjekkLogin() {
        try {
            assertTrue(UserManager.checkLogin(brukernavn, passord));
            assertEquals(UserManager._bruker, brukernavn);
        }catch(Exception e){
            fail();
        }
    }
    @org.junit.Test
    public void sjekkBooking() {
        assertTrue(UserManager.addSaltid(dato.format(calendar.getTime()),tidspunkt.format(calendar.getTime()),tidspunkt.format(calendar.getTime()),"15"));
        ArrayList<HashMap<String, ArrayList<String>>> saltid = DatabaseController.getSaltid(dato.format(calendar.getTime()), emne);
        for (HashMap<String,ArrayList<String>> set : saltid) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                String date = entry.getKey();
                assertEquals(dato.format(calendar.getTime()), date);
                ArrayList<String> values = entry.getValue();
                assertEquals(tidspunkt.format(calendar.getTime()), values.get(0));
                assertEquals(tidspunkt.format(calendar.getTime()), values.get(1));
                assertEquals("15", values.get(2));
            }
        }
        assertTrue(UserManager.addStudassPåSal(dato.format(calendar.getTime()), tidspunkt.format(calendar.getTime()), "15"));
        ArrayList<HashMap<String, ArrayList<String>>> sps = DatabaseController.getStudassPåSal(dato.format(calendar.getTime()), emne);
        for (HashMap<String,ArrayList<String>> set : sps) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                String date = entry.getKey();
                assertEquals(dato.format(calendar.getTime()), date);
                ArrayList<String> values = entry.getValue();
                assertEquals(tidspunkt.format(calendar.getTime()), values.get(0));
                assertEquals(emne, values.get(1));
                assertEquals(brukernavn, values.get(2));
                assertEquals("15", values.get(3));
            }
        }
        assertTrue(UserManager.booking(dato.format(calendar.getTime()),tidspunkt.format(calendar.getTime()),brukernavn));
        ArrayList<HashMap<String, ArrayList<String>>> book = DatabaseController.getBooking(brukernavn);
        for (HashMap<String,ArrayList<String>> set : book) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> values = entry.getValue();
                assertEquals(emne, values.get(0));
                assertEquals(dato.format(calendar.getTime()), values.get(1));
                assertEquals(tidspunkt.format(calendar.getTime()), values.get(2));
                assertEquals(brukernavn, values.get(3));
            }
        }
    }
    @org.junit.Test
    public void sjekkRolle(){
        assertTrue(DatabaseController.updateRolle(emne,brukernavn,"student"));
    }
    @org.junit.Test
    public void sjekkMelding(){
        assertTrue(DatabaseController.addMelding(brukernavn,brukernavn,"hello"));
        ArrayList<HashMap<String, ArrayList<String>>> meldinger = DatabaseController.getMeldinger(brukernavn, brukernavn);
        for (HashMap<String,ArrayList<String>> set : meldinger) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> values = entry.getValue();
                assertEquals(brukernavn, values.get(0));
                assertEquals("hello", values.get(1));
            }
        }
    }
    @org.junit.Test
    public void sjekkInnlevering(){
        assertTrue(DatabaseController.addOving(UserManager._aktivtEmne,"Øving Test","Øving Test", "2030-01-01"));
        File fil = new File("test.pdf");
        assertTrue(DatabaseController.addInnlevering(DatabaseController.getMaxID("OvingID", "Oving"), brukernavn, "Øving Test", fil));
        assertTrue(DatabaseController.addRetting(DatabaseController.getMaxID("InnleveringID", "Innlevering"), brukernavn, "1", "Ok"));
    }






}