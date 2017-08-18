package com.viewfin.metaverse.rpcconnector;

import com.viewfin.metaverse.rpcconnector.exception.AuthenticationException;
import com.viewfin.metaverse.rpcconnector.utils.ConfigFileReader;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MetaverseCreateAccountTest {

    public static final Logger LOG = Logger.getLogger(MetaverseCreateAccountTest.class.getName());

    private MVSCryptoCurrencyRPC mvsCryptoCurrencyRPC;


    @Before
    public void init() throws AuthenticationException {
        LOG.info(System.getProperty("user.dir"));
        System.out.println(System.getProperty("user.dir"));
        String configFilePath = System.getProperty("user.dir") +
                "/src/test/java/com/viewfin/metaverse/rpcconnector/config.yaml";
        ConfigFileReader configFileReader = new ConfigFileReader(configFilePath);
        final String rpcUser = configFileReader.getUsername();
        final String rpcPassword = configFileReader.getPassword();
        final String rpcHost = configFileReader.getHost();
        final String rpcPort = configFileReader.getPort();
        final String httpProtocol = configFileReader.getProtocol();
        mvsCryptoCurrencyRPC = new MVSCryptoCurrencyRPC(httpProtocol, rpcUser, rpcPassword, rpcHost, rpcPort);
    }

    @Test
    public void testGetWalletInfo() {

        System.out.println(mvsCryptoCurrencyRPC.getPathToBaseUrl());
        double balance = mvsCryptoCurrencyRPC.getBalance().doubleValue();

        LOG.info(balance);
        assertEquals(1, 1);
    }

    @Test
    public void testGetAddressList() {
        List<String> addressList = mvsCryptoCurrencyRPC.getAddressList();
        addressList.forEach(address -> {
            LOG.info(address);
        });
    }
}