package com.ywanhzy.demo.drag;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ywanhzy.demo.R;
import com.ywanhzy.demo.widget.GridViewForScrollView;

import java.util.ArrayList;
import java.util.List;


public class DragGridView extends GridViewForScrollView {
    private static final int MOVE_DURATION = 300;
    private static final int SCROLL_SPEED = 60;
    private static final int EXTEND_LENGTH = 20;

    private Vibrator vibrator;
    private int lastX = -1;
    private int lastY = -1;

    /**
     * 拖动时的图像 和 它的位置
     */
    private BitmapDrawable hoverCell;
    private Rect currentRect;

    /**
     * 要拖动的view
     */
    private View selectView;
    private int originPosition = INVALID_POSITION;
    private int currentPosition = INVALID_POSITION;

    private boolean isEdit;
    public static boolean isDrag;
    private boolean isSwap;

    private DragCallback dragCallback;

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public DragAdapterInterface getInterface() {
        return (DragAdapterInterface) getAdapter();
    }

    public void setDragCallback(DragCallback dragCallback) {
        this.dragCallback = dragCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDrag) {
                    int offsetX = x - lastX;
                    int offsetY = y - lastY;

                    lastX = x;
                    lastY = y;

                    currentRect.offset(offsetX, offsetY);
                    if (hoverCell != null) {
                        hoverCell.setBounds(currentRect);
                    }
                    invalidate();
                    if (!isSwap) {
                        swapItems(x, y);
                    }
                    handleScroll();
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDrag) {
                    endDrag();
                }
                break;
            default:
        }

        return super.onTouchEvent(ev);
    }

    public void clicked(int position) {
        if (isEdit) {
            isEdit = false;
            return;
        }
        // resumeView();
        Log.i("drag", "点击 Item " + position);
    }

    private void resumeView() {

        if (selectView != null) {
            // selectView.findViewById(R.id.delete_img).setVisibility(INVISIBLE);
            selectView.findViewById(R.id.item_container).setVisibility(VISIBLE);
            selectView.findViewById(R.id.item_container).setBackgroundColor(Color.WHITE);
        }
    }

    public void startDrag(int position) {
        if (position == INVALID_POSITION) {
            return;
        }
        // 恢复之前的图像,改变背景,去除删除按钮
        // resumeView();
        selectView = getChildAt(position - getFirstVisiblePosition());
        if (selectView != null) {
            isDrag = true;
            isEdit = true;

            /**
             * 移动的图像背景要有区别,并显示删除按钮
             */
            // selectView.findViewById(R.id.item_container).setBackgroundColor(Color.parseColor("#f0f0f0"));
            selectView.findViewById(R.id.delete_img).setVisibility(VISIBLE);

            originPosition = position;
            currentPosition = position;

            // vibrator.vibrate(60); //震动

            // 获取图像
            hoverCell = getHoverCell(selectView);

            selectView.findViewById(R.id.item_container).setVisibility(INVISIBLE);

            if (dragCallback != null) {
                dragCallback.startDrag(position);
            }

        }
    }

    private void swapItems(int x, int y) {
        int endPosition = pointToPosition(x, y);

        if (endPosition != INVALID_POSITION && endPosition != currentPosition) {
            isSwap = true;
            isEdit = false;
            resumeView();

            // 交换数据内容
            getInterface().reOrder(currentPosition, endPosition);

            selectView = getChildAt(endPosition - getFirstVisiblePosition());
            selectView.findViewById(R.id.item_container).setVisibility(INVISIBLE);
            selectView.findViewById(R.id.item_container).setBackgroundColor(Color.parseColor("#f0f0f0"));
            selectView.findViewById(R.id.delete_img).setVisibility(VISIBLE);

            // 动画显示交换过程
            animateSwap(endPosition);

        }
    }

    private void animateSwap(int endPosition) {
        List<Animator> animators = new ArrayList<>();
        if (endPosition < currentPosition) {
            for (int i = endPosition + 1; i <= currentPosition; i++) {
                View view = getChildAt(i - getFirstVisiblePosition());
                if (i % getNumColumns() == 0) {
                    animators.add(createTranslationAnimations(view, view.getWidth() * (getNumColumns() - 1), 0,
                            -view.getHeight(), 0));
                } else {
                    animators.add(createTranslationAnimations(view, -view.getWidth(), 0, 0, 0));
                }
            }
        } else {
            for (int i = currentPosition; i < endPosition; i++) {
                View view = getChildAt(i - getFirstVisiblePosition());
                if ((i + 1) % getNumColumns() == 0) {
                    animators.add(createTranslationAnimations(view, -view.getWidth() * (getNumColumns() - 1), 0,
                            view.getHeight(), 0));
                } else {
                    animators.add(createTranslationAnimations(view, view.getWidth(), 0, 0, 0));
                }
            }
        }

        currentPosition = endPosition;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.setDuration(MOVE_DURATION);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isSwap = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    private Animator createTranslationAnimations(View view, float startX, float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }

    private void endDrag() {
        currentRect.set(selectView.getLeft(), selectView.getTop(), selectView.getRight(), selectView.getBottom());
        animateBound();
    }

    private void animateBound() {
        TypeEvaluator<Rect> evaluator = new TypeEvaluator<Rect>() {
            @Override
            public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
                return new Rect(interpolate(startValue.left, endValue.left, fraction),
                        interpolate(startValue.top, endValue.top, fraction),
                        interpolate(startValue.right, endValue.right, fraction),
                        interpolate(startValue.bottom, endValue.bottom, fraction));
            }

            public int interpolate(int start, int end, float fraction) {
                return (int) (start + fraction * (end - start));
            }

        };

        ObjectAnimator animator = ObjectAnimator.ofObject(hoverCell, "bounds", evaluator, currentRect);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isDrag = false;

                if (currentPosition != originPosition) {
                    resumeView();
                    originPosition = currentPosition;
                }

                hoverCell = null;
                selectView.findViewById(R.id.item_container).setVisibility(VISIBLE);

                if (dragCallback != null) {
                    dragCallback.endDrag(currentPosition);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
    }

    private BitmapDrawable getHoverCell(View view) {
        int left = view.getLeft();
        int top = view.getTop();
        int w = view.getWidth();
        int h = view.getHeight();

        Bitmap bitmap = getBitmapFromView(view);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        currentRect = new Rect(left - EXTEND_LENGTH, top - EXTEND_LENGTH, left + w + EXTEND_LENGTH,
                top + h + EXTEND_LENGTH);

        drawable.setBounds(currentRect);
        return drawable;
    }

    private Bitmap getBitmapFromView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void handleScroll() {
        int offset = computeVerticalScrollOffset();
        int height = getHeight();
        int extent = computeVerticalScrollExtent();
        int range = computeHorizontalScrollRange();
        if (currentRect.top <= 0 && offset > 0) {
            smoothScrollBy(-SCROLL_SPEED, 0);
        } else if (currentRect.bottom >= height && (offset + extent) < range) {
            smoothScrollBy(SCROLL_SPEED, 0);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (hoverCell != null) {
            hoverCell.draw(canvas);
        }
    }

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//			if (isDrag) {
//				return false;
//			} else {
//			}
//		}
//		return super.onInterceptTouchEvent(ev);
//	}

}
