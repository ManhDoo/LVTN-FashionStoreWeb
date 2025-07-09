package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.model.MauSac;
import com.example.FashionStoreBE.repository.ColorRepository;
import com.example.FashionStoreBE.service.ColorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;

    public ColorServiceImpl(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Override
    public List<MauSac> getAllColors() {
        return colorRepository.findAll();
    }
}
