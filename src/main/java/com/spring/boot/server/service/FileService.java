package com.spring.boot.server.service;

import com.spring.boot.server.model.ServerInfo;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Service
@RequiredArgsConstructor
public class FileService {


    @Autowired
    private final ServerService serverService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private Environment env;

    private static void checkDir(ZipEntry entry, File destDir, ServerInfo serverInfo) {
        if (entry.getName().equals("server/lib/")) {
            serverInfo.setLibDir(destDir + File.separator + entry.getName() + "*");
        }

        if (entry.getName().equals("server/rsc/")) {
            serverInfo.setRscDir(destDir + File.separator + entry.getName() + "*");
        }
    }

    private static void write(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
    }

    @PostConstruct
    public void init() throws IOException, ParserConfigurationException, SAXException {
        File folder = new File(System.getProperty("user.dir") + File.separator + env
                .getProperty("paths.uploadedFiles"));
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                ServerInfo serverInfo = new ServerInfo();
                for (File serverFiles : file.listFiles()) {
                    if (serverFiles.getName().equals("lib")) {
                        serverInfo.setLibDir(serverFiles.getCanonicalPath());
                    }
                    if (serverFiles.getName().equals("rsc")) {
                        serverInfo.setRscDir(serverFiles.getCanonicalPath());
                    }
                    if (serverFiles.getName().contains(".jar")) {
                        serverInfo.setJarDir(serverFiles.getCanonicalPath());
                    }
                }
                serverInfo = serverService.recordServerInfo(serverInfo);
                serverService.getServers().add(serverInfo);
            }
        }
        File mainLogFolder = new File(System.getProperty("user.dir") + File.separator + env
                .getProperty("paths.logs"));
        for (File logFolder : mainLogFolder.listFiles()) {
            if (logFolder.isDirectory()) {
                for (ServerInfo server : serverService.getServers()) {
                    if (server.getName().equals(logFolder.getName())) {
                        for (File logFile : logFolder.listFiles()) {
                            if (!logFile.isDirectory()) {
                                String fileName = logFile.getName();
                                String date = LocalDateTime
                                        .ofInstant(Instant.ofEpochSecond(Long.decode(
                                                fileName.substring(fileName.lastIndexOf("_") + 1,
                                                        fileName.lastIndexOf(".")))),
                                                ZoneId.systemDefault())
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                server.getLogFiles().put(logFile, date);
                            }
                        }
                    }
                }
            }
        }
    }

    private ServerInfo unZipFile(File zipFile) throws ParserConfigurationException, SAXException {
        ServerInfo serverInfo = new ServerInfo();

        try {
            String property = "lab.desc";
            ZipFile zip = new ZipFile(zipFile);
            Enumeration entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                if (entry.isDirectory()) {
                    checkDir(entry, zipFile.getParentFile().getCanonicalFile(), serverInfo);
                    new File(zipFile.getParent(), entry.getName()).mkdirs();
                } else {
                    if (!entry.getName().equals(property)) {
                        write(zip.getInputStream(entry),
                                new BufferedOutputStream(new FileOutputStream(
                                        new File(zipFile.getParent(), entry.getName()))));
                    }
                    if (entry.getName().contains("server.jar")) {
                        serverInfo.setJarDir(
                                zipFile.getParentFile().getCanonicalFile() + File.separator + entry
                                        .getName());
                    }
                }
                logger.info(entry.getName());
            }
            logger.info(zipFile.getName() + " unzip");

            zip.close();
            if (!serverService.contains(serverInfo)) {
                serverService.recordServerInfo(serverInfo);
                serverService.getServers().add(serverInfo);
                new File("logs/" + serverInfo.getName()).mkdirs();
            }
            deleteFile(zipFile);
        } catch (IOException e) {
            logger.error("Error unzip file " + zipFile.getName(), e.fillInStackTrace());
        }
        return serverInfo;
    }

    private void deleteFile(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                File f = new File(dir, child);
                deleteFile(f);
            }

            if (dir.delete()) {
                logger.info("Delete dir " + dir.getAbsolutePath());
            } else {
                logger.warn("Dir wasn't deleted");
            }
        } else {
            if (dir.delete()) {
                logger.info("Delete file " + dir.getAbsolutePath());
            } else {
                logger.warn("File wasn't deleted");
            }
        }
    }

    public ServerInfo upload(MultipartFile uploadFile)
            throws ParserConfigurationException, SAXException {
        String name = uploadFile
                .getOriginalFilename();

        ServerInfo serverInfo = null;
        if (name.substring(name.lastIndexOf('.') + 1).equalsIgnoreCase("zip")) {
            if (!uploadFile.isEmpty()) {
                try {
                    byte[] bytes = uploadFile.getBytes();
                    Path path = Paths.get(System.getProperty("user.dir") + File.separator + env
                            .getProperty("paths.uploadedFiles") + File.separator + name);
                    Files.write(path, bytes);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                File unzipFile = new File(System.getProperty("user.dir") + File.separator + env
                        .getProperty("paths.uploadedFiles"), name);
                serverInfo = unZipFile(unzipFile);

            } else {
                logger.error("Empty zip file " + uploadFile.getName());
            }
        } else {
            logger.error("Invalid file extension expected 'zip', actually '" + name
                    .substring(name.lastIndexOf('.') + 1) + "'");
        }
        return serverInfo;
    }

    public Resource download(ServerInfo serverInfo, String fileName) throws MalformedURLException {

        Resource resource = null;
        for (File file : serverInfo.getLogFiles().keySet()) {
            if (file.getName().equals(fileName)) {
                resource = new UrlResource(file.toURI());
            }
        }
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("File cannot be downloaded!");
        }
    }

}
