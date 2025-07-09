package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.model.KichCo;
import com.example.FashionStoreBE.repository.SizeRepository;
import com.example.FashionStoreBE.service.SizeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;

    public SizeServiceImpl(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    @Override
    public List<KichCo> getAllSizes() {
        return sizeRepository.findAll();
    }
}
