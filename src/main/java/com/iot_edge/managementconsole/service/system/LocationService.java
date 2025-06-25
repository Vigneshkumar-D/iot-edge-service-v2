package com.iot_edge.managementconsole.service.system;

import com.iot_edge.managementconsole.dto.system.LocationDto;
import com.iot_edge.managementconsole.entity.system.Location;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.system.LocationRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LocationService {


    private final LocationRepository locationRepository;

    private final ModelMapper modelMapper;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public LocationService(LocationRepository locationRepository, ModelMapper modelMapper, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.locationRepository = locationRepository;
        this.modelMapper = modelMapper;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ResponseEntity<ResponseModel<?>> add(LocationDto locationDto){
        try{
                Location location = new Location();
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                modelMapper.getConfiguration().setPropertyCondition(conditions -> {
                    return conditions.getSource() != null;
                });
                modelMapper.map(locationDto, location);
                return ResponseEntity.ok(new ResponseModel<>(true, "Success", locationRepository.save(location)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding location: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> list(){
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", locationRepository.findAll()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error fetching data: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> update(Integer locationId, LocationDto locationDto){
        try{
            Optional<Location> locationOptional = locationRepository.findById(locationId);
            if (locationOptional.isEmpty()) {
                throw new RuntimeException("Firm Details not found with id: " + locationId);
            }
            Location location = locationOptional.get();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            modelMapper.getConfiguration().setPropertyCondition(conditions -> {
                return conditions.getSource() != null;
            });
            modelMapper.map(locationDto, location);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", locationRepository.save(location)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Authentication Error: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(Integer locationId) {
        try {
            if (!locationRepository.existsById(locationId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(false, "Firm not found", 404));
            }
            locationRepository.deleteById(locationId);
            return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting location: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }
}
