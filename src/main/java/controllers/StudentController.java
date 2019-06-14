package controllers;

import app.Check;
import app.DatabaseController;
import app.UserManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;

import java.text.SimpleDateFormat;
import java.util.*;


public class StudentController {
    /*
     *  Klassen som er koblet opp mot student.fxml
     *
     *  Metoder:
     *      book(ActionEvent event)
     *          -Sender en booking request til app.UserManager når man trykker på tilhørende knapp
     *      showTid(ActionEvent event)
     *          -Viser saltider for studasser når man trykker på tilhørende knapp
     *
     */
    @FXML public Label lblStatus;
    @FXML public Label lblTid;
    @FXML public Label lblBrukernavn;
    @FXML public Label lblDato;
    @FXML public TextField txtStudass;
    @FXML public TextField txtDato;
    @FXML public TextField txtTidspunkt;
    @FXML public Button btnBook;

    @FXML private TableView<List<StringProperty>> table;
    @FXML private TableColumn<List<StringProperty>, String> datoColumn;
    @FXML private TableColumn<List<StringProperty>, String> tidspunktColumn;
    @FXML private TableColumn<List<StringProperty>, String> emneColumn;
    @FXML private TableColumn<List<StringProperty>, String> studassColumn;
    @FXML private TableColumn<List<StringProperty>, String> varighetColumn;

    private Calendar calendar;
    private SimpleDateFormat defaultF = new SimpleDateFormat("yyyy-MM-dd");

    //Kjøres når siden startes
    @FXML protected void initialize() throws Exception {
        lblBrukernavn.setText(UserManager._bruker);
        calendar = Calendar.getInstance();
        showDate(0);
        table.visibleProperty().setValue(true);

        //Gjør det mulig å kunne trykke i tabellen og booke direkte
        table.setOnMouseClicked(new ListViewHandler(){
            @Override
            public void handle(javafx.scene.input.MouseEvent event)  {
                List<StringProperty> list = table.getSelectionModel().getSelectedItem();
                if(UserManager.booking(list.get(0).getValue(),list.get(1).getValue(),list.get(3).getValue())){
                    lblStatus.setText("Booking success!");
                }else {
                    lblStatus.setText("Booking failed!");
                }
                showTable();
            }
        });
    }

    //Viser dato og kan gå frem og tilbake
    private void showDate(int i) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Calendar today = Calendar.getInstance();
        if(defaultF.format(today.getTime()).equals(defaultF.format(calendar.getTime())) && i == -1 ){
            return;
        }
        calendar.add(Calendar.DAY_OF_MONTH, i);
        lblDato.setText(sdf.format(calendar.getTime()));
        txtDato.setText(defaultF.format(calendar.getTime()));
        showTable();
    }
    @FXML protected void showNextDay(ActionEvent event) throws Exception {
        showDate(1);
    }
    @FXML protected void showPrevDay(ActionEvent event) throws Exception {
        showDate(-1);
    }
    //Sender book request til usermanager
    @FXML protected void book(ActionEvent event) throws Exception {
        if (Check.checkDato(txtDato.getText()) && Check.checkTidspunkt(txtTidspunkt.getText())){
            if (UserManager.booking(txtDato.getText(), txtTidspunkt.getText(), txtStudass.getText())){
                lblStatus.setText("Booking success!");
            }else {
                lblStatus.setText("Booking failed!");
            }
        }
        showTable();
    }
    //Viser tabell med studasspåsal tider
    private void showTable() {
        datoColumn.setCellValueFactory(param -> param.getValue().get(0));
        tidspunktColumn.setCellValueFactory(param -> param.getValue().get(1));
        emneColumn.setCellValueFactory(param -> param.getValue().get(2));
        studassColumn.setCellValueFactory(param -> param.getValue().get(3));
        varighetColumn.setCellValueFactory(param -> param.getValue().get(4));

        table.setItems(getData());
    }
    //Henter informasjon til tabell
    public ObservableList<List<StringProperty>> getData()  {
        ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();
        ArrayList<HashMap<String, ArrayList<String>>> dbOutput = DatabaseController.getStudassPåSal(defaultF.format(calendar.getTime()), UserManager._aktivtEmne);
        for (HashMap<String,ArrayList<String>> set : dbOutput) {
            for (Map.Entry<String, ArrayList<String>> entry : set.entrySet()) {
                List<StringProperty> row = new ArrayList<>();
                String key = entry.getKey();
                ArrayList<String> values = entry.getValue();
                ArrayList<HashMap<String, ArrayList<String>>> booking = DatabaseController.getUnikBooking(key, values.get(0), values.get(2));
                if (booking.isEmpty()){
                    row.add(new SimpleStringProperty(key));
                    for (String v : values) {
                        row.add(new SimpleStringProperty(v));
                    }
                    data.add(row);
                }
            }
        }
        return data;
    }

    @FXML protected void back(ActionEvent event) throws Exception {
        EmneController ec = new EmneController();
        ec.back(btnBook);
    }
    @FXML protected void logout(ActionEvent event) throws Exception {
        LoginController l = new LoginController();
        l.logout(btnBook);
    }
    @FXML protected void openInnlevering(ActionEvent event) throws Exception {
        InnleveringController i = new InnleveringController();
        i.openInnlevering(btnBook);
    }

}
