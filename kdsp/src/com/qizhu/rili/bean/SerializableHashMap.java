package com.qizhu.rili.bean;

import java.io.Serializable;
import java.util.Map;

/**
 */

public class SerializableHashMap implements Serializable{
    private Map<String,Object> map;

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
