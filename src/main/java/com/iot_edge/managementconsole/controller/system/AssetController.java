package com.iot_edge.managementconsole.controller.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot_edge.common.exceptions.BadRequestException;
import com.iot_edge.common.exceptions.ExpectationFailedException;
import com.iot_edge.managementconsole.dto.request.AssetRequestDTO;
import com.iot_edge.managementconsole.dto.system.AssetDTO;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.service.system.AssetService;
import com.iot_edge.managementconsole.utils.annotations.AuthenticatedUserDetails;
import com.iot_edge.managementconsole.utils.user.AuthenticationDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
public class AssetController {

    private static final Logger log = LoggerFactory.getLogger(AssetController.class);

    private final AssetService assetService;
    private final ObjectMapper objectMapper;

    public AssetController(AssetService assetService, ObjectMapper objectMapper) {
        this.assetService = assetService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<ResponseModel<?>> add(@RequestBody AssetRequestDTO assetRequestDTO) {
        log.info("Received request: {}", assetRequestDTO);
        try {
            return ResponseEntity.ok(assetService.add(assetRequestDTO).getBody());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseModel.error("Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<String> getAllAssetsPaged(
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
            return ResponseEntity.ok(objectMapper.writeValueAsString(assetService.getAllAssetsForLoggedInUser(search, pageable, sort, authenticationDetails)));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{assetUuid}")
    public ResponseEntity<ResponseModel<?>> update(@PathVariable("assetUuid") String assetUuid, @RequestBody AssetRequestDTO assetRequestDTO){
        try {
            return  assetService.update(assetUuid, assetRequestDTO);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{assetUuid}")
    @PreAuthorize("hasRole('SuperAdmin') or hasRole('SuperAdmin')")
    public ResponseEntity<ResponseModel<?>> delete(@PathVariable("assetUuid") String assetUuid) throws ExpectationFailedException {
        try {
            return assetService.delete(assetUuid);
        } catch (Exception e) {
            ResponseModel<?> response = new ResponseModel<>(false, "Something went wrong: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
