package org.example.phone_store.entity;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Product {
    private Integer productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String imageUrl;
    private Integer categoryId;
    private LocalDateTime createdAt;
    private Boolean isFeatured;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
