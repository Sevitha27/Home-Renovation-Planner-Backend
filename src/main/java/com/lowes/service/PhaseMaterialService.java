package com.lowes.service;


import com.lowes.convertor.PhaseMaterialConvertor;
import com.lowes.dto.request.PhaseMaterialRequest;
import com.lowes.dto.response.PhaseMaterialResponse;
import com.lowes.entity.Material;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.EmptyException;
import com.lowes.repository.MaterialRepository;
import com.lowes.repository.PhaseMaterialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhaseMaterialService {
    private final PhaseMaterialRepository phaseMaterialRepository;
//    private final PhaseRepository phaseRepository;
    private final MaterialRepository materialRepository;

    public List<PhaseMaterialResponse> getPhaseMaterialsByPhaseId(int phaseId){

//        if(!phaseRepository.existsById(phaseId)){
//            throw new NotFoundException("Phase Not Found To Fetch Phase Materials");
//        }

        List<PhaseMaterial> phaseMaterialList = phaseMaterialRepository.findByPhaseId(phaseId);
        List<PhaseMaterialResponse> phaseMaterialResponseList = new ArrayList<>();
        for(PhaseMaterial phaseMaterial : phaseMaterialList){
            phaseMaterialResponseList.add(PhaseMaterialConvertor.phaseMaterialToPhaseMaterialResponse(phaseMaterial));
        }
        return phaseMaterialResponseList;
    }

    @Transactional
    public List<PhaseMaterialResponse> addPhaseMaterialsToPhaseByPhaseId(int phaseId, List<PhaseMaterialRequest> phaseMaterialRequestList){

        Optional<Phase> optionalPhase = phaseRepository.findById(phaseId);
        if(optionalPhase.isEmpty()){
            throw new ElementNotFoundException("Phase Not Found To Add Phase Materials");
        }
        Phase phase = optionalPhase.get();
        List<PhaseMaterialResponse> phaseMaterialResponseList = new ArrayList<>();
        if(phaseMaterialRequestList.isEmpty()){
            throw new EmptyException("List Of Phase Materials To Add To Phase Is Empty");
        }
        for(PhaseMaterialRequest phaseMaterialRequest : phaseMaterialRequestList){
            PhaseMaterial phaseMaterial = PhaseMaterialConvertor.phaseMaterialRequestToPhaseMaterial(phase,phaseMaterialRequest);

            Optional<Material> optionalMaterial = materialRepository.findById(phaseMaterialRequest.getMaterialId());
            if(optionalMaterial.isEmpty()){
                throw new ElementNotFoundException("Material Not Found To Add Phase Material");
            }
            Material material = optionalMaterial.get();

            phaseMaterial.setMaterial(material);
            phaseMaterial.setName(material.getName());
            phaseMaterial.setUnit(material.getUnit());
            phaseMaterial.setPricePerQuantity(material.getPricePerQuantity());
            phaseMaterial.setRenovationType(material.getRenovationType());
            double totalPrice = phaseMaterialRequest.getQuantity()*material.getPricePerQuantity();
            phaseMaterial.setTotalPrice(totalPrice);

            phase.getPhaseMaterialList().add(phaseMaterial);
            material.getPhaseMaterialList().add(phaseMaterial);

            phaseRepository.save(phase);
            materialRepository.save(material);
            phaseMaterialRepository.save(phaseMaterial);

            phaseMaterialResponseList.add(PhaseMaterialConvertor.phaseMaterialToPhaseMaterialResponse(phaseMaterial));


        }
        return phaseMaterialResponseList;
    }

    @Transactional
    public PhaseMaterialResponse updatePhaseMaterialQuantityById(int id, int quantity){
        if(quantity<=0){
            throw new IllegalArgumentException("Quantity of Phase Material must be a number greater than 0");
        }
        Optional<PhaseMaterial> optionalPhaseMaterial = phaseMaterialRepository.findById(id);
        if(optionalPhaseMaterial.isEmpty()){
            throw new ElementNotFoundException("Phase Material Not Found To Update Quantity");
        }
        PhaseMaterial phaseMaterial = optionalPhaseMaterial.get();
        phaseMaterial.setQuantity(quantity);
        phaseMaterial.setTotalPrice(quantity*phaseMaterial.getPricePerQuantity());
        PhaseMaterial updatedPhaseMaterial = phaseMaterialRepository.save(phaseMaterial);
        PhaseMaterialResponse phaseMaterialResponse = PhaseMaterialConvertor.phaseMaterialToPhaseMaterialResponse(updatedPhaseMaterial);
        return phaseMaterialResponse;
    }

    @Transactional
    public PhaseMaterialResponse deletePhaseMaterialById(int id){
        Optional<PhaseMaterial> optionalPhaseMaterial = phaseMaterialRepository.findById(id);
        if(optionalPhaseMaterial.isEmpty()){
            throw new ElementNotFoundException("Phase Material Not Found To Delete It");
        }
        PhaseMaterial phaseMaterial = optionalPhaseMaterial.get();

        Material material = phaseMaterial.getMaterial();
        material.getPhaseMaterialList().remove(phaseMaterial);

        Phase phase = phaseMaterial.getPhase();
        phase.getPhaseMaterialList().remove(phaseMaterial);

        phaseMaterialRepository.deleteById(id);
        PhaseMaterialResponse phaseMaterialResponse = PhaseMaterialConvertor.phaseMaterialToPhaseMaterialResponse(phaseMaterial);
        return phaseMaterialResponse;
    }



}
