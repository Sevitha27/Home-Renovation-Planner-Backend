package com.lowes.controller;


import com.lowes.dto.request.MaterialRequest;
import com.lowes.dto.response.MaterialResponse;
import com.lowes.entity.enums.RenovationType;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MaterialController {
    private final MaterialService materialService;

    Logger logger = LoggerFactory.getLogger(MaterialController.class);

    @GetMapping("/all/materials")
    public ResponseEntity getAllMaterials(){
        try{
            List<MaterialResponse> materialResponseList = materialService.getAllMaterials();
            return new ResponseEntity(materialResponseList,HttpStatus.OK);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/admin/materials/{id}")
    public ResponseEntity getMaterialById(@PathVariable("id") int id){
        try{
            MaterialResponse materialResponse = materialService.getMaterialById(id);
            return new ResponseEntity(materialResponse,HttpStatus.OK);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all/materials")
    public ResponseEntity getMaterialsByRenovationType(@RequestParam(name = "renovationType",required = true) String typeOfRenovation){
        try{
            RenovationType renovationType = RenovationType.valueOf(typeOfRenovation.toUpperCase());
            List<MaterialResponse> materialResponseList = materialService.getMaterialsByRenovationType(renovationType);
            return new ResponseEntity(materialResponseList, HttpStatus.OK);
        }
        catch (IllegalArgumentException exception){
            logger.error("Invalid Argument",exception);
            return new ResponseEntity("Invalid Argument!",HttpStatus.BAD_REQUEST);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }




    }

    @PostMapping("/admin/materials")
    public ResponseEntity addMaterial(@RequestBody MaterialRequest materialRequest){
        try{
            MaterialResponse materialResponse = materialService.addMaterial(materialRequest);
            return new ResponseEntity(materialResponse,HttpStatus.CREATED);
        }
        catch(DataIntegrityViolationException exception){
            logger.error("Data Integrity Violation",exception);
            return new ResponseEntity("Data Integrity Violation: "+exception.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admin/materials/{id}")
    public ResponseEntity updateMaterialById(@PathVariable("id") int id, @RequestBody MaterialRequest materialRequest){
        try{
            MaterialResponse materialResponse = materialService.updateMaterialById(id,materialRequest);
            return new ResponseEntity(materialResponse,HttpStatus.OK);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
        }

        catch(DataIntegrityViolationException exception){
            logger.error("Data Integrity Violation",exception);
            return new ResponseEntity("Data Integrity Violation: "+exception.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/admin/materials/{id}")
    public ResponseEntity deleteMaterialById(@PathVariable("id") int id){
        try{
            MaterialResponse materialResponse = materialService.deleteMaterialById(id);
            return new ResponseEntity(materialResponse,HttpStatus.OK);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
        }

        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }




}
