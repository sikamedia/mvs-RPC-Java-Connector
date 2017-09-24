package com.viewfin.metaverse.rpcconnector.vo.mvs;

import java.math.BigInteger;

public class Balance {

    private String address;
    private BigInteger confirmed;
    private BigInteger received;
    private BigInteger unspent;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(BigInteger confirmed) {
        this.confirmed = confirmed;
    }

    public BigInteger getReceived() {
        return received;
    }

    public void setReceived(BigInteger received) {
        this.received = received;
    }

    public BigInteger getUnspent() {
        return unspent;
    }

    public void setUnspent(BigInteger unspent) {
        this.unspent = unspent;
    }
}
