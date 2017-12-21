package org.mvs.rpcconnector.vo.mvs.assets;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;

public class Asset {
    private String symbol;
    private BigInteger maximumSupply;
    private int decimalNumber;
    private String address;
    private String description;
    private String status;
    private String issuer;

    public Asset() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("maximum_supply")
    public BigInteger getMaximumSupply() {
        return maximumSupply;
    }

    public void setMaximumSupply(BigInteger maximumSupply) {
        this.maximumSupply = maximumSupply;
    }

    @JsonProperty("decimal_number")
    public int getDecimalNumber() {
        return decimalNumber;
    }

    public void setDecimalNumber(int decimalNumber) {
        this.decimalNumber = decimalNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
