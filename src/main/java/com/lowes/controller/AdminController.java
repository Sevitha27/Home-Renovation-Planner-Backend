package com.lowes.controller;


import com.lowes.dto.request.MaterialRequestAdminDTO;
import com.lowes.dto.response.MaterialResponseAdminDTO;
import com.lowes.dto.response.UserResponseAdminDTO;
import com.lowes.dto.response.VendorResponseAdminDTO;

import com.lowes.entity.enums.PhaseType;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.OperationNotAllowedException;
import com.lowes.service.AdminService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    Logger logger = LoggerFactory.getLogger(MaterialController.class);

    //CUSTOMER

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseAdminDTO>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }


    //VENDOR
    @GetMapping("/vendors")
    public ResponseEntity<List<VendorResponseAdminDTO>> getAllVendors() {
        return ResponseEntity.ok(adminService.getAllVendors());
    }

    @PutMapping("/vendor/{id}/approve")
    public ResponseEntity<String> updateVendorApproval(@PathVariable UUID id, @RequestParam boolean approved) {
        adminService.updateVendorApproval(id, approved);
        return ResponseEntity.ok("Vendor approval updated.");
    }

    @DeleteMapping("/vendor/{id}")
    public ResponseEntity<String> deleteVendor(@PathVariable UUID id) {
        adminService.deleteVendor(id);
        return ResponseEntity.ok("Vendor deleted successfully.");
    }


    //MATERIALS
   @GetMapping("/materials")
   public ResponseEntity<?> getAllMaterials(@RequestParam(name = "phaseType", required = false) PhaseType phaseType, @RequestParam(name = "deleted", required = false) Boolean deleted){
       try{
           List<MaterialResponseAdminDTO> materialResponseAdminDTOList = adminService.getAllMaterials(phaseType,deleted);
           return ResponseEntity.ok(materialResponseAdminDTOList);
       }
       catch(Exception exception){
           logger.error("Exception",exception);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
       }

   }

    @GetMapping("/materials/{id}")
    public ResponseEntity<?> getMaterialByExposedId(@PathVariable("id") UUID id){
        try{
            MaterialResponseAdminDTO materialResponseAdminDTO = adminService.getMaterialByExposedId(id);
            return ResponseEntity.ok(materialResponseAdminDTO);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }
    }

    @PostMapping("/materials")
    public ResponseEntity<?> addMaterial(@RequestBody MaterialRequestAdminDTO materialRequestAdminDTO){
        try{
            MaterialResponseAdminDTO materialResponseAdminDTO = adminService.addMaterial(materialRequestAdminDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(materialResponseAdminDTO);

        }
        catch(DataIntegrityViolationException exception){
            logger.error("Data Integrity Violation",exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Integrity Violation: "+exception.getMessage());
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }
    }

    @PutMapping("/materials/{id}")
    public ResponseEntity<?> updateMaterialByExposedId(@PathVariable("id") UUID id, @RequestBody MaterialRequestAdminDTO materialRequestAdminDTO){
        try{
            MaterialResponseAdminDTO materialResponseAdminDTO = adminService.updateMaterialByExposedId(id, materialRequestAdminDTO);
            return ResponseEntity.ok(materialResponseAdminDTO);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }

        catch(DataIntegrityViolationException exception){
            logger.error("Data Integrity Violation",exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Integrity Violation: "+exception.getMessage());
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }

    }

    @PatchMapping("/materials/delete/{id}")
    public ResponseEntity<?> deleteMaterialByExposedId(@PathVariable("id") UUID id){
        try{
            MaterialResponseAdminDTO materialResponseAdminDTO = adminService.deleteMaterialByExposedId(id);
            return ResponseEntity.ok(materialResponseAdminDTO);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
        catch(OperationNotAllowedException exception){
            logger.error(exception.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }

    }

    @PatchMapping("/materials/re-add/{id}")
    public ResponseEntity<?> reAddMaterialByExposedId(@PathVariable("id") UUID id){
        try{
            MaterialResponseAdminDTO materialResponseAdminDTO = adminService.reAddMaterialByExposedId(id);
            return ResponseEntity.ok(materialResponseAdminDTO);
        }
        catch (ElementNotFoundException exception){
            logger.error(exception.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
        catch(OperationNotAllowedException exception){
            logger.error(exception.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch(Exception exception){
            logger.error("Exception",exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }
    }
}
