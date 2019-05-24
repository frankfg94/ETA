package fr.tldr.eta.helper;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XMLconfig {
    private String pathXML;

    public XMLconfig(String path){
        this.pathXML = path;
    }

    public XMLconfig(String path,String templateChoosed,String description,String projectName){
            this(path);
            generateProjectConfig(path,templateChoosed,description,projectName);
    }


    private void generateProjectConfig(String pathDirectoryDefault,String templateChoosed,String description,String projectName)
    {
        try {
            DocumentBuilderFactory XML_Fabrique_Constructeur = DocumentBuilderFactory.newInstance();
            DocumentBuilder XML_Constructeur = XML_Fabrique_Constructeur.newDocumentBuilder();

            Document XML_Document = XML_Constructeur.newDocument();
            Document document =  XML_Constructeur.newDocument();
            Element documentation = XML_Document.createElement("Setting");
            XML_Document.appendChild(documentation);

            Element path = XML_Document.createElement("Paths");
            documentation.appendChild(path);
            Attr attribut1 = XML_Document.createAttribute("pathExec");
            attribut1.setValue("");
            path.setAttributeNode(attribut1);

            Element pathDirectory = XML_Document.createElement("Directory");
            documentation.appendChild( pathDirectory);
            Attr attribut2 = XML_Document.createAttribute("DirectoryPath");
            attribut2.setValue(pathDirectoryDefault);
            pathDirectory.setAttributeNode(attribut2);

            Element creationDate = XML_Document.createElement("Date");
            documentation.appendChild(creationDate);
            Attr attribut3 = XML_Document.createAttribute("CreationDate");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            attribut3.setValue(dtf.format(now));
            creationDate.setAttributeNode(attribut3);

            Element template = XML_Document.createElement("initialTemplate");
            documentation.appendChild(template);
            Attr attribut4 = XML_Document.createAttribute("Template");
            attribut4.setValue(templateChoosed);
            template.setAttributeNode(attribut4);

            Element projectN = XML_Document.createElement("ProjectName");
            documentation.appendChild(projectN);
            Attr attribut5 = XML_Document.createAttribute("Name");
            attribut5.setValue(projectName);
            projectN.setAttributeNode(attribut5);

            Element arguments = XML_Document.createElement("ArgumentField");
            documentation.appendChild(arguments);
            Attr attribut6 = XML_Document.createAttribute("Argument");
            attribut6.setValue("");
            arguments.setAttributeNode(attribut6);

            Element outputPaths = XML_Document.createElement("outputPaths");
            documentation.appendChild(outputPaths);
            Attr attribut7 = XML_Document.createAttribute("Paths");
            attribut7.setValue("");
            outputPaths.setAttributeNode(attribut7);

            Element executorPath = XML_Document.createElement("executorPath");
            documentation.appendChild(executorPath);
            Attr attribut8 = XML_Document.createAttribute("Paths");
            attribut8.setValue("");
            executorPath.setAttributeNode(attribut8);

            Element compilerPath = XML_Document.createElement("compilerPath");
            documentation.appendChild(compilerPath);
            Attr attribut9 = XML_Document.createAttribute("Paths");
            attribut9.setValue("");
            compilerPath.setAttributeNode(attribut9);

            Element Description = XML_Document.createElement("Description");
            documentation.appendChild(Description);
            Attr attribut10 = XML_Document.createAttribute("DescriptionString");
            attribut10.setValue(description);
            Description.setAttributeNode(attribut10);


            TransformerFactory XML_Fabrique_Transformeur = TransformerFactory.newInstance();
            Transformer XML_Transformeur = XML_Fabrique_Transformeur.newTransformer();
            XML_Transformeur.setOutputProperty(OutputKeys.INDENT, "yes");
            XML_Transformeur.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(XML_Document);
            StreamResult resultat = new StreamResult(new File(pathDirectoryDefault +"/project.xml"));
            XML_Transformeur.transform(source, resultat);

            System.out.println("Le fichier XML a été généré !");
        } catch (ParserConfigurationException | TransformerException pce)
        {
            pce.printStackTrace();
        }
        pathXML = pathDirectoryDefault +"/project.xml";

    }

    public void setDirectory(String directoryPath)
    {
        try{
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            Node directory = doc.getElementsByTagName("Directory").item(0);

            // update staff attribute
            NamedNodeMap attr = directory.getAttributes();
            Node nodeAttr = attr.getNamedItem("DirectoryPath");
            nodeAttr.setTextContent(directoryPath);
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        }catch (ParserConfigurationException | SAXException | IOException | TransformerException pce) {
            pce.printStackTrace();
        }


    }

    public String getPathExe() {
        String execPath = "";
        try {
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            NodeList list = doc.getElementsByTagName("Paths");

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                execPath =  list.item(i).getAttributes().getNamedItem("pathExec").getNodeValue();
            }
        }catch (ParserConfigurationException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
        return execPath;
    }
    public void setPathExe(String pathExec){
        try{
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            Node path = doc.getElementsByTagName("Paths").item(0);

            // update staff attribute
            NamedNodeMap attr = path.getAttributes();
            Node nodeAttr = attr.getNamedItem("pathExec");
            nodeAttr.setTextContent(pathExec);
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        }catch (ParserConfigurationException | TransformerException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
    }

    public  String getDirectoryPath() {
        String directoryPath = "";
        try {
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            Node path = doc.getElementsByTagName("Directory").item(0);
            NodeList list = doc.getElementsByTagName("Directory");
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                directoryPath =  list.item(i).getAttributes().getNamedItem("DirectoryPath").getNodeValue();
            }
        }catch (ParserConfigurationException | SAXException | IOException pce) {
            pce.printStackTrace();
        }
        return directoryPath;
    }


    public String getPathXML() {
        return pathXML;
    }

    public String getProjecName() {
        String name = "";
        try {
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            NodeList list = doc.getElementsByTagName("ProjectName");

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                name =  list.item(i).getAttributes().getNamedItem("Name").getNodeValue();
            }
        }catch (ParserConfigurationException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
        return name;
    }
    public void setProjectName (String projectName){
        try{
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            Node path = doc.getElementsByTagName("ProjectName").item(0);

            // update staff attribute
            NamedNodeMap attr = path.getAttributes();
            Node nodeAttr = attr.getNamedItem("Name");
            nodeAttr.setTextContent(projectName);
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        }catch (ParserConfigurationException | TransformerException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
    }

    public String getArgumentField() {
        String arguments = "";
        try {
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            NodeList list = doc.getElementsByTagName("ArgumentField");

            for (int i = 0; i < list.getLength(); i++) {
                arguments =  list.item(i).getAttributes().getNamedItem("Argument").getNodeValue();
            }
        }catch (ParserConfigurationException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
        return arguments;
    }
    public void setArgumentField (String argumentField){
        try{
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            Node path = doc.getElementsByTagName("ArgumentField").item(0);

            // update staff attribute
            NamedNodeMap attr = path.getAttributes();
            Node nodeAttr = attr.getNamedItem("Argument");
            nodeAttr.setTextContent(argumentField);
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        }catch (ParserConfigurationException | TransformerException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
    }

    public String getOutputPaths() {
        String OutputPaths = "";
        try {
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            NodeList list = doc.getElementsByTagName("outputPaths");

            for (int i = 0; i < list.getLength(); i++) {
                OutputPaths =  list.item(i).getAttributes().getNamedItem("Paths").getNodeValue();
            }
        }catch (ParserConfigurationException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
        return OutputPaths;
    }
    public void setOutputPaths (String OutputPaths ){
        try{
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            Node path = doc.getElementsByTagName("outputPaths").item(0);

            // update staff attribute
            NamedNodeMap attr = path.getAttributes();
            Node nodeAttr = attr.getNamedItem("Paths");
            nodeAttr.setTextContent(OutputPaths );
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        }catch (ParserConfigurationException | TransformerException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
    }

    public String getExecutorPath() {
        String executorPath = "";
        try {
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            NodeList list = doc.getElementsByTagName("executorPath");

            for (int i = 0; i < list.getLength(); i++) {
                executorPath =  list.item(i).getAttributes().getNamedItem("Paths").getNodeValue();
            }
        }catch (ParserConfigurationException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
        return executorPath;
    }
    public void setExecutorPath (String executorPath ){
        try{
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            Node path = doc.getElementsByTagName("executorPath").item(0);

            // update staff attribute
            NamedNodeMap attr = path.getAttributes();
            Node nodeAttr = attr.getNamedItem("Paths");
            nodeAttr.setTextContent(executorPath );
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        }catch (ParserConfigurationException | TransformerException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
    }

    public String getCompilerPath() {
        String CompilerPath = "";
        try {
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            NodeList list = doc.getElementsByTagName("compilerPath");

            for (int i = 0; i < list.getLength(); i++) {
                CompilerPath =  list.item(i).getAttributes().getNamedItem("Paths").getNodeValue();
            }
        }catch (ParserConfigurationException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
        return CompilerPath;
    }
    public void setCompilerPath (String compilerPath ){
        try{
            String filepath = pathXML;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            Node path = doc.getElementsByTagName("compilerPath").item(0);

            // update staff attribute
            NamedNodeMap attr = path.getAttributes();
            Node nodeAttr = attr.getNamedItem("Paths");
            nodeAttr.setTextContent(compilerPath );
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filepath));
            transformer.transform(source, result);
        }catch (ParserConfigurationException | TransformerException | IOException | SAXException pce) {
            pce.printStackTrace();
        }
    }

}
