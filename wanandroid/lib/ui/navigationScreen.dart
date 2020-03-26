import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/data/navigationModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/ui/baseWidget.dart';
import 'package:wanandroid/utils/commonUtil.dart';
import 'package:wanandroid/utils/routeUtil.dart';
import 'package:wanandroid/utils/toastUtil.dart';
import 'package:wanandroid/widgets/refreshFooter.dart';

class NavigationScreen extends BaseWidget {

  @override
  BaseWidgetState<BaseWidget> attachState() {
    return _NavigationScreenState();
  }

}

class _NavigationScreenState extends BaseWidgetState<NavigationScreen> {

  RefreshController _refreshController =
  RefreshController(initialRefresh: false);

  //listview的控制器
  ScrollController _scrollController = ScrollController();

  /// 是否显示悬浮按钮
  bool _isShowFAB = false;

  List<NavigationBean> _navigationList = List();

@override
  void initState() {
    // TODO: implement initState
    super.initState();
    setAppBarVisible(false);
    showLoading().then((value){
      getNavigationList();
    });
    _scrollController.addListener(() {
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

  @override
  void dispose() {
    _refreshController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  Future getNavigationList() async {
    apiService.getNavigationList((NavigationModel navigationModel) {
      if (navigationModel.errorCode == Constants.STATUS_SUCCESS) {
        if (navigationModel.data.length > 0) {
          showContent().then((value) {
            _refreshController.refreshCompleted();
            setState(() {
              _navigationList.clear();
              _navigationList.addAll(navigationModel.data);
            });
          });
        } else {
          showEmpty();
        }
      } else {
        showError();
        ToastUtil.show(navigationModel.errorMsg);
      }
    }, (DioError error) {
      showError();
    });
  }

  @override
  AppBar attachAppBar() {
    return null;
  }

  @override
  Widget attachContentWidget(BuildContext context) {
    return  Scaffold(
      body: SmartRefresher(
        enablePullDown: true,
        header: WaterDropHeader(),
        footer: RefreshFooter(),
        controller: _refreshController,
        onRefresh: getNavigationList,
        child: ListView.builder(
          controller: _scrollController,
          itemBuilder: (BuildContext context, int index) {
            NavigationBean item = _navigationList[index];
            return Container(
              padding: EdgeInsets.fromLTRB(16, 8, 16, 8),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                children: <Widget>[
                  Container(
                    alignment: Alignment.centerLeft,
                    padding: EdgeInsets.only(bottom: 8),
                    child: Text(
                      item.name,
                      style: TextStyle(fontSize: 16),
                      textAlign: TextAlign.left,
                    ),
                  ),
                  Container(
                    alignment: Alignment.topLeft,
                    child: Wrap(
                      children: item.articles.map((bean) {
                        return InkWell(
                          onTap: () =>
                              RouteUtil.toWebView(context, bean.title, bean.link),
                          child: Container(
                            padding: EdgeInsets.all(5),
                            margin: EdgeInsets.all(5),
                            decoration: BoxDecoration(
                                color: Colors.grey[300],
                                borderRadius: BorderRadius.circular(3.0),
                            ),
                            child: Text(bean.title, style: TextStyle(
                                fontSize: 12.0,
                                color: CommonUtil.randomColor(),
                                fontStyle: FontStyle.italic)),
                          ),
                        );
                      }).toList(),
                    ),
                  )
                ],
              ),
            );
          },
          itemCount: _navigationList.length,
        ),
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
  }

}