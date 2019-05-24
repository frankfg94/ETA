package fr.tldr.eta.controler;

import fr.tldr.eta.OpenFile;
import fr.tldr.eta.ProjectTemplate;
import fr.tldr.eta.TreeItemFile;
import fr.tldr.eta.data.CommonData;
import fr.tldr.eta.helper.ThreadHelper;
import fr.tldr.eta.helper.XMLconfig;
import fr.tldr.eta.utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//import javafx.scene.input.DragEvent;

public class MainWindow
{
	@FXML
	public MenuBar menuBar;
	@FXML
	public Menu fileMenu;
	@FXML
	public MenuItem menuNewFile;
	@FXML
	public MenuItem menuOpenFile;
	@FXML
	public MenuItem menuSaveFile;
	@FXML
	public MenuItem menuSaveAsFile;

	@FXML
	public Menu editMenu;
	@FXML
	public Menu helpMenu;
	@FXML
	public Menu preferences;
	@FXML
	public Button btnExec;
	@FXML
	public Button btnCompilation;
	@FXML
	public TabPane editTab;
	@FXML
	public TreeView<TreeItemFile> fileManager;
	@FXML
	public TextArea terminal;
	public MenuItem menuNewProject;
	public MenuItem menuChangeProject;
	public SplitPane subSplitPane;
    public Menu projectMenu;
    public MenuItem menuProjectSettings;
    private String terminalInputString = "";
	private OutputStream outputWriter;
	private Stage primaryStage;
	private XMLconfig projectConfig = null;


	public MainWindow()
	{
	}


	@FXML
	private void initialize()
	{
		this.editTab.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
		this.editTab.getTabs().add(new OpenFile(this.primaryStage));
		if (CommonData.getSysName().equals("Mac OS X"))
			this.menuBar.useSystemMenuBarProperty().set(true);

		//initTreeViewAuto();


		Image compilImg = new Image(this.getClass().getResourceAsStream("/assets/images/compl.png"),15,15,true,true);
		Image execImg = new Image(this.getClass().getResourceAsStream("/assets/images/run.png"),15,15,true,true);
		ImageView img1 = new ImageView(compilImg);
		ImageView img2 = new ImageView(execImg);
		this.btnExec.setText("");
		this.btnCompilation.setText("");
		this.btnCompilation.setGraphic(img1);
		this.btnExec.setGraphic(img2);


	}

	/**
	 * create a new file do not save the file
	 */
	@FXML
	public void onNew()
	{
		Tab tab = new OpenFile(this.primaryStage);
		editTab.getTabs().add(tab);
		this.editTab.getSelectionModel().select(tab);
	}

	/**
	 * open a new file
	 */
	@FXML
	public void onOpen()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open File");

