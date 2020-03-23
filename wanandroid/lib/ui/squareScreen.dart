import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import 'package:wanandroid/common/Common.dart';
import 'package:wanandroid/data/articleModel.dart';
import 'package:wanandroid/net/api/apiService.dart';
import 'package:wanandroid/ui/baseWidget.dart';
import 'package:wanandroid/utils/toastUtil.dart';
import 'package:wanandroid/widgets/itemArticleList.dart';
import 'package:wanandroid/widgets/refreshFooter.dart';

class SquareScreen extends BaseWidget {
  @override
  BaseWidgetState<BaseWidget> attachState() {
    return SquareScreenState();
  }
}

class SquareScreenState extends BaseWidgetState<SquareScreen> {
  RefreshController _refreshController = RefreshController();

  /// 首页文章列表数据
  List<ArticleBean> _articles = new List();

  /// 是否显示悬浮按钮
  bool _isShowFAB = false;

  /// listview 控制器
  ScrollController _scrollController = new ScrollController();

  /// 页码，从0开始
  int _page = 0;


  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    setAppBarVisible(false);
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

  @override
  void didChangeDependencies() {
    // TODO: implement didChangeDependencies
    super.didChangeDependencies();
    showLoading().then((value) {
      getSquareList();
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
        controller: _refreshController,
        enablePullDown: true,
        enablePullUp: true,
        header: WaterDropHeader(),
        footer: RefreshFooter(),
        onRefresh: getSquareList,
        onLoading: getMoreArticleList,
        child: ListView.builder(
          itemBuilder: itemView,
          controller: _scrollController,
          physics: AlwaysScrollableScrollPhysics(),
          itemCount: _articles.length,
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

  /// ListView 中每一行的视图
  Widget itemView(BuildContext context, int index) {
    if (index > _articles.length) return null;
    ArticleBean item = _articles[index];
    return ItemArticleList(item: item);
  }


  @override
  void dispose() {
    _refreshController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  Future getSquareList() async{
    _page = 0;
    apiService.getSquareList((ArticleModel model){
      if(model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.datas.length > 0) {
          showContent().then((value) {
            _refreshController.refreshCompleted(resetFooterState: true);
            setState(() {
              _articles.clear();
              _articles.addAll(model.data.datas);
            });
          });
        } else {
          showEmpty();
        }
      }else {
        showError();
        ToastUtil.show(model.errorMsg);
      }
    }, (error){
      showError();
    }, _page);
  }

  Future getMoreArticleList() async{
    _page ++;
    apiService.getSquareList((ArticleModel model){
      if(model.errorCode == Constants.STATUS_SUCCESS) {
        if (model.data.datas.length > 0) {
          setState(() {
            _refreshController.loadComplete();
            _articles.addAll(model.data.datas);
          });
        } else {
          _refreshController.loadNoData();
        }
      }else {
        _refreshController.loadFailed();
        ToastUtil.show(model.errorMsg);
      }
    }, (error){
      showError();
      _refreshController.loadFailed();
    }, _page);
  }
}
