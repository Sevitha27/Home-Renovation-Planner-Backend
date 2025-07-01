package com.lowes.controller;

import com.lowes.dto.request.MaterialRequestAdminDTO;

import com.lowes.entity.enums.PhaseType;
import com.lowes.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    //CUSTOMER
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return adminService.getAllCustomers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        return adminService.deleteUser(id);
    }


    //VENDOR
    @GetMapping("/vendors")
    public ResponseEntity<?> getAllVendors() {
        return adminService.getAllVendors();
    }

    @PutMapping("/vendor/{id}/approve")
    public ResponseEntity<?> updateVendorApproval(@PathVariable UUID id, @RequestParam boolean approved) {
        return adminService.updateVendorApproval(id, approved);
    }

    @DeleteMapping("/vendor/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable UUID id) {
        return adminService.deleteVendor(id);
    }


    //MATERIALS
    @GetMapping("/materials")
    public ResponseEntity<?> getAllMaterials(@RequestParam(name = "phaseType", required = false) PhaseType phaseType, @RequestParam(name = "deleted", required = false) Boolean deleted){
        return adminService.getAllMaterials(phaseType,deleted);
    }

    @GetMapping("/materials/{id}")
    public ResponseEntity<?> getMaterialByExposedId(@PathVariable("id") UUID id){
        return adminService.getMaterialByExposedId(id);
    }

    @PostMapping("/materials")
    public ResponseEntity<?> addMaterial(@RequestBody MaterialRequestAdminDTO materialRequestAdminDTO){
        return adminService.addMaterial(materialRequestAdminDTO);
    }

    @PutMapping("/materials/{id}")
    public ResponseEntity<?> updateMaterialByExposedId(@PathVariable("id") UUID id, @RequestBody MaterialRequestAdminDTO materialRequestAdminDTO){
        return adminService.updateMaterialByExposedId(id, materialRequestAdminDTO);
    }

    @PatchMapping("/materials/delete/{id}")
    public ResponseEntity<?> deleteMaterialByExposedId(@PathVariable("id") UUID id){
        return adminService.deleteMaterialByExposedId(id);
    }

    @PatchMapping("/materials/re-add/{id}")
    public ResponseEntity<?> reAddMaterialByExposedId(@PathVariable("id") UUID id){
        return adminService.reAddMaterialByExposedId(id);
    }
}
