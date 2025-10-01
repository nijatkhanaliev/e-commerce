package com.company.model.mapper;

import com.company.dao.entity.Product;
import com.company.model.dto.request.ProductRequest;
import com.company.model.dto.response.ProductResponse;
import org.mapstruct.Mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface ProductMapper {

    Product toProduct(ProductRequest request);

    ProductResponse toProductResponse(Product product);
}
