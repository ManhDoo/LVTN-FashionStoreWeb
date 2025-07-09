package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<DanhMuc, Integer> {
    List<DanhMuc> findByPhai(String phai);
}
