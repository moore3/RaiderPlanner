package View;

import Controller.AccountController;
import Model.Account;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by Zilvinas on 04/05/2017.
 */
public class UIManager
{
    public Account createAccount() throws Exception
    {
        AccountController accountControl = new AccountController();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CreateAccount.fxml"));
        loader.setController(accountControl);
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root, 550, 232));
        stage.setTitle("Create Account");
        stage.resizableProperty().setValue(false);
        stage.getIcons().add(new Image("file:icon.png"));
        stage.showAndWait();

        if (accountControl.isSuccess())
        {
            Account newAccount = accountControl.getAccount();
            return newAccount;
        } else
            throw new Exception("User quit.");

    }

    public void mainMenu() throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/MainMenu.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root, 700, 750));
        stage.setTitle("Main");
        stage.getIcons().add(new Image("file:icon.png"));
        stage.getScene().getStylesheets().add("Content/stylesheet.css");
        stage.showAndWait();

    }
}