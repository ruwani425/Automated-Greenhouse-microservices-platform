package com.kns.agms.zoneservice.controller;



import com.kns.agms.zoneservice.entity.Zone;
import com.kns.agms.zoneservice.repository.ZoneRepository;
import com.kns.agms.zoneservice.service.ExternalApiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")

public class ZoneController {
    private final ZoneRepository zoneRepository;
    private final ExternalApiService externalApiService;

    public ZoneController(ZoneRepository zoneRepository, ExternalApiService externalApiService) {
        this.zoneRepository = zoneRepository;
        this.externalApiService = externalApiService;
    }


    @PostMapping
    public Zone createZone(@RequestBody Zone zone) {
        if (zone.getMinTemperature() >= zone.getMaxTemperature()) {
            throw new IllegalArgumentException("minTemperature must be less than maxTemperature");
        }
        String deviceId = externalApiService.registerDevice(zone.getName());
        zone.setDeviceId(deviceId);
        return zoneRepository.save(zone);
    }


    @GetMapping
    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }


    @GetMapping("/{id}")
    public Zone getZoneById(@PathVariable Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
    }


    @PutMapping("/{id}")
    public Zone updateZone(@PathVariable Long id, @RequestBody Zone updatedZone) {
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        zone.setName(updatedZone.getName());
        zone.setMinTemperature(updatedZone.getMinTemperature());
        zone.setMaxTemperature(updatedZone.getMaxTemperature());
        zone.setMinHumidity(updatedZone.getMinHumidity());
        zone.setMaxHumidity(updatedZone.getMaxHumidity());

        return zoneRepository.save(zone);
    }


    @DeleteMapping("/{id}")
    public String deleteZone(@PathVariable Long id) {
        zoneRepository.deleteById(id);
        return "Zone deleted successfully";
    }
}
