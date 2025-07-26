package com.lowes.controller;


import com.lowes.dto.request.PhaseMaterialUserRequest;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.EmptyException;
import com.lowes.exception.OperationNotAllowedException;
import com.lowes.service.PhaseMaterialService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PhaseMaterialController {

    private final PhaseMaterialService phaseMaterialService;

    Logger logger = LoggerFactory.getLogger(PhaseMaterialController.class);

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PostMapping("/user/phase/{phase-id}/phase-materials")
    public ResponseEntity addPhaseMaterialsToPhaseByPhaseId(@PathVariable("phase-id") UUID phaseId, @RequestBody List<PhaseMaterialUserRequest> phaseMaterialUserRequestList){
        try{
            List<PhaseMaterialUserResponse> phaseMaterialUserResponseList = phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phaseId,phaseMaterialUserRequestList);
            return new ResponseEntity(phaseMaterialUserResponseList,HttpStatus.CREATED);
        }
        catch(ElementNotFoundException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
        }
        catch(EmptyException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch(OperationNotAllowedException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @PatchMapping("/user/phase-materials/{phase-material-id}")
    public ResponseEntity updatePhaseMaterialQuantityByExposedId(@PathVariable("phase-material-id") UUID id, @RequestParam("quantity") int quantity){
        try{
            PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.updatePhaseMaterialQuantityByExposedId(id,quantity);
            return new ResponseEntity(phaseMaterialUserResponse,HttpStatus.OK);
        }
        catch(IllegalArgumentException exception){
            logger.error("Illegal Argument Exception",exception);
            return new ResponseEntity("Illegal Argument Exception : "+exception.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
        }
        catch (Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @DeleteMapping("/user/phase-materials/{phase-material-id}")
    public ResponseEntity deletePhaseMaterialByExposedId(@PathVariable("phase-material-id") UUID id){
        try{
            PhaseMaterialUserResponse phaseMaterialUserResponse = phaseMaterialService.deletePhaseMaterialByExposedId(id);
            return new ResponseEntity(phaseMaterialUserResponse, HttpStatus.OK);
        }
        catch(ElementNotFoundException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
