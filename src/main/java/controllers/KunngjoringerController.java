package controllers;


import app.DatabaseController;
import app.UserManager;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.*;


public class KunngjoringerController {
    /*
     * Siden som er koblet opp mot kunngjoringer.fxml
     * Man kan å sende kunngjøringer til alle/studenter/studasser i faget.
     */

    @FXML public Button btnSend;
    @FXML public Button btnBack;
    @FXML public Button btnLoggut;
    @FXML public JFXTextArea txtMelding;
    @FXML public JFXTextField txtBruker;
    @FXML public Label lblMeldinger;
    @FXML public Label lblStatus;
    @FXML public Label lblBrukernavn;
    @FXML public Label lblTil;
    @FXML public ScrollPane scrollPane;
    @FXML public AnchorPane vindu;
    @FXML public AnchorPane anchorPane;
    @FXML public JFXListView listView;
    @FXML public JFXComboBox<String> comboBox;

    //Holder styr på hvilket chat vindu en skal vise.
    public String sender;
    private ArrayList<ArrayList<String>> mottakere;


    @FXML protected void initialize() throws Exception {
        lblBrukernavn.setText(UserManager._bruker);
        showAvsendere();
        getMottakere();
        //Gjør at en kan trykke i listviewen med motakkere og sende melding til dem
        listView.setOnMouseClicked(new ListViewHandler(){
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                String s = listView.getSelectionModel().getSelectedItem().toString();
                int i = s.indexOf("'");
                lblTil.setText(s.substring(i+1,s.length()-1));
                sender = s.substring(i+1,s.length()-1);
                DatabaseController.updateUlest(sender,UserManager._bruker);
                //update(sender);
            }
        });

        txtMelding.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    sendMsg(new ActionEvent());
                }
            }
        });
    }

    //Viser mulige mottakergrupper i listviewen
    public void showAvsendere(){
        ArrayList<String> list = new ArrayList();
        list.add("alle");
        list.add("studass");
        list.add("student");
        for (String b : list) {
            listView.getItems().add(new Label(b));
        }
    }

    public void getMottakere(){
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getBruker("all");
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> row = new ArrayList<String>();
                ArrayList<String> values = entry.getValue();
                String key = entry.getKey();
                if (values.get(2).equals(UserManager._aktivtEmne)){
                    row.add(key);
                    row.add(values.get(1));
                    list.add(row);
                }
            }
        }
        mottakere = list;
    }
    //Oppdaterer meldingsvinduet
    public void update(String sender){
        //lblMeldinger.setPrefHeight(0);
        String data="";
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getMeldinger(sender,UserManager._bruker);
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> values = entry.getValue();
                String tid;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String dateInString = values.get(3);
                    Date date = sdf.parse(dateInString);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    SimpleDateFormat f = new SimpleDateFormat("d MMM yyyy HH:mm");
                    tid = f.format(calendar.getTime());
                }catch (Exception e){
                    System.out.println(e.getStackTrace().toString());
                    tid = values.get(3);
                }
                String row = tid + " - " + values.get(0) + ":\n" + values.get(1) +"\n";
                data += row;
            }
        }
        lblMeldinger.setText(data);
        int i = 0;
        for (char c : data.toCharArray()){
            if (c == '\n'){
                i++;
            }
        }
        lblMeldinger.setPrefHeight(lblMeldinger.getPrefHeight()+17*i);
        vindu.setPrefHeight(lblMeldinger.getPrefHeight());
    }
    //Sender en kunngjøring(melding) til databasen og oppdaterer meldingsvinduet
    public void sendMsg(ActionEvent event){
        String msg = "KUNNGJØRING for " + UserManager._aktivtEmne+"!\n";
        msg += txtMelding.getText().trim();
        for (ArrayList l : mottakere){
            if(sender.equals("alle")){
                DatabaseController.addMelding(UserManager._bruker, l.get(0).toString(),msg);
            }
            else if (l.get(1).equals(sender)){
                DatabaseController.addMelding(UserManager._bruker, l.get(0).toString(),msg);
            }

        }
        lblMeldinger.setText(lblMeldinger.getText()+"\n"+msg);
        txtMelding.setText("");
    }

    @FXML protected void back(ActionEvent event) throws Exception {
        EmneController ec = new EmneController();
        ec.back(btnBack);
    }
    @FXML protected void logout(ActionEvent event) throws Exception {
        LoginController l = new LoginController();
        l.logout(btnLoggut);
    }

    //Åpner meldingssiden
    public void openKunngjoringer(Button b) throws Exception{
        Stage stage = (Stage) b.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/kunngjoringer.fxml"));
        Scene scene =  new Scene(root, 700 ,500);
        stage.setTitle("Kunngjøringer");
        stage.setScene(scene);
        stage.show();
    }


}
