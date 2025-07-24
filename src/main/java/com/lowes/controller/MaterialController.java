package com.lowes.controller;


import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.entity.enums.PhaseType;
import com.lowes.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MaterialController {
    private final MaterialService materialService;

    Logger logger = LoggerFactory.getLogger(MaterialController.class);


    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
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










}
