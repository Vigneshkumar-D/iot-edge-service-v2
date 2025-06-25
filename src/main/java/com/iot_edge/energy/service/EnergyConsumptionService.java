package com.iot_edge.energy.service;

import com.iot_edge.energy.dto.EnergyConsumptionDto;
import com.iot_edge.energy.entity.EnergyConsumption;
import com.iot_edge.energy.repository.EnergyConsumptionRepository;
import com.iot_edge.energy.specification.EnergyConsumptionSpecification;
import com.iot_edge.managementconsole.model.user.ResponseModel;
import com.iot_edge.managementconsole.utils.ExceptionHandler.ExceptionHandlerUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class EnergyConsumptionService {

    @Autowired
    private EnergyConsumptionRepository energyConsumptionRepository;

    @Autowired
    private ExceptionHandlerUtil exceptionHandlerUtil;

    @Autowired
    private ModelMapper modelMapper;

//    public void calculateEnergyConsumption(EnergyConsumptionDto energyConsumptionDto){
//        System.out.println("Hit Energy consumption");
//        Double existingCumulative = 0.0d;
//        Double existingMeterReading = 0.0d;
//        Double existingMonthlyConsumption = 0.0d;
//        Instant date = energyConsumptionDto.getTimestamp();
//        LocalDate localDate = date.atZone(ZoneId.systemDefault()).toLocalDate();
//        Instant startOfDay = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
//        Instant endOfDay = localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
//        EnergyConsumption energyConsumption = energyConsumptionRepository.findByAssetAndTimestampBetween(energyConsumptionDto.getAsset(), startOfDay, endOfDay);
//
//        if(energyConsumption==null){
//            EnergyConsumption newEnergyConsumption = new EnergyConsumption();
//            newEnergyConsumption.setMonthlyConsumption(energyConsumptionDto.getMeterReading());
//            newEnergyConsumption.setTodayConsumption(energyConsumptionDto.getMeterReading());
//            newEnergyConsumption.setCumulative(energyConsumptionDto.getMeterReading());
//            newEnergyConsumption.setCurrent(energyConsumptionDto.getCurrent());
//            newEnergyConsumption.setFrequency(energyConsumptionDto.getFrequency());
//            newEnergyConsumption.setPowerFactor(energyConsumptionDto.getPowerFactor());
//            newEnergyConsumption.setMeterReading(energyConsumptionDto.getMeterReading());
//            newEnergyConsumption.setTimestamp(energyConsumptionDto.getTimestamp());
//            newEnergyConsumption.setAsset(energyConsumptionDto.getAsset());
//            energyConsumptionRepository.save(newEnergyConsumption);
//
//        }else {
//
//            if(energyConsumption.getMeterReading() != null){
//                existingMeterReading = energyConsumption.getMeterReading();
//            }if(energyConsumption.getCumulative()!=null){
//                existingCumulative = energyConsumption.getCumulative();
//            }if(energyConsumption.getMonthlyConsumption() != null){
//                existingMonthlyConsumption = energyConsumption.getMonthlyConsumption();
//            }
//
//            Double todayConsumption = existingCumulative - existingMeterReading;
//            Double monthlyConsumption = existingMonthlyConsumption + todayConsumption;
//            Double cumulativeConsumption = existingCumulative + todayConsumption;
//
//            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//            modelMapper.getConfiguration().setPropertyCondition(conditions -> {
//                return conditions.getSource() != null;
//            });
//            modelMapper.map(energyConsumptionDto, energyConsumption);
//
//            energyConsumption.setTodayConsumption(todayConsumption);
//            energyConsumption.setMonthlyConsumption(monthlyConsumption);
//            energyConsumption.setCumulative(cumulativeConsumption);
//            System.out.println("saved data: "+ energyConsumption);
//            energyConsumptionRepository.save(energyConsumption);
//        }
//
//    }

    public void calculateEnergyConsumption(EnergyConsumptionDto energyConsumptionDto) {
        System.out.println("Hit Energy consumption");

        Instant timestamp = energyConsumptionDto.getTimestamp();
        LocalDate localDate = timestamp.atZone(ZoneId.systemDefault()).toLocalDate();
        Instant startOfDay = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endOfDay = localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        EnergyConsumption energyConsumption = energyConsumptionRepository.findByAssetAndTimestampBetween(
                energyConsumptionDto.getAsset(), startOfDay, endOfDay);

        if (energyConsumption == null) {
            energyConsumption = createNewEnergyConsumption(energyConsumptionDto);
        } else {
            updateExistingEnergyConsumption(energyConsumption, energyConsumptionDto);
        }

        energyConsumptionRepository.save(energyConsumption);
    }

    private EnergyConsumption createNewEnergyConsumption(EnergyConsumptionDto dto) {
        return EnergyConsumption.builder()
                .monthlyConsumption(dto.getMeterReading())
                .todayConsumption(dto.getMeterReading())
                .cumulative(dto.getMeterReading())
                .current(dto.getCurrent())
                .frequency(dto.getFrequency())
                .powerFactor(dto.getPowerFactor())
                .meterReading(dto.getMeterReading())
                .timestamp(dto.getTimestamp())
                .asset(dto.getAsset())
                .build();
    }

    private void updateExistingEnergyConsumption(EnergyConsumption existing, EnergyConsumptionDto dto) {
        Double existingMeterReading = Optional.ofNullable(existing.getMeterReading()).orElse(0.0);
        Double existingCumulative = Optional.ofNullable(existing.getCumulative()).orElse(0.0);
        Double existingMonthlyConsumption = Optional.ofNullable(existing.getMonthlyConsumption()).orElse(0.0);

        Double todayConsumption = dto.getMeterReading() - existingMeterReading;
        Double monthlyConsumption = existingMonthlyConsumption + todayConsumption;
        Double cumulativeConsumption = existingCumulative + todayConsumption;

        modelMapper.map(dto, existing);
        existing.setTodayConsumption(todayConsumption);
        existing.setMonthlyConsumption(monthlyConsumption);
        existing.setCumulative(cumulativeConsumption);
    }

    public ResponseEntity<ResponseModel<?>> list(Integer assetId, Instant start, Instant end,
                                                 Double minPowerFactor, Double maxPowerFactor) {

        try{
            Specification<EnergyConsumption> specification =
                    EnergyConsumptionSpecification.filterByAssetId(assetId)
                            .and(EnergyConsumptionSpecification.filterByDateRange(start, end))
                            .and(EnergyConsumptionSpecification.filterByPowerFactor(minPowerFactor, maxPowerFactor));
            return ResponseEntity.ok(new ResponseModel<>(true, "Success", energyConsumptionRepository.findAll(specification)));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(false, "Error retrieving Energy Consumption details: " + exceptionHandlerUtil.sanitizeErrorMessage(e.getMessage()), 500));
        }

    }
}
