package com.iot_edge.managementconsole.service.system;

import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.common.exceptions.ExpectationFailedException;
import com.iot_edge.managementconsole.dto.request.AssetRequestDTO;
import com.iot_edge.managementconsole.dto.system.AssetDTO;
import com.iot_edge.managementconsole.entity.system.Asset;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.mapper.AssetMapper;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.system.AssetRepository;
import com.iot_edge.managementconsole.repository.system.FirmRepository;
import com.iot_edge.managementconsole.repository.system.LocationRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;

    private final LocationRepository locationRepository;

    private final FirmRepository firmRepository;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public AssetService(AssetRepository assetRepository, LocationRepository locationRepository, FirmRepository firmRepository, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.assetRepository = assetRepository;
        this.locationRepository = locationRepository;
        this.firmRepository = firmRepository;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ResponseEntity<ResponseModel<?>> add(AssetRequestDTO assetRequestDTO) {
        try{
            Asset asset = Asset.builder()
                    .assetName(assetRequestDTO.getAssetName())
                    .scriptCode(assetRequestDTO.getScriptCode())
                    .assetCategory(assetRequestDTO.getAssetCategory())
                    .description(assetRequestDTO.getDescription())
                    .clientId(assetRequestDTO.getClientId())
                    .subTopicName(assetRequestDTO.getSubTopicName())
                    .pubTopicName(assetRequestDTO.getPubTopicName())
                    .isActive(assetRequestDTO.isActive())
                    .parameters(assetRequestDTO.getParameters())
                    .build();

            Firm firm = firmRepository.findById(assetRequestDTO.getFirm().getId())
                    .orElseThrow(() -> new RuntimeException("Firm not found in DB for ID: " + assetRequestDTO.getFirm().getId()));
            asset.setFirm(firm);
            Asset savedAsset = assetRepository.save(asset);
            log.info("Saved Asset: {}", savedAsset);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", savedAsset));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding Asset: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<AssetDTO> getAllAssetsForLoggedInUser(
            String search,
            Pageable pageable,
            List<String> sort,
            AuthenticationDetails authenticationDetails) throws BadRequestException {

        Specification<Asset> specification = (root, query, cb) -> {
            String searchPattern = "%" + search.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // Filter by firm
            predicates.add(cb.equal(root.get("firm").get("uuid"), UUID.fromString(authenticationDetails.getOrganizationUuid())));

            // Search
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("assetName")), searchPattern),
                    cb.like(cb.lower(root.get("description")), searchPattern),
                    cb.like(cb.lower(root.get("clientId")), searchPattern),
                    cb.like(cb.lower(root.get("subTopicName")), searchPattern),
                    cb.like(cb.lower(root.get("pubTopicName")), searchPattern)
            ));

            assert query != null;
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return returnAllAssetsSorted(pageable, sort, specification);
    }

    private Page<AssetDTO> returnAllAssetsSorted(Pageable pageable, List<String> sort, Specification<Asset> specification) throws BadRequestException {
        Page<Asset> assetPage = assetRepository.findAll(specification, sanitizeAssetPageable(pageable));

        if (sort.isEmpty()) {
            return assetPage.map(AssetMapper.INSTANCE::toAssetDTO);
        }

        Page<AssetDTO> assetDTOs = assetPage.map(AssetMapper.INSTANCE::toAssetDTO);

        Comparator<AssetDTO> comparator = (a, b) -> 0;

        if (sort.contains("assetName")) {
            comparator = Comparator.comparing(AssetDTO::getAssetName, String.CASE_INSENSITIVE_ORDER);
        } else if (sort.contains("assetCategory")) {
            comparator = Comparator.comparing(a -> a.getAssetCategory().name());
        } else if (sort.contains("createdDate")) {
            comparator = Comparator.comparing(AssetDTO::getCreatedDate);
        } else {
            throw new BadRequestException("Invalid sort parameter!");
        }

        if (sort.contains("DESC")) {
            comparator = comparator.reversed();
        }

        List<AssetDTO> sortedAssets = assetDTOs.stream().sorted(comparator).toList();
        return new PageImpl<>(sortedAssets, pageable, assetDTOs.getTotalElements());
    }

    private Pageable sanitizeAssetPageable(Pageable pageable) {
        Map<String, String> propertyMapping = Map.of(
                "name", "assetName",
                "firm", "firm",
                "category", "assetCategory",
                "createdDate", "createdDate",
                "isActive", "isActive"
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

    public ResponseEntity<ResponseModel<?>> update(String assetUuid, AssetRequestDTO assetRequestDTO){
        try{
            Optional<Asset> assetOptional = assetRepository.findByUuid(UUID.fromString(assetUuid));
            if (assetOptional.isEmpty()) {
                throw new RuntimeException("Asset Details not found with id: " + assetUuid);
            }
            Asset asset = assetOptional.get();
            asset.setAssetName(assetRequestDTO.getAssetName());
            asset.setAssetCategory(assetRequestDTO.getAssetCategory());
            asset.setDescription(assetRequestDTO.getDescription());
            asset.setParameters(assetRequestDTO.getParameters());
            asset.setActive(assetRequestDTO.isActive());
            asset.setClientId(assetRequestDTO.getClientId());
            assetRequestDTO.setFirm(assetRequestDTO.getFirm());
            asset.setPubTopicName(assetRequestDTO.getPubTopicName());
            assetRequestDTO.setSubTopicName(assetRequestDTO.getSubTopicName());
            asset = assetRepository.save(asset);
            AssetDTO assetDTO = AssetMapper.INSTANCE.toAssetDTO(asset);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", assetDTO));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Authentication Error: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(String assetUuid) throws ExpectationFailedException {
        try {
            Optional<Asset> optionalAsset = assetRepository.findByUuid(UUID.fromString(assetUuid));
            if (optionalAsset.isEmpty()) {
                throw new ExpectationFailedException("Asset Not Found!");
            } else {
                Asset asset = optionalAsset.get();
                assetRepository.delete(asset);
                return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting asset: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

}
