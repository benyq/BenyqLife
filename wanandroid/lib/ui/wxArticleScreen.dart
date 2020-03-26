import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/data/wxArticleModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/ui/baseWidget.dart';
import 'package:wanandroid/utils/toastUtil.dart';
import 'package:wanandroid/widgets/refreshFooter.dart';

import 'itemWeChatList.dart';

class WXArticleScreen extends BaseWidget {

  final int id;

  WXArticleScreen(this.id);

  @override
  BaseWidgetState<BaseWidget> attachState() {
    // TODO: implement attachState
    return _WXArticleScreenState();
  }

}

class _WXArticleScreenState extends BaseWidgetState<WXArticleScreen> {

  List<WXArticleBean> _wxArticleList = new List();

  /// listview 控制器
  ScrollController _scrollController = new ScrollController();

  /// 是否显示悬浮按钮
  bool _isShowFAB = false;
  int _page = 1;

  RefreshController _refreshController = RefreshController(initialRefresh: true);

  @override
  void initState() {
    super.initState();
    setAppBarVisible(false);
    showContent().then((value){
      getWXArticleList();
    });
    _scrollController.addListener((){
      if (_scrollController.offset < 200 && _isShowFAB) {
        setState(() {
          _isShowFAB = false;
        });
      } else if (_scrollController.offset >= 200 && !_isShowFAB) {
        setState(() {
          _isShowFAB = true;
        });
      }
    });
  }

  Future getWXArticleList() async{
    _page = 1;
    apiService.getWXArticleList((WXArticleModel model){
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        _refreshController.refreshCompleted(resetFooterState: true);
        setState(() {
          _wxArticleList.clear();
          _wxArticleList.addAll(model.data.datas);
        });
      }else {
        ToastUtil.show(model.errorMsg);
      }
    }, (DioError error) {
      showError();
    }, widget.id
    , _page);
  }

  Future getMoreWXArticleList() async {
    _page ++;
    apiService.getWXArticleList((WXArticleModel model){
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.datas.length > 0) {
          _refreshController.loadComplete();
          setState(() {
            _wxArticleList.addAll(model.data.datas);
          });
        }else {
          _refreshController.loadNoData();
        }
      }else {
        _refreshController.loadFailed();
        ToastUtil.show(model.errorMsg);
      }
    }, (DioError error) {
      _refreshController.loadFailed();
    }, widget.id
        , _page);
  }

  @override
  AppBar attachAppBar() {
    return null;
  }

  @override
  Widget attachContentWidget(BuildContext context) {
    // TODO: implement attachContentWidget
    return Scaffold(
      body: SmartRefresher(
        enablePullDown: true,
        enablePullUp: true,
        header: WaterDropHeader(),
        footer: RefreshFooter(),
        controller: _refreshController,
        onRefresh: getWXArticleList,
        onLoading: getMoreWXArticleList,
        child: ListView.builder(itemBuilder: (BuildContext context, int index){
          WXArticleBean item = _wxArticleList[index];
          return ItemWeChatList(item: item);
        },
          physics: new AlwaysScrollableScrollPhysics(),
          controller: _scrollController,
          itemCount: _wxArticleList.length,),
      ),
      floatingActionButton: !_isShowFAB
          ? null
          : FloatingActionButton(
        heroTag: "home",
        child: Icon(Icons.arrow_upward),
        onPressed: () {
          /// 回到顶部时要执行的动画
          _scrollController.animateTo(0,
              duration: Duration(milliseconds: 2000), curve: Curves.ease);
        },
      ),
    );
  }

  @override
  void onClickErrorWidget() {
    getWXArticleList();
  }
}