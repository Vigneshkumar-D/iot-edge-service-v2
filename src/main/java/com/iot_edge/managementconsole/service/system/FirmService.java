package com.iot_edge.managementconsole.service.system;

import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.common.exceptions.ExpectationFailedException;
import com.iot_edge.managementconsole.dto.request.FirmRequestDTO;
import com.iot_edge.managementconsole.dto.system.AssetDTO;
import com.iot_edge.managementconsole.dto.system.FirmDTO;
import com.iot_edge.managementconsole.dto.system.LocationDTO;
import com.iot_edge.managementconsole.entity.system.Asset;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.system.Location;
import com.iot_edge.managementconsole.mapper.AssetMapper;
import com.iot_edge.managementconsole.mapper.FirmMapper;
import com.iot_edge.managementconsole.mapper.LocationMapper;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.system.FirmRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import jakarta.persistence.criteria.Predicate;
import org.apache.kafka.common.protocol.types.Field;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FirmService {

    private final FirmRepository firmRepository;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public FirmService(FirmRepository firmRepository, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.firmRepository = firmRepository;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ResponseEntity<ResponseModel<?>> add(FirmRequestDTO firmRequestDTO){
        try{
            Firm existingFirm = firmRepository.findByName(firmRequestDTO.getName());
            if(existingFirm ==null){
                Firm firm = Firm.builder()
                        .name(firmRequestDTO.getName())
                        .email(firmRequestDTO.getEmail())
                        .website(firmRequestDTO.getWebsite())
                        .parent(FirmMapper.INSTANCE.toFirm(firmRequestDTO.getParent())) //check this
                        .contactNo(firmRequestDTO.getContactNo())
                        .logoUrl(firmRequestDTO.getLogoUrl())
                        .location(LocationMapper.INSTANCE.toLocation(firmRequestDTO.getLocation()))
                        .build();
                firm = firmRepository.save(firm);
                FirmDTO firmDTO = FirmMapper.INSTANCE.toFirmDTO(firm);
                return ResponseEntity.ok(new ResponseModel<>(true, "Success", firmDTO));
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding Firm: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
        return null;
    }

    public ResponseEntity<ResponseModel<?>> list(){
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", firmRepository.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error fetching data: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> update(String firmUuid, FirmDTO firmDto){
        try{
            Optional<Firm> firmOptional = firmRepository.findByUuid(UUID.fromString(firmUuid));
            if (firmOptional.isEmpty()) {
                throw new RuntimeException("Firm Details not found with uuid: " + firmUuid);
            }

            Firm parentFirm = FirmMapper.INSTANCE.toFirm(firmDto.getParent());
            Location location = LocationMapper.INSTANCE.toLocation(firmDto.getLocation());
            Firm firm = firmOptional.get();
            firm.setName(firmDto.getName());
            firm.setEmail(firmDto.getEmail());
            firm.setLocation(location);
            firm.setParent(parentFirm);
            firm.setLogoUrl(firmDto.getLogoUrl());
            firm.setWebsite(firmDto.getWebsite());
            firm.setContactNo(firmDto.getContactNo());
            firm = firmRepository.save(firm);
            FirmDTO firmDTO = FirmMapper.INSTANCE.toFirmDTO(firm);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", firmDTO));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Authentication Error: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(String firmUuid) {
        try {
            Optional<Firm> optionalFirm = firmRepository.findByUuid(UUID.fromString(firmUuid));
            if (optionalFirm.isEmpty()) {
                throw new ExpectationFailedException("Firm Not Found!");
            } else {
                Firm firm = optionalFirm.get();
                firmRepository.delete(firm);
                return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting firm: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<FirmDTO> getAllFirmsForLoggedInUser(
            String search,
            Pageable pageable,
            List<String> sort,
            AuthenticationDetails authenticationDetails) throws BadRequestException {

        Specification<Firm> specification = (root, query, cb) -> {
            String searchPattern = "%" + search.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // Filter by firm
            predicates.add(cb.equal(root.get("firm").get("uuid"), UUID.fromString(authenticationDetails.getOrganizationUuid())));

            // Search
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("contactNo")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern),
                    cb.like(cb.lower(root.get("website")), searchPattern),
                    cb.like(cb.lower(root.get("location")), searchPattern)
            ));

            assert query != null;
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return returnAllAssetsSorted(pageable, sort, specification);
    }

    private Page<FirmDTO> returnAllAssetsSorted(Pageable pageable, List<String> sort, Specification<Firm> specification) throws BadRequestException {
        Page<Firm> firmPage = firmRepository.findAll(specification, sanitizeFirmPageable(pageable));

        if (sort.isEmpty()) {
            return firmPage.map(FirmMapper.INSTANCE::toFirmDTO);
        }

        Page<FirmDTO> firmDTOs = firmPage.map(FirmMapper.INSTANCE::toFirmDTO);

        Comparator<FirmDTO> comparator = (a, b) -> 0;

        if (sort.contains("name")) {
            comparator = Comparator.comparing(FirmDTO::getName, String.CASE_INSENSITIVE_ORDER);
        } else if (sort.contains("contactNo")) {
            comparator = Comparator.comparing(FirmDTO::getContactNo);
        } else if (sort.contains("email")) {
            comparator = Comparator.comparing(FirmDTO::getEmail);
        }else if (sort.contains("website")) {
            comparator = Comparator.comparing(FirmDTO::getWebsite);
        }else if (sort.contains("location")) {
            comparator = Comparator.comparing(
                    FirmDTO::getLocation,
                    (loc1, loc2) -> {
                        if (loc1 == null && loc2 == null) return 0;
                        if (loc1 == null) return -1;
                        if (loc2 == null) return 1;
                        return loc1.getLocationName().compareTo(loc2.getLocationName());
                    }
            );
        } else {
            throw new BadRequestException("Invalid sort parameter!");
        }

        if (sort.contains("DESC")) {
            comparator = comparator.reversed();
        }

        List<FirmDTO> sortedAssets = firmDTOs.stream().sorted(comparator).toList();
        return new PageImpl<>(sortedAssets, pageable, firmDTOs.getTotalElements());
    }

    private Pageable sanitizeFirmPageable(Pageable pageable) {
        Map<String, String> propertyMapping = Map.of(
                "name", "name",
                "email", "email",
                "location", "location",
                "createdDate", "createdDate",
                "website", "website"
        );

        List<Sort.Order> sanitizedOrders = pageable.getSort().stream()
                .map(order -> {
                    String mappedProperty = propertyMapping.get(order.getProperty());
                    if (mappedProperty != null && !mappedProperty.isEmpty()) {
                        return new Sort.Order(order.getDirection(), mappedProperty);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        Sort sanitizedSort = sanitizedOrders.isEmpty() ? Sort.unsorted() : Sort.by(sanitizedOrders);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sanitizedSort);
    }



}
