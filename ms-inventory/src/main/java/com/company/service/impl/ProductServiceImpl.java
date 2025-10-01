package com.company.service.impl;

import com.company.dao.entity.Product;
import com.company.dao.repository.ProductRepository;
import com.company.exception.InsufficientStockException;
import com.company.exception.NotFoundException;
import com.company.model.dto.request.ProductRequest;
import com.company.model.dto.response.ProductResponse;
import com.company.model.mapper.ProductMapper;
import com.company.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorCode.IN_SUFFICIENT_STOCK;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;
import static com.company.exception.constant.ErrorMessage.IN_SUFFICIENT_STOCK_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public void addProduct(ProductRequest request) {
        log.info("Adding product, productName {}", request.getName());
        Product product = productMapper.toProduct(request);
        productRepository.save(product);
    }

    @Override
    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse updateStock(Long id, int quantity) {
        log.info("Updating stock, productId {}, quantity {}", id, quantity);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        if (quantity < 0 && product.getStock() < (-quantity)) {

            throw new InsufficientStockException(
                    String.format(IN_SUFFICIENT_STOCK_MESSAGE,
                            quantity, product.getStock(), product.getId()),
                    IN_SUFFICIENT_STOCK
            );

        }

        Integer newStock = product.getStock() + quantity;
        product.setStock(newStock);
        productRepository.save(product);

        return productMapper.toProductResponse(product);
    }

}
