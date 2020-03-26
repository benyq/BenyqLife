import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/material/app_bar.dart';
import 'package:wanandroid/data/wxChaptersModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/ui/baseWidget.dart';
import 'package:wanandroid/ui/wxArticleScreen.dart';

class WeChatScreen extends BaseWidget {
  @override
  BaseWidgetState<BaseWidget> attachState() {
    return _WeChatScreenState();
  }
}

class _WeChatScreenState extends BaseWidgetState<WeChatScreen>
    with TickerProviderStateMixin {
  TabController _tabController;

  List<WXChaptersBean> _chaptersList = List();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    setAppBarVisible(false);
    showLoading().then((value) {
      getWXChaptersList();
    });
  }

  @override
  AppBar attachAppBar() {
    return AppBar(title: Text(""));
  }


  @override
  Widget attachContentWidget(BuildContext context) {
    _tabController =
    new TabController(length: _chaptersList.length, vsync: this);
    return Material(
      color: Colors.white,
      child: Column(
        children: <Widget>[
          Container(
            height: 50,
            color: Theme.of(context).primaryColor,
            child: TabBar(
                indicatorColor: Colors.white,
                labelStyle: TextStyle(fontSize: 16),
                indicatorSize: TabBarIndicatorSize.label,
                unselectedLabelStyle: TextStyle(fontSize: 16),
                controller: _tabController,
                isScrollable: true,
                tabs: _chaptersList.map((WXChaptersBean item) {
                  return Tab(text: item.name);
                }).toList()),
          ),
          Expanded(
            child: TabBarView(
                controller: _tabController,
                children: _chaptersList.map((item) {
                  return WXArticleScreen(item.id);
                }).toList()),
          )
        ],
      ),
    );
  }

  @override
  void onClickErrorWidget() {}

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }


  Future getWXChaptersList() async {
    apiService.getWXChaptersList((WXChaptersModel model){
      setState(() {
        _chaptersList.addAll(model.data);
        showContent();
      });
    }, (DioError error) {
      showEmpty();
    });
  }
}
