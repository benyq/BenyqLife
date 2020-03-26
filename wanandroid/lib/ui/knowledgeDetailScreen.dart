import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/material/app_bar.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/data/knowledgeDetailModel.dart';
import 'package:wanandroid/data/knowledgeTreeModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/ui/baseWidget.dart';
import 'package:wanandroid/utils/routeUtil.dart';
import 'package:wanandroid/utils/toastUtil.dart';
import 'package:wanandroid/widgets/likeButtonWidget.dart';
import 'package:wanandroid/widgets/refreshFooter.dart';

class KnowledgeDetailScreen extends StatefulWidget {
  KnowledgeTreeBean bean;

  KnowledgeDetailScreen(ValueKey<KnowledgeTreeBean> key) : super(key: key) {
    this.bean = key.value;
  }

  @override
  State<StatefulWidget> createState() {
    return _KnowledgeDetailScreenState();
  }
}

class _KnowledgeDetailScreenState extends State<KnowledgeDetailScreen>
    with TickerProviderStateMixin {
  KnowledgeTreeBean bean;
  TabController _tabController;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    bean = widget.bean;
  }

  @override
  Widget build(BuildContext context) {
    _tabController =
        new TabController(length: bean.children.length, vsync: this);
    return Scaffold(
      appBar: AppBar(
        title: Text(bean.name),
        elevation: 0,
        bottom: TabBar(
          indicatorColor: Colors.white,
          labelStyle: TextStyle(fontSize: 16),
          unselectedLabelStyle: TextStyle(fontSize: 16),
          controller: _tabController,
          isScrollable: true,
          tabs: bean.children.map((item) {
            return Tab(text: item.name);
          }).toList(),
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: bean.children.map((item) {
          return KnowledgeArticleScreen(item.id);
        }).toList(),
      ),
    );
  }
}

class KnowledgeArticleScreen extends BaseWidget {
  final int id;

  KnowledgeArticleScreen(this.id);

  @override
  BaseWidgetState<BaseWidget> attachState() {
    return _KnowledgeArticleScreenState();
  }
}

class _KnowledgeArticleScreenState
    extends BaseWidgetState<KnowledgeArticleScreen> {
  RefreshController _refreshController =
      RefreshController(initialRefresh: false);

  //listview的控制器
  ScrollController _scrollController = ScrollController();
  int _page = 0;

  /// 是否显示悬浮按钮
  bool _isShowFAB = false;

  List<KnowledgeDetailChild> _list = new List();

  @override
  void initState() {
    // TODO: implement initState
    setAppBarVisible(false);
    showLoading().then((value) {
      getKnowledgeDetailList();
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
  AppBar attachAppBar() {
    return null;
  }

  @override
  Widget attachContentWidget(BuildContext context) {
    return Scaffold(
      body: SmartRefresher(
        enablePullUp: true,
        enablePullDown: true,
        header: WaterDropHeader(),
        footer: RefreshFooter(),
        controller: _refreshController,
        onRefresh: getKnowledgeDetailList,
        onLoading: getMoreKnowledgeDetailList,
        child: ListView.builder(
          controller: _scrollController,
          itemBuilder: (BuildContext context, int index) {
            KnowledgeDetailChild item = _list[index];
            return InkWell(
              onTap: () => RouteUtil.toWebView(context, item.title, item.link),
              child: Column(
                children: <Widget>[
                  Container(
                    padding: EdgeInsets.fromLTRB(16, 10, 16, 10),
                    child: Row(
                      children: <Widget>[
                        Expanded(
                          child: Text(
                            item.author,
                            style: TextStyle(
                                color: Colors.grey[600], fontSize: 12),
                          ),
                        ),
                        Text(
                          item.niceDate,
                          style:
                              TextStyle(color: Colors.grey[600], fontSize: 12),
                        )
                      ],
                    ),
                  ),
                  Container(
                    padding: EdgeInsets.fromLTRB(16, 0, 16, 0),
                    alignment: Alignment.topLeft,
                    child: Text(
                      item.title,
                      style: TextStyle(fontSize: 16),
                    ),
                  ),
                  Container(
                    alignment: Alignment.topLeft,
                    padding: EdgeInsets.fromLTRB(16, 10, 16, 10),
                    child: Row(
                      children: <Widget>[
                        Expanded(
                          child: Text(
                            item.superChapterName + " / " + item.chapterName,
                            style: TextStyle(
                                fontSize: 12, color: Colors.grey[600]),
                            textAlign: TextAlign.left,
                          ),
                        ),
                        LikeButtonWidget(
                          isLike: item.collect,
                          onClick: () {
//                        addOrCancelCollect(item);
                          },
                        )
                      ],
                    ),
                  ),
                  Divider(height: 1),
                ],
              ),
            );
          },
          itemCount: _list.length,
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
  void onClickErrorWidget() {}

  Future getKnowledgeDetailList() async {
    _page = 0;
    int _id = widget.id;
    apiService.getKnowledgeDetailList((KnowledgeDetailModel model) {
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.datas.length > 0) {
          showContent();
          _refreshController.refreshCompleted(resetFooterState: true);
          setState(() {
            _list.clear();
            _list.addAll(model.data.datas);
          });
        } else {
          showEmpty();
        }
      } else {
        showError();
        ToastUtil.show(model.errorMsg);
      }
    }, (DioError error) {
      showError();
    }, _page, _id);
  }

  Future getMoreKnowledgeDetailList() async {
    _page++;
    int _id = widget.id;
    apiService.getKnowledgeDetailList((KnowledgeDetailModel model) {
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.datas.length > 0) {
          _refreshController.loadComplete();
          setState(() {
            _list.addAll(model.data.datas);
          });
        } else {
          _refreshController.loadNoData();
        }
      } else {
        _refreshController.loadFailed();
        ToastUtil.show(model.errorMsg);
      }
    }, (DioError error) {
      _refreshController.loadFailed();
    }, _page, _id);
  }
}
