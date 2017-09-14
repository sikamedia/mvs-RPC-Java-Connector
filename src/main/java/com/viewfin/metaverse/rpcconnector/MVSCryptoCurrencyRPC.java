package com.viewfin.metaverse.rpcconnector;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.*;
import com.viewfin.metaverse.rpcconnector.exception.AuthenticationException;
import com.viewfin.metaverse.rpcconnector.exception.CallApiCryptoCurrencyRpcException;
import com.viewfin.metaverse.rpcconnector.exception.CryptoCurrencyRpcException;
import com.viewfin.metaverse.rpcconnector.exception.CryptoCurrencyRpcExceptionHandler;
import org.apache.log4j.Logger;

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


    public JsonObject callAPIMethodAsynchronous(APICalls callMethod, Object... params) throws CallApiCryptoCurrencyRpcException {
        try {
            JsonObject jsonObj;
            HttpClient httpClient = HttpClient.of(this.getPathToBaseUrl());

            JSONRequestBody body = new JSONRequestBody();
            body.setMethod(callMethod.toString());
            if (params != null && params.length > 0) {
                body.setParams(params);
            }

            AggregatedHttpMessage getJson = AggregatedHttpMessage.of(
                    HttpHeaders.of(HttpMethod.POST, "").set(HttpHeaderNames.ACCEPT, "application/json"),
                    HttpData.of(new Gson().toJson(body, JSONRequestBody.class).getBytes())
            );

            AggregatedHttpMessage jsonResponse = httpClient.execute(getJson).aggregate().join();

            jsonObj = new JsonParser().parse(jsonResponse.content().toStringUtf8()).getAsJsonObject();

            return jsonObj;
        } catch (Exception e) {
            throw new CallApiCryptoCurrencyRpcException(e.getMessage());
        }
    }

    public BigDecimal getBalance() throws CryptoCurrencyRpcException {
        // JsonObject jsonObj = this.callAPIMethod(APICalls.GET_BALANCE, rpcUser, rpcPassword);
        JsonObject jsonObj = this.callAPIMethodAsynchronous(APICalls.GET_BALANCE, rpcUser, rpcPassword);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("total-unspent").getAsBigDecimal();
    }

    public List<String> getAddressList() throws CryptoCurrencyRpcException {
        JsonObject jsonObject = this.callAPIMethod(APICalls.LIST_ADDRESSES, rpcUser, rpcPassword);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObject);
        jsonObject.get("addresses").getAsJsonArray().forEach(address -> {
            addressList.add(address.getAsString());
        });
        return addressList;
    }

}
