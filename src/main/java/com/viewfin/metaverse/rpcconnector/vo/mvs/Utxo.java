package com.viewfin.metaverse.rpcconnector.vo.mvs;


import java.util.List;

class point {
    private String hash;
    private Integer index;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}

public class Utxo {

    private List<point> points;
    private Integer change;


    public List<point> getPoints() {
        return points;
    }

    public void setPoints(List<point> points) {
        this.points = points;
    }

    public Integer getChange() {
        return change;
    }

    public void setChange(Integer change) {
        this.change = change;
    }
}
