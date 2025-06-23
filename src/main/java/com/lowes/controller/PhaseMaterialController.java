package com.lowes.controller;

import com.example.Home_Renovation.dto.request.PhaseMaterialRequest;
import com.example.Home_Renovation.dto.response.PhaseMaterialResponse;
import com.example.Home_Renovation.exception.ElementNotFoundException;
import com.example.Home_Renovation.exception.EmptyException;
import com.example.Home_Renovation.service.PhaseMaterialService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PhaseMaterialController {

    private final PhaseMaterialService phaseMaterialService;

    Logger logger = LoggerFactory.getLogger(PhaseMaterialController.class);

    @GetMapping("/user/phase/{phase-id}/phase-materials")
    public ResponseEntity getPhaseMaterialsByPhaseId(@PathVariable("phase-id") int phaseId){
        try{
            List<PhaseMaterialResponse> phaseMaterialResponseList = phaseMaterialService.getPhaseMaterialsByPhaseId(phaseId);
            return new ResponseEntity(phaseMaterialResponseList, HttpStatus.OK);
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

    @PostMapping("/user/phase/{phase-id}/phase-materials")
    public ResponseEntity addPhaseMaterialsToPhaseByPhaseId(@PathVariable("id") int phaseId, @RequestBody List<PhaseMaterialRequest> phaseMaterialRequestList){
        try{
            List<PhaseMaterialResponse> phaseMaterialResponseList = phaseMaterialService.addPhaseMaterialsToPhaseByPhaseId(phaseId,phaseMaterialRequestList);
            return new ResponseEntity(phaseMaterialResponseList,HttpStatus.CREATED);
        }
        catch(ElementNotFoundException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
        }
        catch(EmptyException exception){
            logger.error(exception.toString());
            return new ResponseEntity(exception.getMessage(),HttpStatus.BAD_REQUEST);
        }
        catch (Exception exception){
            logger.error("Exception",exception);
            return new ResponseEntity("Internal Server Error : "+exception.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/user/phase-materials/{phase-material-id}")
    public ResponseEntity updatePhaseMaterialQuantityById(@PathVariable("phase-material-id") int id, @RequestParam("quantity") int quantity){
        try{
            PhaseMaterialResponse phaseMaterialResponse = phaseMaterialService.updatePhaseMaterialQuantityById(id,quantity);
            return new ResponseEntity(phaseMaterialResponse,HttpStatus.OK);
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
}
