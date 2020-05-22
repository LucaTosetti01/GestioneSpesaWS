package GestioneSpesaWS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author galimberti_francesco + il prof
 */
public class MyParser {

    private List prodotti;

    public MyParser() {
        prodotti = new ArrayList();
    }

    public List parseDocument(String filename) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document document;
        Element root, element;
        NodeList nodelist;
        Prodotto prodotto;
        // creazione dell’albero DOM dal documento XML
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.parse(filename);

        root = document.getDocumentElement();
        // generazione della lista degli elementi "table"        
        nodelist = root.getElementsByTagName("prodotto");
        if (nodelist != null && nodelist.getLength() > 0) {
            for (int i = 0; i < nodelist.getLength(); i++) {
                // solo la prima table contiene cio che mi interessa
                element = (Element) nodelist.item(i);
                prodotto = getProdotto(element);
                prodotti.add(prodotto);
            }
        }

        return prodotti;
    }

    private Element getSimpleChild(Element parentElement, String childElementName) {
        NodeList nodelist = parentElement.getElementsByTagName(childElementName);
        return (Element) nodelist.item(0);
    }

    private NodeList getComplexChild(Element parentElement, String childElementName) {
        NodeList nodelist = parentElement.getElementsByTagName(childElementName);
        return nodelist;
    }

    private Prodotto getProdotto(Element element1) {
        Prodotto prodotto;
        try {
            int idProdotto = MyLibXML.getIntValue(element1, "idProdotto");
            String genere = MyLibXML.getTextValue(element1, "genere");
            String etichetta = MyLibXML.getTextValue(element1, "etichetta");
            double costo = MyLibXML.getDoubleValue(element1, "costo");
            String nome = MyLibXML.getTextValue(element1, "nome");
            String marca = MyLibXML.getTextValue(element1, "marca");
            String descrizione = MyLibXML.getTextValue(element1, "descrizione");
            
            prodotto = new Prodotto(idProdotto,genere, etichetta, costo, nome, marca, descrizione);
        return prodotto;
        } catch (Exception ex) {
            String genere = MyLibXML.getTextValue(element1, "genere");
            String etichetta = MyLibXML.getTextValue(element1, "etichetta");
            double costo = MyLibXML.getDoubleValue(element1, "costo");
            String nome = MyLibXML.getTextValue(element1, "nome");
            String marca = MyLibXML.getTextValue(element1, "marca");
            String descrizione = MyLibXML.getTextValue(element1, "descrizione");
            
            prodotto = new Prodotto(genere, etichetta, costo, nome, marca, descrizione);
        return prodotto;
        }

        
    }

}
