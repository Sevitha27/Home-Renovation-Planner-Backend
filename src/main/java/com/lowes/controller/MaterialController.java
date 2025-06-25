package com.lowes.controller;


import com.lowes.dto.request.MaterialAdminRequest;
import com.lowes.dto.response.MaterialAdminResponse;
import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.entity.enums.RenovationType;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.OperationNotAllowedException;
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

    @GetMapping("/admin/materials")
    public ResponseEntity getAllMaterials(@RequestParam(name = "renovationType", required = false) RenovationType renovationType, @RequestParam(name = "deleted", required = false) Boolean deleted){
        try{
            List<MaterialAdminResponse> materialAdminResponseList = materialService.getAllMaterials(renovationType,deleted);
            return new ResponseEntity(materialAdminResponseList,HttpStatus.OK);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/user/materials")
    public ResponseEntity getExistingMaterials(@RequestParam(name = "renovationType",required = false) RenovationType renovationType){
        try{
            List<MaterialUserResponse> materialUserResponseList = materialService.getExistingMaterials(renovationType);
            return new ResponseEntity(materialUserResponseList,HttpStatus.OK);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/materials/{id}")
    public ResponseEntity getMaterialById(@PathVariable("id") int id){
        try{
            MaterialAdminResponse materialAdminResponse = materialService.getMaterialById(id);
            return new ResponseEntity(materialAdminResponse,HttpStatus.OK);
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



    @PostMapping("/admin/materials")
    public ResponseEntity addMaterial(@RequestBody MaterialAdminRequest materialAdminRequest){
        try{
            MaterialAdminResponse materialAdminResponse = materialService.addMaterial(materialAdminRequest);
            return new ResponseEntity(materialAdminResponse,HttpStatus.CREATED);
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
    public ResponseEntity updateMaterialById(@PathVariable("id") int id, @RequestBody MaterialAdminRequest materialAdminRequest){
        try{
            MaterialAdminResponse materialAdminResponse = materialService.updateMaterialById(id, materialAdminRequest);
            return new ResponseEntity(materialAdminResponse,HttpStatus.OK);
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

    @PatchMapping("/admin/materials/delete/{id}")
    public ResponseEntity deleteMaterialById(@PathVariable("id") int id){
        try{
            MaterialAdminResponse materialAdminResponse = materialService.deleteMaterialById(id);
            return new ResponseEntity(materialAdminResponse,HttpStatus.OK);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
        }
        catch(OperationNotAllowedException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PatchMapping("/admin/materials/re-add/{id}")
    public ResponseEntity reAddMaterialById(@PathVariable("id") int id){
        try{
            MaterialAdminResponse materialAdminResponse = materialService.reAddMaterialById(id);
            return new ResponseEntity(materialAdminResponse,HttpStatus.OK);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
        }
        catch(OperationNotAllowedException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}
