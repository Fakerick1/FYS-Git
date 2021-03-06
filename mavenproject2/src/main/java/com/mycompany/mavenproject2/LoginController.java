package com.mycompany.mavenproject2;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * Controller for the login screen, contains methods for logging in and changing
 * languages Stan 1-77, Matthijs 78-114
 *
 * @author Matthijs Snijders 500780453, Stan van Weringh 500771870
 */
public class LoginController implements Initializable {

    @FXML
    private Text labelEmail, labelLastname, textWarning, textCaseSensitive;

    @FXML
    private TextField textEmail, textLastname;

    @FXML
    private AnchorPane paneCustomer;

    @FXML
    private Button buttonEmployee, buttonPassenger;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ResourceBundle mybundle = ResourceBundle.getBundle("languages.Language");
        labelEmail.setText(mybundle.getString("E-Mail"));
        textEmail.setPromptText(mybundle.getString("Enter_your_E-mail"));
        labelLastname.setText(mybundle.getString("Lastname"));
        textLastname.setPromptText(mybundle.getString("Enter_your_lastname"));
        buttonPassenger.setText(mybundle.getString("Login"));
        textWarning.setText(mybundle.getString("Warning!"));
        textCaseSensitive.setText(mybundle.getString("Login_is_case_sensitive!"));
        buttonEmployee.setText(mybundle.getString("Employee_Login"));
    }

    Utilities utilities = new Utilities();

    // Methods for changing the language
    @FXML
    private void setLanguageEnglish() {
        System.out.println("Set language to English");
        loadLanguage("en", "EN");
        utilities.newAnchorpane("Login", paneCustomer);
    }

    @FXML
    private void setLanguageDutch() {
        System.out.println("Set language to Dutch");
        loadLanguage("nl", "NL");
        utilities.newAnchorpane("Login", paneCustomer);
    }

    @FXML
    private void setLanguageTurkish() {
        System.out.println("Set language to Turkish");
        loadLanguage("tr", "TR");
        utilities.newAnchorpane("Login", paneCustomer);
    }

    // Main method for changing languages
    private void loadLanguage(String language, String lang) {
        System.out.println("Current Locale: " + Locale.getDefault());
        Locale.setDefault(new Locale(language, lang));
    }

    @FXML
    private void goToEmployee(ActionEvent event) {
        utilities.newAnchorpane("LoginEmployee", paneCustomer);
    }

    //Login for passenger
    @FXML
    private void handleButtonActionPassenger(ActionEvent event) throws SQLException {
        Database db = new Database();
        String email = textEmail.getText();

        String lastname = textLastname.getText();

        //SQL query checks if email and lastname is equal to input.
        String sql = String.format("SELECT * FROM Passenger "
                + "WHERE email = '%s' "
                + "and lastname = '%s' ",
                email, lastname);

        ResultSet resultSet = db.executeResultSetQuery(sql);
        if (!resultSet.next()) {
            Utilities.infoBox("Enter Correct labelnummer And Lastname", "Failed", null);
        } else {
            Utilities.infoBox("Login Successful", "Success", null);
            
            utilities.newAnchorpane("CustomerHomescreen", paneCustomer);
        }
    }
}
