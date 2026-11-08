package com.example.spring_api;

import com.example.spring_api.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts(){
        return productRepository.findAll();
    }

    public Product getProduct(Long id){
        return productRepository.findById(id).orElse(null);
    }

    public Product addProduct(Product product){
        return productRepository.save(product);
    }

    public  Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }
}
