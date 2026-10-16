package com.clubdeportivo.config;

import com.clubdeportivo.service.FileStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * ========================================================================================
 * CONFIGURACIÓN WEB MVC: WebMvcConfig
 * ========================================================================================
 * Configura los manejadores de recursos estáticos de Spring MVC.
 *
 * Mapeo clave:
 * Permite que las imágenes de rostros almacenadas en el sistema de archivos local
 * (ej: uploads/rostros/uuid.jpg) puedan ser servidas públicamente a las vistas Thymeleaf
 * a través de la URL web "/uploads/rostros/**".
 * ========================================================================================
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileStorageService fileStorageService;

    public WebMvcConfig(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = fileStorageService.getRootLocation();
        String uploadPath = uploadDir.toUri().toString();

        // Mapea la ruta web /uploads/rostros/** hacia la carpeta física en disco
        registry.addResourceHandler("/uploads/rostros/**")
                .addResourceLocations(uploadPath.endsWith("/") ? uploadPath : uploadPath + "/");

        // Recursos estáticos generales (Plantilla Sneat)
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
    }
}
