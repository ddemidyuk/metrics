package com.example.metrics.entity;

import java.util.HashMap;
import java.util.Map;

public enum AggregationTypeEnum {
    AVERAGE(1);
    /*SUM(2),
    LAST(3),
    MAX(4),
    MIN(5);*/

    private long code;
    private static Map<Long, AggregationTypeEnum> enumMap = new HashMap<>();

    static {
        for (AggregationTypeEnum anEnum : AggregationTypeEnum.values()){
            enumMap.put(anEnum.code, anEnum);
        }
    }

    AggregationTypeEnum(long code) {
        this.code = code;
    }

    public long getCode() {
        return code;
    }

    public static AggregationTypeEnum getEnumByCode(long code){
        return enumMap.get(code);
    }
}
