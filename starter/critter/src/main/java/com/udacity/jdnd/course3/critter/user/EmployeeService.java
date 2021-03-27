package com.udacity.jdnd.course3.critter.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Transactional
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO) {
        EmployeeEntity employeeEntity = employeeRepository.save(employeeDtoToEntity(employeeDTO));
        employeeDTO.setId(employeeEntity.getId());
        return employeeDTO;
    }

    public EmployeeDTO getEmployee(long employeeId) {
        Optional<EmployeeEntity> entityOptional = employeeRepository.findById(employeeId);
        if (entityOptional.isPresent()) {
            return employeeEntityToDto(entityOptional.get());
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find an employee");
        }
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        Optional<EmployeeEntity> entityOptional = employeeRepository.findById(employeeId);
        if (entityOptional.isPresent()) {
            EmployeeEntity employeeEntity = entityOptional.get();
            employeeEntity.setDaysAvailable(daysAvailable);
            employeeRepository.save(employeeEntity);
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find an employee");
        }
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        DayOfWeek dayOfWeek = employeeDTO.getDate().getDayOfWeek();
        Set<EmployeeSkill> skills = employeeDTO.getSkills();
        return employeeRepository.findByDaysAvailableContaining(dayOfWeek)
                .stream()
                .filter(item -> item.getSkills().containsAll(skills))
                .map(this::employeeEntityToDto)
                .collect(Collectors.toList());
    }

    private EmployeeDTO employeeEntityToDto(EmployeeEntity entity) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSkills(entity.getSkills());
        dto.setDaysAvailable(entity.getDaysAvailable());
        return dto;
    }

    private EmployeeEntity employeeDtoToEntity(EmployeeDTO employeeDTO) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setId(employeeDTO.getId());
        employeeEntity.setName(employeeDTO.getName());
        employeeEntity.setSkills(employeeDTO.getSkills());
        employeeEntity.setDaysAvailable(employeeDTO.getDaysAvailable());
        return employeeEntity;
    }
}
