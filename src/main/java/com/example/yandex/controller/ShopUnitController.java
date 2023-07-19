package com.example.yandex.controller;


import com.example.yandex.model.Error;
import com.example.yandex.model.ShopUnit;
import com.example.yandex.model.ShopUnitImport;
import com.example.yandex.model.ShopUnitImportRequest;
import com.example.yandex.model.ShopUnitType;
import com.example.yandex.service.ShopUnitService;
import com.example.yandex.service.ShopUnitStatisticUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ShopUnitController {
    @Autowired
    ShopUnitService shopUnitService;

    @Autowired
    ShopUnitStatisticUnitService shopUnitStatisticUnitService;

    //TODO can't change category (create custom exception and throw it from the service)
    @PostMapping("/imports")
    public ResponseEntity<String> createShopUnits(@RequestBody ShopUnitImportRequest request){
        Set<UUID> importedItems = new HashSet<>();
        Set<UUID> importedCategories = new HashSet<>();
        for(ShopUnitImport unitImport : request.getItems()){
            if(!importedItems.add(unitImport.getId())){
                return new ResponseEntity<String>(new Error(400, "Validation failed").toString(), HttpStatusCode.valueOf(400));
            }
            if(unitImport.getType().equals(ShopUnitType.CATEGORY)){
                importedCategories.add(unitImport.getId());
            }
        }
        for(ShopUnitImport unitImport : request.getItems()){
            if(unitImport.getId() == null || unitImport.getType() == null || unitImport.getName() == null){
                return new ResponseEntity<String>(new Error(400, "Validation failed").toString(), HttpStatusCode.valueOf(400));
            }
            if(unitImport.getParentId() != null){
                if(!importedCategories.contains(unitImport.getParentId())){
                    ShopUnit parent = shopUnitService.findUnitById(unitImport.getParentId());
                    if(parent == null || parent.getType().equals(ShopUnitType.OFFER)){
                        return new ResponseEntity<String>(new Error(400, "Validation failed").toString(), HttpStatusCode.valueOf(400));
                    }
                }
            }

            if(unitImport.getType() == ShopUnitType.CATEGORY){
                if(unitImport.getPrice() != null){
                    return new ResponseEntity<String>(new Error(400, "Validation failed").toString(), HttpStatusCode.valueOf(400));
                }
            }
            else{
                if(unitImport.getPrice() == null || unitImport.getPrice() < 0){
                    return new ResponseEntity<String>(new Error(400, "Validation failed").toString(), HttpStatusCode.valueOf(400));
                }
            }
        }
        request.getItems().forEach(item -> {
            ShopUnit newUnit = new ShopUnit();
            newUnit.setType(item.getType());
            newUnit.setName(item.getName());
            newUnit.setPrice(item.getPrice());
            newUnit.setId(item.getId());
            if(item.getType().equals(ShopUnitType.OFFER)){
                newUnit.setChildren(null);
            }
            else{
                newUnit.setChildren(new ArrayList<>());
            }
            newUnit.setParentId(item.getParentId());
            newUnit.setDate(OffsetDateTime.parse(request.getUpdateDate(), DateTimeFormatter.ISO_DATE_TIME));
            if(shopUnitService.existsUnitById(newUnit.getId())){
                shopUnitService.updateShopUnit(newUnit);
            }
            else{
                shopUnitService.createShopUnit(newUnit);
            }
        });
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
