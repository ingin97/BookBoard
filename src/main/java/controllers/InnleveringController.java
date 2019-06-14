package controllers;


import app.Check;
import app.DatabaseController;
import app.UserManager;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;


public class InnleveringController {
    /*
     * Siden som er koblet opp mot innleveringer.fxml
     * Kan levere inn øvinger
     */

    @FXML public Button btnLever;
    @FXML public Button btnBack;
    @FXML public Button btnLoggut;
    @FXML public Button btnOpenFile;
    @FXML public JFXTextArea txtBeskrivelse;
    @FXML public JFXTextField txtFilnavn;
    @FXML public Label lblMeldinger;
    @FXML public Label lblStatus;
    @FXML public Label lblBrukernavn;
    @FXML public Label lblØving;
    @FXML public JFXListView listView;
    @FXML public AnchorPane anchorPane;


    //Lokale variabler
    public String ovingID;
    public File file;
    public File fileLevert;
    public String frist;
    public Stage mainStage;

    @FXML protected void initialize() throws Exception {

        lblBrukernavn.setText(UserManager._bruker);
        showOvinger();
        //Gjør at en kan trykke i listviewen med øvinger og få opp informasjon
        listView.setOnMouseClicked(new ListViewHandler(){
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                btnOpenFile.setVisible(false);
                String s = listView.getSelectionModel().getSelectedItem().toString();
                int i = s.indexOf("'");
                String str = s.substring(i+1,s.length()-1);
                lblØving.setText(str);
                //Øvingsinformasjon
                ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getOvingID(UserManager._aktivtEmne,str);
                for (HashMap<String,ArrayList<String>> set : dbOutput) {
                    for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                        ovingID = entry.getKey();
                        ArrayList<String> values = entry.getValue();
                        frist = values.get(1);
                        lblStatus.setText("Frist: " + MeldingerController.getTid(values.get(1)) + "\nBeskrivelse: " + values.get(0));
                    }
                }
                String last = "";
                //Henter siste innlevering informasjon
                dbOutput = DatabaseController.getUnikInnlevering(DatabaseController.getMaxIDInnlevering(UserManager._bruker, ovingID));
                for (HashMap<String,ArrayList<String>> set : dbOutput) {
                    for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                        btnOpenFile.setVisible(true);
                        fileLevert = DatabaseController.getInnlevering(DatabaseController.getMaxIDInnlevering(UserManager._bruker, ovingID), str);
                        ArrayList<String> values = entry.getValue();
                        last = "\n\nLevert: " + MeldingerController.getTid(values.get(2)) + "\nBeskrivelse: " + values.get(3);
                    }
                }
                //Henter siste retting og innlevering informasjon
                dbOutput = DatabaseController.getUnikRetting(DatabaseController.getMaxIDInnlevering(UserManager._bruker, ovingID));
                for (HashMap<String,ArrayList<String>> set : dbOutput) {
                    for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                        btnOpenFile.setVisible(true);
                        ArrayList<String> values = entry.getValue();
                        fileLevert = DatabaseController.getInnlevering(DatabaseController.getMaxIDInnlevering(UserManager._bruker, ovingID), str);
                        String godkjent = (values.get(5).equals("1")) ? "Ja" : "Nei";
                        last = "\n\nLevert: " + MeldingerController.getTid(values.get(2))
                                + "\nBeskrivelse: " + values.get(3)
                                + "\n\nRetting: " + values.get(4) + " den " + MeldingerController.getTid(values.get(7))
                                + "\nGodkjent: " + godkjent
                                + "\nKommentar: " + values.get(6);

                    }
                }
                lblStatus.setText(lblStatus.getText()+last);
            }
        });
    }
    //Åpner fil i default program
    @FXML protected void openFile(ActionEvent event) throws Exception {
        Desktop.getDesktop().open(fileLevert);
    }
    //Viser øvinger i listviewen
    public void showOvinger(){
        listView.getItems().clear();
        ArrayList<String> list = new ArrayList();
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getOvinger(UserManager._aktivtEmne);
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> values = entry.getValue();
                list.add(values.get(1));
            }
        }
        for (String b : list) {
            listView.getItems().add(new Label(b));
        }
    }

    //Sender inn en innlevering til databasen og sjekker at det er innenfor fristen
    @FXML public void lever(ActionEvent event){
        if(Check.future(frist.substring(0,10))){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat f = new SimpleDateFormat("hh:mm:ss");
            if(Check.checkTime(f.format(c.getTime()),frist.substring(11))){
                if(DatabaseController.addInnlevering(ovingID,UserManager._bruker, txtBeskrivelse.getText(), file)){
                    lblStatus.setText("Add success!");
                }else {
                    lblStatus.setText("Add failed!");
                }
                txtFilnavn.setText("");
                txtBeskrivelse.setText("");
            }
        }else {
            lblStatus.setText("Fristen har gått ut!");
        }
    }
    //Åpner filutforsker for å finne fil å laste opp
    @FXML protected void openExplorer(ActionEvent event) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
        mainStage = (Stage) anchorPane.getScene().getWindow();
        FileChooser chooser= new FileChooser();

        File chosenFile = chooser.showOpenDialog(mainStage);

        file = chosenFile;
        txtFilnavn.setText(file.getPath());
    }


    @FXML protected void back(ActionEvent event) throws Exception {
        EmneController ec = new EmneController();
        ec.back(btnBack);
    }
    @FXML protected void logout(ActionEvent event) throws Exception {
        LoginController l = new LoginController();
        l.logout(btnLoggut);
    }

    //Åpner innleveringssiden
    public void openInnlevering(Button b) throws Exception{
        Stage stage = (Stage) b.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/innlevering.fxml"));
        Scene scene =  new Scene(root, 700 ,500);
        stage.setTitle("Innlevering");
        stage.setScene(scene);
        stage.show();
    }


}
