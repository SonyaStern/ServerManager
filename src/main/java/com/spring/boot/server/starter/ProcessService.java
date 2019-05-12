package com.spring.boot.server.starter;

import static com.spring.boot.server.zip_reader.FileService.jarDir;
import static com.spring.boot.server.zip_reader.FileService.libDir;
import static com.spring.boot.server.zip_reader.FileService.rscDir;

import com.spring.boot.server.model.ServerInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class ProcessService {

  public static Map<Long, ServerInfo> servers = new HashMap<>();

  public Process starter() throws IOException, ParserConfigurationException, SAXException {

    Process server = new ProcessBuilder(
        "java", "-cp", "\"",
        libDir, ";", rscDir, "\"",
        "-jar",
        jarDir).start();

    recordServerInfo(server, rscDir);
//    System.out.println("java" + "-cp" + "\"" +
//        libDir + ";" + rscDir + "\"" +
//        "-jar" +
//        jarDir);
    new Thread(() -> {
      try {
        Reader errorReader = new InputStreamReader(server.getErrorStream());
        int ch;
        while ((ch = errorReader.read()) != -1) {
          System.out.print((char) ch);
        }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();

    new Thread(() -> {
      try {
        Reader reader = new InputStreamReader(server.getInputStream());
        int ch;
        while ((ch = reader.read()) != -1) {
          System.out.print((char) ch);
        }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();

    return server;
  }

  public  void kill(Long pid) {
    servers.get(pid).getProcess().destroy();
    System.out.println("Program complete");
  }

  public  void recordServerInfo(Process process, String rscDir)
      throws ParserConfigurationException, IOException, SAXException {

    String path = rscDir.replace("/*", "") + File.separator + "ConfigServer.xml";
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(path);

    doc.getDocumentElement().normalize();

    NodeList nList = doc.getElementsByTagName("ConfigServer");

    Node nNode = nList.item(0);

    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
      Element eElement = (Element) nNode;
      String name =
          eElement.getElementsByTagName("Name").item(0).getAttributes().item(0).getNodeValue();
      String port = eElement.getElementsByTagName("Port").item(0).getAttributes().item(0)
          .getNodeValue();
      String login = eElement.getElementsByTagName("UserInfo").item(0).getAttributes().item(0)
          .getNodeValue();
      String password = eElement.getElementsByTagName("UserInfo").item(0).getAttributes().item(1)
          .getNodeValue();
      servers.put(process.pid(), new ServerInfo(name, port, login, password, process));
    }
  }

  public  ServerInfo getInfo(Long pid) {
    return servers.get(pid);
  }

}
