package ms.aurora.gui.account;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import jfx.messagebox.MessageBox;
import ms.aurora.core.model.Account;
import ms.aurora.api.javafx.Dialog;
import ms.aurora.api.javafx.FXUtils;

import static jfx.messagebox.MessageBox.OK;
import static ms.aurora.gui.Messages.getString;
import static ms.aurora.api.javafx.FXUtils.showModalDialog;

/**
 * @author Rick
 */
public class AccountOverview extends Dialog {
    private final ObservableList<AccountModel> accounts = FXCollections.observableArrayList();

    @FXML
    private TableColumn<AccountModel, String> colBankPin;
    @FXML
    private TableColumn<AccountModel, String> colPassword;
    @FXML
    private TableColumn<AccountModel, String> colUsername;
    @FXML
    private TableView<AccountModel> tblAccounts;

    public AccountOverview() {
        FXUtils.load(getClass().getResource("AccountOverview.fxml"), this);
    }

    @FXML
    void initialize() {
        colUsername.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("password"));
        colBankPin.setCellValueFactory(new PropertyValueFactory<AccountModel, String>("bankPin"));

        rebuild();
    }

    @FXML
    void onNewAccount() {
        new NewAccount().showAndWait();
        rebuild();
    }

    @FXML
    void onRemoveSelected() {
        AccountModel selectedAccount = tblAccounts.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            selectedAccount.getAccount().remove();
            accounts.remove(selectedAccount);
        }
        rebuild();
    }

    @FXML
    void onEditSelected() {
        AccountModel selectedAccount = tblAccounts.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            new EditAccount(selectedAccount).showAndWait();
            rebuild();
        } else {
            MessageBox.show(getScene().getWindow(), "No account selected", "No account selected", OK);
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        close();
    }

    @FXML
    void onOk() {
        close();
    }


    /**
     * Rebuilds the account table
     */
    private void rebuild() {
        accounts.clear();
        for (Account account : Account.getAll()) {
            AccountModel model = new AccountModel(account);
            accounts.add(model);
        }
        tblAccounts.setItems(accounts);
    }

    @Override
    public String getTitle() {
        return getString("accountOverview.title");
    }
}
