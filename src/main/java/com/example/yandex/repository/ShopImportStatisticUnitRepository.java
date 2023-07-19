package com.example.yandex.repository;

import com.example.yandex.model.ShopUnitStatisticUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShopImportStatisticUnitRepository extends JpaRepository<ShopUnitStatisticUnit, UUID> {
}
