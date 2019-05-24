package fr.tldr.eta.controler;

import fr.tldr.eta.data.IDETheme;
import fr.tldr.eta.data.SettingsData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Settings {

    @FXML
    public VBox vbox;
    @FXML
    Label tabNameLbl;
    @FXML
    TreeView<String> treeView;

    private Stage stage1;
    public Settings() {
    }

    @FXML
    public void initialize() // Constructeur du fichier FXML
    {
        addNodes();
    }
    IDETheme curTheme = SettingsData.loadXMLTheme();

    // Elements/Controles affichés à l'écran
    Label lbl;
    Label lblColorPickBg;
    Label lblColorPickTxt;

    private void addNodes()
    {

        //Create root
        TreeItem<String> root = new TreeItem<>();
        TreeItem<String> treeItem = new TreeItem<>(); // CHILD 1
        TreeItem<String> treeItem2 = new TreeItem<>(); // CHILD 2
        treeItem.setValue("Apparence");
        treeItem2.setValue("Chemins");
        root.setExpanded(true);
        treeView.setShowRoot(false);
        root.getChildren().add(treeItem);
        root.getChildren().add(treeItem2);
        treeView.setRoot(root);
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                System.out.println("Changed");
                tabNameLbl.setText(  (treeView.getSelectionModel().getSelectedItem()).getValue() );
                vbox.getChildren().clear();
                if(tabNameLbl.getText().trim()=="Appearance")
                {
                    System.out.println("Affichage spécial Apparence");
                     lbl = new Label("Choix de votre thème");
                    ComboBox comboBox = new ComboBox();
                    comboBox.getItems().add(new IDETheme(Color.PINK,Color.RED,"Rouge Sang"));
                    comboBox.getItems().add(new IDETheme(Color.BLACK,Color.WHITE,"Nocturne"));
                    comboBox.getItems().add(new IDETheme(Color.WHITE,Color.BLACK,"Blanc"));
                     lblColorPickBg = new Label("Couleur du texte");
                    ColorPicker colPickerTxt = new ColorPicker();
                     lblColorPickTxt = new Label("Couleur du fond d'écran");
                    ColorPicker colPickerBg = new ColorPicker();
                    comboBox.valueProperty().addListener(new ChangeListener() {
                        @Override
                        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                            colPickerTxt.setValue(((IDETheme)comboBox.getValue()).textColor);
                            colPickerBg.setValue(((IDETheme)comboBox.getValue()).backgroundColor);
                            curTheme = (IDETheme) newValue;
                        }
                    });

                    vbox.getChildren().add(lbl);
                    vbox.getChildren().add(comboBox);
                    vbox.getChildren().add(lblColorPickBg);
                    vbox.getChildren().add(colPickerTxt);
                    vbox.getChildren().add(lblColorPickTxt);
                    vbox.getChildren().add(colPickerBg);


                    HBox options = new HBox();
                    options.setSpacing(5);
                    Button applyB = new Button("Apply");
                    Button cancelB = new Button("Cancel");
                    applyB.setOnAction(event -> {
                        SetCurrentTheme(curTheme);
});
                    cancelB.setOnAction(event -> {initialize();});
                    options.getChildren().add(applyB);
                    options.getChildren().add(cancelB);

                    vbox.getChildren().add(options);
                    SettingsData.loadXMLTheme();
                }
                if(tabNameLbl.getText().trim() == "Chemins")
                {
                    //Stockage du fichier Main
                    Label lbl_MainFilePath = new Label("Chemin du fichier Main");
                    TextField tf_MainFilePath = new TextField();

                    //Stockage du repertoire principal
                    Label lbl_FolderPath = new Label("Chemin du repertoire principal");
                    TextField tf_FolderPath = new TextField();

                    //Ajout
                    vbox.getChildren().add(lbl_MainFilePath);
                    vbox.getChildren().add(tf_MainFilePath);
                    vbox.getChildren().add(new Separator());
                    vbox.getChildren().add(lbl_FolderPath);
                    vbox.getChildren().add(tf_FolderPath);

                    HBox options = new HBox();
                    options.setSpacing(5);

                    //Génération des boutons
                    Button applyB = new Button("Appliquer");
                    applyB.setOnAction(event ->
                    {
                        // Quand on applique
                        trySetMainPath(tf_MainFilePath.getText());
                    });

                    Button cancelB = new Button("Annuler");
                    cancelB.setOnAction(event -> {initialize();});

                    options.getChildren().add(applyB);
                    options.getChildren().add(cancelB);
                }
            }

            private void trySetMainPath(String newMainFilePath) {
                // XmlLoader.save(newMainFilePath,"mainFilePathKey");
            }
        });

    }

    private void ApplyThemeToCurrentWindow(IDETheme theme)
    {
        if(lbl!=null)
            lbl.setTextFill(theme.textColor);
        if(lblColorPickBg!=null)
            lblColorPickBg.setTextFill(theme.textColor);
        if(lblColorPickTxt!=null)
            lblColorPickTxt.setTextFill(theme.backgroundColor);
    }

    private void SetCurrentTheme(IDETheme theme)
    {
        if(theme==null)theme = curTheme;
        SettingsData.SaveTheme(theme);
        ApplyThemeToCurrentWindow(theme);

        vbox.setBackground(new Background(new BackgroundFill(theme.backgroundColor,CornerRadii.EMPTY, Insets.EMPTY)));
    }


    public void setStage(Stage stage) {
         stage1 = stage;
    }

    public Stage getStage1() {
        return stage1;
    }
}
