package fr.tldr.eta.data;

import javafx.scene.paint.Color;

public class IDETheme
{
    public Color backgroundColor = null;
    public Color textColor = null;
    public String themeName = "";

    public IDETheme()
    {

    }

    //Voici les noms des propriétés pour le theme en chargement XML
    public static String xmlBgColorID = "themeTxtColor";
    public static String xmlTxtColorID = "themeBgColor";
    public static String xmlThemeName = "themeName";

    public IDETheme(Color bgColor,Color txtColor,String themeName)
    {
        backgroundColor = bgColor;
        textColor = txtColor;
        this.themeName = themeName;
    }

    @Override
    public String toString() {
        return themeName;
    }
}
