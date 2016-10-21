package org.fao.gift.utils;

import com.jcraft.jsch.*;
import org.fao.gift.dto.HostProperties;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

public class SFTP {
    private HostProperties properties;


    public SFTP() { }
    public SFTP(HostProperties properties) throws Exception {
        init(properties);
    }

    public void init(HostProperties properties) throws Exception {
        this.properties = properties;
    }

    public ChannelSftp getConnection() throws Exception {
        Session session = new JSch().getSession(properties.getUser(), properties.getHost(), properties.getPort());
        session.setPassword(properties.getPassword());
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();

        ChannelSftp channel = (ChannelSftp)session.openChannel("org/fao/oecd/policy/upload/attachments");
        channel.connect();
        return channel;
    }
    public void close(ChannelSftp channel) {
        try { channel.disconnect(); } catch (Exception ex) {}
        try { channel.getSession().disconnect(); } catch (Exception ex) {}
    }

    public void mkDir(String path, ChannelSftp channel) throws SftpException {
        SftpATTRS attributes=null;
        try { attributes = channel.stat(path); } catch (Exception e) { }
        if (attributes == null)
            channel.mkdir(path);
    }
    public void rmDir(String path, ChannelSftp channel) throws SftpException {
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
    public void rmFile(String path, ChannelSftp channel) throws SftpException {
        SftpATTRS attributes=null;
        try { attributes = channel.stat(path); } catch (Exception e) { }
        if (attributes != null)
            channel.rm(path);
    }

    SimpleDateFormat timeSuffixFormat = new SimpleDateFormat("yyyyMMddhhmmss");
    public String getTimeSuffix(Date... date) {
        return timeSuffixFormat.format(date!=null && date.length>0 ? date[0] : new Date());
    }

    public String getLastBackupPath(String path, ChannelSftp channel) throws SftpException {
        Vector<ChannelSftp.LsEntry> content = channel.ls(path);
        Collections.sort(content);
        return content.size()>2 ? content.lastElement().getFilename() : null;
    }

    public HostProperties getProperties() {
        return properties;
    }
}
