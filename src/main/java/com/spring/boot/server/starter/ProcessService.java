package com.spring.boot.server.starter;

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

    public Map<Long, ServerInfo> servers = new HashMap<>();

    public Process starter(ServerInfo serverInfo)
            throws IOException, ParserConfigurationException, SAXException {

        System.out.println(serverInfo.getJarDir());
        System.out.println(serverInfo.getRscDir());
        Process server = new ProcessBuilder(
                "java", "-cp", "\"",
                serverInfo.getLibDir(), ";", serverInfo.getRscDir(), "\"",
                "-jar",
                serverInfo.getJarDir()).start();

        recordServerInfo(server, serverInfo);
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

    public void kill(Long pid) {
        servers.get(pid).getProcess().destroy();
        System.out.println("Program complete");
    }

    public void recordServerInfo(Process process, ServerInfo serverInfo)
            throws ParserConfigurationException, IOException, SAXException {

        String path =
                serverInfo.getRscDir().replace("/*", "") + File.separator + "ConfigServer.xml";
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(path);

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("ConfigServer");

        Node nNode = nList.item(0);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            Element eElement = (Element) nNode;
            String name =
                    eElement.getElementsByTagName("Name").item(0).getAttributes().item(0)
                            .getNodeValue();
            String port = eElement.getElementsByTagName("Port").item(0).getAttributes().item(0)
                    .getNodeValue();
            String login = eElement.getElementsByTagName("UserInfo").item(0).getAttributes().item(0)
                    .getNodeValue();
            String password = eElement.getElementsByTagName("UserInfo").item(0).getAttributes()
                    .item(1)
                    .getNodeValue();
            serverInfo.setName(name);
            serverInfo.setPort(port);
            serverInfo.setUserLogin(login);
            serverInfo.setUserPass(password);
            serverInfo.setProcess(process);
            servers.put(process.pid(), serverInfo);
        }
    }

    public ServerInfo getInfo(Long pid) {
        return servers.get(pid);
    }

}
