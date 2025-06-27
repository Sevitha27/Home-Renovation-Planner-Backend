package com.lowes.service;


import com.lowes.convertor.MaterialConvertor;
import com.lowes.dto.request.MaterialAdminRequest;
import com.lowes.dto.response.MaterialAdminResponse;
import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.entity.Material;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.enums.RenovationType;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.OperationNotAllowedException;
import com.lowes.repository.MaterialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;

    public List<MaterialAdminResponse> getAllMaterials(RenovationType renovationType, Boolean deleted){

        List<Material> materialList;
        if(renovationType!=null && deleted!=null){
            materialList = materialRepository.findByRenovationTypeAndDeleted(renovationType,deleted);

        }
        else if(renovationType!=null){
            materialList = materialRepository.findByRenovationType(renovationType);
        }
        else if(deleted!=null){
            materialList = materialRepository.findByDeleted(deleted);
        }
        else{
            materialList = materialRepository.findAll();
        }
        List<MaterialAdminResponse> materialAdminResponseList = new ArrayList<>();
        if(!materialList.isEmpty()){
            for(Material material : materialList){
                materialAdminResponseList.add(MaterialConvertor.materialToMaterialAdminResponse(material));
            }
        }
        return materialAdminResponseList;
    }

    public List<MaterialUserResponse> getExistingMaterialsByRenovationType(RenovationType renovationType){

        List<Material> materialList;

        if(renovationType!=null){
            materialList = materialRepository.findByRenovationTypeAndDeleted(renovationType,false);
        }
        else{
            materialList = materialRepository.findByDeleted(false);
        }
        List<MaterialUserResponse> materialUserResponseList = new ArrayList<>();
        if(!materialList.isEmpty()){
            for(Material material : materialList){
                materialUserResponseList.add(MaterialConvertor.materialToMaterialUserResponse(material));
            }
        }
        return materialUserResponseList;
    }


    public MaterialAdminResponse getMaterialById(int id){
        Optional<Material> optionalMaterial = materialRepository.findById(id);
        if(optionalMaterial.isEmpty()){
            throw new ElementNotFoundException("Material Not Found To Update");
        }
        Material material = optionalMaterial.get();
        MaterialAdminResponse materialAdminResponse = MaterialConvertor.materialToMaterialAdminResponse(material);
        return materialAdminResponse;
    }




    @Transactional
    public MaterialAdminResponse addMaterial(MaterialAdminRequest materialAdminRequest){
        Material material = MaterialConvertor.materialAdminRequestToMaterial(materialAdminRequest);
        Material savedMaterial = materialRepository.save(material);
        MaterialAdminResponse materialAdminResponse = MaterialConvertor.materialToMaterialAdminResponse(savedMaterial);
        return materialAdminResponse;
    }

    @Transactional
    public MaterialAdminResponse updateMaterialById(int id, MaterialAdminRequest materialAdminRequest){
        Optional<Material> optionalMaterial = materialRepository.findById(id);
        if(optionalMaterial.isEmpty()){
            throw new ElementNotFoundException("Material Not Found To Update");
        }
        Material existingMaterial = optionalMaterial.get();

        existingMaterial.setName(materialAdminRequest.getName());
        existingMaterial.setUnit(materialAdminRequest.getUnit());
        existingMaterial.setRenovationType(materialAdminRequest.getRenovationType());
        existingMaterial.setPricePerQuantity(materialAdminRequest.getPricePerQuantity());

        Material updatedMaterial = materialRepository.save(existingMaterial);
        MaterialAdminResponse materialAdminResponse = MaterialConvertor.materialToMaterialAdminResponse(updatedMaterial);
        return materialAdminResponse;
    }

    @Transactional
    public MaterialAdminResponse deleteMaterialById(int id){
        Optional<Material> optionalMaterial = materialRepository.findById(id);
        if(optionalMaterial.isEmpty()){
            throw new ElementNotFoundException("Material Not Found To Delete");
        }
        Material material = optionalMaterial.get();

        if(material.isDeleted()){
            throw new OperationNotAllowedException("Cannot Delete A Material That Is Already Deleted");
        }
        material.setDeleted(true);
        Material savedMaterial = materialRepository.save(material);
        MaterialAdminResponse materialAdminResponse = MaterialConvertor.materialToMaterialAdminResponse(savedMaterial);
        return materialAdminResponse;
    }

    @Transactional
    public MaterialAdminResponse reAddMaterialById(int id){
        Optional<Material> optionalMaterial = materialRepository.findById(id);
        if(optionalMaterial.isEmpty()){
            throw  new ElementNotFoundException("Material Not Found To Re-Add");
        }
        Material material = optionalMaterial.get();

        if(!material.isDeleted()){
            throw new OperationNotAllowedException("Cannot Re Add A Material That Is Not Deleted");
        }

        material.setDeleted(false);
        Material savedMaterial = materialRepository.save(material);

        MaterialAdminResponse materialAdminResponse = MaterialConvertor.materialToMaterialAdminResponse(savedMaterial);
        return materialAdminResponse;
    }
}
