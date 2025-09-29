package com.company.service;

import com.company.model.dto.request.ProductRequest;
import com.company.model.dto.response.ProductResponse;

public interface ProductService {

    void addProduct(ProductRequest request);

    ProductResponse getProduct(Long id);

    ProductResponse updateStock(Long id, int quantity);
}
