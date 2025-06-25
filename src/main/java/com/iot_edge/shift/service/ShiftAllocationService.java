package com.iot_edge.shift.service;

import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import com.iot_edge.shift.dto.ShiftAllocationDto;
import com.iot_edge.shift.entity.ShiftAllocation;
import com.iot_edge.shift.repository.ShiftAllocationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ShiftAllocationService {
    @Autowired
    private ShiftAllocationRepository shiftAllocationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExceptionHandlerUtil exceptionHandlerUtil;

    public ResponseEntity<ResponseModel<?>> list() {
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", shiftAllocationRepository.findAll()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving shift allocation details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> add(ShiftAllocationDto shiftAllocationDto) {
        try{
            ShiftAllocation shiftAllocation = modelMapper.map(shiftAllocationDto, ShiftAllocation.class);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", shiftAllocationRepository.save(shiftAllocation)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding shift allocation details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> update(Long shiftAllocationId, ShiftAllocationDto shiftAllocationDto) {
        try{
            ShiftAllocation shiftAllocation = shiftAllocationRepository.findById(shiftAllocationId).orElse(null);
            if (shiftAllocation == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseModel<>(false, "Shift allocation details not found!"));
            }
            modelMapper.map(shiftAllocationDto, shiftAllocation);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", shiftAllocationRepository.save(shiftAllocation)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error updating shift allocation details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(Long shiftAllocationId) {
        try {
            if (!shiftAllocationRepository.existsById(shiftAllocationId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(false, "shift Allocation not found"));
            }
            shiftAllocationRepository.deleteById(shiftAllocationId);
            return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting shift Allocation: " + e.getMessage()));
        }
    }


}
