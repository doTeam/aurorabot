package ms.aurora.gui.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ms.aurora.core.model.Account;
import ms.aurora.api.javafx.Dialog;

import static ms.aurora.gui.Messages.getString;
import static ms.aurora.api.javafx.FXUtils.load;

/**
 * 03-07-2013: Removed a password stealer here.
 *             Need to take some extra measures to prevent shit from happening here...
 * @author Rick
 */
public class NewAccount extends Dialog {

    private final AccountModel accountModel;
    @FXML
    private TextField txtBankPin;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtUsername;

    public NewAccount() {
        accountModel = new AccountModel(new Account());
        load(getClass().getResource("NewAccount.fxml"), this);
    }

    @FXML
    void onCancel(ActionEvent event) {
        close();
    }

    @FXML
    void onOk(ActionEvent event) {
        accountModel.setUsername(txtUsername.getText());
        accountModel.setPassword(txtPassword.getText());
        accountModel.setBankPin(txtBankPin.getText());
        accountModel.getAccount().save();
        close();
    }

    @FXML
    void initialize() {
        txtUsername.setText(accountModel.getUsername());
        txtPassword.setText(accountModel.getPassword());
        txtBankPin.setText(accountModel.getBankPin());
    }

    @Override
    public String getTitle() {
        return getString("newAccount.title");
    }
}
