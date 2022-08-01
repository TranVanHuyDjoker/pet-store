package com.hivetech.utils.constants;

public class MailConstants {
    public static final class SEND_MAIL_SUBJECT {
        public static final String ClIENT_REGISTER = "XÁC NHẬN TẠO MỚI THÔNG TIN NGƯỜI DÙNG";
        public static final String ClIENT_FORGOT = "XÁC NHẬN ĐỔI MẬT KHẨU NGƯỜI DÙNG";
        public static final String ClIENT_ORDER = "XÁC NHẬN ĐƠN HÀNG NGƯỜI DÙNG";
        public static final String ClIENT_ORDER_DETAILS = "THÔNG TIN ĐƠN HÀNG NGƯỜI DÙNG";
    }

    public static final class TEMPLATE_FILE_NAME{
        public static final String ClIENT_REGISTER = "mail/verify-register";
        public static final String ClIENT_FORGOT = "mail/forgot";
        public static final String CLIENT_ORDER = "mail/verify-order";
        public static final String CLIENT_ORDER_DETAILS = "pdf/order";
    }

}
