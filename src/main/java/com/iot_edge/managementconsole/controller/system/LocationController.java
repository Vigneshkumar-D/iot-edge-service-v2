package com.iot_edge.managementconsole.controller.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.managementconsole.dto.request.LocationRequestDTO;
import com.iot_edge.managementconsole.dto.system.LocationDTO;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.service.system.LocationService;
import com.iot_edge.managementconsole.utils.annotations.AuthenticatedUserDetails;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {

    private final LocationService locationService;

    private final ObjectMapper objectMapper;

    public LocationController(LocationService locationService, ObjectMapper objectMapper) {
        this.locationService = locationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("get-all-locations-paged")
    public ResponseEntity<String> getAllLocationsForLoggedInUser(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "false") boolean desc,
            @RequestParam(required = false, defaultValue = "createdDate") List<String> sort,
            @AuthenticatedUserDetails AuthenticationDetails authenticationDetails) {
        try {
            Sort _sort;
            Sort.Direction _direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable;
            if (sort.contains("createdDate")) {
                _sort = Sort.by(Sort.Direction.DESC, "createdDate");
                sort.add("DESC");
                pageable = PageRequest.of(page, size, _sort);
            } else {
                _sort = Sort.by(_direction, sort.getFirst());
                sort.add(_direction.isAscending() ? "ASC" : "DESC");
                pageable = PageRequest.of(page, size, _sort);
            }
            return ResponseEntity.ok(objectMapper.writeValueAsString(locationService.getAllLocationsForLoggedInUser(search, pageable, sort, authenticationDetails)));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ResponseModel<?>> add(@RequestBody LocationRequestDTO locationRequestDTO) {
        try {
            return locationService.add(locationRequestDTO);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseModel<?>> list() {
        return locationService.list();
    }

    @PutMapping("/{locationUuid}")
    public ResponseEntity<ResponseModel<?>> update(@PathVariable("locationUuid") String locationUuid, @RequestBody LocationDTO locationDTO){
        try {
            return locationService.update(locationUuid, locationDTO);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{locationUuid}")
    public ResponseEntity<ResponseModel<?>> delete(@PathVariable("locationUuid") String locationUuid) {
        try {
            return locationService.delete(locationUuid);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
