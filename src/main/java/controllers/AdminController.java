package controllers;

import app.DatabaseController;
import app.UserManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class AdminController {
    /*
     *  Klassen som er koblet opp mot admin.fxml
     *
     *  Metoder:
     *      addUser(ActionEvent event)
     *          -Legger til en bruker i databasen
     *      addEmne(ActionEvent event)
     *          -Legger til et emne i databasen
     *      logout(Button b)
     *          -Gjør at en kan logge ut fra andre fxml filer og åpne Login.fxml
     */


    @FXML public Label lblStatus;
    @FXML public Label lblBrukernavn;
    @FXML public Label lblDato;
    @FXML public TextField txtBrukernavn;
    @FXML public TextField txtNavn;
    @FXML public TextField txtPassord;
    @FXML public Button btnBruker;
    @FXML public TextField txtEmneID;
    @FXML public TextField txtEmneNavn;
    @FXML public TextField txtFaglærer;
    @FXML public Button btnEmne;
    @FXML public TextField txtBrukernavnRolle;
    @FXML public TextField txtEmneIDRolle;
    @FXML public TextField txtRolle;
    @FXML public Button btnRolle;
    @FXML public Button btnMeldinger;


    //Tabell med kolonner
    @FXML private TableView<List<StringProperty>> table;
    @FXML private TableColumn<List<StringProperty>, String> brukernavnColumn;
    @FXML private TableColumn<List<StringProperty>, String> navnColumn;
    @FXML private TableColumn<List<StringProperty>, String> emneColumn;
    @FXML private TableColumn<List<StringProperty>, String> rolleColumn;

    //
    private Calendar calendar;
    private  SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

    //Kode som kjøres når siden blir startet opp.
    @FXML protected void initialize() {
        //Viser brukernavn
        lblBrukernavn.setText(UserManager._bruker);
        //Viser dato idag
        calendar = Calendar.getInstance();
        lblDato.setText(sdf.format(calendar.getTime()));

        //Viser tabell med brukere
        table.visibleProperty().setValue(true);
        showTable();

        //Sjekker om det er noen uleste meldinger
        MeldingerController msg = new MeldingerController();
        int i = msg.checkUleste();
        if (i>0){
            btnMeldinger.setText(btnMeldinger.getText()+" ("+i+")");
        }
    }
    //Legger til bruker i databasen
    @FXML protected void addBruker(ActionEvent event) {
        if (DatabaseController.addBruker(txtBrukernavn.getText(),txtNavn.getText(),txtPassord.getText())) {
            lblStatus.setText("Add success!");
        }else {
            lblStatus.setText("Add failed!");
        }
    }
    //Legger til emne i databasen
    @FXML protected void addEmne(ActionEvent event) {
        if (DatabaseController.addEmne(txtEmneID.getText(),txtEmneNavn.getText()) &&
        DatabaseController.addRolle(txtEmneID.getText(), txtFaglærer.getText(), "faglærer")) {
            lblStatus.setText("Add success!");
        }else{
            lblStatus.setText("Add failed!");
        }
    }
    //Endrer rolle om det finst en rolle allerede, ellers legger den til en ny
    @FXML protected void addRolle (ActionEvent event) {
        if(DatabaseController.getRolle(txtBrukernavnRolle.getText(), txtEmneIDRolle.getText())!= ""){
            if(DatabaseController.updateRolle(txtEmneIDRolle.getText(),txtBrukernavnRolle.getText(), txtRolle.getText())) {
                lblStatus.setText(txtBrukernavnRolle.getText()+" har fått rollen "+txtRolle.getText());
            }else{
                lblStatus.setText("Update failed!");
            }
        }
        else{
            if(DatabaseController.addRolle(txtEmneIDRolle.getText(),txtBrukernavnRolle.getText(), txtRolle.getText())){
                lblStatus.setText(txtBrukernavnRolle.getText()+" har fått rollen "+txtRolle.getText());
            }else{
                lblStatus.setText("Add failed!");
            }
        }
    }

    //Gjør klart for å vise tabell
    private void showTable() {
        brukernavnColumn.setCellValueFactory(param -> param.getValue().get(0));
        navnColumn.setCellValueFactory(param -> param.getValue().get(1));
        emneColumn.setCellValueFactory(param -> param.getValue().get(2));
        rolleColumn.setCellValueFactory(param -> param.getValue().get(3));
        table.setItems(getData());
    }
    //Henter informasjon fra database som skal inn i tabell
    public ObservableList<List<StringProperty>> getData()  {
        ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();
        //Database.getBruker("all") henter informasjon om alle brukere
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getBruker("all");
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                //Legger informasjon fra databasen inn i en List row for så å legge det til i OservableList data.
                List<StringProperty> row = new ArrayList<>();
                String key = entry.getKey();
                ArrayList<String> values = entry.getValue();
                row.add(new SimpleStringProperty(key));
                for (String v : values) {
                    row.add(new SimpleStringProperty(v));
                }
                data.add(row);
            }
        }
        return data;
    }

    @FXML protected void logout(ActionEvent event) throws Exception {
        LoginController l = new LoginController();
        l.logout(btnBruker);
    }
    @FXML public void showMeldingsside(ActionEvent event) throws Exception{
        MeldingerController m = new MeldingerController();
        m.openMeldinger(btnBruker);
    }
}
