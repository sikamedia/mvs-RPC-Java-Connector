package org.mvs.rpcconnector;

import com.gargoylesoftware.htmlunit.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.HttpHeaders;
import org.mvs.rpcconnector.exception.AuthenticationException;
import org.mvs.rpcconnector.exception.CallApiCryptoCurrencyRpcException;
import org.mvs.rpcconnector.exception.CryptoCurrencyRpcException;
import org.mvs.rpcconnector.exception.CryptoCurrencyRpcExceptionHandler;
import org.mvs.rpcconnector.pojo.Transaction;
import org.mvs.rpcconnector.utils.JSONUtils;
import org.apache.log4j.Logger;

import javax.json.Json;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

// import org.asynchttpclient.*;

public class CryptoCurrencyRPC {

    public static final Logger LOG = Logger.getLogger(CryptoCurrencyRPC.class.getName());

    private WebClient client;
    private String baseUrl;
    private CryptoCurrencyRpcExceptionHandler cryptoCurrencyRpcExceptionHandler = new CryptoCurrencyRpcExceptionHandler();
    private Gson gson = new Gson();
    private String rpcUser;
    private String httpProtocol;
    private String rpcHost;
    private String rpcPort;

    public CryptoCurrencyRPC(String rpcUser, String rpcPassword, String rpcHost, String rpcPort) throws AuthenticationException {
        client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setPrintContentOnFailingStatusCode(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        baseUrl = new String("http://" + rpcUser + ":" + rpcPassword + "@" + rpcHost + ":" + rpcPort + "/");

        try {
            if (client.getPage(baseUrl).getWebResponse().getStatusCode() == 401) {  //401 is Http Unauthorized
                throw new AuthenticationException();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Safely copies wallet.dat to destination, which can be a directory or a
     * path with filename.
     *
     * @param destination
     * @return
     * @throws Exception
     */
    public boolean backupWallet(String destination) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.BACKUP_WALLET, destination);
        if (jsonObj.get("error") == null) {
            return true;
        }
        return false;
    }

    /**
     * Produces a human-readable JSON object for a raw transaction.
     *
     * @param hex
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public JsonObject decodeRawTransaction(String hex) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.DECODE_RAW_TRANSACTION, hex);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsJsonObject();
    }

    /**
     * Reveals the private key corresponding to <address>
     *
     * @param address
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public String dumpPrivateKey(String address) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.DUMP_PRIVATE_KEY, address);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns raw transaction representation for given transaction id.
     *
     * @param txid
     * @return returns the hex string for the given transaction id
     * @throws CryptoCurrencyRpcException
     */
    public String getRawTransaction(String txid) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_RAW_TRANSACTION, txid);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns the account associated with the given address.
     *
     * @param address
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public String getAccount(String address) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_ACCOUNT, address);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns the current Litecoin address for receiving payments to this
     * account.
     *
     * @param account
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public String getAccountAddress(String account) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_ACCOUNT_ADDRESS, account);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns the list of addresses for the given account.
     *
     * @param account
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public JsonArray getAddressesByAccount(String account) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_ADDRESSES_BY_ACCOUNT, account);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsJsonArray();
    }

    /**
     * Returns the balance in the account.
     *
     * @param account
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public BigDecimal getBalance(String account) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_BALANCE, account);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsBigDecimal();
    }

    /**
     * Returns the wallet's total available balance.
     *
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public BigDecimal getBalance() throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_BALANCE);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsBigDecimal();
    }

    /**
     * Returns the total amount received by addresses with [account] in
     * transactions
     *
     * @param account
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public BigDecimal getReceivedByAccount(String account) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_RECEIVED_BY_ACCOUNT, account);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsBigDecimal();
    }

    /**
     * Returns a new address for receiving payments.
     *
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public String getNewAddress() throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_NEW_ADDRESS);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns a new address for receiving payments.
     *
     * @param account
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public String getNewAddress(String account) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_NEW_ADDRESS, account);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsString();
    }

    /**
     * Returns the total amount received by <address> in transactions
     *
     * @param address
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public BigDecimal getReceivedByAddress(String address) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_RECEIVED_BY_ADDRESS, address);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsBigDecimal();
    }

    /**
     * Returns an object about the given transaction containing: amount,
     * confirmations, txid, time[1], details (an array of objects containing:
     * account, address, category, amount, fee)
     *
     * @param txid
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public Transaction getTransaction(String txid) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.GET_TRANSACTION, txid);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return gson.fromJson(jsonObj.get("result").getAsJsonObject(), Transaction.class);
    }

    /**
     * Returns Object that has account names as keys, account balances as
     * values.
     *
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public JsonObject listAccounts() throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_ACCOUNTS);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsJsonObject();
    }

    /**
     * Returns an array of objects containing: account, amount, confirmations
     *
     * @return
     */
    public JsonArray listReceivedByAccount() throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_RECEIVED_BY_ACCOUNT);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsJsonArray();
    }

    /**
     * Returns an array of objects containing: address, account, amount,
     * confirmations
     *
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public JsonArray listReceivedByAddress() throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_RECEIVED_BY_ADDRESS);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsJsonArray();
    }

    /**
     * <amount> is a real and is rounded to 8 decimal places. Will send the
     * given amount to the given address, ensuring the account has a valid
     * balance using [minconf] confirmations. Returns the transaction ID if
     * successful
     *
     * @param fromAccount
     * @param toAddress
     * @param amount
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public String sendFrom(String fromAccount, String toAddress, BigDecimal amount) throws CryptoCurrencyRpcException {
        JsonObject response = callAPIMethod(APICalls.SEND_FROM, fromAccount, toAddress, amount);
        cryptoCurrencyRpcExceptionHandler.checkException(response);
        return response.get("result").getAsString();
    }

    /**
     * < amount > is a real and is rounded to the nearest 0.00000001
     *
     * @param toAddress
     * @param amount
     * @return TransactionID
     * @throws CryptoCurrencyRpcException
     */
    public String sendToAddress(String toAddress, BigDecimal amount) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.SEND_TO_ADDRESS, toAddress, amount);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsString();
    }

    public boolean validateAddress(String address) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.VALIDATE_ADDRESS, address);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsJsonObject().get("isvalid").getAsBoolean();
    }

    /**
     * Sets the account associated with the given address. Assigning address
     * that is already assigned to the same account will create a new address
     * associated with that account.
     *
     * @param address
     * @param account
     * @throws CryptoCurrencyRpcException
     */
    public void setAccount(String address, String account) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.SET_ACCOUNT, address, account);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
    }

    /**
     * Returns up to [count] most recent transactions skipping the first [from]
     * transactions for account [account].
     *
     * @param account
     * @param count
     * @param from
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public List<Transaction> listTransactions(String account, int count, int from) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_TRANSACTIONS, account, count, from);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);

        return Arrays.asList(gson.fromJson(jsonObj.get("result").getAsJsonArray(), Transaction[].class));
    }

    /**
     * Returns all unspent outputs with at least [minconf] and at most [maxconf]
     * confirmations.
     *
     * @param minconf
     * @param maxconf
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public JsonArray listUnspent(int minconf, int maxconf) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_UNSPENT, minconf, maxconf);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);

        return jsonObj.get("result").getAsJsonArray();
    }

    /**
     * Returns all unspent outputs with at least [minconf] and at most 9999999
     * confirmations; Further limited to outputs that pay at least one of the
     * given addresses in the [address] array.
     *
     * @param minconf
     * @param address
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public JsonArray listUnspent(int minconf, String[] address) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_UNSPENT, minconf, address);

        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsJsonArray();
    }


    /**
     * Returns all unspent outputs with at least [minconf] and at most 9999999
     * confirmations.
     *
     * @param minconf
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public JsonArray listUnspent(int minconf) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_UNSPENT, minconf);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);
        return jsonObj.get("result").getAsJsonArray();
    }

    /**
     * Returns all unspent outputs with at least [minconf] and at most [maxconf]
     * confirmations; Further limited to outputs that pay at least one of the
     * given addresses in the [address] array.
     *
     * @param minconf
     * @param maxconf
     * @param address
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public JsonArray listUnspent(int minconf, int maxconf, String[] address) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.LIST_UNSPENT, minconf, maxconf, address);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);

        return jsonObj.get("result").getAsJsonArray();
    }

    /**
     * Returns an unsigned transaction that spends the outputs [prevOut] to new
     * outputs [Out] and encodes it as hex format.
     *
     * @param prevOut is an array of JsonObjects, each with the properties
     *                "txid" and "vout".
     * @param out     is an JsonObject with the receiving addresses as properties
     *                and the receiving amount as value of each property(=address)
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public Transaction createRawTransaction(JsonObject[] prevOut, JsonObject out) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.CREATE_RAW_TRANSACTION, prevOut, out);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);

        return gson.fromJson(jsonObj.get("result").getAsJsonObject(), Transaction.class);
    }

    /**
     * Returns a signed transaction in hex format using private keys stored in
     * the wallet and the output from createRawTransaction()
     *
     * @param hexString
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public Transaction signRawTransaction(String hexString) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.SIGN_RAW_TRANSACTION, hexString);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);

        return gson.fromJson(jsonObj.get("result").getAsJsonObject(), Transaction.class);
    }

    /**
     * Validates a signed transaction in hex format and broadcasts it to the
     * network.
     *
     * @param hexString
     * @return
     * @throws CryptoCurrencyRpcException
     */
    public String sendRawTransaction(String hexString) throws CryptoCurrencyRpcException {
        JsonObject jsonObj = callAPIMethod(APICalls.SEND_RAW_TRANSACTION, hexString);
        cryptoCurrencyRpcExceptionHandler.checkException(jsonObj);

        return jsonObj.get("result").getAsString();
    }

    public JsonObject callAPIMethod(APICalls callMethod, Object... params) throws CallApiCryptoCurrencyRpcException {
        try {
            JsonObject jsonObj = null;
            WebRequest req = new WebRequest(new URL(baseUrl));
            req.setAdditionalHeader("Content-type", "application/json");
            req.setHttpMethod(HttpMethod.POST);
            JSONRequestBody body = new JSONRequestBody();
            body.setMethod(callMethod.toString());
            if (params != null && params.length > 0) {
                body.setParams(params);
            }
            req.setRequestBody(new Gson().toJson(body, JSONRequestBody.class));
            WebResponse resp = client.getPage(req).getWebResponse();
            jsonObj = new JsonParser().parse(resp.getContentAsString()).getAsJsonObject();

            StringBuffer buffer = new StringBuffer("");
            for (Object item : params) {
                buffer.append(item.toString() + " | ");
            }
            LOG.info("Bitcoin RPC Request: Method: " + callMethod + " Params: " + buffer.toString() +
                    "\nBitcoin RPC Response : " + jsonObj);

            return jsonObj;
        } catch (Exception e) {
            throw new CallApiCryptoCurrencyRpcException(e.getMessage());
        }
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
                    HttpHeaders.of(com.linecorp.armeria.common.HttpMethod.POST, "").set(HttpHeaderNames.ACCEPT, "application/json"),
                    HttpData.of(new Gson().toJson(body, JSONRequestBody.class).getBytes())
            );

            AggregatedHttpMessage jsonResponse = httpClient.execute(getJson).aggregate().join();

            //jsonObj = new JsonParser().parse(jsonResponse.content().toStringUtf8()).getAsJsonObject();
            LOG.info(jsonResponse.content().toStringUtf8());
            if (!JSONUtils.isJSONValid(jsonResponse.content().toStringUtf8())) {
                jsonObj = gson.fromJson(jsonResponse.content().toStringUtf8(), JsonObject.class);
            } else {
                String jsonString = Json.createObjectBuilder()
                        .add("result", jsonResponse.content().toStringUtf8())
                        .build()
                        .toString();
                jsonObj = gson.fromJson(jsonString, JsonObject.class);

            }
            return jsonObj;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new CallApiCryptoCurrencyRpcException(e.getMessage());
        }
    }

    public void setMVSRPCUrl(String http, String rpcHost, String rpcPort) {
        this.baseUrl = (new StringBuilder()).append(http).append("://").append(rpcHost).append(':').append(rpcPort).append("/rpc").toString();
    }

    public String getPathToBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }

    public String getRpcUser() {
        return rpcUser;
    }

    public void setRpcUser(String rpcUser) {
        this.rpcUser = rpcUser;
    }

    public String getHttpProtocol() {
        return httpProtocol;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

    public String getRpcHost() {
        return rpcHost;
    }

    public void setRpcHost(String rpcHost) {
        this.rpcHost = rpcHost;
    }

    public String getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(String rpcPort) {
        this.rpcPort = rpcPort;
    }
}
