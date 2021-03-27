package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.user.CustomerEntity;
import com.udacity.jdnd.course3.critter.user.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Transactional
@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public PetDTO savePet(PetDTO petDTO) {
        PetEntity petEntity = petDtoToEntity(petDTO);
        Optional<CustomerEntity> optionalOwner = customerRepository.findById(petDTO.getOwnerId());
        if (optionalOwner.isPresent()) {
            petEntity.setCustomer(optionalOwner.get());
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Customer with this id was not found");
        }
        PetEntity resultEntity = petRepository.save(petEntity);
        return petEntityToDto(resultEntity);
    }

    public PetDTO getPet(long petId) {
        Optional<PetEntity> petEntity = petRepository.findById(petId);
        if (petEntity.isPresent()) {
            return petEntityToDto(petEntity.get());
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Unable to find a pet");
        }
    }

    public List<PetDTO> getPets() {
        return petRepository.findAll().stream().map(this::petEntityToDto).collect(Collectors.toList());
    }

    public List<PetDTO> getPetsByOwner(long ownerId) {
        return petRepository.findAllByCustomerId(ownerId).stream().map(this::petEntityToDto).collect(Collectors.toList());
    }

    private PetDTO petEntityToDto(PetEntity petEntity) {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(petEntity.getId());
        petDTO.setName(petEntity.getName());
        petDTO.setType(petEntity.getType());
        petDTO.setBirthDate(petEntity.getBirthDate());
        petDTO.setNotes(petEntity.getNotes());
        if (petEntity.getCustomer() != null) {
            petDTO.setOwnerId(petEntity.getCustomer().getId());
        }
        return petDTO;
    }

    private PetEntity petDtoToEntity(PetDTO petDTO) {
        PetEntity petEntity = new PetEntity();
        petEntity.setId(petDTO.getId());
        petEntity.setName(petDTO.getName());
        petEntity.setType(petDTO.getType());
        petEntity.setBirthDate(petDTO.getBirthDate());
        petEntity.setNotes(petDTO.getNotes());
        return petEntity;
    }
}
