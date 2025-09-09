package com.yl.musinsa2.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;

@Converter(autoApply = true)
public class GenderFilterConverter implements AttributeConverter<GenderFilter, String> {

    @Override
    public String convertToDatabaseColumn(GenderFilter attribute) {
        return Objects.requireNonNullElse(attribute, GenderFilter.ALL).getCode();
    }

    @Override
    public GenderFilter convertToEntityAttribute(String dbData) {
        return GenderFilter.fromCode(dbData);
    }
}
