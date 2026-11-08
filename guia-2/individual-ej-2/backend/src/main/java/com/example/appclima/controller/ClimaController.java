package com.example.appclima.controller;

import com.example.appclima.service.ClimaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ciudad")
public class ClimaController {


  private final ClimaService climaService;

  public ClimaController(ClimaService climaService) {
    this.climaService = climaService;
  }


  @CrossOrigin(origins = "http://localhost:5173/")
  @GetMapping("/clima/{ciudad}")
  public ResponseEntity<String> obtenerClima(@PathVariable String ciudad) {
    return climaService.obtenerClima(ciudad);
  }
}
