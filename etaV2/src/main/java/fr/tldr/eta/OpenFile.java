package fr.tldr.eta;

import fr.tldr.eta.data.converter.DataConverter;
import javafx.embed.swing.SwingNode;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class OpenFile extends Tab {
	/*variables */
	private File file = null;
//	private TextArea textArea = new TextArea(); //old system
//	private WebView webView;
//	private WebEngine webEngine;

	private String fileMd5sum;
	
	private String fileName;
	
	private boolean fileHasChanged = false;
	private boolean fileCanChange = true;
	private Stage primaryStage;
	private SwingNode swingNode;
	private JTextPane textPane;
	
	/**
	 * constructor
	 * create a new file
	 */
	public OpenFile(Stage primaryStage) {

		super();

		this.swingNode = new SwingNode();
		this.setContent(this.swingNode);
		this.primaryStage = primaryStage;
		this.setText(this.fileName = "untitled");
		this.init();
		// fixme this.fileMd5sum = this.hashString(this.textArea.getText());
		textPane = new JTextPane();
		((AbstractDocument)textPane.getDocument()).setDocumentFilter(new CustomDocumentFilter(textPane));
		TextLineNumber tln = new TextLineNumber(textPane);
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setRowHeaderView(tln);
		this.swingNode.setContent(scrollPane);
		
	}
	
	/**
	 * constructor
	 *
	 * @param file the file that will be open
	 */
	@SuppressWarnings("unused")
	public OpenFile(File file, Stage primaryStage) {
		this(primaryStage);
		openAFile(file);
	}
	
	
	/**
	 * Open a file that ont the on this tab
	 * and display the text in {@code TextArea}
	 *
	 * @param file the {@code File} that will be open
	 */
	public void openAFile(File file) {
		assert file != null;
		if (!file.isFile())
		{
			return;
		}
		this.file = file;
		//this.setContent(this.textArea);
		StringBuilder fromFileBuilder = new StringBuilder(); // create a temporary StringBuilder to append strings
		BufferedReader reader = null;
		//try to open file
		try {

			reader = new BufferedReader(new FileReader(file));
			String line;
			
			// get each lines and append it to the String builder
			while ((line = reader.readLine()) != null) {
				fromFileBuilder.append(line).append("\n");
				textPane.setText(textPane.getText() + "\n" + line );
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//try to close file
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//set text in the text area

		this.fileCanChange = false;
		//fixme this.textArea.setText(fromFileBuilder.toString());
		this.fileCanChange = true;
		
		this.setText(this.fileName = this.file.getName());
		//fixme this.fileMd5sum = this.hashString(this.textArea.getText());
	}
	
	
	/**
	 * add somme {@code EventHandler} and {@code Listener} to the {@code Tab}
	 * and the {@code TextArea}
	 */
	private void init() {
		// set up event handler of a closing request
		this.setOnCloseRequest(
				new EventHandler<Event>() {
					/**
					 * Invoked when a specific event of the type for which this handler is
					 * registered happens.
					 *
					 * @param event the event which occurred
					 */
					@Override
					public void handle(Event event) {
						if (fileHasChanged) {
							Alert alert = new Alert(Alert.AlertType.WARNING);
							alert.setTitle("Save Changes ?");
							alert.setHeaderText(null);
							alert.setContentText(fileName + " has been modified,do you want to save changes?");
							ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
							ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.NO);
							ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
							alert.getButtonTypes().setAll(btnYes, btnNo, btnCancel);
							Optional<ButtonType> result = alert.showAndWait();
							if (result.isPresent()) {
								if (result.get() == btnYes) {
									save();
									if (fileHasChanged) {
										event.consume();
									}
								} else if (result.get() == btnCancel) {
									event.consume();
								}
							}
						}
						
					}
				}
		);
	}


	public void saveIfNotSaved()
	{
		if (fileHasChanged) {
			save();
		}
	}



	/**
	 * change file name or choose a name for the file
	 */
	public void saveAs() {
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("LUA files (*.lua)", "*.lua");
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.getExtensionFilters().add(extFilter2);
		
		// get the file name
		File newFile = fileChooser.showSaveDialog(this.primaryStage);
		// test if new file name is null
		
		if (newFile == null || newFile.isDirectory())
			return;
		// set the new file as file pasth
		this.file = newFile;
		
		this.setText(this.fileName = this.file.getName());
		_save();
	}
	
	/**
	 * test if the file can be save or must be saved as
	 */
	public void save() {
		if (file == null || !file.exists() || file.isDirectory()) {
			saveAs();
		} else {
			_save();
		}
		
	}
	
	
	/**
	 * save the file in the computer
	 */
	private void _save() {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(this.file));
			writer.write(this.textPane.getText());
			this.setText(this.fileName);
			this.fileHasChanged = false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	/**
	 * try to hash the string with the MD5 algorithm
	 *
	 * @param s the {@code String} to be hashed
	 * @return the hash code as a {@code String} in upper case
	 */
	private String hashString(String s) {
		MessageDigest md5Digest;
		try {
			md5Digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			//e.printStackTrace();
			return Integer.toString(s.hashCode());
		}
		md5Digest.update(s.getBytes());
		return DataConverter.printHexBinary(md5Digest.digest()).toUpperCase();
	}
	


	public boolean isEmptyFile() {
		return !fileHasChanged && file == null;
	}
	
	
	public File getFile() {
		return this.file;
	}
	
	/*
	public void compilation(); // move in MainWindow
	public void execution();// move in MainWindow
	private void processFlux(String[] commande); // move in MainWindow
	*/










}
