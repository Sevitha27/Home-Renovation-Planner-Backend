package com.lowes.service;


import com.lowes.convertor.MaterialConvertor;
import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.entity.Material;
import com.lowes.entity.enums.PhaseType;
import com.lowes.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;

    public List<MaterialUserResponse> getExistingMaterialsByPhaseType(PhaseType phaseType){
        List<Material> materialList = materialRepository.findByPhaseTypeAndDeleted(phaseType,false,Sort.by(Sort.Direction.ASC,"id"));
        List<MaterialUserResponse> materialUserResponseList = new ArrayList<>();
        if(!materialList.isEmpty()){
            for(Material material : materialList){
                materialUserResponseList.add(MaterialConvertor.materialToMaterialUserResponse(material));
            }
        }
        return materialUserResponseList;
    }
}