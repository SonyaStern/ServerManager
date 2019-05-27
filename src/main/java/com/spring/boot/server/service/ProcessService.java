package com.spring.boot.server.service;

import com.spring.boot.server.model.ServerInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
@RequiredArgsConstructor
public class ProcessService {

    @Autowired
    private final ServerService serverService;

    public Process starter(ServerInfo serverInfo)
            throws IOException, ParserConfigurationException, SAXException {

        Process server = new ProcessBuilder(
                "java", "-cp", "\"",
                serverInfo.getLibDir(), ";", serverInfo.getRscDir(), "\"",
                "-jar",
                serverInfo.getJarDir()).start();

        ServerInfo newServerInfo = recordServerInfo(server, serverInfo);
        new Thread(() -> {
            try {
                InputStream in = server.getErrorStream();
                int ch;
                byte[] buffer = new byte[1024];
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
                        new File("logs/" + newServerInfo.getName() + "Error" + LocalDate
                                .now().toString() + ".txt")));
                while ((ch = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, ch);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                InputStream in = server.getInputStream();
                int ch;
                byte[] buffer = new byte[1024];
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
                        new File("logs/" + newServerInfo.getName() + LocalDate.now()
                                .toString() + ".txt")));
                while ((ch = in.read(buffer)) >= 0) {
                    out.write(buffer, 0, ch);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        return server;
    }

    public void kill(Long pid) {
        ConcurrentSkipListSet<ServerInfo> servers = serverService.getServers();
        for (ServerInfo serverInfo : servers) {
            if (serverInfo.getPid().equals(pid)) {
                serverInfo.getProcess().destroy();
                servers.remove(serverInfo);
            }
        }
        System.out.println("Program completed");
    }

    private ServerInfo recordServerInfo(Process process, ServerInfo serverInfo)
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
            serverInfo.setPid(process.pid());
            serverService.getServers().add(serverInfo);
        }
        return serverInfo;
    }

    public ServerInfo getInfo(Long pid) {
        ServerInfo returnedInfo = new ServerInfo();
        ConcurrentSkipListSet<ServerInfo> servers = serverService.getServers();
        for (ServerInfo serverInfo : servers) {
            if (serverInfo.getPid().equals(pid)) {
                returnedInfo = serverInfo;
            }
        }
        return returnedInfo;
    }

}
