package com.example.FashionStoreBE.config;

public class VNPayConfig {
    public static final String VNP_TMN_CODE = "RFHY9H6E"; // Provided by VNPay
    public static final String VNP_HASH_SECRET = "J7AT93QRG2CKM07V7BWBVRI2PBL9XBE9"; // Provided by VNPay
    public static final String VNP_URL = "http://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String VNP_RETURN_URL = "https://db6633053e94.ngrok-free.app/api/vnpay/return";
    public static final String VNP_VERSION = "2.1.0";
    public static final String VNP_COMMAND = "pay";
    public static final String VNP_ORDER_TYPE = "billpayment";
}