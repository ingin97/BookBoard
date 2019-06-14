package controllers;


import app.DatabaseController;
import app.UserManager;
import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.*;


public class MeldingerController {
    /*
     * Siden som er koblet opp mot meldinger.fxml
     * Man kan velge noen å sende melding til og lese meldinger man har fått.
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


    @FXML protected void initialize() throws Exception {
        lblBrukernavn.setText(UserManager._bruker);
        showAvsendere();
        showAllBrukere();
        //Gjør at en kan trykke i listviewen med avsendere og få opp meldingene
        listView.setOnMouseClicked(new ListViewHandler(){
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                String s = listView.getSelectionModel().getSelectedItem().toString();
                int i = s.indexOf("'");
                String str = s.substring(i+1,s.length()-1);
                try {
                    int j = str.indexOf("(");
                    str = str.substring(0,j-1);
                }catch(Exception e){
                }
                lblTil.setText(str);
                sender = str;
                DatabaseController.updateUlest(sender,UserManager._bruker);
                update(sender);
                showAvsendere();
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
    //Viser alle brukere i comboboxen
    public void showAllBrukere() {
        ArrayList<String> list = new ArrayList();
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getBrukere();
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                String key = entry.getKey();
                list.add(key);
            }
        }
        for (String e : list){
            comboBox.getItems().add(e);
        }
        comboBox.setEditable(false);
        comboBox.setPromptText("Velg bruker");
    }
    //Henter opp en ny chat for den avsenderen man velger
    @FXML protected void addChat(ActionEvent event){
        sender = comboBox.getValue();
        lblTil.setText(sender);
        update(sender);
    }
    //Sjekker om det finst noen uleste meldinger og returnerer hvor mange
    public int checkUleste() {
        int i = 0;
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getAvsendere(UserManager._bruker);
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> values = entry.getValue();
                if (values.get(1).equals("1")){
                    i++;
                }
            }
        }
        return i;
    }
    public int checkUleste(String bruker) {
        int i = 0;
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getAvsendere(UserManager._bruker);
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> values = entry.getValue();
                if (values.get(0).equals(bruker) && values.get(1).equals("1")){
                    i++;
                }
            }
        }
        return i;
    }

    //Viser avsendere i listviewen
    public void showAvsendere(){
        listView.getItems().clear();
        ArrayList<String> list = new ArrayList();
        ArrayList<String> listVarsler = new ArrayList();
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getAvsendere(UserManager._bruker);
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> values = entry.getValue();
                if(!list.contains(values.get(0))){
                    int i = checkUleste(values.get(0));
                    list.add(values.get(0));
                    if (i>0){
                        listVarsler.add(values.get(0)+" ("+i+")");
                    }else {
                        listVarsler.add(values.get(0));
                    }

                }
            }
        }
        for (String b : listVarsler) {
            listView.getItems().add(new Label(b));
        }
    }
    //Oppdaterer meldings vinduet med meldinger fra nåværende avsender og mottaker
    public void update(String sender){
        lblMeldinger.setPrefHeight(0);
        lblMeldinger.setWrapText(true);
        String data="";
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getMeldinger(sender,UserManager._bruker);
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                ArrayList<String> values = entry.getValue();
                String tid = getTid(values.get(3));
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

    public static String getTid(String dateInString) {
        String tid;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = sdf.parse(dateInString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat f = new SimpleDateFormat("d MMM yyyy HH:mm");
            tid = f.format(calendar.getTime());
        }catch (Exception e){
            System.out.println(e.getStackTrace().toString());
            tid = dateInString;
        }
        return tid;
    }

    //Sender en melding til databasen og oppdaterer meldingsvinduet
    public void sendMsg(ActionEvent event){
        DatabaseController.addMelding(UserManager._bruker, sender,txtMelding.getText().trim());
        txtMelding.setText("");
        update(sender);
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
    public void openMeldinger(Button b) throws Exception{
        Stage stage = (Stage) b.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/meldinger.fxml"));
        Scene scene =  new Scene(root, 700 ,500);
        stage.setTitle("Meldinger");
        stage.setScene(scene);
        stage.show();
    }


}
