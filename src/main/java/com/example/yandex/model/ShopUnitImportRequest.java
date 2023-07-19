package com.example.yandex.model;

import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShopUnitImportRequest {
    private List<ShopUnitImport> items;
    private String updateDate;
}
