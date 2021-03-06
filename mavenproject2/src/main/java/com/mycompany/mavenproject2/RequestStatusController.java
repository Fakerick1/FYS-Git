package com.mycompany.mavenproject2;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the request status section, can read, update and delete
 * entries from the database
 *
 * @author Rick den Otter 500749952 (778 lines)
 */
public class RequestStatusController implements Initializable {

    @FXML
    private Label labelLooking;
            
    @FXML
    private Label labelRegistrationnumber, labelDatefound, labelTimefound,
    labelType, labelBrand, labelFlightnumber, labelBagagelabelnumber, labelLocationfound, labelMaincolour,
    labelSecondarycolour, labelSize, labelWeight, labelPassenger, labelSpecialChar;

    @FXML
    private RadioButton labelLost, labelFound;

    @FXML
    private Button submitButton;

    @FXML
    private TableView foundLuggageTableView;

    @FXML
    private StackPane StackButtonPane;

    @FXML
    private Button editSelectedButton;

    private ObservableList<Luggage> foundLuggageList
            = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private ComboBox<String> mainColourComboBox;

    @FXML
    private ComboBox<String> secondaryColourComboBox;

    @FXML
    private ToggleGroup lostFoundGroup;

    @FXML
    private AnchorPane requestpage;

    @FXML
    private VBox vbox1, vbox2;

    @FXML
    private HBox hboxRadio, editHbox;

    @FXML
    private TextField regNrField, dateFoundField, timeFoundField,
            brandField, flightNrField, labelNrField, locationFoundField,
            sizeField, weightField, nameAndCityField, specialCharField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ResourceBundle mybundle = ResourceBundle.getBundle("languages.Language");

        labelLooking.setText(mybundle.getString("Looking_for:"));
        labelLost.setText(mybundle.getString("Lost"));
        labelFound.setText(mybundle.getString("Found"));
        labelRegistrationnumber.setText(mybundle.getString("Registration_Number"));
        labelDatefound.setText(mybundle.getString("Date_Found"));
        labelTimefound.setText(mybundle.getString("Time_Found"));
        labelType.setText(mybundle.getString("Type"));
        labelBrand.setText(mybundle.getString("Brand"));
        labelFlightnumber.setText(mybundle.getString("Flight_Number"));
        labelBagagelabelnumber.setText(mybundle.getString("Luggage_label_Number"));
        labelLocationfound.setText(mybundle.getString("Location_Found"));
        labelMaincolour.setText(mybundle.getString("Main_colour"));
        labelSecondarycolour.setText(mybundle.getString("Secoundary_colour"));
        labelSize.setText(mybundle.getString("Size"));
        labelWeight.setText(mybundle.getString("Weight"));
        labelPassenger.setText(mybundle.getString("Passenger_name_&_city"));
        labelSpecialChar.setText(mybundle.getString("Special_Characteristics"));
        submitButton.setText(mybundle.getString("Submit"));

        foundLuggageTableView.setItems(this.foundLuggageList);

        for (int i = 0; i < foundLuggageTableView.getColumns().size(); i++) {
            TableColumn tc = (TableColumn) foundLuggageTableView.getColumns().get(i);
            String propertyName = tc.getId();
            if (propertyName != null && !propertyName.isEmpty()) {
                tc.setCellValueFactory(new PropertyValueFactory<>(propertyName));
                System.out.println("Attached column '" + propertyName + "' in tableview to matching attribute");
            }
        }

        ObservableList<String> coloursList = FXCollections.observableArrayList();
        ObservableList<String> typesList = FXCollections.observableArrayList();

        String language = Locale.getDefault().getLanguage();

