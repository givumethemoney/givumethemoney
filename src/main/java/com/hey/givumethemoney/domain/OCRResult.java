package com.hey.givumethemoney.domain;

import groovy.transform.builder.Builder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OCRResult {
    private String productName;
    private String quantity;
    private String unitPrice;
    private String totalAmount;

    @Builder
    public OCRResult(String productName, String quantity, String unitPrice, String totalAmount) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
    }
}
