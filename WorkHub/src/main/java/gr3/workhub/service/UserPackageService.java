package gr3.workhub.service;

import gr3.workhub.entity.User;
import gr3.workhub.entity.UserPackage;
import gr3.workhub.repository.UserPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPackageService {

    private final UserPackageRepository userPackageRepository;

    public UserPackage createUserPackage(UserPackage userPackage) {
        return userPackageRepository.save(userPackage);
    }

    public UserPackage createUserPackageByUserId(Integer userId, UserPackage userPackage) {
        userPackage.setUser(new User(userId)); // Assuming the `User` entity has a constructor with `id`
        return userPackageRepository.save(userPackage);
    }

    public UserPackage updateUserPackage(Integer id, UserPackage userPackage) {
        UserPackage existing = userPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserPackage not found"));
        existing.setRenewalDate(userPackage.getRenewalDate());
        existing.setExpirationDate(userPackage.getExpirationDate());
        existing.setPrice(userPackage.getPrice());
        existing.setStatus(userPackage.getStatus());
        existing.setDescription(userPackage.getDescription());
        return userPackageRepository.save(existing);
    }

    public void deleteUserPackage(Integer id) {
        userPackageRepository.deleteById(id);
    }

    public List<UserPackage> getUserPackagesByUserId(Integer userId) {
        return userPackageRepository.findByUserId(userId);
    }

    public List<UserPackage> getAllUserPackages() {
        return userPackageRepository.findAll();
    }

    public UserPackage getUserPackageById(Integer id) {
        return userPackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserPackage not found"));
    }
}