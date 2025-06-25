package com.iot_edge.managementconsole.service.system;

import com.iot_edge.managementconsole.dto.system.AssetDto;
import com.iot_edge.managementconsole.entity.system.Asset;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.system.AssetRepository;
import com.iot_edge.managementconsole.repository.system.FirmRepository;
import com.iot_edge.managementconsole.repository.system.LocationRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AssetService {

    private final AssetRepository assetRepository;

    private final LocationRepository locationRepository;

    private final FirmRepository firmRepository;

    private final ModelMapper modelMapper;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public AssetService(AssetRepository assetRepository, LocationRepository locationRepository, FirmRepository firmRepository, ModelMapper modelMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.assetRepository = assetRepository;
        this.locationRepository = locationRepository;
        this.firmRepository = firmRepository;
        this.modelMapper = modelMapper;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

//    public ResponseEntity<ResponseModel<?>> add(AssetDto assetDto){
//        try{
//            Asset existingAsset = assetRepository.findByAssetName(assetDto.getAssetName());
//
//            if(existingAsset ==null){
//                Asset asset = new Asset();
//                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//                modelMapper.getConfiguration().setPropertyCondition(conditions -> {
//                    return conditions.getSource() != null;
//                });
//                modelMapper.map(assetDto, asset);
//                Location location = locationRepository.findById(assetDto.getLocation().getId())
//                        .orElseThrow(() -> new RuntimeException("Location not found with ID: " + assetDto.getLocation().getId()));
//
//                asset.setLocation(location);
//                System.out.println("location "+ asset.getLocation().getLocationName());
//                return ResponseEntity.ok(new ResponseModel<>(true, "Success",200, assetRepository.save(asset)));
//            }
//            return ResponseEntity.ok(new ResponseModel<>(true, "Success",200, existingAsset));
//        }catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ResponseModel<>(false, "Error adding Asset: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
//        }
//    }

    public ResponseEntity<ResponseModel<?>> add(AssetDto assetDto) {
        try{
            Asset asset = new Asset();
            asset.setAssetName(assetDto.getAssetName());
            asset.setScriptCode(assetDto.getScriptCode());
            asset.setAssetCategory(assetDto.getAssetCategory());
            asset.setDescription(assetDto.getDescription());
            asset.setClientId(assetDto.getClientId());
            asset.setSubTopicName(assetDto.getSubTopicName());
            asset.setPubTopicName(assetDto.getPubTopicName());
            asset.setActive(assetDto.isActive());
            asset.setParameters(assetDto.getParameters());


//            log.info("Checking location with ID: {}", assetDto.getLocation().getId());

//            Location location = locationRepository.findById(assetDto.getLocation().getId())
//                    .orElseThrow(() -> new RuntimeException("Location not found in DB for ID: " + assetDto.getLocation().getId()));
            Firm firm = firmRepository.findById(assetDto.getFirm().getId())
                    .orElseThrow(() -> new RuntimeException("Firm not found in DB for ID: " + assetDto.getFirm().getId()));
            asset.setFirm(firm);
//            log.info("Location set: {}", location);

            Asset savedAsset = assetRepository.save(asset);
            log.info("Saved Asset: {}", savedAsset);

            return ResponseEntity.ok(new ResponseModel<>(true, "Success", savedAsset));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding Asset: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> list(){
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", assetRepository.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error fetching data: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> update(Integer assetId, AssetDto assetDto){
        try{
            Optional<Asset> assetOptional = assetRepository.findById(assetId);
            if (assetOptional.isEmpty()) {
                throw new RuntimeException("Asset Details not found with id: " + assetId);
            }
            Asset asset = assetOptional.get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            modelMapper.getConfiguration().setPropertyCondition(conditions -> {
                return conditions.getSource() != null;
            });
            modelMapper.map(assetDto, asset);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", assetRepository.save(asset)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Authentication Error: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(Integer assetId) {
        try {
            if (!assetRepository.existsById(assetId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(false, "Asset not found"));
            }
            assetRepository.deleteById(assetId);
            return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting asset: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

}
