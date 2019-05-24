package fr.tldr.eta.controler;


import fr.tldr.eta.Main;
import fr.tldr.eta.utils;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

public class GitPusher {

    public TextField userTF;
    public TextField repoTF;
    public PasswordField passwordBox;
    public Button sendRepoButton;
    public AnchorPane pane;


    private void printFilesRecursive(Git git, File f) throws IOException {
        File f2 = new File(git.getRepository().getDirectory().getParent(), f.getName());

        System.out.println("Fichier :  " + f2.getAbsolutePath());
        if(f2.isDirectory())
        {
            File[] files = f2.listFiles();
            for(File fil : files)
            {
                System.out.println("On va aller dans : " + fil.getAbsolutePath());
                try {
                    createFilesRecursive(git,fil);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void createFilesRecursive(Git git, File f) throws IOException {
        File f2 = new File(git.getRepository().getDirectory().getParent(), f.getName());

            System.out.println("Fichier :  " + f2.getAbsolutePath());
            f2.createNewFile();
        if(f2.isDirectory())
            {
                File[] files = f2.listFiles();
                for(File fil : files)
                {
                    try {
                            createFilesRecursive(git,fil);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

    }

    public  void TestArea(String curProjectPath) throws GitAPIException, URISyntaxException, IOException  {


        System.out.println("Repository : " + repoTF.getText());
        System.out.println("User : " + userTF.getText());
       // System.out.println("Password : " + passwordBox.getText());

        File gitFolder = new File(MainWindow.curProjectPath + "/.git");
        if(gitFolder.exists())
        {
            gitFolder.delete();
            System.out.println(".git supprimé");
        }
        try (Git git = Git.init().setDirectory(new File(MainWindow.curProjectPath)).call()) {
            System.out.println("Created repository: " + git.getRepository().getDirectory());


            // On prend tous les fichiers disponibles
          //createFilesRecursive(git,new File(curProjectPath));

            // add remote repo:
            RemoteAddCommand remoteAddCommand = git.remoteAdd();
            remoteAddCommand.setName("origin");
            remoteAddCommand.setUri(new URIish(repoTF.getText()));
            // you can add more settings here if needed
            remoteAddCommand.call();

            git.add().addFilepattern(".").call();

            CommitCommand commit = git.commit();
            commit.setMessage("Commit automatique");
            commit.setCredentialsProvider(new UsernamePasswordCredentialsProvider(userTF.getText(), passwordBox.getText()));
            commit.call();
            System.out.println("Commit OK");
            // push to remote:
            PushCommand pushCommand = git.push();
            pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(userTF.getText(), passwordBox.getText()));
            // you can add more settings here if needed
            pushCommand.call();
            System.out.println("Push OK");
            pane.setOpacity(0.2f);
            utils.sendNotification("Push done !","Project Pushed to " + repoTF.getText()+ " sucessfully");
            pane.setOpacity(1);
        } catch (GitAPIException e) {
            utils.sendNotification("Push Failed","Please verify fields or that the project doesn't contain a .git folder");
            e.printStackTrace();
        }
    }

    public void sendRepository(ActionEvent event)
    {

        try {
            if (MainWindow.curProjectPath != null) {
                try (Git git = Git.init().setDirectory(new File(MainWindow.curProjectPath)).call()) {
                    System.out.println("Created repository: " + git.getRepository().getDirectory());
                    //printFilesRecursive(git, new File(MainWindow.curProjectPath));
                    TestArea(MainWindow.curProjectPath);
                } catch (GitAPIException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void checkSendButtonActivation(KeyEvent event)
    {
        if (passwordBox.getText().isEmpty() || repoTF.getText().isEmpty() || userTF.getText().isEmpty()) {
            sendRepoButton.setDisable(true);
        } else {
            sendRepoButton.setDisable(false);
        }
    }
}
