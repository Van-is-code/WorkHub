package gr3.workhub.service;

import gr3.workhub.entity.ServiceFeature;
import gr3.workhub.repository.ServiceFeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceFeatureService {

    private final ServiceFeatureRepository serviceFeatureRepository;

    public ServiceFeature createServiceFeature(ServiceFeature serviceFeature) {
        return serviceFeatureRepository.save(serviceFeature);
    }

    public ServiceFeature updateServiceFeature(Integer id, ServiceFeature serviceFeature) {
        ServiceFeature existing = serviceFeatureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ServiceFeature not found"));
        existing.setFeatureName(serviceFeature.getFeatureName());
        existing.setDescription(serviceFeature.getDescription());
        existing.setPostAt(serviceFeature.getPostAt());
        existing.setJobPostLimit(serviceFeature.getJobPostLimit());
        existing.setCvLimit(serviceFeature.getCvLimit());
        return serviceFeatureRepository.save(existing);
    }

    public void deleteServiceFeature(Integer id) {
        serviceFeatureRepository.deleteById(id);
    }

    public List<ServiceFeature> getAllServiceFeatures() {
        return serviceFeatureRepository.findAll();
    }

    public ServiceFeature getServiceFeatureById(Integer id) {
        return serviceFeatureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ServiceFeature not found"));
    }
}