package com.example.yandex.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

// TODO price for categories and children for offers
@Entity(name = "t_shop_units")
@Data
public class ShopUnit {
    @Id
    private UUID id;
    private String name;
    private OffsetDateTime date;
    @Nullable
    private UUID parentId;
    @Enumerated
    private ShopUnitType type;
    @Nullable
    private Long price;
    @Nullable
    @OneToMany(fetch = FetchType.EAGER)
    private List<ShopUnit> children;

}
