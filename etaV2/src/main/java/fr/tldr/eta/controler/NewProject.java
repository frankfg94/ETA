package fr.tldr.eta.controler;

import fr.tldr.eta.ProjectTemplate;
import fr.tldr.eta.helper.XMLconfig;
import fr.tldr.eta.utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NewProject
{

    public Button cancelB;
    public AnchorPane pane;
    public TextArea ProjectDescriptionTA;
    public Button createProjectB;
    public TextField projectNameTF;
    public ChoiceBox templateBox;
    private XMLconfig xmlModif;

    Stage primaryStage;



    public void setPrimaryStage(Stage s)
    {
        boolean primaryStageNull = false;
        primaryStage = s;
    }

    public void onCancelProject(ActionEvent event)
    {
        Stage stage = (Stage) cancelB.getScene().getWindow();
        stage.close();
    }

    public boolean generateProject(File file, ProjectTemplate template)
    {
        switch (template)
        {
            case MVC:
                if(!file.mkdirs())return false;
                //TODO : duplicata de chemin de racine de projet
                if(!new File(file.getAbsolutePath() + "/Model").mkdirs()) return false;
                if(!new File(file.getAbsolutePath() + "/View").mkdirs()) return false;
                if(!new File(file.getAbsolutePath() + "/Component").mkdirs()) return false;
                break;
            case Vide:
                System.out.println(file.getAbsolutePath());
                if(!file.mkdirs())return false;
                break;
            case Basique:
                if(!file.mkdirs())return false;
                if(!new File(file.getAbsolutePath() + "/src").mkdirs()) return false;
                if(!new File(file.getAbsolutePath() + "/doc").mkdirs()) return false;
                break;
                default:
                    if(!file.mkdirs())return false;
        }
        return true;
    }

    public void onCreateProject() {
        // TODO : chemin de la racine des projets redondant
        String path = "";
        try {
             path = new File(".").getCanonicalPath()+"/Projects"+"/"+ projectNameTF.getText();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File f = new File(path);

        if(generateProject(f,(ProjectTemplate) templateBox.getSelectionModel().getSelectedItem()))
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/fxmlFiles/MainWindow.fxml"));
            Parent root = null;
            try {
                if(f!=null)
                {
                    root = loader.load();
                    if(primaryStage != null)
                    {
                        primaryStage.setTitle(f.getAbsolutePath());
                        primaryStage.setScene(new Scene(root,800, 600));
                        primaryStage.show();
                        Stage s = (Stage) cancelB.getScene().getWindow();
                        xmlModif =  new XMLconfig(path,((ProjectTemplate) templateBox.getSelectionModel().getSelectedItem()).name(),ProjectDescriptionTA.getText(),projectNameTF.getText());
                        s.close();
                    }
                    else
                        System.err.println("Erreur primaryStage NULL");
                }
                else
                    System.err.println("Erreur fichier NULL");


            } catch (IOException e) {
               // System.err.println("Erreur de création d'un nouveau projet");
                e.printStackTrace();
            }
        }
        else
        {
            System.err.println("Erreur de création du dossier du nouveau projet");
        }

    }

    /**
     * Vérifie si le projet a un nom qui n'existe pas déjà
     */
    public void enableProjectNameVerificator() {
        projectNameTF.setOnKeyReleased(event -> {
            try {
                // TODO : Duplicata du chemin de racine des projets
               String s =  new File(".").getCanonicalPath()+"/Projects";
               File f = new File(s);

               File[] files = f.listFiles();
                ArrayList<String> projectList = new ArrayList<>();
               for(File file : files)
               {
                   if(file.isDirectory())
                       projectList.add(file.getName());
               }
               // System.out.println("Project List " + projectList);
               if(projectList.contains(projectNameTF.getText().trim()))
               {
                   createProjectB.setText("Already exist");
                   createProjectB.setDisable(true);
               }
               else
               {
                   createProjectB.setDisable(false);
                   createProjectB.setText("create");
               }
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
    }

    public XMLconfig getXmlModif() {
        return xmlModif;
    }
}
