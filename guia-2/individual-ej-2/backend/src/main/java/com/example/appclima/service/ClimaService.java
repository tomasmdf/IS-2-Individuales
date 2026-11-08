package com.example.appclima.service;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ClimaService  {

  public ResponseEntity<String> obtenerClima(String ciudad) {
    try {
      String apiKey = "216cef17e42e5e274d70755c5de6ebc8";
      String url = "https://api.openweathermap.org/data/2.5/weather?q=" + ciudad + "&appid=" + apiKey;

      RestTemplate restTemplate = new RestTemplate();
      String clima = restTemplate.getForObject(url, String.class);
      return ResponseEntity.ok(clima);
      
    } catch (HttpClientErrorException.Unauthorized e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Error de autenticación: claves de API incorrectas\"}");
    } catch (HttpClientErrorException.Forbidden e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\": \"Error de autorización: acceso prohibido\"}");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error interno del servidor\"}");
    }
  }
}
