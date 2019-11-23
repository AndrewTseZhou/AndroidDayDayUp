package com.andrewtse.testdemo.nestedscrolling

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * @author Andrew Tse
 * @date 2019-06-05
 */
class NestedScrollingDetailContainer : ViewGroup, NestedScrollingParent2 {

    val TAG_NESTED_SCROLL_WEBVIEW = "nested_scroll_webview"
    val TAG_NESTED_SCROLL_RECYCLERVIEW = "nested_scroll_recyclerview"

    private val FLYING_FROM_WEBVIEW_TO_PARENT = 0
    private val FLYING_FROM_PARENT_TO_WEBVIEW = 1
    private val FLYING_FROM_RVLIST_TO_PARENT = 2

    private var mParentHelper: NestedScrollingParentHelper?
    private var mScroller: Scroller?
    private var mVelocityTracker: VelocityTracker?

    private var mMaximumVelocity: Int
    private var mCurFlyingType: Int = FLYING_FROM_PARENT_TO_WEBVIEW
    private var mInnerScrollHeight: Int = 0
    private val TOUCH_SLOP: Int
    private val mScreenWidth: Int
    private var mLastY: Int = 0

    private var mLastMotionY: Int = 0
    private var mIsSetFlying: Boolean = false
    private var mIsRvFlyingDown: Boolean = false
    private var mIsBeingDragged: Boolean = false

    private var mChildRecyclerView: RecyclerView? = null
    private var mChildWebView: NestedScrollingWebView? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val viewConfiguration = ViewConfiguration.get(context)
        mMaximumVelocity = viewConfiguration.scaledMaximumFlingVelocity
        TOUCH_SLOP = viewConfiguration.scaledTouchSlop
        mScreenWidth = resources.displayMetrics.widthPixels

