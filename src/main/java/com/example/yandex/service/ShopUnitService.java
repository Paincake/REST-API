package com.example.yandex.service;

import com.example.yandex.model.ShopUnit;
import com.example.yandex.repository.ShopUnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class ShopUnitService {
    @Autowired
    ShopUnitRepository shopUnitRepository;
    public void createShopUnit(ShopUnit shopUnit){
        ShopUnit item = shopUnitRepository.findById(shopUnit.getId()).orElse(null);
        if(item != null){

        }
        if(shopUnit.getParentId() != null){
            ShopUnit parent = shopUnitRepository.findById(shopUnit.getParentId()).orElse(null);
            parent.getChildren().add(shopUnit);
        }
    }
    public ShopUnit findUnitById(UUID id){
        return shopUnitRepository.findById(id).orElse(null);
    }
    public void updateShopUnit(ShopUnit shopUnit){
        ShopUnit unitToUpdate = shopUnitRepository.findOne(Example.of(shopUnit)).orElse(null);
        if(shopUnit.getParentId() != unitToUpdate.getParentId()){
            unitToUpdate.setParentId(shopUnit.getParentId());
        }
        if(shopUnit.getName() != unitToUpdate.getName()){
            unitToUpdate.setName(shopUnit.getName());
        }
        if(shopUnit.getPrice() != unitToUpdate.getPrice()){
            unitToUpdate.setPrice(shopUnit.getPrice());
        }
        unitToUpdate.setDate(shopUnit.getDate());
        shopUnitRepository.save(unitToUpdate);
    }

    public boolean existsUnitById(UUID unitId) {
        return shopUnitRepository.existsById(unitId);
    }
}
