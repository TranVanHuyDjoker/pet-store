package com.hivetech.utils.constants;

public class PromotionCodeMessage {
    public static final String NOT_FOUND = "Không tìm thấy mã giảm giá này";
    public static final String  DATE_BAD_REQUEST = "Ngày hết hạn mã phải lớn hơn ngày tạo";
    public static final String EXPIRED_CODE = "Mã giảm giá này đã hết hạn sử dụng";
    public static final String QUANTITY_IS_OVER = "Mã giảm giá này đã hết lượt sử dụng";
    public static final String CAN_NOT_APPLY = "Mã giảm giá này không thể áp dụng cho đơn hàng này";
    public static final String PRICE_DELIVERY_CODE = "Mã giảm giá vận chuyển không thể lớn hơn 1.5$";
    public static final String PERCENT_PRICE = "Mã giảm giá theo phần trăm không thể vượt quá 100";
}
