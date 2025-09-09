package com.yl.musinsa2.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum GenderFilter {
    ALL("A", "전체"),
    MALE("M", "남성"),
    FEMALE("F", "여성");

    private final String code;
    private final String displayName;

    public static GenderFilter fromCode(String code) {
        if (StringUtils.isBlank(code)) {
            return ALL;
        }

        for (GenderFilter filter : values()) {
            if (filter.code.equals(code)) {
                return filter;
            }
        }
        return ALL;
    }

    public static GenderFilter fromCodeOrDefault(String code) {
        return fromCode(code);
    }
}
