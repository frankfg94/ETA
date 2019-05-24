package fr.tldr.eta.data;

import javafx.scene.control.Alert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Properties;


public class SettingsData {

    public SettingsData(){
        //loadConfig(SettingsData.class.getResourceAsStream("/assets/config/defaultConfig.xml"));
    }


    public static void SaveTheme(IDETheme theme)
    {
        try
        {
            Properties Data = new Properties();
            Data.setProperty(IDETheme.xmlTxtColorID,theme.textColor.toString());
            Data.setProperty(IDETheme.xmlBgColorID,theme.backgroundColor.toString());
            Data.setProperty(IDETheme.xmlThemeName,theme.themeName);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            displayMsgBox("Erreur","Erreur lors de la sauvegarde de votre thème");
        }
    }

    private  static void displayMsgBox(String title,String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void saveXMLTheme(IDETheme theme) {

    }

    public static IDETheme loadXMLTheme() {
        try {
        JAXBContext jaxbContext = JAXBContext.newInstance(IDETheme.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            IDETheme theme = (IDETheme) jaxbUnmarshaller.unmarshal(new File("/assets/config/theme.xml"));
            return theme;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadConfig(){

    }

}
