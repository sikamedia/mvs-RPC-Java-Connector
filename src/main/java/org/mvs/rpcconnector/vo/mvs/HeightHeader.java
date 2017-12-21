package com.viewfin.metaverse.rpcconnector.vo.mvs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class HeightHeader {
    private String bits;
    private String hash;
    private String merkleTreeHash;
    private String nonce;
    private String previousBlockHash;
    private Timestamp timestamp;
    private String version;
    private String mixHash;
    private Integer number;
    private Integer transactionCount;

    public String getBits() {
        return bits;
    }

    public void setBits(String bits) {
        this.bits = bits;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @JsonProperty("merkle_tree_hash")
    public String getMerkleTreeHash() {
        return merkleTreeHash;
    }

    public void setMerkleTreeHash(String merkleTreeHash) {
        this.merkleTreeHash = merkleTreeHash;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    @JsonProperty("previous_block_hash")
    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    @JsonProperty("time_stamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("mixhash")
    public String getMixHash() {
        return mixHash;
    }

    public void setMixHash(String mixHash) {
        this.mixHash = mixHash;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @JsonProperty("transaction_count")
    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }
}
