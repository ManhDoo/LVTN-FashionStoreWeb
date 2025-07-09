package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.KichCo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepository extends JpaRepository<KichCo, Integer> {
}
