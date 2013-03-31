package ms.aurora.gui.script;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import ms.aurora.api.script.Script;
import ms.aurora.core.Session;
import ms.aurora.core.SessionRepository;
import ms.aurora.core.script.ScriptLoader;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static ms.aurora.gui.ApplicationGUI.getSelectedApplet;

/**
 * @author rvbiljouw
 */
public class ScriptOverview extends AnchorPane {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<String> cbxCategory;

    @FXML
    private TableView<ScriptModel> tblScripts;

    @FXML
    private TableColumn<ScriptModel, String> colAuthor;

    @FXML
    private TableColumn<ScriptModel, String> colCategory;

    @FXML
    private TableColumn<ScriptModel, String> colName;

    @FXML
    private TableColumn<ScriptModel, String> colShortDesc;

    @FXML
    private TextField txtName;

    public ScriptOverview() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ScriptOverview.fxml"));

        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void onCancel(ActionEvent event) {
        getScene().getWindow().hide();
    }

    @FXML
    void onOk(ActionEvent event) {
        ScriptModel model = tblScripts.getSelectionModel().selectedItemProperty().getValue();
        Session session = SessionRepository.get(getSelectedApplet().hashCode());
        if(session != null && model != null) {
            session.getScriptManager().start(model.script);
        }
        getScene().getWindow().hide();
    }

    @FXML
    void onSearch(ActionEvent event) {
        List<Script> scripts = ScriptLoader.getScripts();
        ObservableList<ScriptModel> scriptModelList = FXCollections.observableArrayList();
        String selectedCategory = cbxCategory.getSelectionModel().getSelectedItem();
        String filterName = txtName.getText().toLowerCase();

        for(Script script : scripts) {
            if(selectedCategory.equals("All") || selectedCategory.equals(script.getManifest().category())) {
                if(filterName.length() == 0 || script.getManifest().name().toLowerCase().contains(filterName)) {
                    scriptModelList.add(new ScriptModel(script));
                }
            }
        }
        tblScripts.setItems(scriptModelList);
    }

    @FXML
    void initialize() {
        assert cbxCategory != null : "fx:id=\"cbxCategory\" was not injected: check your FXML file 'ScriptOverview.fxml'.";
        assert tblScripts != null : "fx:id=\"tblScripts\" was not injected: check your FXML file 'ScriptOverview.fxml'.";
        assert txtName != null : "fx:id=\"txtName\" was not injected: check your FXML file 'ScriptOverview.fxml'.";
        colName.setCellValueFactory(new PropertyValueFactory<ScriptModel, String>("name"));
        colShortDesc.setCellValueFactory(new PropertyValueFactory<ScriptModel, String>("shortDesc"));
        colCategory.setCellValueFactory(new PropertyValueFactory<ScriptModel, String>("category"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<ScriptModel, String>("author"));


        ObservableList<String> categoryList = FXCollections.observableArrayList();
        categoryList.add("All");

        List<Script> scripts = ScriptLoader.getScripts();
        ObservableList<ScriptModel> scriptModelList = FXCollections.observableArrayList();
        for(Script script : scripts) {
            if(!categoryList.contains(script.getManifest().category())) {
                categoryList.add(script.getManifest().category());
            }
            scriptModelList.add(new ScriptModel(script));
        }
        tblScripts.setItems(scriptModelList);
        cbxCategory.setItems(categoryList);
        cbxCategory.getSelectionModel().selectFirst();
    }

    public static class ScriptModel {
        protected final Script script;
        private SimpleStringProperty name;
        private SimpleStringProperty shortDesc;
        private SimpleStringProperty category;
        private SimpleStringProperty author;

        public ScriptModel(Script script) {
            this.script = script;

            this.name = new SimpleStringProperty(script.getManifest().name());
            this.shortDesc = new SimpleStringProperty(script.getManifest().shortDescription());
            this.category = new SimpleStringProperty(script.getManifest().category());
            this.author = new SimpleStringProperty(script.getManifest().author());
        }

        public String getName() {
            return name.getValue();
        }

        public String getShortDesc() {
            return shortDesc.getValue();
        }

        public String getCategory() {
            return category.getValue();
        }


        public String getAuthor() {
            return author.getValue();
        }
    }
}
