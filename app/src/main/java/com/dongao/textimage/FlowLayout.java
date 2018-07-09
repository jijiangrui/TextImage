package com.dongao.textimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class FlowLayout extends ViewGroup {

    /**
     * 用来记录描述有多少行View
     */
    private List<Line> mLines = new ArrayList<Line>();
    /**
     * 用来记录当前已经添加到了哪一行
     */
    private Line mCurrrenLine;
    /**
     * 水平方向上子View之间的间距
     */
    private int horizontalSpace = 10;
    /**
     * 垂直方向上行之间的间距
     */
    private int verticalSpace = 10;
    /**
     * 是否均匀分配剩余长度，默认为false
     */
    private boolean distributeEvenly;
    /**
     * 是否反向开始布局，默认为false
     */
    private boolean reverseLayout;
    /**
     * 每行要显示(最多显示)的子View个数，设置了此值相当于{@link android.widget.GridLayout}效果
     */
    private int spanCount;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, int spanCount) {
        super(context);
        setSpanCount(spanCount);
    }

    public FlowLayout(Context context, int spanCount, boolean distributeEvenly) {
        super(context);
        setSpanCount(spanCount);
        setDistributeEvenly(distributeEvenly);
    }

    public FlowLayout(Context context, int spanCount, boolean distributeEvenly, int horizontalSpace, int verticalSpace) {
        super(context);
        setSpanCount(spanCount);
        setDistributeEvenly(distributeEvenly);
        setSpace(horizontalSpace, verticalSpace);
    }

    public FlowLayout(Context context, int spanCount, boolean distributeEvenly, boolean reverseLayout) {
        super(context);
        setSpanCount(spanCount);
        setDistributeEvenly(distributeEvenly);
        setReverseLayout(reverseLayout);
    }

    public FlowLayout(Context context, int spanCount, boolean distributeEvenly, boolean reverseLayout, int horizontalSpace, int verticalSpace) {
        super(context);
        setSpanCount(spanCount);
        setDistributeEvenly(distributeEvenly);
        setReverseLayout(reverseLayout);
        setSpace(horizontalSpace, verticalSpace);
    }

    public FlowLayout(Context context, boolean reverseLayout) {
        super(context);
        setReverseLayout(reverseLayout);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        reverseLayout = typedArray.getBoolean(R.styleable.FlowLayout_reverseLayout, false);
        distributeEvenly = typedArray.getBoolean(R.styleable.FlowLayout_distributeEvenly, false);
        spanCount = typedArray.getInt(R.styleable.FlowLayout_spanCount, 0);
        horizontalSpace = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_horizontalSpace, 10);
        verticalSpace = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_verticalSpace, 10);
        typedArray.recycle();
    }

    /**
     * @param reverseLayout 如果设置为true，则从右往左布局
     */
    public void setReverseLayout(boolean reverseLayout) {
        this.reverseLayout = reverseLayout;
    }

    /**
     * @param distributeEvenly 如果设置为true，则子View平分剩余的长度
     */
    public void setDistributeEvenly(boolean distributeEvenly) {
        this.distributeEvenly = distributeEvenly;
    }

    /**
     * @param horizontalSpace 水平方向上子View之间的间距
     * @param verticalSpace   垂直方向上行之间的间距
     */
    public void setSpace(int horizontalSpace, int verticalSpace) {
        this.horizontalSpace = dip2px(getContext(), horizontalSpace);
        this.verticalSpace = dip2px(getContext(), verticalSpace);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * @param spanCount 每行显示的子View的个数
     */
    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mLines.clear();
        mCurrrenLine = null;
        int layoutWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 获取行最大的宽度
        int maxLineWidth = layoutWidth - getPaddingLeft() - getPaddingRight();
        // 测量孩子
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            // 如果孩子不可见
            if (view.getVisibility() == View.GONE) {
                continue;
            }
            // 测量孩子
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            // 往lines添加孩子
            if (mCurrrenLine == null) {
                // 说明还没有开始添加孩子
                mCurrrenLine = new Line(maxLineWidth);
                // 添加到 Lines中
                mLines.add(mCurrrenLine);
                // 行中一个孩子都没有
                mCurrrenLine.addView(view);
            } else {
                // 行不为空,行中有孩子了
                boolean canAdd = mCurrrenLine.canAdd(view);
                if (canAdd) {
                    // 可以添加
                    mCurrrenLine.addView(view);
                } else {
                    // 不可以添加,装不下去,新建行
                    mCurrrenLine = new Line(maxLineWidth);
                    // 添加到lines中
                    mLines.add(mCurrrenLine);
                    // 将view添加到line
                    mCurrrenLine.addView(view);
                }
            }
        }
        // 设置自己的宽度和高度
        int measuredWidth = layoutWidth;
        // paddingTop + paddingBottom + 所有的行间距 + 所有的行的高度
        float allHeight = 0;
        for (int i = 0; i < mLines.size(); i++) {
            float mHeigth = mLines.get(i).mHeigth;
            // 加行高
            allHeight += mHeigth;
            // 加间距
            if (i != 0) {
                allHeight += verticalSpace;
            }
        }
        int measuredHeight = (int) (allHeight + getPaddingTop() + getPaddingBottom() + 0.5f);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 给Child 布局---> 给Line布局
        int offsetLeft = getPaddingLeft();
        int offsetTop = getPaddingTop();
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            // 给行布局
            line.layout(offsetLeft, offsetTop);
            offsetTop += line.mHeigth + verticalSpace;
        }
    }

    class Line {
        /**
         * 用来记录每一行有几个View
         */
        private List<View> mViews = new ArrayList<View>();
        /**
         * 行最大的宽度
         */
        private float mMaxWidth;
        /**
         * 已经使用了多少宽度
         */
        private float mUsedWidth;
        /**
         * 行的高度
         */
        private float mHeigth;
        private float mMarginLeft;
        private float mMarginRight;
        private float mMarginTop;
        private float mMarginBottom;

        public Line(int maxWidth) {
            this.mMaxWidth = maxWidth;
        }

        /**
         * 添加view，记录属性的变化
         */
        public void addView(View view) {
            int size = mViews.size();
            int viewWidth = view.getMeasuredWidth();
            int viewHeight = view.getMeasuredHeight();
            // 计算宽和高
            if (size == 0) {
                // 还没有添加View
                if (spanCount == 0) {
                    if (viewWidth > mMaxWidth) {
                        mUsedWidth = mMaxWidth;
                    } else {
                        mUsedWidth = viewWidth;
                    }
                }
                mHeigth = viewHeight;
            } else {
                // 多个view的情况
                if (spanCount == 0) {
                    mUsedWidth += viewWidth + horizontalSpace;
                }
                mHeigth = mHeigth < viewHeight ? viewHeight : mHeigth;
            }
            // 将View记录到集合中
            mViews.add(view);
        }

        /**
         * 用来判断是否可以将View添加到line中
         */
        public boolean canAdd(View view) {
            // 判断是否能添加View
            if (spanCount != 0) {
                if (mViews.size() < spanCount) {
                    return true;
                }
                return false;
            } else {
                int size = mViews.size();
                if (size == 0) {
                    return true;
                }
                int viewWidth = view.getMeasuredWidth();
                // 预计使用的宽度
                float planWidth = mUsedWidth + horizontalSpace + viewWidth;
                if (planWidth > mMaxWidth) {
                    // 加不进去
                    return false;
                }
                return true;
            }
        }

        /**
         * 给孩子布局
         */
        public void layout(int offsetLeft, int offsetTop) {
            int currentLeft;
            if (reverseLayout) {
                currentLeft = (int) (mMaxWidth - offsetLeft);
            } else {
                currentLeft = offsetLeft;
            }
            int size = mViews.size();
            //判断是否设置每行最大个数
            if (spanCount == 0) {
                // 判断已经使用的宽度是否小于最大的宽度
                float extra = 0;
                float widthAvg = 0;
                if (mMaxWidth > mUsedWidth) {
                    extra = mMaxWidth - mUsedWidth;
                    widthAvg = extra / size;
                }
                for (int i = 0; i < size; i++) {
                    View view = mViews.get(i);
                    int viewWidth = view.getMeasuredWidth();
                    int viewHeight = view.getMeasuredHeight();
                    if (distributeEvenly) {
                        // 判断是否有富余,行中的view平分剩余的宽度
                        if (widthAvg != 0) {
                            // 改变宽度
                            int newWidth = (int) (viewWidth + widthAvg + 0.5f);
                            int widthMeasureSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY);
                            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
                            view.measure(widthMeasureSpec, heightMeasureSpec);

                            viewWidth = view.getMeasuredWidth();
                            viewHeight = view.getMeasuredHeight();
                        }
                    }

                    if (reverseLayout) {
                        // 布局
                        int left = currentLeft - viewWidth;
                        int top = (int) (offsetTop + (mHeigth - viewHeight) / 2 + 0.5f);
                        int right = left + viewWidth;
                        int bottom = top + viewHeight;
                        view.layout(left, top, right, bottom);

                        currentLeft -= (viewWidth + horizontalSpace);
                    } else {
                        // 布局
                        int left = currentLeft;
                        int top = (int) (offsetTop + (mHeigth - viewHeight) / 2 + 0.5f);
                        int right = left + viewWidth;
                        int bottom = top + viewHeight;
                        view.layout(left, top, right, bottom);

                        currentLeft += viewWidth + horizontalSpace;
                    }
                }
            } else {
                int widthAvg = (int) ((mMaxWidth - horizontalSpace * (spanCount - 1)) / spanCount);
                for (int i = 0; i < size; i++) {
                    View view = mViews.get(i);
                    int viewWidth = view.getMeasuredWidth();
                    int viewHeight = view.getMeasuredHeight();
                    if (distributeEvenly || viewWidth > widthAvg) {
                        // 改变宽度为平均宽度
                        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthAvg, MeasureSpec.EXACTLY);
                        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
                        view.measure(widthMeasureSpec, heightMeasureSpec);

                        viewWidth = view.getMeasuredWidth();
                        viewHeight = view.getMeasuredHeight();
                    }

                    if (reverseLayout) {
                        // 布局
                        int left = currentLeft - (widthAvg - viewWidth) / 2 - viewWidth;
                        int top = (int) (offsetTop + (mHeigth - viewHeight) / 2 + 0.5f);
                        int right = left + viewWidth;
                        int bottom = top + viewHeight;
                        view.layout(left, top, right, bottom);

                        currentLeft -= (widthAvg + horizontalSpace);
                    } else {
                        // 布局
                        int left = currentLeft + (widthAvg - viewWidth) / 2;
                        int top = (int) (offsetTop + (mHeigth - viewHeight) / 2 + 0.5f);
                        int right = left + viewWidth;
                        int bottom = top + viewHeight;
                        view.layout(left, top, right, bottom);

                        currentLeft += widthAvg + horizontalSpace;
                    }
                }
            }
        }
    }
}
