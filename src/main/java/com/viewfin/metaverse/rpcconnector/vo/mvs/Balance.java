package com.viewfin.metaverse.rpcconnector.vo.mvs;

public class Balance {

    private String address;
    private Integer confirmed;
    private Integer received;
    private Integer unspent;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }

    public Integer getReceived() {
        return received;
    }

    public void setReceived(Integer received) {
        this.received = received;
    }

    public Integer getUnspent() {
        return unspent;
    }

    public void setUnspent(Integer unspent) {
        this.unspent = unspent;
    }
}
