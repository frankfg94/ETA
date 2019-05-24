package fr.tldr.eta.controler;

import fr.tldr.eta.helper.XMLconfig;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ProjectSettingsDialog {

    public TextField projectNameField;
    public TextField mainPathTextField;
    public TextField argumentsField;
    public TextField outputPathTextField;
    public TextField compilerPathTextField;
    public TextField executorPathTextField;
    public Button mainPathButton;
    public Button outputPathButton;
    public Button compilerPathButton;
    public Button executorPathButton;
    public Button okBtn;
    public Button cancelBtn;
    private Stage stage;
    private XMLconfig projectConf;

    public ProjectSettingsDialog() {

    }

    private void readFile()
    {
        this.mainPathTextField.setText(this.projectConf.getPathExe());
        this.projectNameField.setText(this.projectConf.getProjecName());
        this.argumentsField.setText(this.projectConf.getArgumentField());
        this.outputPathTextField.setText(this.projectConf.getOutputPaths());
        this.compilerPathTextField.setText(this.projectConf.getCompilerPath());
        this.executorPathTextField.setText(this.projectConf.getExecutorPath());
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    public void onOk(ActionEvent actionEvent) {
        this.projectConf.setProjectName(this.projectNameField.getText());
        this.projectConf.setPathExe(this.mainPathTextField.getText());
        this.projectConf.setArgumentField(this.argumentsField.getText());
        this.projectConf.setOutputPaths(this.outputPathTextField.getText());
        this.projectConf.setCompilerPath(this.compilerPathTextField.getText());
        this.projectConf.setExecutorPath(this.executorPathTextField.getText());
        this.onCancel(actionEvent);




    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) this.cancelBtn.getScene().getWindow();
        stage.close();
    }

    public void onChooseMainPath(ActionEvent actionEvent) {
        //String directoryName

        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(projectConf.getDirectoryPath())); // D:\toto\prj.xml
        chooser.setTitle("Choose Your Main file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Lua File", "*.lua"));
        File main= chooser.showOpenDialog(this.stage);
        if(main !=null)
        {
            this.mainPathTextField.setText(main.getAbsolutePath());
        }
    }

    public void onChooseOutputDirectoryPath(ActionEvent actionEvent) {


        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File(projectConf.getDirectoryPath()));
        chooser.setTitle("chose the output directory");
        File dir= chooser.showDialog(this.stage);
        if(dir !=null)
        {
            this.outputPathTextField.setText(dir.getAbsolutePath());
        }

    }

    public void onChooseCompilerPath(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();

        chooser.setTitle("Select the compiler");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("program file", "*.lua"),new FileChooser.ExtensionFilter("Other", "*.*"));
        File file= chooser.showOpenDialog(this.stage);
        if(file !=null)
        {
            this.compilerPathTextField.setText(file.getAbsolutePath());
        }
    }

    public void onChooseExecutorPath(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select the Executor");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("program file", "*.lua"),new FileChooser.ExtensionFilter("Other", "*.*"));
        File file= chooser.showOpenDialog(this.stage);
        if(file !=null)
        {
            this.executorPathTextField.setText(file.getAbsolutePath());
        }
    }

    public void setProjectSettings(XMLconfig prjConfig)
    {
        this.projectConf=prjConfig;
        this.readFile();
    }
}
