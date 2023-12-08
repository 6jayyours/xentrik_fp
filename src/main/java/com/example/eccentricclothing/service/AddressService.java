package com.example.eccentricclothing.service;

import com.example.eccentricclothing.model.Address;
import com.example.eccentricclothing.model.User;
import com.example.eccentricclothing.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveAddress(Address address, User user)
    {
        address.setUser(user);
        user.getAddresses().add(address);
        addressRepository.save(address);
    }

    public List<Address> getAddressesByUser(User user) {
        return addressRepository.findByUser(user);
    }

    public void removeAddressById(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    public Optional<Address> getAddressById(Long addressId) {
        return addressRepository.findById(addressId);
    }

    public void updateAddress(Address updatedAddress) {
        // Logic to update the address

        // Check if the address already exists in the database
        Optional<Address> existingAddressOptional = addressRepository.findById(updatedAddress.getId());

        if (existingAddressOptional.isPresent()) {

            Address existingAddress = existingAddressOptional.get();


            existingAddress.setName(updatedAddress.getName());
            existingAddress.setMobile(updatedAddress.getMobile());
            existingAddress.setHousename(updatedAddress.getHousename());
            existingAddress.setStreet(updatedAddress.getStreet());
            existingAddress.setCity(updatedAddress.getCity());
            existingAddress.setState(updatedAddress.getState());
            existingAddress.setLandmark(updatedAddress.getLandmark());
            existingAddress.setPincode(updatedAddress.getPincode());

            addressRepository.save(existingAddress);
        } else {

        }
    }
}
