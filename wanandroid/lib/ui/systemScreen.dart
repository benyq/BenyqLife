import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/material/app_bar.dart';
import 'package:wanandroid/ui/baseWidget.dart';
import 'package:wanandroid/ui/knowledgeTreeScreen.dart';
import 'package:wanandroid/ui/navigationScreen.dart';

class SystemScreen extends BaseWidget {
  @override
  BaseWidgetState<BaseWidget> attachState() {
    return _SystemScreen();
  }
}

class _SystemScreen extends BaseWidgetState<SystemScreen>
    with TickerProviderStateMixin {
  var _list = ["体系", "导航"];
  TabController _tabController;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    setAppBarVisible(false);
    showContent();
  }

  @override
  AppBar attachAppBar() {
    return null;
  }

  @override
  Widget attachContentWidget(BuildContext context) {
    _tabController = TabController(length: _list.length, vsync: this);
    return Scaffold(
      body: Column(
        children: <Widget>[
          Container(
            color: Theme.of(context).primaryColor,
            height: 50,
            child: TabBar(
              indicatorColor: Colors.white,
              labelStyle: TextStyle(fontSize: 16),
              unselectedLabelStyle: TextStyle(fontSize: 16),
              controller: _tabController,
              isScrollable: false,
              indicatorSize: TabBarIndicatorSize.tab,
              tabs: _list.map((item) {
                return Tab(text: item);
              }).toList(),
            ),
          ),
          Expanded(
            child: TabBarView(
              controller: _tabController,
              children: <Widget>[KnowledgeTreeScreen(), NavigationScreen()],
            ),
          )
        ],
      ),
    );
  }

  @override
  void onClickErrorWidget() {}
}
