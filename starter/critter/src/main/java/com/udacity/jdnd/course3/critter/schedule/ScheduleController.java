package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.PetEntity;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.user.EmployeeEntity;
import com.udacity.jdnd.course3.critter.user.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PetRepository petRepository;

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ScheduleEntity scheduleEntity = scheduleDtoToEntity(scheduleDTO);
        scheduleRepository.save(scheduleEntity);
        return scheduleDTO;
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        return scheduleRepository.findAll().stream().map(this::scheduleEntityToDto).collect(Collectors.toList());
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        return scheduleRepository.findAllByPetsId(petId).stream()
                .map(this::scheduleEntityToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        return scheduleRepository.findAllByEmployeesId(employeeId).stream()
                .map(this::scheduleEntityToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        return scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getPets().stream().anyMatch(pet -> pet.getCustomer().getId() == customerId))
                .map(this::scheduleEntityToDto)
                .collect(Collectors.toList());
    }

    private ScheduleEntity scheduleDtoToEntity(ScheduleDTO scheduleDTO) {
        ScheduleEntity scheduleEntity = new ScheduleEntity();
        scheduleEntity.setActivities(scheduleDTO.getActivities());
        scheduleEntity.setDate(scheduleDTO.getDate());

        List<EmployeeEntity> employeeEntities = scheduleDTO.getEmployeeIds().stream()
                .map(id -> employeeRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        scheduleEntity.setEmployees(employeeEntities);

        List<PetEntity> petEntities = scheduleDTO.getPetIds().stream()
                .map(id -> petRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        scheduleEntity.setPets(petEntities);
        return scheduleEntity;
    }

    private ScheduleDTO scheduleEntityToDto(ScheduleEntity scheduleEntity) {
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setActivities(scheduleEntity.getActivities());
        scheduleDTO.setDate(scheduleEntity.getDate());
        scheduleDTO.setEmployeeIds(scheduleEntity.getEmployees().stream()
                .map(EmployeeEntity::getId)
                .collect(Collectors.toList()));
        scheduleDTO.setPetIds(scheduleEntity.getPets().stream()
                .map(PetEntity::getId)
                .collect(Collectors.toList()));
        return scheduleDTO;
    }
}
