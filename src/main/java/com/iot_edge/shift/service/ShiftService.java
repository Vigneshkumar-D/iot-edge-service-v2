package com.iot_edge.shift.service;

import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import com.iot_edge.shift.dto.ShiftDto;
import com.iot_edge.shift.entity.Shift;
import com.iot_edge.shift.repository.ShiftRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ExceptionHandlerUtil exceptionHandlerUtil;

    public ResponseEntity<ResponseModel<?>> list() {
        try{
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", shiftRepository.findAll()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving shift details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }
    }

    public ResponseEntity<ResponseModel<?>> add(ShiftDto shiftDto) {
        try{
            Shift shift = modelMapper.map(shiftDto, Shift.class);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", shiftRepository.save(shift)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error adding shift details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> update(Long shiftId, ShiftDto shiftDto) {
        try{
            Shift shift = shiftRepository.findById(shiftId).orElse(null);
            if (shift == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseModel<>(false, "Shift details not found!"));
            }
            modelMapper.map(shiftDto, shift);
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", shiftRepository.save(shift)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error updating shift details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage())));
        }
    }

    public ResponseEntity<ResponseModel<?>> delete(Long shiftId) {
        try {
            if (!shiftRepository.existsById(shiftId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseModel<>(false, "shift not found"));
            }
            shiftRepository.deleteById(shiftId);
            return ResponseEntity.ok(new ResponseModel<>(true, "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error deleting shift: " + e.getMessage()));
        }
    }

}