        mParentHelper = NestedScrollingParentHelper(this)
        mScroller = Scroller(context)
        mVelocityTracker = VelocityTracker.obtain()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width: Int
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        width = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            mScreenWidth
        }

        val top = paddingTop
        val left = paddingLeft
        val bottom = paddingBottom
        val right = paddingRight

        val count = childCount
        for (i in 0..count) {
            val child = getChildAt(i)
            val layoutParams = child.layoutParams
            val chiledWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, left + right, layoutParams.width)
            val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, top + bottom, layoutParams.height)
            measureChild(child, chiledWidthMeasureSpec, childHeightMeasureSpec)
        }

        setMeasuredDimension(width, heightSize)
        findWebView(this)
        findRecyclerView(this)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childTotalHeight = 0
        mInnerScrollHeight = 0
        for (i in 0..childCount) {
            val child = getChildAt(i)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            child.layout(0, childTotalHeight, childWidth, childHeight + childTotalHeight)
            childTotalHeight += childHeight
            mInnerScrollHeight += childHeight
        }
        mInnerScrollHeight -= measuredHeight
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val pointCount = ev.pointerCount
        if (pointCount > 1) {
            return true
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mIsSetFlying = false
                mIsRvFlyingDown = false
                initOrResetVelocityTracker()
                resetScroller()
                dealWithError()
            }
            MotionEvent.ACTION_MOVE -> {
                initVelocityTrackerIfNotExists()
                mVelocityTracker?.addMovement(ev)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (isParentCenter() && mVelocityTracker != null) {
                    mVelocityTracker?.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                    val yVelocity = -mVelocityTracker!!.yVelocity
                    mCurFlyingType = if (yVelocity > 0) {
                        FLYING_FROM_WEBVIEW_TO_PARENT
                    } else {
                        FLYING_FROM_PARENT_TO_WEBVIEW
                    }
                    recycleVelocityTracker()
                    parentFling(yVelocity)
                }
            }
            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = event.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                if (mLastY == 0) {
                    mLastY = event.y.toInt()
                    return true
                }
                val y = event.y.toInt()
                val dy = y - mLastY
                mLastY = y
                scrollBy(0, -dy)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mLastY = 0
            }
            else -> {
            }
        }
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mLastMotionY = ev.y.toInt()
                mIsBeingDragged = false
            }
            MotionEvent.ACTION_MOVE -> {
                val y = ev.y.toInt()
                val yDiff = Math.abs(y - mLastMotionY)
                val isInNestedChildViewArea = isTouchNestedInnerView(ev.rawX.toInt(), ev.rawY.toInt())
                if (yDiff > TOUCH_SLOP && !isInNestedChildViewArea) {
                    mIsBeingDragged = true
                    mLastMotionY = y
                    val parent = parent
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mIsBeingDragged = false
            }
            else -> {
            }
        }
        return mIsBeingDragged
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mScroller?.abortAnimation()
        mScroller = null
        mVelocityTracker = null
        mChildRecyclerView = null
        mChildWebView = null
        mParentHelper = null
    }

    override fun scrollTo(x: Int, y: Int) {
        var dy = 0
        if (y < 0) {
            dy = 0
        }

        if (y > getInnerScrollHeight()) {
            dy = getInnerScrollHeight()
        }

        super.scrollTo(x, dy)
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            val currY = mScroller!!.currY
            when (mCurFlyingType) {
                FLYING_FROM_WEBVIEW_TO_PARENT//WebView向父控件滑动
                -> {
                    if (mIsRvFlyingDown) {
                        //RecyclerView的区域的fling由自己完成
                        return
                    }
                    scrollTo(0, currY)
                    invalidate()
                    checkRvTop()
                    if (scrollY == getInnerScrollHeight() && !mIsSetFlying) {
                        //滚动到父控件底部，滚动事件交给RecyclerView
                        mIsSetFlying = true
                        recyclerViewFling(mScroller!!.currVelocity.toInt())
                    }
                }
                FLYING_FROM_PARENT_TO_WEBVIEW//父控件向WebView滑动
                -> {
                    scrollTo(0, currY)
                    invalidate()
                    if (currY <= 0 && !mIsSetFlying) {
                        //滚动父控件顶部，滚动事件交给WebView
                        mIsSetFlying = true
                        webViewFling((-mScroller!!.currVelocity).toInt())
                    }
                }
                FLYING_FROM_RVLIST_TO_PARENT//RecyclerView向父控件滑动，fling事件，单纯用于计算速度。RecyclerView的flying事件传递最终会转换成Scroll事件处理.
                -> if (scrollY != 0) {
                    invalidate()
                } else if (!mIsSetFlying) {
                    mIsSetFlying = true
                    //滑动到顶部时，滚动事件交给WebView
                    webViewFling((-mScroller!!.currVelocity).toInt())
                }
            }
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onStopNestedScroll(target: View, type: Int) {
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
    }

    override fun getNestedScrollAxes(): Int {
        return getNestedScrollingHelper().nestedScrollAxes
    }

    private fun getNestedScrollingHelper(): NestedScrollingParentHelper {
        if (mParentHelper == null) {
            mParentHelper = NestedScrollingParentHelper(this)
        }
        return mParentHelper!!
    }

    private fun isTouchNestedInnerView(x: Int, y: Int): Boolean {
        val innerView = ArrayList<View>()
        if (mChildWebView != null) {
            innerView.add(mChildWebView!!)
        }
        if (mChildRecyclerView != null) {
            innerView.add(mChildRecyclerView!!)
        }

        for (nestedView in innerView) {
            if (nestedView.visibility != View.VISIBLE) {
                continue
            }
            val location = IntArray(2)
            nestedView.getLocationOnScreen(location)
            val left = location[0]
            val top = location[1]
            val right = left + nestedView.measuredWidth
            val bottom = top + nestedView.measuredHeight
            if (y in top..bottom && x in left..right) {
                return true
            }
        }
        return false
    }

    private fun webViewFling(v: Int) {
        mChildWebView?.flingScroll(0, v)
    }

    private fun recyclerViewFling(v: Int) {
        mChildRecyclerView?.fling(0, v)
    }

    private fun findRecyclerView(parent: ViewGroup) {
        if (mChildRecyclerView != null) {
            return
        }

        val count = parent.childCount
        for (i in 0 until count) {
            val child = parent.getChildAt(i)
            if (child is RecyclerView && TAG_NESTED_SCROLL_RECYCLERVIEW == child.getTag()) {
                mChildRecyclerView = child
                break
            }
            if (child is ViewGroup) {
                findRecyclerView(child)
            }
        }
    }

    private fun findWebView(parent: ViewGroup) {
        if (mChildWebView != null) {
            return
        }
        val count = parent.childCount
        for (i in 0 until count) {
            val child = parent.getChildAt(i)
            if (child is NestedScrollingWebView && TAG_NESTED_SCROLL_WEBVIEW.equals(child.getTag())) {
                mChildWebView = child as NestedScrollingWebView
                break
            }
            if (child is ViewGroup) {
                findWebView(child)
            }
        }
    }

    private fun isParentCenter(): Boolean {
        return scrollY > 0 && scrollY < getInnerScrollHeight()
    }

    private fun scrollToWebViewBottom() {
        mChildWebView?.scrollToBottom()
    }

    private fun initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        } else {
            mVelocityTracker?.clear()
        }
    }

    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        mVelocityTracker?.recycle()
        mVelocityTracker = null
    }

    private fun resetScroller() {
        if (!mScroller!!.isFinished) {
            mScroller!!.abortAnimation()
        }
        mChildRecyclerView?.stopScroll()
    }

    /**
     * 处理未知的错误情况
     */
    private fun dealWithError() {
        //当父控件有偏移，但是WebView却不在底部时，属于异常情况，进行修复，
        //有两种修复方案：1.将WebView手动滑动到底部，2.将父控件的scroll位置重置为0
        //目前的测试中没有出现这种异常，此代码作为异常防御
        if (isParentCenter() && canWebViewScrollDown()) {
            if (scrollY > measuredHeight / 4) {
                scrollToWebViewBottom()
            } else {
                scrollTo(0, 0)
            }
        }
    }

    private fun parentFling(velocityY: Float) {
        mScroller?.fling(0, scrollY, 0, velocityY.toInt(), 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE)
        invalidate()
    }

    private fun checkRvTop() {
        if (isParentCenter() && !isRvTop()) {
            rvScrollToPosition(0)
        }
    }

    private fun rvScrollToPosition(position: Int) {
        if (mChildRecyclerView == null) {
            return
        }

        mChildRecyclerView!!.scrollToPosition(position)
        val manager = mChildRecyclerView!!.getLayoutManager()
        if (manager is LinearLayoutManager) {
            manager.scrollToPositionWithOffset(position, 0)
        }
    }

    private fun isRvTop(): Boolean {
        return if (mChildRecyclerView == null) {
            false
        } else !mChildRecyclerView!!.canScrollVertically(-1)
    }

    private fun canWebViewScrollDown(): Boolean {
        return mChildWebView != null && mChildWebView!!.canScrollDown()
    }

    private fun getInnerScrollHeight(): Int {
        return mInnerScrollHeight
    }
}