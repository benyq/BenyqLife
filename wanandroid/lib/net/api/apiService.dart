import 'package:dio/dio.dart';
import 'package:wanandroid/data/articleModel.dart';
import 'package:wanandroid/data/bannerModel.dart';
import 'package:wanandroid/data/userModel.dart';
import 'package:wanandroid/net/api/apis.dart';

import '../dioManager.dart';

ApiService _apiService = new ApiService();

ApiService get apiService => _apiService;

class ApiService {
  void login(Function callback, Function errorCallback, String _username,
      String _password) {
    var formData =
        FormData.fromMap({"username": _username, "password": _password});
    dio.post(Apis.USER_LOGIN, data: formData).then((response) {
      callback(UserModel.fromJson(response.data), response);
    }).catchError((e) => errorCallback(e));
  }

  void getBannerList(Function callBack) {
    dio.get(Apis.HOME_BANNER).then((response) {
      callBack(BannerModel.fromJson(response.data));
    });
  }

  void getTopArticleList(Function callBack, Function errorBack) {
    dio.get(Apis.HOME_TOP_ARTICLE_LIST).then((response) {
      callBack(TopArticleModel.fromJson(response.data));
    }).catchError((error) => errorBack(error));
  }

  /// 获取首页文章列表数据
  void getArticleList(Function callback, Function errorCallback, int _page) async {
    dio.get(Apis.HOME_ARTICLE_LIST + "/$_page/json").then((response) {
      callback(ArticleModel.fromJson(response.data));
    }).catchError((e) {
      errorCallback(e);
    });
  }

  /// 获取广场列表数据
  void getSquareList(Function callback, Function errorCallback, int _page) async {
    dio.get(Apis.SQUARE_LIST + "/$_page/json").then((response) {
      callback(ArticleModel.fromJson(response.data));
    }).catchError((e) {
      errorCallback(e);
    });
  }
}
