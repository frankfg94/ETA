package fr.tldr.eta.controler;

import fr.tldr.eta.OpenFile;
import fr.tldr.eta.TreeItemFile;
import fr.tldr.eta.utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class TreeViewOperations
{

    private MainWindow mw;


    /**
     * Colle un fichier du TreeView avec clic droit
     * @return
     */
    MenuItem generatePasteItem()
    {
        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setDisable(true);
        pasteItem.setOnAction(event -> {
            TreeItemFile tvFile;
            TreeItem item  = mw.fileManager.getSelectionModel().getSelectedItem();
            if (item instanceof TreeItemFile)
            {
                tvFile = (TreeItemFile) item;
            }
            else
            {
                tvFile = ((TreeItem<TreeItemFile>) item).getValue();
            }
            boolean isDirectory = true;
            if(utils.copiedFile != null)
            {
                String folderPath = "";
                if(tvFile.getFile().isFile())
                {
                    folderPath = tvFile.getFile().getParentFile().getAbsolutePath();
                    isDirectory = false;
                }
                else if (tvFile.getFile().isDirectory())
                {
                    folderPath = tvFile.getFile().getAbsolutePath();
                    isDirectory = true;
                }
                try {
                    File newFile = new File(folderPath +"/" + utils.copiedFile.getName());						System.out.println("Nouveau chemin " + newFile.getAbsolutePath());
                    //System.out.println("Ancien chemin  : " + utils.copiedFile.getAbsolutePath());
                    //System.out.println("Nouveau chemin :" + newFile.getAbsolutePath());
                    if(newFile.createNewFile())
                    {
                        if(isDirectory)
                            item.getChildren().add(new TreeItem<>(new TreeItemFile(newFile)));
                        else
                            item.getParent().getChildren().add(new TreeItem<>(new TreeItemFile(newFile)));
                    }
                    utils.sendNotification("Fichier collé","Fichier "+ newFile.getName() +" Collé à " + newFile.getAbsolutePath());
                    if(utils.cutEnabled)
                    {
                        if(tvFile.getFile().delete())
                            item.getParent().getChildren().remove(item);
                        else
                            System.err.println("Erreur cut suppression ancien fichier");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return pasteItem;
    }

    /**
     * Copie un fichier du TreeView avec clic droit
     * @return
     */
    MenuItem generateCopyItem(MenuItem pasteItem)
    {
        MenuItem copyItem= new MenuItem("Copy");
        copyItem.setOnAction(event -> {
            TreeItemFile tvFile;
            TreeItem item  = mw.fileManager.getSelectionModel().getSelectedItem();
            if (item instanceof TreeItemFile)
            {
                tvFile = (TreeItemFile) item;
            }
            else
            {
                tvFile = ((TreeItem<TreeItemFile>) item).getValue();
            }
            if(tvFile.getFile().isFile())
            {
                utils.copiedFile = tvFile.getFile();
                if( copyItem.getParentPopup() != null)
                    for(MenuItem mItem : copyItem.getParentPopup().getItems())
                    {
                        if(mItem == pasteItem)
                            mItem.setDisable(false);
                    }
                utils.sendNotification("Fichier Copié","Fichier " + tvFile.getFile().getName() + " copié");
            }
            else
            {
                System.err.println("Vous devez copier un fichier");
            }

        });
        return  copyItem;

    }

    /**
     * Renomme un fichier du TreeView avec clic droit
     * @return
     */
    MenuItem generateRenomItem()
    {

        MenuItem renomItem = new MenuItem("Rename");
        renomItem.setOnAction(event -> {
			TreeItemFile tvFile;
            TreeItem item  = mw.fileManager.getSelectionModel().getSelectedItem();
            if (item instanceof TreeItemFile)
			{
				tvFile = (TreeItemFile) item;
			}
            else
			{
				tvFile = ((TreeItem<TreeItemFile>) item).getValue();
			}

            TextInputDialog dialog = new TextInputDialog(tvFile.getFile().getName());
            dialog.setTitle("Renommer");
            String fileType = "fichier";
            if(tvFile.getFile().isDirectory())
                fileType="dossier";
            dialog.setHeaderText("Renommez le "+fileType+" : " + tvFile.getFile().getName());
            dialog.setContentText("Veuillez entrer le nouveau nom:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()){
                String[] oldFileParts = tvFile.getFile().getAbsolutePath().split("\\\\");
                StringBuilder oldPath = new StringBuilder();
                for(int i = 0 ; i < oldFileParts.length-1;i++)
                {
                    oldPath.append(oldFileParts[i]).append("\\");
                }
                System.out.println("Nouveau début de chemin :" + oldPath);
                File newFile = new File(oldPath+"/" + result.get()); // On recupère la zone de texte
                boolean success = tvFile.getFile().renameTo(newFile);
                if(success) {
                    tvFile = new TreeItemFile(newFile);
                    item.setValue(tvFile);
                }
                else
                    System.err.println("Echec renommage");
            }

        });
        return renomItem;
    }

    /**
     * Supprime un fichier du TreeView avec clic droit
     * @return
     */
    MenuItem generateDelItem()
    {
        MenuItem suppItem = new MenuItem("Delete");
        suppItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem item  = mw.fileManager.getSelectionModel().getSelectedItem();
                TreeItemFile tvFile;
                if (item instanceof TreeItemFile)
                {
                    tvFile = (TreeItemFile) item;
                }
                else
                {
                    tvFile = ((TreeItem<TreeItemFile>) item).getValue();
                }
                if(tvFile.getFile().isFile())
                {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("delete file");
                    alert.setHeaderText("do you want to delete : " + tvFile.getFile().getName());
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK)
                        if(tvFile.getFile().delete())
                            item.getParent().getChildren().remove(item);
                }

            }
        });
        return suppItem;
    }

    /**
     * Copie un chemin d'un fichier dans le presse papier avec clic droit
     * @return
     */
    MenuItem generateCopyPath()
    {
        MenuItem copyPathItem = new MenuItem("Copy Path");
        copyPathItem.setOnAction(event -> {
            TreeItem item  = mw.fileManager.getSelectionModel().getSelectedItem();
            TreeItemFile tvFile;
            if (item instanceof TreeItemFile)
            {
                tvFile = (TreeItemFile) item;
            }
            else
            {
                tvFile = ((TreeItem<TreeItemFile>) item).getValue();
            }

            // On copie dans le presse papier le chemin du fichier ou dossier
            StringSelection stringSelection = new StringSelection(tvFile.getFile().getAbsolutePath());
            Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
        return copyPathItem;
    }

    /**
     * Permet de créer un fichier avec clic droit
     * @return
     */
    MenuItem generateCreateFileItem()
    {
       MenuItem createFileItem =  new MenuItem("Create file");
        createFileItem.setOnAction(event -> {

            TreeItem selectedItem  = mw.fileManager.getSelectionModel().getSelectedItem();
            TreeItemFile tvFile;

            if (selectedItem instanceof TreeItemFile)
            {
                tvFile = (TreeItemFile) selectedItem;
            }
            else
            {
                tvFile = ((TreeItem<TreeItemFile>) selectedItem).getValue();
            }

                System.out.println("clicked folder");

                // On copie le chemin du  dossier
                String path = tvFile.getFile().getAbsolutePath();
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Create file");
                dialog.setHeaderText("add the file in the folder " + tvFile.getFile().getName());
                dialog.setContentText("Enter the file name : ");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    try {
                        // System.out.println("Chemin à partir duquel va être crée le fichier  : " + folderPath);
                        File newFile;
                        File clickedFile = new File(path);
                        if(clickedFile.isDirectory())
                        {
                            newFile = new File(path + "/" + result.get());
                            new File(newFile.getAbsolutePath()).createNewFile();
                            selectedItem.getChildren().add(new TreeItem<>(new TreeItemFile(newFile)));
                        }
                        else
                        {
                            newFile = new File(new File(path).getParentFile().getAbsolutePath() + "/" + result.get());
                            selectedItem.getParent().getChildren().add(new TreeItem<>(new TreeItemFile(newFile)));
                            new File(newFile.getAbsolutePath()).createNewFile();
                        }

                       /* else
                            System.err.println("Echec de création d'un nouveau fichier via le clic droit");*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            else
            {
                System.out.println("Impossible de créer un fichier à partir d'un fichier, il faut faire clic droit sur un dossier");
            }
        });
        return createFileItem;
    }

    // TODO : taille optimisable
    MenuItem generateCreateFolderItem()
    {
        MenuItem createFileItem =  new MenuItem("Create Folder");
        createFileItem.setOnAction(event -> {

            TreeItem selectedItem  = mw.fileManager.getSelectionModel().getSelectedItem();
            TreeItemFile tvFile;

            if (selectedItem instanceof TreeItemFile)
            {
                tvFile = (TreeItemFile) selectedItem;
            }
            else
            {
                tvFile = ((TreeItem<TreeItemFile>) selectedItem).getValue();
            }

            System.out.println("clicked folder");

            // On copie le chemin du  dossier
            String path = tvFile.getFile().getAbsolutePath();
            TextInputDialog dialog = new TextInputDialog("");
            dialog.setTitle("Create folder");
            dialog.setHeaderText("Adding a new folder");
            dialog.setContentText("Enter the folder name : ");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                // System.out.println("Chemin à partir duquel va être crée le dossier  : " + folderPath);
                File newFile;
                File clickedFile = new File(path);
                TreeItemFile newTreeFile;
                if(clickedFile.isDirectory())
                {
                    newFile = new File(path + "/" + result.get());
                    new File(newFile.getAbsolutePath()).mkdir();
                }
                else
                {
                    newFile = new File(new File(path).getParentFile().getAbsolutePath() + "/" + result.get());
                    new File(newFile.getAbsolutePath()).mkdir();
                }
                initTreeViewForProject(mw.curProjectPath);

                       /* else
                            System.err.println("Echec de création d'un nouveau fichier via le clic droit");*/

            }
            else
            {
                System.out.println("Impossible de créer un fichier à partir d'un fichier, il faut faire clic droit sur un dossier");
            }
        });
        return createFileItem;
    }

    /**
     * Crée un onglet contenant un fichier, ou le met au premier plan si il est déjà ouvert dans la liste des onglets
     */
    void createOrFocusClickedTab(MouseEvent event)
    {

        if(event.getClickCount() == 1)
        {
            TreeItem root = mw.fileManager.getRoot();
            int elemCount =  countChildCount(root); // nb elements de l'arbre
            int systemCount = 0;
            systemCount = getFileCount(mw.curProjectPath,systemCount);                     // nb elements du dossier (cherche par le systeme)
            if(elemCount != systemCount)
            {
                initTreeViewForProject(mw.curProjectPath);
            }
            else
            {
                System.out.println("Pas besoin de SYNC");
            }
        }
        if(event.getClickCount() == 2)
        {
            TreeItem item = mw.fileManager.getSelectionModel().getSelectedItem();

            if(item == null) return;
            File f1;
            if(item instanceof TreeItemFile)
            {
               f1 = ((TreeItemFile) item).getFile();
            }
            else
            {
                f1 = ((TreeItemFile) item.getValue()).getFile();
            }
            boolean fileAlreadyOpened= false;
            if(!f1.isDirectory())
            {
                Tab focusTab = null;
                if(mw.editTab.getTabs().size() > 0)
                    for(Tab t : mw.editTab.getTabs())
                    {
                        OpenFile of = (OpenFile)t;

                        if(of.getFile() != null && of.getFile().getAbsolutePath().equals(f1.getAbsolutePath()))
                        {
                            fileAlreadyOpened = true;
                            focusTab = t;
                        }
                    }
                if(!fileAlreadyOpened)
                    mw.openFile(f1);
                else
                    mw.editTab.getSelectionModel().select(focusTab);
            }
        }
    }

    // Sert à auto actualiser le treeViewer
    private int getFileCount(String dirPath, int systemCount) {
        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (files != null)
            for (int i = 0; i < files.length; i++) {
                systemCount++;
                File file = files[i];
                    return files.length + getFileCount(file.getAbsolutePath(),systemCount)+1;
            }
        return 0;
    }

    // Sert à auto actualiser le treeViewer
    private int countChildCount(TreeItem treeNode)
    {
        for(Object itm : treeNode.getChildren())
        {
            return  treeNode.getChildren().size()+ countChildCount((TreeItem)itm) +1;
        }
        return 0;
    }


    public TreeViewOperations(MainWindow mw)
    {
        this.mw = mw;
    }

    /***
     * Le TreeView est un visualisateur de fichiers et de dossiers, il permet d'avoir une vue globale de l'ensemble
     *  du projet.
     * @param projectName Indique le nom du dossier projet à charger
     */
    private void initTreeViewForProject(String projectName)
    {
        System.out.println("Project Name :" + projectName);
            File f = new File(projectName);
            TreeItem<TreeItemFile> root = new TreeItem<>(); // Le root est obligatoire afin de pouvoir commencer l'arbre
            root.setValue(new TreeItemFile(f));
            root.setExpanded(true);
            mw.fileManager.setRoot(root);
            mw.fileManager.setShowRoot(false);
            // Gestion du clic droit


            // Génération des boutons du clic droit sur l'arbre de fichiers
            MenuItem pasteItem = generatePasteItem();
            MenuItem copyItem = generateCopyItem(pasteItem);
            MenuItem renomItem = generateRenomItem();
            MenuItem suppItem = generateDelItem();
            MenuItem copyPathItem = generateCopyPath();
            MenuItem createFileItem = generateCreateFileItem();
            MenuItem createFolderItem = generateCreateFolderItem();

            ContextMenu cm = new ContextMenu(copyItem,pasteItem,renomItem,suppItem,createFileItem ,createFolderItem,new SeparatorMenuItem(),copyPathItem);
            mw.fileManager.setContextMenu(cm);
            // Fin gestion du clic droit



            mw.fileManager.setOnMouseClicked(event -> {
                createOrFocusClickedTab(event);
            });

            fillTreeViewer(projectName,root);
            // Auto resize du controle treeView
            mw.subSplitPane.setDividerPosition(0,0.2 +0.1*mw.fileManager.getExpandedItemCount());

    }


    private void fillTreeViewer(String projectName, TreeItem currentNode)
    {

        ArrayList<TreeItemFile> itemsFolder = getNodesForFolder(projectName,true);
        ArrayList<TreeItemFile> itemsFile = getNodesForFolder(projectName,false);

        for(TreeItemFile tvItem : itemsFolder)
        {
            currentNode.getChildren().add(tvItem);
            if(tvItem.getFile().isDirectory())
                fillTreeViewer(tvItem.getFile().getAbsolutePath(),tvItem);
        }

        for(TreeItemFile tvItem : itemsFile)
            currentNode.getChildren().add(tvItem);

    }

    /**
     * Demande à sélectionner le projet avec une fenêtre graphique puis affiche sa racine
     */
    public String initTreeViewAuto()
    {
        return initTreeViewAuto(null);
    }

    public String initTreeViewAuto(String pathStr)
    {
        try {
            // TODO : multiples références de treeRootPath
            String path;
            if(pathStr == null)
            {
                String pathCheck = new File(".").getCanonicalPath() + "/Projects";
                if(!new File(pathCheck).exists())
                {
                    new File(pathCheck).mkdirs();
                }
                String treeRootPath = new File(".").getCanonicalPath() + "/Projects";
                path = utils.selectFolderDialog(treeRootPath);
            }
            else
            {
                path = pathStr;
            }

            if(path != null)
            {
                initTreeViewForProject(path);
                mw.curProjectPath = path;
                return path;
            }
            else {
                System.err.println("You must create a project first");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<TreeItemFile> getNodesForFolder(String folderToSearch, boolean searchFolders)
    {
        File folder = new File(folderToSearch);
        File[] files = folder.listFiles();
        ArrayList<TreeItemFile> items = new ArrayList<>();
        assert files != null;
        for(File f : files)
        {
            if(searchFolders )
                if(f.isDirectory())
                {
                    items.add(new TreeItemFile(f));
                }
            else if(f.isFile())
                    items.add(new TreeItemFile(f));
        }
        return items;
    }






}
