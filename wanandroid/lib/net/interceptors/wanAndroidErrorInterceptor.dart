import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:wanandroid/utils/toastUtil.dart';

import '../dioManager.dart';

class WanAndroidErrorInterceptor extends InterceptorsWrapper {

  @override
  onError(DioError error) async {
//    String errorMsg = DioManager.handleError(error);
//    T.show(msg: errorMsg);
    return error;
  }

  @override
  Future onResponse(Response response) async{
    var data = response.data;
    if (data is String) {
      data = jsonDecode(data);
    }

    if (data is Map) {
      int errorCode = data['errorCode'] ?? 0; // 表示如果data['errorCode']为空的话把 0赋值给errorCode
      String errorMsg = data['errorMsg'] ?? '请求失败[$errorCode]';
      if (errorCode == 0) { // 正常
        return response;
      } else if (errorCode == -1001 /*未登录错误码*/) {
//        User().clearUserInfo();
//        dio.clear(); // 调用拦截器的clear()方法来清空等待队列。
//        SPUtil.clear();
//        goLogin();
        return dio.reject(errorMsg); // 完成和终止请求/响应
      } else {
        ToastUtil.show(errorMsg);
        return dio.reject(errorMsg); // 完成和终止请求/响应
      }
    }
    return response;
  }
}