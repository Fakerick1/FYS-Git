package com.mycompany.mavenproject2;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * Controller for the User Roles page, shows users registered to the application
 * @author Tarik 500780772 (175 Lines)
 */
public class UserRolesController implements Initializable {

    public static String editUser;
    UserDetails U = new UserDetails();

    private ObservableList<UserDetails> userList
            = FXCollections.observableArrayList();

    Utilities utilities = new Utilities();

    @FXML
    private AnchorPane mainpane;

    @FXML
    private TableView tableUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableUser.setItems(this.userList);
        try {
            loadDataFromDatabase();
        } catch (SQLException ex) {
            Logger.getLogger(UserRolesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (int i = 0; i < tableUser.getColumns().size(); i++) {
            TableColumn tc = (TableColumn) tableUser.getColumns().get(i);
            String propertyName = tc.getId();
            if (propertyName != null && !propertyName.isEmpty()) {
                tc.setCellValueFactory(new PropertyValueFactory<>(propertyName));
                System.out.println("Attached column '" + propertyName + "' in tableview to matching attribute");

            }
        }
    }

    @FXML
    private void loadDataFromDatabase() throws SQLException {
        Database dc = new Database();
        ResultSet rs = dc.executeResultSetQuery("SELECT * FROM Employee;");
        ObservableList<UserDetails> list
                = FXCollections.observableArrayList();
        while (rs.next()) {
            UserDetails user = new UserDetails();

            user.setFirstnameTc(rs.getString("firstname"));
            user.setLastnameTc(rs.getString("lastname"));
            user.setUsernameTc(rs.getString("username"));
            user.setPasswordTc(rs.getString("password"));
            user.setRoleTc(rs.getInt("RoleId"));
            System.out.println(user.getFirstnameTc());

            list.add(user);
        }
        userList.setAll(list);
        dc.close();
    }

/*

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ResourceBundle mybundle = ResourceBundle.getBundle("languages.Language");
        
        but1.setText(mybundle.getString("Add_user"));
        labelName.setText(mybundle.getString("Name"));
        labelRoles.setText(mybundle.getString("Roles"));
        labelRole.setText(mybundle.getString("Rol"));
        labelNameperson.setText(mybundle.getString("Name_Person"));
        labelSubmit.setText(mybundle.getString("Submit"));

    }
*/
        
    @FXML
    private Button but1,labelSubmit;
    
    @FXML
    private Text labelRole, labelNameperson;
    
    @FXML
    private TableColumn labelName, labelRoles;

    @FXML
    private void openAddRoles(ActionEvent event) {
        utilities.newAnchorpane("UserRoles_AddUser", mainpane);
    }

    @FXML
    private void deleteUser(ActionEvent event) throws SQLException {
        Database dc = new Database();

        if (tableUser.getSelectionModel().isEmpty()) {
            Utilities.infoBox("Please Select a row", "!", null);

        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this entry?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                UserDetails deleteUser = (UserDetails) tableUser.getSelectionModel().getSelectedItem();

                System.out.println(deleteUser.getUsernameTc());
                String setSafeOff = "SET SQL_SAFE_UPDATES=0;";
                dc.executeUpdateQuery(setSafeOff);
                String deleteUserString = String.format("DELETE FROM Employee WHERE username = '%s';", deleteUser.getUsernameTc());
                System.out.println(deleteUserString);
                dc.executeUpdateQuery(deleteUserString);
                String setSafeOn = "SET SQL_SAFE_UPDATES=1;";
                dc.executeUpdateQuery(setSafeOn);
                loadDataFromDatabase();

            } else {
                System.out.println("cancelled");
            }
        }

    }

    @FXML
    private void editUser(ActionEvent event) {
        if (tableUser.getSelectionModel().isEmpty()) {
            Utilities.infoBox("Please Select a row", "!", null);
        }
        {
            UserDetails edituser1 = (UserDetails) tableUser.getSelectionModel().getSelectedItem();
            editUser = edituser1.getUsernameTc();
            
            
            
            utilities.newAnchorpane("UserRoles_EditUser", mainpane);
        }
    }

    private boolean areYouSureBox(String infoMessage, String titleBar, String headerMessage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        Optional<ButtonType> result = alert.showAndWait();
        return (result.get() == ButtonType.OK);
    }
}
