package org.fao.gift.upload.impl;

import com.jcraft.jsch.*;
import org.fao.ess.uploader.core.init.UploaderConfig;
import org.fao.gift.upload.dto.Files;
import org.fao.gift.upload.dto.HostProperties;
import org.fao.fenix.commons.utils.FileUtils;
import org.fao.fenix.commons.utils.UIDUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;


@ApplicationScoped
public class FileManager {
    @Inject private UploaderConfig config;
    @Inject private FileUtils fileUtils;
    @Inject private UIDUtils uidUtils;
    private File tmpFolder;
    private HostProperties bulkRemoteProperties;
    private HostProperties attachmentRemoteProperties;
    private HostProperties sharedRemoteProperties;


    //Temporary folder management
    public File createTmpFolder() throws IOException {
        //Create empty tmp folder
        if (tmpFolder==null) {
            String tmpFilePath = config.get("gift.local.folder.tmp");
            tmpFolder = new File(tmpFilePath != null ? tmpFilePath : "/tmp");
        }
        File destinationFolder = new File(tmpFolder, uidUtils.newId());
        destinationFolder.mkdirs();
        //return folder
        return destinationFolder;
    }

    public Map<Files, File> unzip(File folder, InputStream zipPackageStream) throws Exception {
        Map<Files, File> recognizedFilesMap = new HashMap<>();
        fileUtils.unzip(zipPackageStream, folder, true);
        for (File file : folder.listFiles()) {
            Files recognizedFile = file.isFile() ? Files.get(file.getName()) : null;
            if (recognizedFile!=null)
                recognizedFilesMap.put(recognizedFile, file);
        }
        return recognizedFilesMap;
    }

    public void removeTmpFolder(File folder) throws Exception {
        fileUtils.delete(folder);
    }

    public File saveFile(File tmpFolder, String fileName, InputStream fileInput) throws IOException {
        File destinationFile = new File(tmpFolder, fileName);
        OutputStream out = new FileOutputStream(destinationFile);

        byte[] buffer = new byte[1024];
        try {
            for (int c = fileInput.read(buffer); c > 0; c = fileInput.read(buffer))
                out.write(buffer, 0, c);
        } finally {
            out.close();
        }
        return destinationFile;
    }

    //Remote survey folder management
    public void publishSurveyFile(File zipFile, String surveyCode) throws Exception {
        ChannelSftp channel = getConnection();
        try {
            channel.cd(bulkRemoteProperties.getPath());
            uploadFile(channel,zipFile,"GIFT_Survey_"+surveyCode+".zip");
        } finally {
            close(channel);
        }
    }

    public void publishMetadataAttachmentFile(File file, String metadataId) throws Exception {
        ChannelSftp channel = getConnection();
        try {
            channel.cd(attachmentRemoteProperties.getPath());
            mkDir(metadataId, channel);
            channel.cd(metadataId);
            uploadFile(channel,file,file.getName());
        } finally {
            close(channel);
        }
    }




    //SFTP utils

    private void initSFTP() throws Exception {
        if (bulkRemoteProperties ==null)
            bulkRemoteProperties = new HostProperties(
                    null,
                    config.get("gift.remote.host"),
                    new Integer(config.get("gift.remote.port")),
                    config.get("gift.remote.usr"),
                    config.get("gift.remote.psw"),
                    config.get("gift.remote.path.survey")
            );
        if (attachmentRemoteProperties ==null)
            attachmentRemoteProperties = new HostProperties(
                    null,
                    config.get("gift.remote.host"),
                    new Integer(config.get("gift.remote.port")),
                    config.get("gift.remote.usr"),
                    config.get("gift.remote.psw"),
                    config.get("gift.remote.path.metadata.attachment")
            );
        if (sharedRemoteProperties==null)
            sharedRemoteProperties = new HostProperties(
                    null,
                    config.get("gift.remote.host"),
                    new Integer(config.get("gift.remote.port")),
                    config.get("gift.remote.usr"),
                    config.get("gift.remote.psw"),
                    config.get("gift.remote.path.shared")
            );
    }

    private ChannelSftp getConnection() throws Exception {
        initSFTP();

        Session session = new JSch().getSession(bulkRemoteProperties.getUser(), bulkRemoteProperties.getHost(), bulkRemoteProperties.getPort());
        session.setPassword(bulkRemoteProperties.getPassword());
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();

        ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
        channel.connect();
        return channel;
    }
    private void close(ChannelSftp channel) {
        try { channel.disconnect(); } catch (Exception ex) {}
        try { channel.getSession().disconnect(); } catch (Exception ex) {}
    }

    private void mkDir(String path, ChannelSftp channel) throws SftpException {
        SftpATTRS attributes=null;
        try { attributes = channel.stat(path); } catch (Exception e) { }
        if (attributes == null)
            channel.mkdir(path);
    }
    public static void rmDir(String path, ChannelSftp channel) throws SftpException {
        if (!".".equals(path) && !"..".equals(path))
            if (channel.stat(path).isDir()) {
                channel.cd(path);
                Vector<ChannelSftp.LsEntry> entries = channel.ls(".");
                for (ChannelSftp.LsEntry entry: entries)
                    rmDir(entry.getFilename(), channel);
                channel.cd("..");
                channel.rmdir(path);
            } else {
                channel.rm(path);
            }
    }
    private void rmFile(String path, ChannelSftp channel) throws SftpException {
        SftpATTRS attributes=null;
        try { attributes = channel.stat(path); } catch (Exception e) { }
        if (attributes != null)
            channel.rm(path);
    }
    private String uploadFile(ChannelSftp channel, File sourceFile, String fileName) throws Exception {
        DigestInputStream input = new DigestInputStream(new FileInputStream(sourceFile), MessageDigest.getInstance("MD5"));
        channel.put(input, fileName==null ? sourceFile.getName() : fileName, null, ChannelSftp.OVERWRITE);
        return new BigInteger(1, input.getMessageDigest().digest()).toString(16);
    }


    //Other utils
    private String textToFileName(String text) {
        return text!=null ? text.replaceAll("[^\\w\\d_\\-.àèéìòù]"," ").replaceAll(" +"," ").trim().replace(' ','_') : null;
    }

}
