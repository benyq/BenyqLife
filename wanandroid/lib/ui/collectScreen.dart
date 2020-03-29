import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/material/app_bar.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/data/collectionModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/ui/baseWidget.dart';
import 'package:wanandroid/utils/toastUtil.dart';
import 'package:wanandroid/widgets/itemCollectList.dart';
import 'package:wanandroid/widgets/refreshFooter.dart';

///收藏页面
class CollectScreen extends BaseWidget {
  @override
  BaseWidgetState<BaseWidget> attachState() {
    return _CollectScreenState();
  }

}

class _CollectScreenState extends BaseWidgetState<CollectScreen> {

  List<CollectionBean> _collectList = new List();

  /// listview 控制器
  ScrollController _scrollController = new ScrollController();

  /// 是否显示悬浮按钮
  bool _isShowFAB = false;

  /// 页码，从0开始
  int _page = 0;

  RefreshController _refreshController =
  new RefreshController(initialRefresh: false);

  @override
  void initState() {
    super.initState();

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
  void didChangeDependencies() {
    super.didChangeDependencies();
    showLoading().then((value) {
      getCollectionList();
    });
  }

  @override
  AppBar attachAppBar() {
    return AppBar(
      title: Text("收藏"),
      elevation: 0.4,
    );
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
        onLoading: getMoreCollectionList,
        onRefresh: getCollectionList,
        child: ListView.builder(
          itemBuilder: itemView,
          controller: _scrollController,
          itemCount: _collectList.length,),
      ),
      floatingActionButton: !_isShowFAB ? null: FloatingActionButton(
        heroTag: "collect",
        child: Icon(Icons.arrow_upward),
        onPressed: () {
          _scrollController.animateTo(0, duration: Duration(milliseconds: 200), curve: Curves.ease);
        },
      ),
    );
  }

  @override
  void onClickErrorWidget() {
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _refreshController.dispose();
    super.dispose();
  }

  Widget itemView(BuildContext context, int index) {
    CollectionBean item = _collectList[index];
    return ItemCollectList(
      item: item,
      onCollectCallback: (isCollect) {
        if (isCollect) {
          setState(() {
            _collectList.removeAt(index);
          });
        }
      },
    );
  }

  /// 获取收藏文章列表
  Future<Null> getCollectionList() async {
    _page = 0;
    apiService.getCollectionList((CollectionModel model) {
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.datas.length > 0) {
          showContent();
          _refreshController.refreshCompleted(resetFooterState: true);
          setState(() {
            _collectList.clear();
            _collectList.addAll(model.data.datas);
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
    }, _page);
  }

  /// 获取更多文章列表
  Future<Null> getMoreCollectionList() async {
    _page++;
    apiService.getCollectionList((CollectionModel model) {
      if (model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.datas.length > 0) {
          _refreshController.loadComplete();
          setState(() {
            _collectList.addAll(model.data.datas);
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
    }, _page);
  }


}