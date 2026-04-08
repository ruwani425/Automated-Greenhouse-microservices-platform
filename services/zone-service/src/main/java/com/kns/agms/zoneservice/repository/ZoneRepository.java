package com.kns.agms.zoneservice.repository;

import com.kns.agms.zoneservice.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
}