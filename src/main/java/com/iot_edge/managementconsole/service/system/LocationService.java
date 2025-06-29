package com.iot_edge.managementconsole.service.system;

import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.common.exceptions.ExpectationFailedException;
import com.iot_edge.managementconsole.dto.request.LocationRequestDTO;
import com.iot_edge.managementconsole.dto.system.FirmDTO;
import com.iot_edge.managementconsole.dto.system.IoTGatewayDTO;
import com.iot_edge.managementconsole.dto.system.LocationDTO;
import com.iot_edge.managementconsole.entity.system.Firm;
import com.iot_edge.managementconsole.entity.system.IoTGateway;
import com.iot_edge.managementconsole.entity.system.Location;
import com.iot_edge.managementconsole.mapper.IoTGatewayMapper;
import com.iot_edge.managementconsole.mapper.LocationMapper;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.repository.system.LocationRepository;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import jakarta.persistence.criteria.Predicate;
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
public class LocationService {

    private final LocationRepository locationRepository;

    private final ExceptionHandlerUtil exceptionHandlerUtil;

    public LocationService(LocationRepository locationRepository, ExceptionHandlerUtil exceptionHandlerUtil) {
        this.locationRepository = locationRepository;
        this.exceptionHandlerUtil = exceptionHandlerUtil;
    }

    public ResponseEntity<ResponseModel<?>> add(LocationRequestDTO locationRequestDTO){
        try{
                Location location = Location.builder()
                        .locationName(locationRequestDTO.getLocationName())
                        .category(locationRequestDTO.getLocationName())
                        .latitude(locationRequestDTO.getLocationName())
                        .longitude(locationRequestDTO.getLocationName())
                        .parent(LocationMapper.INSTANCE.toLocation(locationRequestDTO.getParent()))
                        .build();
                location = locationRepository.save(location);
                LocationDTO locationDTO = LocationMapper.INSTANCE.toLocationDTO(location);
                return ResponseEntity.ok(new ResponseModel<>(true, "Success", locationDTO));
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

    public ResponseEntity<ResponseModel<?>> update(String locationUuid, LocationDTO locationDto){
        try{
            Optional<Location> locationOptional = locationRepository.findByUuid(UUID.fromString(locationUuid));
            if (locationOptional.isEmpty()) {
                throw new RuntimeException("Location Details not found with id: " + locationUuid);
            }
            Location location = locationOptional.get();
            location.setLocationName(locationDto.getLocationName());
            location.setCategory(locationDto.getLocationName());
            location.setLatitude(locationDto.getLocationName());
            location.setLongitude(locationDto.getLocationName());
            location.setParent(LocationMapper.INSTANCE.toLocation(locationDto.getParent()));
            location = locationRepository.save(location);
            LocationDTO locationDTO = LocationMapper.INSTANCE.toLocationDTO(location);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", locationDTO));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error Updating Location: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(String locationUuIid) {
        try {
            Optional<Location> optionalLocation = locationRepository.findByUuid(UUID.fromString(locationUuIid));
            if (optionalLocation.isEmpty()) {
                throw new ExpectationFailedException("Location Not Found!");
            } else {
                Location location = optionalLocation.get();
                locationRepository.delete(location);
                return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting location: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<LocationDTO> getAllLocationsForLoggedInUser(
            String search,
            Pageable pageable,
            List<String> sort,
            AuthenticationDetails authenticationDetails) throws BadRequestException {

        Specification<Location> specification = (root, query, cb) -> {
            String searchPattern = "%" + search.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();

            // Filter by firm
            predicates.add(cb.equal(root.get("firm").get("uuid"), UUID.fromString(authenticationDetails.getOrganizationUuid())));

            // Search
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("locationName")), searchPattern),
                    cb.like(cb.lower(root.get("category")), searchPattern),
                    cb.like(cb.lower(root.get("latitude")), searchPattern),
                    cb.like(cb.lower(root.get("longitude")), searchPattern),
                    cb.like(cb.lower(root.get("createdDate")), searchPattern),
                    cb.like(cb.lower(root.get("parent")), searchPattern)
            ));

            assert query != null;
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return returnAllLocationsSorted(pageable, sort, specification);
    }

    private Page<LocationDTO> returnAllLocationsSorted(Pageable pageable, List<String> sort, Specification<Location> specification) throws BadRequestException {
        Page<Location> locationsPage = locationRepository.findAll(specification, sanitizeFirmPageable(pageable));

        if (sort.isEmpty()) {
            return locationsPage.map(LocationMapper.INSTANCE::toLocationDTO);
        }

        Page<LocationDTO> locationDTOS = locationsPage.map(LocationMapper.INSTANCE::toLocationDTO);

        Comparator<LocationDTO> comparator = (a, b) -> 0;

        if (sort.contains("locationName")) {
            comparator = Comparator.comparing(LocationDTO::getLocationName, String.CASE_INSENSITIVE_ORDER);
        } else if (sort.contains("parent")) {
            comparator = Comparator.comparing(
                    LocationDTO::getParent,
                    (loc1, loc2) -> {
                        if (loc1 == null && loc2 == null) return 0;
                        if (loc1 == null) return -1;
                        if (loc2 == null) return 1;
                        return loc1.getLocationName().compareTo(loc2.getLocationName());
                    }
            );
        } else if (sort.contains("category")) {
            comparator = Comparator.comparing(LocationDTO::getCategory, String.CASE_INSENSITIVE_ORDER);
        }else if (sort.contains("latitude")) {
            comparator = Comparator.comparing(LocationDTO::getLongitude);
        }else if(sort.contains("longitude")){
            comparator = Comparator.comparing(LocationDTO::getLongitude);
        } else if (sort.contains("createdDate")) {
            comparator = Comparator.comparing(LocationDTO::getCreatedDate);
        } else {
            throw new BadRequestException("Invalid sort parameter!");
        }

        if (sort.contains("DESC")) {
            comparator = comparator.reversed();
        }

        List<LocationDTO> sortedIotGateways = locationDTOS.stream().sorted(comparator).toList();
        return new PageImpl<>(sortedIotGateways, pageable, locationDTOS.getTotalElements());
    }

    private Pageable sanitizeFirmPageable(Pageable pageable) {
        Map<String, String> propertyMapping = Map.of(
                "locationName", "locationName",
                "parent", "parent",
                "category", "category",
                "latitude", "latitude",
                "longitude", "longitude",
                "createdDate", "createdDate"
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
