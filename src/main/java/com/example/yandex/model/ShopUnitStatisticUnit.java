package com.example.yandex.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name="t_statistic_unit")
@Data
public class ShopUnitStatisticUnit {
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

}
