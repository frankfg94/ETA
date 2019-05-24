package assets.fxmlFiles;

import fr.tldr.eta.CustomDocumentFilter;

import javax.swing.*;
import javax.swing.text.AbstractDocument;

public class ColorationTest extends JFrame {

	public ColorationTest()
	{
		JTextPane pane = new JTextPane();
		pane.setText("Coucou ceci est un test");
		((AbstractDocument)pane.getDocument()).setDocumentFilter(new CustomDocumentFilter(pane));
		getContentPane().add(pane);
	}
}