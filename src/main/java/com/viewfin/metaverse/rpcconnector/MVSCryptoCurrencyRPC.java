package com.viewfin.metaverse.rpcconnector;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.viewfin.metaverse.rpcconnector.exception.AuthenticationException;
import com.viewfin.metaverse.rpcconnector.exception.CallApiCryptoCurrencyRpcException;
import com.viewfin.metaverse.rpcconnector.exception.CryptoCurrencyRpcException;
import com.viewfin.metaverse.rpcconnector.exception.CryptoCurrencyRpcExceptionHandler;
import org.apache.log4j.Logger;
import org.asynchttpclient.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    /*
    public JsonObject callAPIMethodAsynchronous(APICalls callMethod, Object... params) throws CallApiCryptoCurrencyRpcException {
        try {
            JsonObject jsonObj = null;
            HttpClient httpClient = HttpClient.of(this.getBaseUrl());
            httpClient.post()

            AggregatedHttpMessage getJson = AggregatedHttpMessage.of(
                    HttpHeaders.of(HttpMethod.POST, "").set(HttpHeaderNames.ACCEPT, "application/json")
            );

            return jsonObj;
        } catch (Exception e) {
            throw new CallApiCryptoCurrencyRpcException(e.getMessage());
        }

    }*/

    public JsonObject callAPIMethodAsynchronous(APICalls callMethod, Object... params) throws CallApiCryptoCurrencyRpcException {

        try {

            AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
            JSONRequestBody body = new JSONRequestBody();
            body.setMethod(callMethod.toString());
            if (params != null && params.length > 0) {
                body.setParams(params);
            }


            LOG.info(this.getPathToBaseUrl());

            Request request = asyncHttpClient.preparePost(this.getPathToBaseUrl()).
                    setHeader("Content-Type","application/json").
                    setBody(body.toString()).build();




            ListenableFuture<JsonObject> f = asyncHttpClient.executeRequest(request,
                    new AsyncCompletionHandler<JsonObject>() {

                        @Override
                        public JsonObject onCompleted(Response response) throws Exception {
                            JsonObject jsonObj = new JsonParser().parse(response.getResponseBody()).getAsJsonObject();
                            return jsonObj;
                        }

                        @Override
                        public void onThrowable(Throwable t){
                            // Something wrong happened.
                        }
                    });

            JsonObject jsonObject = null;

            if (f != null) {
                try {
                    jsonObject = f.get();
                } catch (InterruptedException ex) {
                    throw new CallApiCryptoCurrencyRpcException(ex.getMessage());
                } catch (ExecutionException ex) {
                    throw new CallApiCryptoCurrencyRpcException(ex.getMessage());
                }
            }

            asyncHttpClient.close();
            return jsonObject;
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