        switch (language) {
            case "en":
                coloursList.addAll(Arrays.asList(Utilities.coloursStrings[0]));
                typesList.addAll(Arrays.asList(Utilities.luggageStrings[0]));
                break;
            case "nl":
                coloursList.addAll(Arrays.asList(Utilities.coloursStrings[1]));
                typesList.addAll(Arrays.asList(Utilities.luggageStrings[1]));
                break;
            case "tr":
                coloursList.addAll(Arrays.asList(Utilities.coloursStrings[2]));
                typesList.addAll(Arrays.asList(Utilities.luggageStrings[2]));
                break;
        }

        typeComboBox.setItems(typesList);
        mainColourComboBox.setItems(coloursList);
        secondaryColourComboBox.setItems(coloursList);
    }

    Utilities utilities = new Utilities();

    @FXML
    private void getInput() throws SQLException {

        String query = getQueryFromTextfields();

        // Create database connection, execute the query
        Database database = new Database();
        ResultSet result = database.executeResultSetQuery(query);
        foundLuggageList.clear();

        // Loop through the resultset, making a new 'FoundLuggage' Object 
        // for every result, adding the attributes of the result to the 
        // corresponding attribute in the FoundLuggage object
        if (lostSelected()) {
            foundLuggageList = Utilities.initializeLostLuggageFromResultSet(result, foundLuggageList);
            makeTableViewLost();
        } else {
            foundLuggageList = Utilities.initializeFoundLuggageFromResultSet(result, foundLuggageList);
            makeTableViewFound();
        }

        result.close();
        database.close();

        // Show the TableView
        makeTextFieldsAndLabelsInvisible();
        makeTableViewVisible();
    }

    @FXML
    private void editSelected() {
        System.out.println("You clicked on edit selected!");

        // Acquire the selected luggage item and fill the textFields with the data
        if (foundLuggageTableView.getSelectionModel().getSelectedItem() != null) {
            if (lostSelected()) {
                LostLuggage luggage = (LostLuggage) foundLuggageTableView.getSelectionModel().getSelectedItem();
                initTextFieldsWithLostLuggage(luggage);
            } else {
                FoundLuggage luggage = (FoundLuggage) foundLuggageTableView.getSelectionModel().getSelectedItem();
                initTextFieldsWithFoundLuggage(luggage);
            }
            makeTableViewInvisible();
            makeTextFieldsAndLabelsVisible();
            submitButton.setVisible(false);
            hboxRadio.setVisible(false);
            editHbox.setVisible(true);
        } else {
            notSelectedBox("Select a row please", "Selection Error", "No row selected!");
        }
    }

    @FXML
    private void resetEdit() {
        // Reset all the textfields to their original values from luggage object
        if (lostSelected()) {
            LostLuggage luggage = (LostLuggage) foundLuggageTableView.getSelectionModel().getSelectedItem();
        } else {
            FoundLuggage luggage = (FoundLuggage) foundLuggageTableView.getSelectionModel().getSelectedItem();
        }

    }

    @FXML
    private void deleteEdit() {
        boolean userIsSure = areYouSureBox("Are you sure you want to remove this?", "Remove", "This will remove the luggage?");
        if (userIsSure) {
            Database database = new Database();
            String regNr = regNrField.getText();
            database.executeUpdateQuery("DELETE FROM Lostbagage WHERE registrationnr = " + regNr);
            makeTextFieldsAndLabelsInvisible();

            for (int i = 0; i < foundLuggageList.size(); i++) {
                if (foundLuggageList.get(i).getRegistrationnr().equals(regNr)) {
                    foundLuggageList.remove(i);
                    System.out.println("Removed row with index " + i);
                    break;
                }
            }
            makeTableViewVisible();

        }
    }

    @FXML
    private void cancelEdit() {
        makeTextFieldsAndLabelsInvisible();
        makeTableViewVisible();
    }

    @FXML
    private void saveEdit() throws SQLException {
        // Go through all textfields, see if content is different from .get method

        Database database = new Database();
        String query;

        Luggage luggage = (Luggage) foundLuggageTableView.getSelectionModel().getSelectedItem();
        query = getUpdateQuery(luggage, database);

        database.executeUpdateQuery(query);
        database.close();
        foundLuggageTableView.refresh();
        makeTextFieldsAndLabelsInvisible();
        makeTableViewVisible();
    }

    /**
     * Checks what attributes need to be changed for the luggage given, changes
     * them in the luggage object (to show up in the tableview) and adds them to
     * the update query
     *
     * @param luggage The luggage object to check
     * @param database database connection to use
     * @return
     * @throws SQLException
     */
    private String getUpdateQuery(Luggage luggage, Database database) throws SQLException {
        List<String> queryList = new ArrayList();
        String query;

        if (lostSelected()) {

            LostLuggage lluggage = (LostLuggage) luggage;

            if (dateFoundField.getText() != null) {
                if (!dateFoundField.getText().equals(lluggage.getDateregistered())) {
                    queryList.add("dateregistered = '" + dateFoundField.getText() + "'");
                    lluggage.setDateregistered(dateFoundField.getText());
                }
            }

            if (timeFoundField.getText() != null) {
                if (!timeFoundField.getText().equals(lluggage.getTimeregistered())) {
                    queryList.add("timeregistered = '" + timeFoundField.getText() + "'");
                    lluggage.setTimeregistered(timeFoundField.getText());
                }
            }

            if (!typeComboBox.getValue().equals(lluggage.getLuggagetype())) {
                int ral = Utilities.getNumberFromType(typeComboBox.getValue());
                queryList.add(String.format("luggagetype = %d", ral));
                lluggage.setLuggagetype(Utilities.getTypeFromNumber(Integer.toString(ral)));
            }

            if (brandField.getText() != null) {
                if (!brandField.getText().equals(lluggage.getBrand())) {
                    queryList.add("brand = '" + brandField.getText() + "'");
                    lluggage.setBrand(brandField.getText());
                }
            }

            if (flightNrField.getText() != null) {
                if (!flightNrField.getText().equals(lluggage.getFlightnumber())) {
                    queryList.add("flightnumber = '" + flightNrField.getText() + "'");
                    lluggage.setFlightnumber(flightNrField.getText());
                }
            }

            if (labelNrField.getText() != null) {
                if (!labelNrField.getText().equals(lluggage.getLuggagelabelnr())) {
                    queryList.add("luggagelabelnr = '" + labelNrField.getText() + "'");
                    lluggage.setLuggagelabelnr(labelNrField.getText());
                }
            }

            if (!mainColourComboBox.getValue().equals(luggage.getPrimarycolour())) {
                int ral = Utilities.getRalFromColour(mainColourComboBox.getValue());
                queryList.add(String.format("primarycolour = %d", ral));
                lluggage.setPrimarycolour(mainColourComboBox.getValue());
            }

            if (!secondaryColourComboBox.getValue().equals(luggage.getSecondarycolour())) {
                int ral = Utilities.getRalFromColour(secondaryColourComboBox.getValue());
                queryList.add(String.format("secondarycolour = %d", ral));
                lluggage.setSecondarycolour(secondaryColourComboBox.getValue());
            }

            if (sizeField.getText() != null) {
                if (!sizeField.getText().equals(lluggage.getSize())) {
                    queryList.add("size = '" + sizeField.getText() + "'");
                    lluggage.setSize(sizeField.getText());
                }
            }

            if (weightField.getText() != null) {
                if (!weightField.getText().equals(lluggage.getWeight())) {
                    queryList.add("weight = '" + weightField.getText() + "'");
                    lluggage.setWeight(weightField.getText());
                }
            }

            if (nameAndCityField.getText() != null) {
                if (!nameAndCityField.getText().equals(lluggage.getPassenger_name_city())) {
                    queryList.add("passenger_name_city = '" + nameAndCityField.getText() + "'");
                    lluggage.setPassenger_name_city(nameAndCityField.getText());
                }

                if (!specialCharField.getText().equals(lluggage.getOtherchar())) {
                    queryList.add("otherchar = '" + specialCharField.getText() + "'");
                    lluggage.setOtherchar(specialCharField.getText());
                }
            }

            // Start of the query
            query = stringListToUpdateQuery(queryList, "UPDATE Lostbagage SET ");

            return query;
        } else {

            FoundLuggage fluggage = (FoundLuggage) luggage;

            if (dateFoundField.getText() != null) {
                if (!dateFoundField.getText().equals(fluggage.getDatefound())) {
                    queryList.add("datefound = '" + dateFoundField.getText() + "'");
                    fluggage.setDatefound(dateFoundField.getText());
                }
            }

            if (timeFoundField.getText() != null) {
                if (!timeFoundField.getText().equals(fluggage.getTimefound())) {
                    queryList.add("timefound = '" + timeFoundField.getText() + "'");
                    fluggage.setTimefound(timeFoundField.getText());
                }
            }

            if (!typeComboBox.getValue().equals(fluggage.getLuggagetype())) {
                int ral = Utilities.getNumberFromType(typeComboBox.getValue());
                queryList.add(String.format("luggagetype = %d", ral));
                fluggage.setLuggagetype(Utilities.getTypeFromNumber(Integer.toString(ral)));
            }

            if (brandField.getText() != null) {
                if (!brandField.getText().equals(fluggage.getBrand())) {
                    queryList.add("brand = '" + brandField.getText() + "'");
                    fluggage.setBrand(brandField.getText());
                }
            }

            if (flightNrField.getText() != null) {
                if (!flightNrField.getText().equals(fluggage.getFlightnumber())) {
                    queryList.add("flightnumber = '" + flightNrField.getText() + "'");
                    fluggage.setFlightnumber(flightNrField.getText());
                }
            }

            if (labelNrField.getText() != null) {
                if (!labelNrField.getText().equals(fluggage.getLuggagelabelnr())) {
                    queryList.add("luggagelabelnr = '" + labelNrField.getText() + "'");
                    fluggage.setLuggagelabelnr(labelNrField.getText());
                }
            }

            if (locationFoundField.getText() != null) {
                if (!locationFoundField.getText().equals(fluggage.getLocationfound())) {
                    queryList.add("locationfound = '" + locationFoundField.getText() + "'");
                    fluggage.setLocationfound(locationFoundField.getText());
                }
            }

            if (!mainColourComboBox.getValue().equals(luggage.getPrimarycolour())) {
                int ral = Utilities.getRalFromColour(mainColourComboBox.getValue());
                queryList.add(String.format("primarycolour = %d", ral));
                fluggage.setPrimarycolour(mainColourComboBox.getValue());
            }

            if (!secondaryColourComboBox.getValue().equals(luggage.getSecondarycolour())) {
                int ral = Utilities.getRalFromColour(secondaryColourComboBox.getValue());
                queryList.add(String.format("secondarycolour = %d", ral));
                fluggage.setSecondarycolour(secondaryColourComboBox.getValue());
            }

            if (sizeField.getText() != null) {
                if (!sizeField.getText().equals(fluggage.getSize())) {
                    queryList.add("size = '" + sizeField.getText() + "'");
                    fluggage.setSize(sizeField.getText());
                }
            }

            if (weightField.getText() != null) {
                if (!weightField.getText().equals(fluggage.getWeight())) {
                    queryList.add("weight = '" + weightField.getText() + "'");
                    fluggage.setWeight(weightField.getText());
                }
            }

            if (nameAndCityField.getText() != null) {
                if (!nameAndCityField.getText().equals(fluggage.getPassenger_name_city())) {
                    queryList.add("passenger_name_city = '" + nameAndCityField.getText() + "'");
                    fluggage.setPassenger_name_city(nameAndCityField.getText());
                }
            }

            if (specialCharField.getText() != null) {
                if (!specialCharField.getText().equals(fluggage.getOtherchar())) {
                    queryList.add("otherchar = '" + specialCharField.getText() + "'");
                    fluggage.setOtherchar(specialCharField.getText());
                }
            }

            query = stringListToUpdateQuery(queryList, "UPDATE Foundbagageinventory SET ");

            return query;
        }
    }

    /**
     * Converts a list of strings to the entire update query
     *
     * @param stringList List of strings, formatted like "attribute = 'value'"
     * @param startOfQuery Where the luggage needs to be gotten from
     * @return Returns the query to use
     */
    private String stringListToUpdateQuery(List<String> stringList, String startOfQuery) {
        String query = startOfQuery;
        for (int i = 0; i < stringList.size(); i++) {
            query += stringList.get(i);
            if (i != stringList.size() - 1) {
                query += ", ";
            }
        }
        query += String.format(" WHERE registrationnr = '%s'", regNrField.getText());
        return query;
    }

    private boolean areYouSureBox(String infoMessage, String titleBar, String headerMessage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        Optional<ButtonType> result = alert.showAndWait();
        return (result.get() == ButtonType.OK);
    }

    private void notSelectedBox(String infoMessage, String titleBar, String headerMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    /**
     * Creates the query to find all luggage in the system, conforming to the
     * filled in textfields
     *
     * @return Returns the SELECT query to find wanted luggage
     */
    private String getQueryFromTextfields() {
        // Creates new List of Strings to be included in the query
        List<String> queryList = new ArrayList();
        String query;

        Database database = new Database();

        if (lostSelected()) {
            if (!regNrField.getText().isEmpty()) {
                queryList.add("registrationnr = '" + regNrField.getText() + "' ");
            }

            if (!dateFoundField.getText().isEmpty()) {
                queryList.add("dateregistered = '" + dateFoundField.getText() + "' ");
            }

            if (!timeFoundField.getText().isEmpty()) {
                queryList.add("timeregistered = '" + timeFoundField.getText() + "' ");
            }

            if (typeComboBox.getValue() != null) {
                String type = database.executeStringListQuery(String.format("SELECT idluggage_type FROM Luggagetype WHERE english = '%s'", typeComboBox.getValue()));
                queryList.add(String.format("luggagetype = '%s'", type));
            }

            if (!brandField.getText().isEmpty()) {
                queryList.add("brand = '" + brandField.getText() + "' ");
            }

            if (!flightNrField.getText().isEmpty()) {
                queryList.add("flightnumber = '" + flightNrField.getText() + "' ");
            }

            if (!labelNrField.getText().isEmpty()) {
                queryList.add("luggagelabelnr = '" + labelNrField.getText() + "' ");
            }

            if (!locationFoundField.getText().isEmpty()) {
                queryList.add("locationfound = '" + locationFoundField.getText() + "' ");
            }

            if (mainColourComboBox.getValue() != null) {
                String ral = database.executeStringListQuery(String.format("SELECT ralcode FROM Colour WHERE english = '%s'", mainColourComboBox.getValue()));
                queryList.add(String.format("primarycolour = '%s'", ral));
            }

            if (secondaryColourComboBox.getValue() != null) {
                String ral = database.executeStringListQuery(String.format("SELECT ralcode FROM Colour WHERE english = '%s'", secondaryColourComboBox.getValue()));
                queryList.add(String.format("secondarycolour = '%s'", ral));
            }

            if (!sizeField.getText().isEmpty()) {
                queryList.add("size = '" + sizeField.getText() + "' ");
            }

            if (!weightField.getText().isEmpty()) {
                queryList.add("weight = '" + weightField.getText() + "' ");
            }

            if (!nameAndCityField.getText().isEmpty()) {
                queryList.add("passenger_name_city = '" + nameAndCityField.getText() + "' ");
            }

            if (!specialCharField.getText().isEmpty()) {
                queryList.add("otherchar = '" + specialCharField.getText() + "' ");
            }

            // Start of the query
            query = "SELECT * FROM Lostbagage ";
        } else {
            if (!regNrField.getText().isEmpty()) {
                queryList.add("registrationnr = '" + regNrField.getText() + "' ");
            }

            if (!dateFoundField.getText().isEmpty()) {
                queryList.add("datefound = '" + dateFoundField.getText() + "' ");
            }

            if (!timeFoundField.getText().isEmpty()) {
                queryList.add("timefound = '" + timeFoundField.getText() + "' ");
            }

            if (typeComboBox.getValue() != null) {
                String type = database.executeStringListQuery(String.format("SELECT idluggage_type FROM Luggagetype WHERE english = '%s'", typeComboBox.getValue()));
                queryList.add(String.format("luggagetype = '%s'", type));
            }

            if (!brandField.getText().isEmpty()) {
                queryList.add("brand = '" + brandField.getText() + "' ");
            }

            if (!flightNrField.getText().isEmpty()) {
                queryList.add("flightnumber = '" + flightNrField.getText() + "' ");
            }

            if (!labelNrField.getText().isEmpty()) {
                queryList.add("luggagelabelnr = '" + labelNrField.getText() + "' ");
            }

            if (!locationFoundField.getText().isEmpty()) {
                queryList.add("locationfound = '" + locationFoundField.getText() + "' ");
            }

            if (mainColourComboBox.getValue() != null) {
                String ral = database.executeStringListQuery(String.format("SELECT ralcode FROM Colour WHERE english = '%s'", mainColourComboBox.getValue()));
                queryList.add(String.format("primarycolour = '%s'", ral));
            }

            if (secondaryColourComboBox.getValue() != null) {
                String ral = database.executeStringListQuery(String.format("SELECT ralcode FROM Colour WHERE english = '%s'", secondaryColourComboBox.getValue()));
                queryList.add(String.format("secondarycolour = '%s'", ral));
            }

            if (!sizeField.getText().isEmpty()) {
                queryList.add("size = '" + sizeField.getText() + "' ");
            }

            if (!weightField.getText().isEmpty()) {
                queryList.add("weight = '" + weightField.getText() + "' ");
            }

            if (!nameAndCityField.getText().isEmpty()) {
                queryList.add("passenger_name_city = '" + nameAndCityField.getText() + "' ");
            }

            if (!specialCharField.getText().isEmpty()) {
                queryList.add("otherchar = '" + specialCharField.getText() + "' ");
            }

            // Start of the query
            query = "SELECT * FROM Foundbagageinventory ";
        }
        // Loop through the list of Strings to add to the query, adding 
        // 'WHERE ' in front if it's the first, adding 'AND ' in front if it's 
        // not, to create a valid SQL Query
        for (int i = 0; i < queryList.size(); i++) {
            if (i == 0) {
                query += "WHERE ";
                query += queryList.get(i);
            } else {
                query += "AND ";
                query += queryList.get(i);
            }
        }
        database.close();
        return query;
    }

    private void makeTextFieldsAndLabelsInvisible() {
        // Create an ObservableList consisting of all children nodes of the 
        // vboxes, then making them invisible
        ObservableList<Node> vbox1Children = vbox1.getChildren();
        for (Node node : vbox1Children) {
            node.setVisible(false);
        }

        ObservableList<Node> vbox2Children = vbox2.getChildren();
        for (Node node : vbox2Children) {
            node.setVisible(false);
        }
    }

    private void makeTextFieldsAndLabelsVisible() {
        // Create an ObservableList consisting of all children nodes of the 
        // vboxes, then making them visible
        ObservableList<Node> vbox1Children = vbox1.getChildren();
        for (Node node : vbox1Children) {
            node.setVisible(true);
        }

        ObservableList<Node> vbox2Children = vbox2.getChildren();
        for (Node node : vbox2Children) {
            node.setVisible(true);
        }
    }

    private void makeTableViewInvisible() {
        foundLuggageTableView.setVisible(false);
        StackButtonPane.setVisible(false);
        editSelectedButton.setVisible(false);
    }

    private void makeTableViewVisible() {
        foundLuggageTableView.setVisible(true);
        StackButtonPane.setVisible(true);
        editSelectedButton.setVisible(true);
    }

    private void makeTableViewFound() {
        List<TableColumn> columnList = new ArrayList();
        columnList = foundLuggageTableView.getColumns();

        for (int i = 0; i < columnList.size(); i++) {
            if (columnList.get(i).getText().equals("Date Found")) {
                columnList.get(i).setVisible(true);
            }
            if (columnList.get(i).getText().equals("Time Found")) {
                columnList.get(i).setVisible(true);
            }
            if (columnList.get(i).getText().equals("Location Found")) {
                columnList.get(i).setVisible(true);
            }
            if (columnList.get(i).getText().contains("Date Registered")) {
                columnList.get(i).setVisible(false);
            }
            if (columnList.get(i).getText().contains("Time Registered")) {
                columnList.get(i).setVisible(false);
            }
        }
    }

    private void makeTableViewLost() {
        List<TableColumn> columnList = new ArrayList();
        columnList = foundLuggageTableView.getColumns();

        for (int i = 0; i < columnList.size(); i++) {
            if (columnList.get(i).getText().equals("Date Found")) {
                columnList.get(i).setVisible(false);
            }
            if (columnList.get(i).getText().equals("Time Found")) {
                columnList.get(i).setVisible(false);
            }
            if (columnList.get(i).getText().equals("Location Found")) {
                columnList.get(i).setVisible(false);
            }
            if (columnList.get(i).getText().contains("Date Registered")) {
                columnList.get(i).setVisible(true);
            }
            if (columnList.get(i).getText().contains("Time Registered")) {
                columnList.get(i).setVisible(true);
            }
        }
    }

    private boolean lostSelected() {
        List<Toggle> toggleList = new ArrayList();
        toggleList.addAll(lostFoundGroup.getToggles());

        return (toggleList.get(0).isSelected());
    }

    private void initTextFieldsWithLostLuggage(LostLuggage luggage) {
        regNrField.setText(luggage.getRegistrationnr());
        dateFoundField.setText(luggage.getDateregistered());
        timeFoundField.setText(luggage.getTimeregistered());
        typeComboBox.getSelectionModel().select(luggage.getLuggagetype());
        brandField.setText(luggage.getBrand());
        flightNrField.setText(luggage.getFlightnumber());
        labelNrField.setText(luggage.getLuggagelabelnr());
        mainColourComboBox.getSelectionModel().select(luggage.getPrimarycolour());
        secondaryColourComboBox.getSelectionModel().select(luggage.getSecondarycolour());
        sizeField.setText(luggage.getSize());
        weightField.setText(luggage.getWeight());
        nameAndCityField.setText(luggage.getPassenger_name_city());
        specialCharField.setText(luggage.getOtherchar());

    }

    private void initTextFieldsWithFoundLuggage(FoundLuggage luggage) {
        regNrField.setText(luggage.getRegistrationnr());
        dateFoundField.setText(luggage.getDatefound());
        timeFoundField.setText(luggage.getTimefound());
        typeComboBox.getSelectionModel().select(luggage.getLuggagetype());
        brandField.setText(luggage.getBrand());
        flightNrField.setText(luggage.getFlightnumber());
        labelNrField.setText(luggage.getLuggagelabelnr());
        locationFoundField.setText(luggage.getLocationfound());
        mainColourComboBox.getSelectionModel().select(luggage.getPrimarycolour());
        secondaryColourComboBox.getSelectionModel().select(luggage.getSecondarycolour());
        sizeField.setText(luggage.getSize());
        weightField.setText(luggage.getWeight());
        nameAndCityField.setText(luggage.getPassenger_name_city());
        specialCharField.setText(luggage.getOtherchar());
    }
}
