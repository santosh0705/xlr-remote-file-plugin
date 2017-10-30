package com.xebialabs.xlrelease.plugin.remotefile;

import com.xebialabs.deployit.plugin.api.reflect.PropertyDescriptor;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.overthere.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xebialabs.overthere.ConnectionOptions.*;
import static com.xebialabs.overthere.cifs.CifsConnectionBuilder.WINRM_TIMEMOUT;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.CONNECTION_TYPE;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.SUDO_USERNAME;
import static com.xebialabs.overthere.ssh.SshConnectionType.SUDO;

import org.yaml.snakeyaml.Yaml;
import com.google.gson.Gson;
import java.util.Map;

public class RemoteFile {
    private final ConnectionOptions options = new ConnectionOptions();
    private final String protocol;

    public RemoteFile(ConfigurationItem remoteFile) {
        this.protocol = remoteFile.getProperty("protocol");
        copyPropertiesToConnectionOptions(options, remoteFile);
    }

    private void copyPropertiesToConnectionOptions(ConnectionOptions options, ConfigurationItem ci) {
        // support legacy properties
        if(ci.hasProperty("sudo") && (Boolean) (ci.getProperty("sudo"))) {
            ci.setProperty(CONNECTION_TYPE, SUDO);
            ci.setProperty(SUDO_USERNAME, "root");
        }

        // copy all CI properties to connection properties
        for (PropertyDescriptor pd : ci.getType().getDescriptor().getPropertyDescriptors()) {
            if (!pd.getCategory().equals("output")) {
                Object value = pd.get(ci);
                setConnectionOption(options, pd.getName(), value);
            }
        }
    }

    private void setConnectionOption(ConnectionOptions options, String key, Object value) {
        if (key.equals("script") || key.equals("remotePath") || key.equals("scriptLocation")) {
            return;
        }

        if (value == null || value.toString().isEmpty()) {
            return;
        }

        // support legacy properties
        if(key.equals("temporaryDirectoryPath")) {
            key = TEMPORARY_DIRECTORY_PATH;
        } else if(key.equals("timeout")) {
            key = WINRM_TIMEMOUT;
        }

        if (value instanceof Integer && ((Integer) value).intValue() == 0) {
            logger.debug("Activating workaround for DEPLOYITPB-4775: Integer with value of 0 not passed to Overthere.");
            return;
        }

        if (key.equals(JUMPSTATION)) {
            ConfigurationItem item = (ConfigurationItem) value;

            ConnectionOptions jumpstationOptions = new ConnectionOptions();
            copyPropertiesToConnectionOptions(jumpstationOptions, item);
            options.set(key, jumpstationOptions);
        } else {
            options.set(key, value);
        }
    }

    public String readFile(String filePath) {
        OverthereConnection connection = Overthere.getConnection(protocol, options);
        OverthereFile fileToRead = connection.getFile(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(fileToRead.getInputStream()));
        String contents = new Scanner(br).useDelimiter("\\Z").next();
        connection.close();
        return contents;
    }

    public void writeFile(String filePath, String contents) {
        System.out.println(contents);
        OverthereConnection connection = Overthere.getConnection(protocol, options);
        OverthereFile fileToWrite = connection.getFile(filePath);
        PrintWriter pw = new PrintWriter(fileToWrite.getOutputStream());
        pw.write(contents);
        pw.flush();
        pw.close();
        connection.close();
    }

    public String yamlToJson(String contents) {
        Yaml yaml = new Yaml();
        Gson gson = new Gson();
        Object yamlObj = yaml.load(contents);
        String jsonInString = gson.toJson(yamlObj);
        return jsonInString;
    }

    private static Logger logger = LoggerFactory.getLogger(RemoteFile.class);

}
