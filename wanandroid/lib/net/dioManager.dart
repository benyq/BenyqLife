

import 'dart:io';

import 'package:cookie_jar/cookie_jar.dart';
import 'package:dio/dio.dart';
import 'package:path_provider/path_provider.dart';

import 'api/apis.dart';
import 'interceptors/cookieInterceptor.dart';
import 'interceptors/logInterceptors.dart';
import 'interceptors/wanAndroidErrorInterceptor.dart';

Dio _dio = Dio(); /// 使用默认配置

Dio get dio => _dio;

class DioManager {
  static Future init() async {
    dio.options.baseUrl = Apis.BASE_HOST;
    dio.options.connectTimeout = 30 * 1000;
    dio.options.sendTimeout = 30 * 1000;
    dio.options.receiveTimeout = 30 * 1000;

    dio.interceptors.add(LogInterceptors());
    dio.interceptors.add(WanAndroidErrorInterceptor());

    Directory tempDir = await getTemporaryDirectory();
    String tempPath = tempDir.path + "/dioCookie";
    print('DioUtil : http cookie path = $tempPath');
    CookieJar cj = PersistCookieJar(dir: tempPath, ignoreExpires: true);
    dio.interceptors.add(CookieInterceptor(cj));

  }
}
