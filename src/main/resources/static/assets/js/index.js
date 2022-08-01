function getUrlParams(sParam) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++){
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam)
        {
            return sParameterName[1];
        }
    }
}


$.validator.addMethod("validatePhonenumber", function (value, element) {
    return this.optional(element) || /^0(3\d|5\d|7[0|9|7|6|8]|8[1|2|3|4|5|6|8|9]|9\d)\d{7}/.test(value);
}, "Sai định dạng số điệ thoại");

$.validator.addMethod("noSpace", function (value, element) {
    return this.optional(element) || !value=="";
}, "Vui lòng nhập giá trị");

$.validator.addMethod("list", function (value, element) {
    return this.optional(element) || !value.length !== 0;
}, "Vui lòng nhập giá trị");