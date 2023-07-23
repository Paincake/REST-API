package com.example.yandex.controller;


import com.example.yandex.exception.ValidationException;
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
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/imports", produces = "application/json")
    public ResponseEntity createShopUnits(@RequestBody ShopUnitImportRequest request){
        Set<UUID> importedItems = new HashSet<>();
        Set<UUID> importedCategories = new HashSet<>();
        try{
            for(ShopUnitImport unitImport : request.getItems()){
                if(!importedItems.add(unitImport.getId())){
                    throw new ValidationException();
                }
                if(unitImport.getType().equals(ShopUnitType.CATEGORY)){
                    importedCategories.add(unitImport.getId());
                }
            }
            for(ShopUnitImport unitImport : request.getItems()){
                if(unitImport.getId() == null || unitImport.getType() == null || unitImport.getName() == null){
                    throw new ValidationException();
                }
                if(unitImport.getParentId() != null){
                    if(!importedCategories.contains(unitImport.getParentId())){
                        ShopUnit parent = shopUnitService.findUnitById(unitImport.getParentId());
                        if(parent == null || parent.getType().equals(ShopUnitType.OFFER)){
                            throw new ValidationException();
                        }
                    }
                }

                if(unitImport.getType() == ShopUnitType.CATEGORY){
                    if(unitImport.getPrice() != null){
                        throw new ValidationException();
                    }
                }
                else{
                    if(unitImport.getPrice() == null || unitImport.getPrice() < 0){
                        throw new ValidationException();
                    }
                }
            }
            for(ShopUnitImport item : request.getItems()){
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
                ShopUnit existingUnit = shopUnitService.findOneShopUnitById(newUnit);
                if(existingUnit != null){
                    if(!existingUnit.getType().equals(newUnit.getType())){
                        throw new ValidationException();
                    }
                    shopUnitService.updateShopUnit(newUnit);
                }
                else{
                    shopUnitService.createShopUnit(newUnit);
                }
            }
        } catch (ValidationException exc){
            return new ResponseEntity(new Error(400, "Validation failed"), HttpStatusCode.valueOf(400));
        }
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteShopUnit(@PathVariable String id){
        UUID unitId = null;
        try{
            unitId = UUID.fromString(id);
        } catch (IllegalArgumentException exc){
            return new ResponseEntity(new Error(400, "Validation failed"), HttpStatusCode.valueOf(400));
        }
        if(!shopUnitService.existsUnitById(unitId)){
            return new ResponseEntity(new Error(404, "Item not found"), HttpStatusCode.valueOf(404));
        }
        shopUnitService.deleteShopUnitById(unitId);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity getShopUnit(@PathVariable String id){
        UUID unitId = null;
        try{
            unitId = UUID.fromString(id);
        } catch (IllegalArgumentException exc){
            return new ResponseEntity(new Error(400, "Validation failed"), HttpStatusCode.valueOf(400));
        }
        ShopUnit unit = shopUnitService.findUnitById(unitId);
        if(unit == null){
            return new ResponseEntity(new Error(404, "Item not found"), HttpStatusCode.valueOf(404));
        }
        return new ResponseEntity(unit, HttpStatusCode.valueOf(200));


    }
}
