package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.pet.PetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setName(customerDTO.getName());
        customerEntity.setPhoneNumber(customerDTO.getPhoneNumber());
        // TODO or not? rather not, because we don't support inserting new customer with pet IDs.
        //  User should first create a customer, then add pets for him
        //customerEntity.setPets();
        customerEntity.setNotes(customerDTO.getNotes());
        CustomerEntity resultEntity = customerRepository.save(customerEntity);
        customerDTO.setId(resultEntity.getId());
        return customerDTO;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream().map(this::customerEntityToDto).collect(Collectors.toList());
    }

    public CustomerDTO getOwnerByPet(long petId) {
        Optional<CustomerEntity> optionalCustomerEntity = customerRepository.findByPetsId(petId);
        if (optionalCustomerEntity.isPresent()) {
            return customerEntityToDto(optionalCustomerEntity.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found!");
        }

    }

    private CustomerDTO customerEntityToDto(CustomerEntity customerEntity) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customerEntity.getId());
        customerDTO.setName(customerEntity.getName());
        customerDTO.setPhoneNumber(customerEntity.getPhoneNumber());
        customerDTO.setNotes(customerEntity.getNotes());
        if (customerEntity.getPets() != null) {
            customerDTO.setPetIds(customerEntity.getPets().stream().map(PetEntity::getId).collect(Collectors.toList()));
        }
        return customerDTO;
    }
}
