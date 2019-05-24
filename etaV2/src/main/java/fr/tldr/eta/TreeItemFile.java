package fr.tldr.eta;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class TreeItemFile extends TreeItem<String>
{
    private File file;
    private WritableImage iconFX;
    public TreeItemFile(File f)
    {

        this.file =  f;
        drawIcon(f);
    }

    public void drawIcon(File f)
    {
        ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
        java.awt.Image image = icon.getImage();
        iconFX = SwingFXUtils.toFXImage(utils.toBufferedImage(image),null);

        this.setGraphic(new ImageView(iconFX));
        this.setValue(f.getName());
    }

    public File getFile() {
        return file;
    }

    public WritableImage getIconFX() {
        return iconFX;
    }

    @Override
    public String toString()
    {
        if(file.isDirectory())
            return "[Dossier] "+file.getName();
        else if(file.isFile())
            return "[Fichier] "+file.getName();
        else
            return  "[?] "+file.getName();
    }
}