		openFile(fileChooser.showOpenDialog(this.primaryStage));
	}


	/**
	 * open a file
	 *
	 * @param file the file to open
	 */
	public void openFile(File file) // todo public ?
	{
		Tab currentTab = getCurrentTab();
		OpenFile currentOpenFileTab;

		for (Tab t : this.editTab.getTabs())
		{
			if (currentTab instanceof OpenFile)
			{
				currentOpenFileTab = (OpenFile) currentTab;
				if (currentOpenFileTab.getFile() == file)
				{
					this.editTab.getSelectionModel().select(t);
					return;
				}
			}
		}
		if (currentTab instanceof OpenFile)
		{
			currentOpenFileTab = (OpenFile) currentTab;
			if (currentOpenFileTab.isEmptyFile())
			{
				currentOpenFileTab.openAFile(file);
			}
			else
			{
				OpenFile tab = new OpenFile(file, this.primaryStage);
				this.editTab.getTabs().add(tab);
				this.editTab.getSelectionModel().select(tab);
			}
		}
		else
		{
			OpenFile tab = new OpenFile(file, this.primaryStage);
			this.editTab.getTabs().add(tab);
			this.editTab.getSelectionModel().select(tab);
		}
	}

	/**
	 * save a file
	 * the paths mes exist else @see(onSaveAs)
	 */
	@FXML
	public void onSave()
	{
		Tab current = getCurrentTab();
		if (current instanceof OpenFile)
		{
			OpenFile currentTab = (OpenFile) current;
			currentTab.save();
		}
	}

	/**
	 * select a new location to save the file
	 */
	@FXML
	public void onSaveAs()
	{
		Tab current = this.getCurrentTab();
		if (current instanceof OpenFile)
		{
			OpenFile currentTab = (OpenFile) current;
			currentTab.saveAs();
		}
	}


	@FXML
	public void onCompilation()
	{
		if (projectConfig.getCompilerPath() != null && projectConfig.getCompilerPath() != "" && projectConfig.getOutputPaths() != null && projectConfig.getOutputPaths() != "")
		{
			ArrayList<File> arrayFile = utils.listf(curProjectPath);
			Tab current = this.getCurrentTab();
			if (current instanceof OpenFile) {
				OpenFile currentOpenfile = (OpenFile) current;
				currentOpenfile.saveIfNotSaved();

				ThreadHelper.start(() ->
						this.multipleCompilation(arrayFile));
			}
		}
		else
		{
			utils.sendNotification("No compilator", "please indicate the path for the compilator in project settings");
		}
	}

	@FXML
	public void onExec()
	{
		if (projectConfig.getExecutorPath() != null && projectConfig.getExecutorPath() != "" && projectConfig.getOutputPaths() != null && projectConfig.getOutputPaths() != "")
		{
			ArrayList<File> arrayFile = utils.listf(curProjectPath);
			File file = new File(this.projectConfig.getPathExe());
			Tab current = this.getCurrentTab();
			if (current instanceof OpenFile) {
				OpenFile currentOpenfile = (OpenFile) current;
				currentOpenfile.saveIfNotSaved();
				ThreadHelper.start(() ->
				{
					this.multipleCompilation(arrayFile);
					this.execution(file);
				});
			}
		}
		else
		{
			utils.sendNotification("No runtime environnment","please indicate the path for the runtime environnment in project settings");
		}
	}


	@FXML
	public void onDragAndDrop(DragEvent dragEvent)
	{
		if (dragEvent.getDragboard().hasFiles())
			dragEvent.acceptTransferModes(TransferMode.ANY);
	}

	/**
	 * Create a new tab for each file dropped in the editPanel (the tabPanel)
	 *
	 * @param event DragEvent
	 */
	@FXML
	public void createTabOnDrop(DragEvent event)
	{
		List<File> files = event.getDragboard().getFiles();
		for (File f : files)
		{
			openFile(f);
		}
	}



	public static String curProjectPath;




	public void setPrimaryStage(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
	}

	private Tab getCurrentTab()
	{
		return this.editTab.getSelectionModel().getSelectedItem();
	}



	private void multipleCompilation(ArrayList<File> arrayFile)
	{
		for(File f : arrayFile)
		{
			compilation(f);
		}
	}
	private void compilation(File file)
	{
		String extension ="";
		try {
			extension = file.getCanonicalPath();
			 if (extension.length() > 3)
			 {
				extension = extension.substring(extension.length() - 3);
			} else {
				// whatever is appropriate in this case
				throw new IllegalArgumentException("word has less than 3 characters!");
			}
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println(extension);
		switch (CommonData.getSysName())
		{
			case "Linux":
			case "Mac OS X":
				String[] commandeLinuxComp = new String[0];
				try
				{
					//todo isolate file name from extension
					if (extension.equals("lua"))
					{
						commandeLinuxComp = new String[]{"bash", "-c", projectConfig.getCompilerPath() + " -o " + file.getCanonicalPath().replaceAll(projectConfig.getDirectoryPath(),projectConfig.getOutputPaths()) + "c " + file.getCanonicalPath()};
						System.out.println("Command ="+ commandeLinuxComp);
						processFlux(commandeLinuxComp);
					}
				} catch (IOException e)
				{
					e.printStackTrace();
				}

				break;
			case "Windows 10":
				String[] commandeWindowsComp = new String[0];
				try
				{
					//commandeWindowsComp = new String[] {"cmd.exe", "/C", "luac -o \"" +  file.getCanonicalPath() + "c\" \""+ file.getCanonicalPath()+ "\""};
					if (extension.equals("lua")) {
						commandeWindowsComp = new String[]{"cmd.exe","/C", projectConfig.getCompilerPath() + " -o" + " " + projectConfig.getOutputPaths() + "\\" + file.getName() + "c " + file.getCanonicalPath()};
						processFlux(commandeWindowsComp);
					}

				} catch (IOException e)
				{
					e.printStackTrace();
				}

				break;
			default:
				System.out.println("sysName is not valid");
				break;
		}
	}


	private void execution(File file)
	{

		switch (CommonData.getSysName())
		{
			case "Linux":
			case "Mac OS X":
				String[] commandeLinuxExec = new String[0];
				try
				{
					commandeLinuxExec = new String[]{"bash", "-c", projectConfig.getExecutorPath() + " " + file.getCanonicalPath().replaceAll(projectConfig.getDirectoryPath(),projectConfig.getOutputPaths()) + "c"};
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				processFlux(commandeLinuxExec);
				break;
			case "Windows 10":
				String[] commandeWindowsExec = new String[0];

				//	commandeWindowsExec = new String[] {"cmd.exe", "/C",   "lua \"" +  file.getCanonicalPath()+ "c\""};
				commandeWindowsExec = new String[]{"cmd.exe", "/C",   projectConfig.getExecutorPath() + " " +  projectConfig.getOutputPaths() + "\\" + file.getName() + "c\""};

				processFlux(commandeWindowsExec);
				break;
			default:
				System.out.println("sysName is not valid");
				break;
		}
	}

	private void processFlux(String[] commande)
	{
		ProcessBuilder pb = new ProcessBuilder(commande);
		try
		{
			this.terminalInputString = "";
			this.terminal.setEditable(true);
			Process process = pb.start();

			this.outputWriter = process.getOutputStream();
			DisplayStream errorDS = new DisplayStream(process.getErrorStream(), "error");
			DisplayStream stdoutDS = new DisplayStream(process.getInputStream(), "stdout");

			errorDS.start();
			stdoutDS.start();

			process.waitFor();
			process.destroy();
			System.out.println("  stop");
			this.terminal.setEditable(false);

		} catch (IOException | InterruptedException e)
		{
			e.printStackTrace();
		}

	}

	public void terminallKeyType(KeyEvent keyEvent)
	{

		if (keyEvent.getCode() == KeyCode.BACK_SPACE || keyEvent.getCharacter().equals("\b"))
		{
			if (this.terminalInputString.length() > 0)
			{
				this.terminalInputString = this.terminalInputString.substring(0, this.terminalInputString.length() - 1);
			}
		}
		else
		{
			this.terminalInputString = this.terminalInputString.concat(keyEvent.getCharacter());

		}
		System.out.println(keyEvent.getCharacter() + " -> \" "+this.terminalInputString+"\"");
		System.out.println(terminalInputString.length());


	}

	public void terminallKeyPrses(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.ENTER)
		{
			try
			{
				String cmd = (this.terminalInputString.replaceAll("\r", "") + "\n").replaceAll("\b", "");
				this.outputWriter.write(cmd.getBytes());
				this.outputWriter.flush();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			this.terminalInputString = "";
		}
		/*else if (keyEvent.getCode() == KeyCode.BACK_SPACE)
		{
			if (this.terminalInputString.length() >= 0)
			{
				this.terminalInputString = this.terminalInputString.substring(0, this.terminalInputString.length() - 1);
			}
		}*/
	}
	@FXML
	public MenuItem settingsWindow;

	@FXML
	public void showSettingsWindow()
	{
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/fxmlFiles/Settings.fxml"));
		Parent root = null;
		try {
			root = loader.load();
		stage.initOwner(this.primaryStage);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setTitle("Parametres");
		stage.setScene(new Scene(root, 300, 275));
		//stage.setMinHeight(100); // set the minimum Height value
		//stage.setMinWidth(500); //set the minimum Width value
		stage.show();

		Settings controller = loader.getController();
		controller.setStage(stage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onChangeProject(ActionEvent event)  {
		TreeViewOperations treeViewOperations = new TreeViewOperations(this);
		String path =treeViewOperations.initTreeViewAuto();
		if(path != null)
		{
			this.projectConfig = new XMLconfig(path + (path.charAt(path.length()-1) != '\\' || path.charAt(path.length()-1) != '/'? "/":"") + "project.xml") ;
		}

	}

	public void onNewProject(ActionEvent event) {

			Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/fxmlFiles/NewProject.fxml"));
			Parent root;
			try {
				root = loader.load();
				stage.initOwner(this.primaryStage);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.setTitle("Nouveau Projet");
				stage.setScene(new Scene(root));


				NewProject controller = loader.getController();
				controller.enableProjectNameVerificator();
				ProjectTemplate[] allTemplates = ProjectTemplate.values();

				// Chargement de la combobox
				for(ProjectTemplate template : allTemplates)
					controller.templateBox.getItems().add(template);
				if(controller.templateBox.getItems().size() > 0 )
					controller.templateBox.getSelectionModel().select(controller.templateBox.getItems().get(0));

				if(primaryStage == null)
					return;
				controller.setPrimaryStage(primaryStage);
				stage.showAndWait();
				this.projectConfig = controller.getXmlModif();
				new TreeViewOperations(this).initTreeViewAuto(this.projectConfig.getDirectoryPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

    //Opérations sur l'explorateur de fichiers Arbre / TreeView
    private void ExpandFrom(TreeItem<TreeItemFile> baseNode, boolean isExpanded)
    {
        baseNode.setExpanded(isExpanded);
        for (Object item : baseNode.getChildren())
            ExpandFrom((TreeItem<TreeItemFile>)item,isExpanded);
    }

    public void onExpandTreeView() {
        ExpandFrom(fileManager.getRoot(),true);
    }

    public void onReduceTreeView() {
        for(TreeItem<TreeItemFile> item: fileManager.getRoot().getChildren())
            ExpandFrom(item,false);
    }

    public void onProjectSettings(ActionEvent actionEvent) {

	    //todo test if has project
		if(this.projectConfig == null)
		{
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("No Project Open");
			alert.setHeaderText("There is no project open!! ");
			alert.show();
			return;
		}


        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/fxmlFiles/ProjectSettingsDialog.fxml"));
        Parent root;

        try {

            root = loader.load();
            stage.initOwner(this.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Project Settings");
            stage.setScene(new Scene(root));

            ProjectSettingsDialog controller = loader.getController();
            controller.setStage(stage);


            if (false)//todo change folse by project setting var != null
            {
                controller.setProjectSettings(this.projectConfig);//todo complete this function
            }




            stage.show();





        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void onGitHubSend(ActionEvent event)
	{
		Stage stage = new Stage();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/fxmlFiles/GitPusher.fxml"));
		Parent root = null;
		try {
			root = loader.load();
			stage.initOwner(this.primaryStage);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setTitle("Git Connector");
			stage.setScene(new Scene(root));
			stage.show();
	} catch (IOException e) {
			e.printStackTrace();
		}
	}


		public class DisplayStream extends Thread
	{
		private InputStream is;
		private String type;
		DisplayStream(InputStream is, String type)
		{
			this.is = is;
			this.type = type;
		}

		@Override
		public void run()
		{
			try
			{
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while (((line = br.readLine()) != null)){
					System.out.println(type + " > " + line);
					String str =line+"\n";
					Platform.runLater(new Runnable() {

						/**
						 * When an object implementing interface <code>Runnable</code> is used
						 * to create a thread, starting the thread causes the object's
						 * <code>run</code> method to be called in that separately executing
						 * thread.
						 * <p>
						 * The general contract of the method <code>run</code> is that it may
						 * take any action whatsoever.
						 *
						 * @see Thread#run()
						 */
						@Override
						public void run()
						{
							terminal.appendText(str);
						}
					});


				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
