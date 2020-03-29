import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/data/projectTreeModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/ui/baseWidget.dart';
import 'package:wanandroid/ui/projectArticleScreen.dart';

class ProjectScreen extends BaseWidget {

  @override
  BaseWidgetState<BaseWidget> attachState() {
    return _ProjectScreen();
  }

}


class _ProjectScreen extends BaseWidgetState<ProjectScreen>  with TickerProviderStateMixin{

  List<ProjectTreeBean> _projectTreeList = new List();
  TabController _tabController;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    setAppBarVisible(false);
    showLoading().then((value){
      getProjects();
    });
  }


  Future getProjects() async {
    apiService.getProjectTreeList((ProjectTreeModel model){
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.length > 0) {
          _projectTreeList.clear();
          _projectTreeList.addAll((model.data));
          showContent();
        }else {
          showEmpty();
        }
      }else {
        showError();
      }
    }, (DioError error){
      showError();
    });
  }

  @override
  AppBar attachAppBar() {
    return null;
  }

  @override
  Widget attachContentWidget(BuildContext context) {
    _tabController = TabController(length: _projectTreeList.length, vsync: this);
    return Scaffold(
      body: Column(
        children: <Widget>[
          Container(
            height: 50,
            color: Theme.of(context).primaryColor,
            child: TabBar(
              indicatorColor: Colors.white,
              labelStyle: TextStyle(fontSize: 16),
              unselectedLabelStyle: TextStyle(fontSize: 16),
              controller: _tabController,
              isScrollable: true,
              indicatorSize: TabBarIndicatorSize.tab,
              tabs: _projectTreeList.map((item){
                return Tab(text: item.name,);
              }).toList(),
            ),
          ),
          Expanded(
            child: TabBarView(
              controller: _tabController,
              children: _projectTreeList.map((item){
                return ProjectArticleScreen(item.id);
              }).toList(),
            ),
          )
        ],
      ),
    );
  }

  @override
  void onClickErrorWidget() {
    showLoading().then((value){
      getProjects();
    });
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }
}