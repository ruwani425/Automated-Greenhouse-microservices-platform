package com.kns.agms.cropservice.repository;

import com.kns.agms.cropservice.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByZoneId(String zoneId);
}
