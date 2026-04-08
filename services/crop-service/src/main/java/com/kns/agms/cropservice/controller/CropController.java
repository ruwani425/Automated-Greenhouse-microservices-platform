package com.kns.agms.cropservice.controller;
import com.kns.agms.cropservice.entity.Crop;
import com.kns.agms.cropservice.enums.CropStatus;
import com.kns.agms.cropservice.repository.CropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
public class CropController {

    @Autowired
    private CropRepository cropRepository;

    @PostMapping
    public ResponseEntity<Crop> registerCrop(@RequestBody Crop crop) {
        crop.setStatus(CropStatus.SEEDLING);
        return ResponseEntity.ok(cropRepository.save(crop));
    }

    @GetMapping
    public ResponseEntity<List<Crop>> getInventory() {
        return ResponseEntity.ok(cropRepository.findAll());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Crop> updateStatus(@PathVariable Long id, @RequestParam CropStatus status) {
        Crop crop = cropRepository.findById(id).orElseThrow(() -> new RuntimeException("Crop not found"));
        crop.setStatus(status);
        return ResponseEntity.ok(cropRepository.save(crop));
    }
}
