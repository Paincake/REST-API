package com.example.yandex.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;
// TODO price for categories
@Data
public class ShopUnitImport {
    @Id
    private UUID id;
    private String name;
    private UUID parentId;
    private ShopUnitType type;
    private Long price;

}
