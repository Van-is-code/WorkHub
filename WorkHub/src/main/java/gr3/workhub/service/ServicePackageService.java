package gr3.workhub.service;

import gr3.workhub.entity.ServicePackage;
import gr3.workhub.repository.ServicePackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicePackageService {

    private final ServicePackageRepository servicePackageRepository;

    public ServicePackage createServicePackage(ServicePackage servicePackage) {
        return servicePackageRepository.save(servicePackage);
    }

    public ServicePackage updateServicePackage(Integer id, ServicePackage servicePackage) {
        ServicePackage existing = servicePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));
        existing.setName(servicePackage.getName());
        existing.setPrice(servicePackage.getPrice());
        existing.setDuration(servicePackage.getDuration());
        existing.setDescription(servicePackage.getDescription());
        existing.setStatus(servicePackage.getStatus());
        return servicePackageRepository.save(existing);
    }

    public void deleteServicePackage(Integer id) {
        servicePackageRepository.deleteById(id);
    }

    public List<ServicePackage> getAllServicePackages() {
        return servicePackageRepository.findAll();
    }

    public ServicePackage getServicePackageById(Integer id) {
        return servicePackageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ServicePackage not found"));
    }
}