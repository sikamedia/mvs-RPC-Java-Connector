package org.mvs.rpcconnector.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;


public final class JSONUtils {

    public static final Logger logger = Logger.getLogger(JSONUtils.class.getName());

    private static final Gson gson = new Gson();

    private JSONUtils(){}

    public static boolean isJSONValid(String jsonInString) {
        return new JsonParser().parse(jsonInString).isJsonPrimitive();
    }
}
