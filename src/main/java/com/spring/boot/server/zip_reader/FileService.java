package com.spring.boot.server.zip_reader;

import com.spring.boot.server.model.ServerInfo;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Getter
    List<ServerInfo> uploadedServers = new ArrayList<>();
    @Autowired
    private Environment env;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void checkDir(ZipEntry entry, File destDir, ServerInfo serverInfo) {
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

    private ServerInfo unZipFile(File zipFile) {
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
            uploadedServers.add(serverInfo);
            deleteFile(zipFile);
        } catch (IOException e) {
            logger.error("Error unzip file " + zipFile.getName(), e.fillInStackTrace());
        }
        return serverInfo;
    }

    private void deleteFile(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File f = new File(dir, children[i]);
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

    public ServerInfo upload(MultipartFile uploadFile) {
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
}
