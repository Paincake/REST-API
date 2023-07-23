package com.example.yandex.service;

import com.example.yandex.model.ShopUnit;
import com.example.yandex.model.ShopUnitType;
import com.example.yandex.repository.ShopUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShopUnitService {
    @Autowired
    private ShopUnitRepository shopUnitRepository;
    public void createShopUnit(ShopUnit shopUnit){
        if(shopUnit.getParentId() != null){
            ShopUnit parent = shopUnitRepository.findById(shopUnit.getParentId()).orElse(null);
            parent.getChildren().add(shopUnit);
        }
        shopUnitRepository.save(shopUnit);
    }
    public ShopUnit findUnitById(UUID id){
        return shopUnitRepository.findById(id).orElse(null);
    }
    public void updateShopUnit(ShopUnit shopUnit){
        ShopUnit unitToUpdate = shopUnitRepository.findOne(Example.of(shopUnit)).orElse(null);
        if(shopUnit.getParentId() != unitToUpdate.getParentId()){
            unitToUpdate.setParentId(shopUnit.getParentId());
        }
        if(!Objects.equals(shopUnit.getName(), unitToUpdate.getName())){
            unitToUpdate.setName(shopUnit.getName());
        }
        if(!Objects.equals(shopUnit.getPrice(), unitToUpdate.getPrice())){
            unitToUpdate.setPrice(shopUnit.getPrice());
        }
        unitToUpdate.setDate(shopUnit.getDate());
        shopUnitRepository.save(unitToUpdate);
    }
    public ShopUnit findOneShopUnitById(ShopUnit shopUnit){
        return shopUnitRepository.findOne(Example.of(shopUnit)).orElse(null);
    }
    public void deleteShopUnitById(UUID id){
        ShopUnit unitToDelete = shopUnitRepository.findById(id).orElse(null);
        List<ShopUnit> childrenToDelete = unitToDelete.getChildren();
        shopUnitRepository.delete(unitToDelete);
        if(unitToDelete.getType().equals(ShopUnitType.CATEGORY)){
            childrenToDelete.forEach(c -> deleteShopUnitById(c.getId()));
        }

    }

    public boolean existsUnitById(UUID unitId) {
        return shopUnitRepository.existsById(unitId);
    }
}
