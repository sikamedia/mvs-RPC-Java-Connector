package org.mvs.rpcconnector.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.mvs.rpcconnector.vo.ServerConfig;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;

import java.io.File;

public class ConfigFileReader {

    public static final Logger LOG = Logger.getLogger("rpcLogger");

    private ServerConfig serverConfig;

    public ConfigFileReader(String fileName) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            serverConfig = mapper.readValue(new File(fileName), ServerConfig.class);
            LOG.info(ReflectionToStringBuilder.toString(serverConfig, ToStringStyle.MULTI_LINE_STYLE));
            LOG.info(serverConfig.getMetaverse().get("host"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getHost() {
        return serverConfig.getMetaverse().get("host");
    }

    public String getUsername() {
        return serverConfig.getMetaverse().get("username");
    }

    public String getPassword() {
        return serverConfig.getMetaverse().get("password");
    }

    public String getPort() {
        return serverConfig.getMetaverse().get("port");
    }

    public String getProtocol() {
        return serverConfig.getMetaverse().get("protocol");
    }
}
