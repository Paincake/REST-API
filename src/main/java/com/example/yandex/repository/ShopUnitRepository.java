package com.example.yandex.repository;

import com.example.yandex.model.ShopUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShopUnitRepository extends JpaRepository<ShopUnit, UUID> {
}
