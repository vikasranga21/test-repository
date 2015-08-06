package example.test;

import org.w3c.dom.Document;

/**
 * @author vikas
 *
 */
public class RunTest {

  public static void main(String[] args) {
    final String remoteXML_URI = "https://appvigil.co/test1.xml";
    final String localXML_URI = "src/main/resources/test2.xml";
    final String masterXML_URI = "bin/masterXML.xml";
    final int bufferSize = 3;

    Document remoteXMLDoc = XMLUtils.readRemoteXML(remoteXML_URI);
    Document localXMLDoc = XMLUtils.readLocalXML(localXML_URI);

    Document mergeXMLDoc = XMLUtils.mergeDOMDocuments(remoteXMLDoc, localXMLDoc);
    XMLUtils.writeToXML(mergeXMLDoc, masterXML_URI);

    ProducerConsumerForXML.implementProducerConsumerForXML(masterXML_URI, bufferSize);
  }

}
