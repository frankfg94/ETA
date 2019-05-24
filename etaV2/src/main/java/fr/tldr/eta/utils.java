package fr.tldr.eta;

import javafx.animation.PauseTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class utils
{
    public static File copiedFile = null;
	public static boolean cutEnabled;


	public static BufferedImage toBufferedImage(Image image)
	{
		if (image instanceof BufferedImage)
		{
			return (BufferedImage) image;
		}

		// Create a buffered image with transparency (icon can have transparency)
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB); // Alpha + Red + Green + Blue

		// Draw the image on to the buffered image
		Graphics2D bGr = bufferedImage.createGraphics();
		bGr.drawImage(image, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bufferedImage;
	}


	private static void _listf(String directoryName, ArrayList<File> files) {
		File directory = new File(directoryName);

		// Get all files from a directory.
		File[] fList = directory.listFiles();
		if(fList != null)
		{
			for (File file : fList)
			{
				if (file.isFile())
				{
					files.add(file);
				}
				else if (file.isDirectory())
				{
					_listf(file.getAbsolutePath(), files);
				}
			}
		}
	}

	public static ArrayList<File> listf(String directoryName)
	{
		ArrayList<File> lof = new ArrayList();

		_listf(directoryName, lof);

		return lof;
	}


	public static String selectFolderDialog(String treeRootPath)
	{
		File folder = new File(treeRootPath);
		File[] files = folder.listFiles();
		assert files != null;
		//System.out.println(files.length + " éléments trouvés au chemin :"  + treeRootPath);
		ArrayList<String> folderNames = new ArrayList<>();
		for(File f : files)
		{
			if(f.isDirectory())
			{
				//System.out.println("Dossier trouvé : " + f.getName() + " | on ajoute à la liste ");
				folderNames.add(f.getName());
			}
			/*else
				System.out.println("Fichier trouvé : " + f.getName());*/
		}
		File f = new File(treeRootPath);
		if(folderNames.size() == 0)
		{
			utils.sendNotification("Aucun projet trouvé","Vous n'avez aucun projet, veuillez en créer un");
			return null;
		}
		ChoiceDialog<String> dialog = new ChoiceDialog<>(folderNames.get(0),folderNames);
		dialog.setTitle("Oppen");
		dialog.setHeaderText(folderNames.size() + " found project");
		dialog.setContentText("select the project to be load");
		dialog.getDialogPane();
		Optional<String> result = dialog.showAndWait();
		if(result.isPresent())
		{
			System.out.println("selectedFolderName : "+ result.get());
			try {
				return new File(".").getCanonicalPath() + "\\Projects\\"+result.get();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.err.println("Aucun projet séléctionné");
		return null;
	}

	/**
	 * Affiche une notification graphique sur notre application
	 * @param message le texte à afficher
	 */
	public static void sendNotification(String title,String message)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION,"");
		alert.setTitle("Information Dialog");
		alert.setHeaderText(title);
		alert.setContentText(message);
		// On enlève la barre d'outils

		alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
		Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
		alert.initStyle(StageStyle.UNDECORATED);
		alertStage.setAlwaysOnTop(true);
		alertStage.toFront();
		alert.show();

		PauseTransition delay = new PauseTransition(Duration.seconds(2));
		delay.play();
		delay.setOnFinished(e -> alert.hide());
	}
}
