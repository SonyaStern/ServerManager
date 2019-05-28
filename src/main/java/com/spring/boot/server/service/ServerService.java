package com.spring.boot.server.service;

import com.spring.boot.server.model.ServerInfo;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class ServerService {

    @Getter
    public ConcurrentSkipListSet<ServerInfo> servers = new ConcurrentSkipListSet<>();

//    @Getter
//    public ConcurrentSkipListSet<String> logFiles = new ConcurrentSkipListSet<>();

    boolean contains(ServerInfo serverInfo) {

        for (ServerInfo server : servers) {
            if (server.getJarDir().equals(serverInfo.getJarDir()) || server.getName()
                    .equals(serverInfo.getName())) {
                return true;
            }
        }
        return false;
    }

    ServerInfo recordServerInfo(ServerInfo serverInfo)
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
            servers.add(serverInfo);
        }
        return serverInfo;
    }

    public ServerInfo getServer(String name) {
        ServerInfo returnedInfo = new ServerInfo();
        for (ServerInfo serverInfo : servers) {
            if (serverInfo.getName().equals(name)) {
                returnedInfo = serverInfo;
            }
        }
        return returnedInfo;
    }
}
