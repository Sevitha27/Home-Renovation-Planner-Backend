package com.lowes.controller;


import com.lowes.dto.request.MaterialAdminRequest;
import com.lowes.dto.response.MaterialAdminResponse;
import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.entity.enums.PhaseType;
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
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MaterialController {
    private final MaterialService materialService;

    Logger logger = LoggerFactory.getLogger(MaterialController.class);

    @GetMapping("/admin/materials")
    public ResponseEntity getAllMaterials(@RequestParam(name = "phaseType", required = false) PhaseType phaseType, @RequestParam(name = "deleted", required = false) Boolean deleted){
        try{
            List<MaterialAdminResponse> materialAdminResponseList = materialService.getAllMaterials(phaseType,deleted);
            return new ResponseEntity(materialAdminResponseList,HttpStatus.OK);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/user/materials")
    public ResponseEntity getExistingMaterialsByPhaseType(@RequestParam("phaseType") PhaseType phaseType){
        try{
            List<MaterialUserResponse> materialUserResponseList = materialService.getExistingMaterialsByPhaseType(phaseType);
            return new ResponseEntity(materialUserResponseList,HttpStatus.OK);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/materials/{id}")
    public ResponseEntity getMaterialByExposedId(@PathVariable("id") UUID id){
        try{
            MaterialAdminResponse materialAdminResponse = materialService.getMaterialByExposedId(id);
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
    public ResponseEntity updateMaterialByExposedId(@PathVariable("id") UUID id, @RequestBody MaterialAdminRequest materialAdminRequest){
        try{
            MaterialAdminResponse materialAdminResponse = materialService.updateMaterialByExposedId(id, materialAdminRequest);
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
    public ResponseEntity deleteMaterialByExposedId(@PathVariable("id") UUID id){
        try{
            MaterialAdminResponse materialAdminResponse = materialService.deleteMaterialByExposedId(id);
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
    public ResponseEntity reAddMaterialByExposedId(@PathVariable("id") UUID id){
        try{
            MaterialAdminResponse materialAdminResponse = materialService.reAddMaterialByExposedId(id);
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
