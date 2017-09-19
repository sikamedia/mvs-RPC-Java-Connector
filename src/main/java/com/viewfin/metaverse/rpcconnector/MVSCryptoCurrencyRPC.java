package com.viewfin.metaverse.rpcconnector;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.viewfin.metaverse.rpcconnector.exception.AuthenticationException;
import com.viewfin.metaverse.rpcconnector.exception.CryptoCurrencyRpcException;
import com.viewfin.metaverse.rpcconnector.exception.CryptoCurrencyRpcExceptionHandler;
import com.viewfin.metaverse.rpcconnector.vo.mvs.Balance;
import com.viewfin.metaverse.rpcconnector.vo.mvs.HeightHeader;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MVSCryptoCurrencyRPC extends CryptoCurrencyRPC {

    public static final Logger LOG = Logger.getLogger(MVSCryptoCurrencyRPC.class.getName());

    private CryptoCurrencyRpcExceptionHandler cryptoCurrencyRpcExceptionHandler = new CryptoCurrencyRpcExceptionHandler();
    private List<String> addressList = new ArrayList<>();
    private String rpcUser;
    private String rpcPassword;

    public MVSCryptoCurrencyRPC(String httpProtocol, String rpcUser, String rpcPassword, String rpcHost, String rpcPort) throws AuthenticationException {
        super(rpcUser, rpcPassword, rpcHost, rpcPort);
        this.setMVSRPCUrl(httpProtocol, rpcHost, rpcPort);
        this.rpcUser = rpcUser;
        this.rpcPassword = rpcPassword;
    }

    public BigDecimal getBalance() throws CryptoCurrencyRpcException {
        JsonObject jsonObj = this.callAPIMethodAsynchronous(APICalls.GET_BALANCE, rpcUser, rpcPassword);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("total-unspent").getAsBigDecimal();
    }

    public List<String> getAddressList() throws CryptoCurrencyRpcException {
        JsonObject jsonObject = this.callAPIMethodAsynchronous(APICalls.LIST_ADDRESSES, rpcUser, rpcPassword);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObject);
        jsonObject.get("addresses").getAsJsonArray().forEach(address -> {
            addressList.add(address.getAsString());
        });
        return addressList;
    }

    public HeightHeader getHeightHeader(Integer height) throws CryptoCurrencyRpcException {
        HeightHeader heightHeader = null;
        JsonObject jsonObject = this.callAPIMethodAsynchronous(APICalls.FETCH_HEADER_MVS, "-t", height);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObject);
        ObjectMapper mapper = new ObjectMapper();
        try {
            heightHeader = mapper.readValue(jsonObject.get("result").toString(), HeightHeader.class);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return heightHeader;
    }

    public Balance getBalanceFor(String address) throws CryptoCurrencyRpcException {
        Balance balance = null;
        JsonObject jsonObject = this.callAPIMethodAsynchronous(APICalls.GET_BALANCE_MVS, address);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObject);
        ObjectMapper mapper = new ObjectMapper();

        try {
            balance = mapper.readValue(jsonObject.get("balance").toString(), Balance.class);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return balance;
    }

    public void fetchUTXO(Integer toBeSpent, String paymentAddress) {
        JsonObject jsonObject = this.callAPIMethodAsynchronous(APICalls.FETCH_HEADER_MVS, toBeSpent, paymentAddress);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObject);
    }

}
