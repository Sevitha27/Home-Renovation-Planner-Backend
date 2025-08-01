package com.lowes.service;


import com.lowes.convertor.PhaseMaterialConvertor;
import com.lowes.dto.request.PhaseMaterialUserRequest;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.entity.Material;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.EmptyException;
import com.lowes.exception.OperationNotAllowedException;
import com.lowes.repository.MaterialRepository;
import com.lowes.repository.PhaseMaterialRepository;
import com.lowes.repository.PhaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhaseMaterialService {
    private final PhaseService phaseService;
    private final PhaseMaterialRepository phaseMaterialRepository;
    private final PhaseRepository phaseRepository;
    private final MaterialRepository materialRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<PhaseMaterialUserResponse> addPhaseMaterialsToPhaseByPhaseId(UUID phaseId, List<PhaseMaterialUserRequest> phaseMaterialUserRequestList){

        Optional<Phase> optionalPhase = phaseRepository.findById(phaseId);
        if(optionalPhase.isEmpty()){
            throw new ElementNotFoundException("Phase Not Found To Add Phase Materials");
        }
        Phase phase = optionalPhase.get();
        if(phase.getPhaseStatus() == PhaseStatus.NOTSTARTED || phase.getPhaseStatus() == PhaseStatus.COMPLETED){
            throw new OperationNotAllowedException("Cannot Add Phase Materials To A Phase That Is NOTSTARTED or COMPLETED");
        }
        List<PhaseMaterialUserResponse> phaseMaterialUserResponseList = new ArrayList<>();
        if(phaseMaterialUserRequestList.isEmpty()){
            throw new EmptyException("List Of Phase Materials To Add To Phase Is Empty");
        }
        for(PhaseMaterialUserRequest phaseMaterialUserRequest : phaseMaterialUserRequestList){
            if(phaseMaterialUserRequest.getQuantity()<=0){
                throw new OperationNotAllowedException("Quantity of phase material has to be greater than 0");
            }
            PhaseMaterial phaseMaterial = PhaseMaterialConvertor.phaseMaterialUserRequestToPhaseMaterial(phase,phaseMaterialUserRequest);

            Optional<Material> optionalMaterial = materialRepository.findByExposedId(phaseMaterialUserRequest.getMaterialExposedId());
            if(optionalMaterial.isEmpty()){
                throw new ElementNotFoundException("Material Not Found To Add Phase Material");
            }
            Material material = optionalMaterial.get();

            if((material.getPhaseType()!=phase.getPhaseType())){
                throw new OperationNotAllowedException("Found Mismatch in the Phase Type of Material and the Phase Type of Phase to which it is being added");
            }

            phaseMaterial.setMaterial(material);
            phaseMaterial.setName(material.getName());
            phaseMaterial.setUnit(material.getUnit());
            phaseMaterial.setPricePerQuantity(material.getPricePerQuantity());
            phaseMaterial.setPhaseType(material.getPhaseType());
            int totalPrice = phaseMaterialUserRequest.getQuantity()*material.getPricePerQuantity();
            phaseMaterial.setTotalPrice(totalPrice);

            phase.getPhaseMaterialList().add(phaseMaterial);
            material.getPhaseMaterialList().add(phaseMaterial);

            phaseRepository.save(phase);
            materialRepository.save(material);
            phaseMaterialRepository.save(phaseMaterial);

            phaseMaterialUserResponseList.add(PhaseMaterialConvertor.phaseMaterialToPhaseMaterialUserResponse(phaseMaterial));


        }
        entityManager.flush();
        entityManager.clear();
        phaseService.calculateTotalCost(phaseId);
        return phaseMaterialUserResponseList;
    }

    @Transactional
    public PhaseMaterialUserResponse updatePhaseMaterialQuantityByExposedId(UUID id, int quantity){
        if(quantity<=0){
            throw new IllegalArgumentException("Quantity of Phase Material must be a number greater than 0");
        }
        Optional<PhaseMaterial> optionalPhaseMaterial = phaseMaterialRepository.findByExposedId(id);
        if(optionalPhaseMaterial.isEmpty()){
            throw new ElementNotFoundException("Phase Material Not Found To Update Quantity");
        }
        PhaseMaterial phaseMaterial = optionalPhaseMaterial.get();
        Phase phase = phaseMaterial.getPhase();
        if(phase.getPhaseStatus() == PhaseStatus.NOTSTARTED || phase.getPhaseStatus() == PhaseStatus.COMPLETED){
            throw new OperationNotAllowedException("Cannot Update Quantities Of Phase Materials Of A Phase That Is NOTSTARTED or COMPLETED");
        }
        phaseMaterial.setQuantity(quantity);
        phaseMaterial.setTotalPrice(quantity*phaseMaterial.getPricePerQuantity());
        PhaseMaterial updatedPhaseMaterial = phaseMaterialRepository.save(phaseMaterial);
        phaseService.calculateTotalCost(phaseMaterial.getPhase().getId());
        PhaseMaterialUserResponse phaseMaterialUserResponse = PhaseMaterialConvertor.phaseMaterialToPhaseMaterialUserResponse(updatedPhaseMaterial);
        return phaseMaterialUserResponse;
    }

    @Transactional
    public PhaseMaterialUserResponse deletePhaseMaterialByExposedId(UUID id){
        Optional<PhaseMaterial> optionalPhaseMaterial = phaseMaterialRepository.findByExposedId(id);
        if(optionalPhaseMaterial.isEmpty()){
            throw new ElementNotFoundException("Phase Material Not Found To Delete It");
        }
        PhaseMaterial phaseMaterial = optionalPhaseMaterial.get();

        Phase phase = phaseMaterial.getPhase();
        if(phase.getPhaseStatus() == PhaseStatus.NOTSTARTED || phase.getPhaseStatus() == PhaseStatus.COMPLETED){
            throw new OperationNotAllowedException("Cannot Delete Phase Materials Of A Phase That Is NOTSTARTED or COMPLETED");
        }
        phase.getPhaseMaterialList().remove(phaseMaterial);

        Material material = phaseMaterial.getMaterial();
        material.getPhaseMaterialList().remove(phaseMaterial);

        phaseMaterialRepository.deleteByExposedId(id);
        phaseService.calculateTotalCost(phase.getId());
        PhaseMaterialUserResponse phaseMaterialUserResponse = PhaseMaterialConvertor.phaseMaterialToPhaseMaterialUserResponse(phaseMaterial);
        return phaseMaterialUserResponse;
    }



}