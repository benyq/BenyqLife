import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class LikeButtonWidget extends StatefulWidget {
  bool isLike = false;
  Function onClick;

  LikeButtonWidget({Key key, this.isLike, this.onClick}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _LikeButtonWidgetState();
  }
}

class _LikeButtonWidgetState extends State<LikeButtonWidget>
    with SingleTickerProviderStateMixin {
  double size = 24.0;

  AnimationController controller;
  Animation animation;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    controller =
        AnimationController(vsync: this, duration: Duration(milliseconds: 150));
    animation = Tween(begin: size, end: size * 0.5).animate(controller);
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
//      child: LikeAnimation(
//        controller: controller,
//        animation: animation,
//        isLike: widget.isLike,
//        onClick: widget.onClick,
//      ),
      child: ScaleTransition(
        scale: Tween(begin: 1.0, end: 0.5).animate(controller),
        child: GestureDetector(
          child: Icon(
            widget.isLike ? Icons.favorite : Icons.favorite_border,
            size: size,
            color: widget.isLike ? Colors.red : Colors.grey[600],
          ),
          onTapDown: (dragDownDetails) {
            controller.forward();
          },
          onTapUp: (dragDownDetails) {
            Future.delayed(Duration(milliseconds: 100), () {
              controller.reverse();
              widget.onClick();
            });
          },
        ),
      ),
    );
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}

class LikeAnimation extends AnimatedWidget {
  AnimationController controller;
  Animation animation;
  Function onClick;
  bool isLike = false;

  LikeAnimation({this.controller, this.animation, this.isLike, this.onClick})
      : super(listenable: controller);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      child: Icon(
        isLike ? Icons.favorite : Icons.favorite_border,
        size: animation.value,
        color: isLike ? Colors.red : Colors.grey[600],
      ),
      onTapDown: (dragDownDetails) {
        controller.forward();
      },
      onTapUp: (dragDownDetails) {
        Future.delayed(Duration(milliseconds: 100), () {
          controller.reverse();
          onClick();
        });
      },
    );
  }
}

