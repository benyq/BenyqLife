import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/material/app_bar.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/data/knowledgeTreeModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/ui/baseWidget.dart';
import 'package:wanandroid/utils/routeUtil.dart';
import 'package:wanandroid/widgets/refreshFooter.dart';

import 'knowledgeDetailScreen.dart';

class KnowledgeTreeScreen extends BaseWidget {
  @override
  BaseWidgetState<BaseWidget> attachState() {
    // TODO: implement attachState
    return _KnowledgeTreeScreenState();
  }
}

class _KnowledgeTreeScreenState extends BaseWidgetState<KnowledgeTreeScreen> {
  List<KnowledgeTreeBean> _list = List();

  /// listview 控制器
  ScrollController _scrollController = new ScrollController();

  /// 是否显示悬浮按钮
  bool _isShowFAB = false;

  RefreshController _refreshController =
      RefreshController(initialRefresh: true);

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    setAppBarVisible(false);
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

  Future getKnowledgeTreeList() async {
    apiService.getKnowledgeTreeList((KnowledgeTreeModel knowledgeTreeModel) {
      if (knowledgeTreeModel.errorCode == Constants.STATUS_SUCCESS) {
        if (knowledgeTreeModel.data.length > 0) {
          showContent().then((value) {
            _refreshController.refreshCompleted();
            setState(() {
              _list.clear();
              _list.addAll(knowledgeTreeModel.data);
            });
          });
        } else {
          showEmpty();
        }
      }
    }, (DioError error) {
      print(error.response);
      showError();
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
        enablePullDown: true,
        header: WaterDropHeader(),
        footer: RefreshFooter(),
        controller: _refreshController,
        onRefresh: getKnowledgeTreeList,
        child: ListView.builder(
          itemBuilder: itemView,
          physics: new AlwaysScrollableScrollPhysics(),
          controller: _scrollController,
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
  void onClickErrorWidget() {
    getKnowledgeTreeList();
  }

  Widget itemView(BuildContext context, int index) {
    KnowledgeTreeBean item = _list[index];
    return InkWell(
      onTap: () => RouteUtil.push(context, KnowledgeDetailScreen(ValueKey(item))),
      child: Container(
        padding: EdgeInsets.fromLTRB(16, 8, 16, 8),
        child: Column(
          children: <Widget>[
            Row(
              children: <Widget>[
                Expanded(
                  child: Column(
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
                          spacing: 10,
                          runSpacing: 6,
                          alignment: WrapAlignment.start,
                          children: item.children.map((bean) {
                            return Text(
                              bean.name,
                              style: TextStyle(color: Color(0xFF757575)),
                            );
                          }).toList(),
                        ),
                      )
                    ],
                  ),
                ),
                Icon(
                  Icons.chevron_right,
                  color: Colors.grey,
                )
              ],
            ),
            Divider(height: 1,)
          ],
        ),
      ),
    );
  }
}
