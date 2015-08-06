package example.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author vikas
 *
 */
public class XMLUtils {

  /**
   * Reads an XML file from remote location and returns it as a DOM Document
   * 
   * @param remoteXML_URI
   *          - URL of the XML file at remote location
   * @return DOM document created from the XML provided
   */
  public static Document readRemoteXML(String remoteXML_URI) {
    Document domDocument = null;
    try {
      URL url = new URL(remoteXML_URI);
      URLConnection urlConnection = url.openConnection();
      InputStream is = new BufferedInputStream(urlConnection.getInputStream());

      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      domDocument = dBuilder.parse(is);
      printDOMContent(domDocument);

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    return domDocument;
  }

  /**
   * Reads an XML file from a local directory and returns it as a DOM Document
   * 
   * @param localXML_URI
   *          - URL of the local XML file
   * @return DOM document created from the XML provided
   */
  public static Document readLocalXML(String localXML_URI) {
    Document domDocument = null;
    try {
      File file = new File(localXML_URI);
      DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuildFactory.newDocumentBuilder();
      domDocument = docBuilder.parse(file);
      printDOMContent(domDocument);

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
    return domDocument;
  }

  /**
   * Prints the content of a DOM Document
   * 
   * @param domDoc
   *          - the DOM Document
   */
  private static void printDOMContent(Document domDoc) {
    domDoc.getDocumentElement().normalize();

    System.out.println("\n\nRoot element : " + domDoc.getDocumentElement().getNodeName());
    NodeList nodeList = domDoc.getDocumentElement().getChildNodes();
    System.out.println("----------------------------");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        // Recursively get child node contents
        getChildNodeContent(node);
      }
    }
  }

  /**
   * @param node
   */
  private static void getChildNodeContent(Node node) {
    NodeList childNodes = node.getChildNodes();
    int childCount = childNodes.getLength();
    // If any child element node is present, get its children's nodes content
    // recursively
    if (childCount > 1) {
      System.out.println("\nCurrent Element :" + node.getNodeName());
      for (int i = 0; i < childCount; i++) {
        Node tempNode = childNodes.item(i);
        if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
          getChildNodeContent(tempNode);
        }
      }
    } else {
      System.out.println(node.getNodeName() + " = " + node.getTextContent());
    }
  }

  /**
   * Merges two DOM documents into a single DOM document and returns the single
   * merged document
   * 
   * @param domDoc1
   *          - First DOM document
   * @param domDoc2
   *          - Second DOM document
   * @return The merged document
   */
  public static Document mergeDOMDocuments(Document domDoc1, Document domDoc2) {
    domDoc1.getDocumentElement().normalize();
    domDoc2.getDocumentElement().normalize();
    NodeList nodeList1 = domDoc1.getDocumentElement().getChildNodes();
    NodeList nodeList2 = domDoc2.getDocumentElement().getChildNodes();

    for (int x1 = 0; x1 < nodeList1.getLength(); x1++) {
      Node node1 = nodeList1.item(x1);
      if (node1.getNodeType() == Node.ELEMENT_NODE) {
        for (int x2 = 0; x2 < nodeList2.getLength(); x2++) {
          Node node2 = nodeList2.item(x2);

          // If an element node from doc2 has same node name as the current
          // element node from doc1 then append the child elements from doc2 to
          // doc1
          if ((node2.getNodeType() == Node.ELEMENT_NODE) && (node2.getNodeName().equals(node1.getNodeName()))) {
            String tagName1 = node1.getNodeName();
            NodeList elementsNodeList1 = domDoc1.getElementsByTagName(tagName1);
            NodeList childNodesList2 = node2.getChildNodes();
            for (int i = 0; i < childNodesList2.getLength(); i++) {
              Node childNode2 = childNodesList2.item(i);
              if (childNode2.getNodeType() == Node.ELEMENT_NODE) {
                String childElement2TagName = childNode2.getNodeName();
                Node newNode = domDoc1.importNode(domDoc2.getElementsByTagName(childElement2TagName).item(0), true);
                elementsNodeList1.item(0).appendChild(newNode);
              }
            }
          }
        }
      }
    }

    return domDoc1;
  }

  /**
   * Based on a provided DOM document, writes an XML document to a given local
   * URI.
   * 
   * @param domDoc
   *          - The DOM document for creating XML
   * @param targetXML_URI
   *          - Local URI for the target XML file
   */
  public static void writeToXML(Document domDoc, String targetXML_URI) {
    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      DOMSource source = new DOMSource(domDoc);
      StreamResult result = new StreamResult(targetXML_URI);
      transformer.transform(source, result);
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (TransformerFactoryConfigurationError e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }
  }

  /**
   * Parses a local XML document and return the list of nodes from it.
   * 
   * @param xmlURI
   *          - Local URI of the target XML file
   * @return The list of nodes
   */
  public static List<String> getXMLNodesList(String xmlURI) {
    Document doc = null;
    List<String> xmlNodesList = new LinkedList<>();
    try {
      File file = new File(xmlURI);
      DocumentBuilderFactory docbuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docbuilderFactory.newDocumentBuilder();
      doc = docBuilder.parse(file);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }
    if (doc == null) {
      return null;
    }

    doc.getDocumentElement().normalize();

    NodeList nodeList = doc.getDocumentElement().getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        getChildCustom(node, xmlNodesList);
      }
    }

    return xmlNodesList;
  }

  /**
   * @param node
   * @param xmlNodesList
   */
  private static void getChildCustom(Node node, List<String> xmlNodesList) {
    NodeList childNodes = node.getChildNodes();
    int childCount = childNodes.getLength();
    if (childCount > 1) {
      for (int i = 0; i < childCount; i++) {
        Node tempNode = childNodes.item(i);
        if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
          getChildCustom(tempNode, xmlNodesList);
        }
      }
    } else {
      xmlNodesList.add(node.getTextContent());
    }
  }

}
