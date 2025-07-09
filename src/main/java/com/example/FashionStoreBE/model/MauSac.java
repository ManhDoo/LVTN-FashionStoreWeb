    package com.example.FashionStoreBE.model;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Entity
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(name = "mau_sac")
    public class MauSac {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int maMau;
        private String tenMau;
        private String maBangMau;
    }
