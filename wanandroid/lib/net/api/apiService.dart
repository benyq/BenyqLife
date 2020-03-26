import 'package:dio/dio.dart';
import 'package:wanandroid/data/articleModel.dart';
import 'package:wanandroid/data/bannerModel.dart';
import 'package:wanandroid/data/knowledgeDetailModel.dart';
import 'package:wanandroid/data/knowledgeTreeModel.dart';
import 'package:wanandroid/data/navigationModel.dart';
import 'package:wanandroid/data/userModel.dart';
import 'package:wanandroid/data/wxArticleModel.dart';
import 'package:wanandroid/data/wxChaptersModel.dart';
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

  /// 获取公众号名称
  void getWXChaptersList(Function callback, Function errorCallback) async {
    dio.get(Apis.WX_CHAPTERS_LIST).then((response) {
      callback(WXChaptersModel.fromJson(response.data));
    }).catchError((e) {
      errorCallback(e);
    });
  }

  /// 获取公众号文章列表数据
  void getWXArticleList(
      Function callback, Function errorCallback, int _id, int _page) async {
    dio.get(Apis.WX_ARTICLE_LIST + "/$_id/$_page/json").then((response) {
      callback(WXArticleModel.fromJson(response.data));
    }).catchError((e) {
      errorCallback(e);
    });
  }

  /// 获取知识体系数据
  void getKnowledgeTreeList(Function callback, Function errorCallback) async {
    dio.get(Apis.KNOWLEDGE_TREE_LIST).then((response) {
      callback(KnowledgeTreeModel.fromJson(response.data));
    }).catchError((e) {
      errorCallback(e);
    });
  }

  /// 获取知识体系详情数据
  void getKnowledgeDetailList(
      Function callback, Function errorCallback, int _page, int _id) async {
    dio.get(Apis.KNOWLEDGE_DETAIL_LIST + "/$_page/json?cid=$_id")
        .then((response) {
      callback(KnowledgeDetailModel.fromJson(response.data));
    }).catchError((e) {
      errorCallback(e);
    });
  }

  /// 获取导航列表数据
  void getNavigationList(Function callback, Function errorCallback) async {
    dio.get(Apis.NAVIGATION_LIST).then((response) {
      callback(NavigationModel.fromJson(response.data));
    }).catchError((e) {
      errorCallback(e);
    });
  }
}
