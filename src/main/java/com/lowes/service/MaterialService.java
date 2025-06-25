package com.lowes.service;


import com.lowes.convertor.MaterialConvertor;
import com.lowes.dto.request.MaterialRequest;
import com.lowes.dto.response.MaterialResponse;
import com.lowes.entity.Material;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.enums.RenovationType;
import com.lowes.exception.ElementNotFoundException;
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

    public List<MaterialResponse> getAllMaterials(){
        List<Material> materialList = materialRepository.findAll();
        List<MaterialResponse> materialResponseList = new ArrayList<>();
        if(!materialList.isEmpty()){
            for(Material material : materialList){
                materialResponseList.add(MaterialConvertor.materialToMaterialResponse(material));
            }
        }
        return materialResponseList;
    }


    public MaterialResponse getMaterialById(int id){
        Optional<Material> optionalMaterial = materialRepository.findById(id);
        if(optionalMaterial.isEmpty()){
            throw new ElementNotFoundException("Material Not Found To Update");
        }
        Material material = optionalMaterial.get();
        MaterialResponse materialResponse = MaterialConvertor.materialToMaterialResponse(material);
        return materialResponse;
    }


    public List<MaterialResponse> getMaterialsByRenovationType(RenovationType renovationType){
        List<Material> materialList = materialRepository.findByRenovationType(renovationType);
        List<MaterialResponse> materialResponseList = new ArrayList<>();
        for(Material material : materialList){
            materialResponseList.add(MaterialConvertor.materialToMaterialResponse(material));
        }
        return materialResponseList;

    }

    @Transactional
    public MaterialResponse addMaterial(MaterialRequest materialRequest){
        Material material = MaterialConvertor.materialRequestToMaterial(materialRequest);
        Material savedMaterial = materialRepository.save(material);
        MaterialResponse materialResponse = MaterialConvertor.materialToMaterialResponse(savedMaterial);
        return materialResponse;
    }

    @Transactional
    public MaterialResponse updateMaterialById(int id, MaterialRequest materialRequest){
        Optional<Material> optionalMaterial = materialRepository.findById(id);
        if(optionalMaterial.isEmpty()){
            throw new ElementNotFoundException("Material Not Found To Update");
        }
        Material existingMaterial = optionalMaterial.get();

        existingMaterial.setName(materialRequest.getName());
        existingMaterial.setUnit(materialRequest.getUnit());
        existingMaterial.setRenovationType(materialRequest.getRenovationType());
        existingMaterial.setPricePerQuantity(materialRequest.getPricePerQuantity());

        Material updatedMaterial = materialRepository.save(existingMaterial);
        MaterialResponse materialResponse = MaterialConvertor.materialToMaterialResponse(updatedMaterial);
        return materialResponse;
    }

    @Transactional
    public MaterialResponse deleteMaterialById(int id){
        Optional<Material> optionalMaterial = materialRepository.findById(id);
        if(optionalMaterial.isEmpty()){
            throw new ElementNotFoundException("Material Not Found To Delete");
        }
        Material material = optionalMaterial.get();

        List<PhaseMaterial> phaseMaterialList = material.getPhaseMaterialList();

        if(!phaseMaterialList.isEmpty()){
            for(PhaseMaterial phaseMaterial : phaseMaterialList){
                phaseMaterial.setMaterial(null);
            }
        }
        materialRepository.deleteById(id);
        MaterialResponse materialResponse = MaterialConvertor.materialToMaterialResponse(material);
        return materialResponse;
    }
}
