package com.viewfin.metaverse.rpcconnector;

import com.viewfin.metaverse.rpcconnector.exception.AuthenticationException;
import com.viewfin.metaverse.rpcconnector.utils.ConfigFileReader;
import com.viewfin.metaverse.rpcconnector.vo.mvs.Balance;
import com.viewfin.metaverse.rpcconnector.vo.mvs.HeightHeader;
import com.viewfin.metaverse.rpcconnector.vo.mvs.Utxo;
import com.viewfin.metaverse.rpcconnector.vo.mvs.assets.Asset;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
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

        LOG.info(mvsCryptoCurrencyRPC.getPathToBaseUrl());
        double balance = mvsCryptoCurrencyRPC.getBalance().doubleValue();

        LOG.info(String.format("%.8f", balance / 100000000));
        assertEquals(1, 1);
    }

    @Test
    public void testGetAddressList() {
        List<String> addressList = mvsCryptoCurrencyRPC.getAddressList();
        addressList.forEach(address -> {
            LOG.info(address);
        });
    }

    @Test
    public void testGetHeightHeader() {
        Integer height = 253243;
        mvsCryptoCurrencyRPC.fetchHeight();
        HeightHeader heightHeader = mvsCryptoCurrencyRPC.getHeightHeader(height);
        assertEquals(height, heightHeader.getNumber());
    }

    @Test
    public void testListAssets() {
        List<Asset> assets =  mvsCryptoCurrencyRPC.listAssets();
        LOG.info(assets.size());
        assets.forEach(asset -> {
            LOG.info(asset.getSymbol());
            LOG.info(asset.getMaximumSupply());
            LOG.info(asset.getDescription());
        });

    }

    @Test
    public void testFetchUTXO() {
        List<String> addressList = mvsCryptoCurrencyRPC.getAddressList();
        addressList.forEach(address -> {
            Utxo utxo = mvsCryptoCurrencyRPC.fetchUTXO(1000, address);
            if (utxo != null) {
                Balance balance = mvsCryptoCurrencyRPC.getBalanceFor(address);
                LOG.info(balance.getUnspent());
                LOG.info(utxo.getChange());
            }
        });
    }
}