package org.mvs.rpcconnector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.mvs.rpcconnector.exception.AuthenticationException;
import org.mvs.rpcconnector.exception.CryptoCurrencyRpcException;
import org.mvs.rpcconnector.exception.CryptoCurrencyRpcExceptionHandler;
import org.mvs.rpcconnector.vo.mvs.Balance;
import org.mvs.rpcconnector.vo.mvs.HeightHeader;
import org.mvs.rpcconnector.vo.mvs.Utxo;
import org.mvs.rpcconnector.vo.mvs.assets.Asset;
import org.apache.log4j.Logger;
import org.mvs.rpcconnector.vo.mvs.blocks.Height;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
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
        JsonObject jsonObject = this.callAPIMethodAsynchronous(APICalls.FETCH_HEADER_MVS, "-t", height.toString());
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

    public Utxo fetchUTXO(Integer toBeSpent, String paymentAddress) {
        Utxo utxo = null;
        JsonObject jsonObject = this.callAPIMethodAsynchronous(APICalls.FETCH_UTXO_MVS, toBeSpent, paymentAddress);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObject);
        ObjectMapper mapper = new ObjectMapper();

        if (jsonObject.has("error")) {
            LOG.error(jsonObject.get("error").getAsString());
            return null;
        }

        if (jsonObject.get("points").isJsonArray()) {

            try {
                utxo = mapper.readValue(jsonObject.toString(), Utxo.class);
                return utxo;
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }

        if (jsonObject.get("points").getAsString().isEmpty()) {
            utxo = new Utxo();
            utxo.setPoints(new ArrayList<>());
            utxo.setChange(0L);
            return utxo;
        }

        return utxo;
    }

    public List<Asset> listAssets() {
        LinkedList<Asset> assetList = new LinkedList();
        Asset assetTemp;
        JsonObject jsonObject = this.callAPIMethodAsynchronous(APICalls.LIST_ASSETS_MVS);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObject);
        ObjectMapper mapper = new ObjectMapper();
        for (JsonElement asset : jsonObject.get("assets").getAsJsonArray()) {
            try {
                assetTemp = mapper.readValue(asset.getAsJsonObject().toString(), Asset.class);
                assetList.add(assetTemp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return assetList;
    }

    public Height fetchHeight() {
        JsonObject jsonObject = this.callAPIMethodAsynchronous(APICalls.FETCH_HEIGHT_MVS);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObject);
        ObjectMapper mapper = new ObjectMapper();
        Height height = null;
        try {
            height = mapper.readValue(jsonObject.toString(), Height.class);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return height;
    }
}
