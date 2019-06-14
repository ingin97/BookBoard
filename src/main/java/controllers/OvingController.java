package controllers;


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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OvingController {
    /*
     * Siden som er koblet opp mot innleveringer.fxml
     * Kan levere inn øvinger
     */

    @FXML public Button btnOpprett;
    @FXML public Button btnBack;
    @FXML public Button btnLoggut;
    @FXML public JFXTextArea txtBeskrivelse;
    @FXML public JFXTextField txtFrist;
    @FXML public JFXTextField txtTittel;
    @FXML public Label lblStatus;
    @FXML public Label lblBrukernavn;
    @FXML public Label lblØving;
    @FXML public JFXListView listView;

    //Lokale variabler
    public String ovingID;
    public String frist;


    @FXML protected void initialize() throws Exception {
        lblBrukernavn.setText(UserManager._bruker);
        showOvinger();
        //Gjør at en kan trykke i listviewen med øvinger og få opp info
        listView.setOnMouseClicked(new ListViewHandler(){
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                String s = listView.getSelectionModel().getSelectedItem().toString();
                int i = s.indexOf("'");
                String str = s.substring(i+1,s.length()-1);
                lblØving.setText(str);
                ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getOvingID(UserManager._aktivtEmne,str);
                for (HashMap<String,ArrayList<String>> set : dbOutput) {
                    for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                        ovingID = entry.getKey();
                        ArrayList<String> values = entry.getValue();
                        frist = values.get(1);
                        lblStatus.setText("Frist: " + MeldingerController.getTid(values.get(1)) + "\nBeskrivelse: " + values.get(0));
                    }
                }
            }
        });
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

    //Oppretter en øving i databasen
    @FXML public void opprett(ActionEvent event){
        if(DatabaseController.addOving(UserManager._aktivtEmne, txtTittel.getText(), txtBeskrivelse.getText(), txtFrist.getText())){
            lblStatus.setText("Add success!");
        }else {
            lblStatus.setText("Add failed!");
        }
        showOvinger();
        txtTittel.setText("");
        txtFrist.setText("");
        txtBeskrivelse.setText("");
    }
    @FXML protected void back(ActionEvent event) throws Exception {
        EmneController ec = new EmneController();
        ec.back(btnBack);
    }
    @FXML protected void logout(ActionEvent event) throws Exception {
        LoginController l = new LoginController();
        l.logout(btnLoggut);
    }

    //Åpner ovingssiden
    public void openOving(Button b) throws Exception{
        Stage stage = (Stage) b.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/oving.fxml"));
        Scene scene =  new Scene(root, 700 ,500);
        stage.setTitle("Øving");
        stage.setScene(scene);
        stage.show();
    }


}
