package com.andrewtse.testdemo.nestedscrolling

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.webkit.WebView
import android.widget.Scroller
import androidx.annotation.Nullable
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat


/**
 * @author Andrew Tse
 * @date 2019-06-10
 */
class NestedScrollingWebView : WebView, NestedScrollingChild2 {
    private var mIsSelfFling: Boolean = false
    private var mHasFling: Boolean = false

    private var TOUCH_SLOP: Int
    private var mMaximumVelocity: Int
    private var mFirstY: Int = 0
    private var mLastY: Int = 0
    private var mMaxScrollY: Int = 0
    private var mWebViewContentHeight: Int = 0
    private val mScrollConsumed = IntArray(2)

    private var DENSITY: Float

    private var mChildHelper: NestedScrollingChildHelper? = null
    private var mParentView: NestedScrollingDetailContainer? = null
    private var mScroller: Scroller? = null
    private var mVelocityTracker: VelocityTracker? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
        mScroller = Scroller(context)
        val configuration = ViewConfiguration.get(context)
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
        TOUCH_SLOP = configuration.scaledTouchSlop
        DENSITY = resources.displayMetrics.density
    }

    fun getWebViewContentHeight(): Int {
        if (mWebViewContentHeight == 0) {
            mWebViewContentHeight = (contentHeight * DENSITY).toInt()
        }

        return mWebViewContentHeight
    }

    fun canScrollDown(): Boolean {
        val range = getWebViewContentHeight() - height
        if (range <= 0) {
            return false
        }

        val offset = scrollY
        return offset < range - TOUCH_SLOP
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mWebViewContentHeight = 0
                mLastY = event.rawY.toInt()
                mFirstY = mLastY
                if (!mScroller!!.isFinished) {
                    mScroller!!.abortAnimation()
                }
                initOrResetVelocityTracker()
                mIsSelfFling = false
                mHasFling = false
                mMaxScrollY = getWebViewContentHeight() - height
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                initVelocityTrackerIfNotExists()
                mVelocityTracker!!.addMovement(event)
                val y = event.rawY.toInt()
                val dy = y - mLastY
                mLastY = y
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                if (!dispatchNestedPreScroll(0, -dy, mScrollConsumed, null)) {
                    scrollBy(0, -dy)
                }
                if (Math.abs(mFirstY - y) > TOUCH_SLOP) {
                    //屏蔽WebView本身的滑动，滑动事件自己处理
                    event.action = MotionEvent.ACTION_CANCEL
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (isParentResetScroll() && mVelocityTracker != null) {
                mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                val yVelocity = (-mVelocityTracker!!.yVelocity).toInt()
                recycleVelocityTracker()
                mIsSelfFling = true
                flingScroll(0, yVelocity)
            }
        }
        super.onTouchEvent(event)
        return true
    }

    override fun flingScroll(vx: Int, vy: Int) {
        mScroller!!.fling(0, scrollY, 0, vy, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE)
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recycleVelocityTracker()
        stopScroll()
        mChildHelper = null
        mScroller = null
        mParentView = null
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            val currY = mScroller!!.currY
            if (!mIsSelfFling) {
                // parent flying
                scrollTo(0, currY)
                invalidate()
                return
            }

            if (isWebViewCanScroll()) {
                scrollTo(0, currY)
                invalidate()
            }
            if (!mHasFling
                    && mScroller!!.startY < currY
                    && !canScrollDown()
                    && startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                    && !dispatchNestedPreFling(0f, mScroller!!.currVelocity)) {
                //滑动到底部时，将fling传递给父控件和RecyclerView
                mHasFling = true
                dispatchNestedFling(0f, mScroller!!.currVelocity, false)
            }
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        var y = y
        if (y < 0) {
            y = 0
        }
        if (mMaxScrollY != 0 && y > mMaxScrollY) {
            y = mMaxScrollY
        }
        if (isParentResetScroll()) {
            super.scrollTo(x, y)
        }
    }

    fun scrollToBottom() {
        val y = getWebViewContentHeight()
        super.scrollTo(0, y - height)
    }

    private fun getNestedScrollingHelper(): NestedScrollingChildHelper {
        if (mChildHelper == null) {
            mChildHelper = NestedScrollingChildHelper(this)
        }
        return mChildHelper!!
    }

    private fun initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        } else {
            mVelocityTracker!!.clear()
        }
    }

    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun initWebViewParent() {
        if (this.mParentView != null) {
            return
        }
        var parent: View? = parent as View
        while (parent != null) {
            if (parent is NestedScrollingDetailContainer) {
                this.mParentView = parent
                break
            } else {
                parent = parent.parent as View
            }
        }
    }

    private fun isParentResetScroll(): Boolean {
        if (mParentView == null) {
            initWebViewParent()
        }
        return if (mParentView != null) {
            mParentView!!.scrollY == 0
        } else true
    }

    private fun stopScroll() {
        if (mScroller != null && !mScroller!!.isFinished) {
            mScroller!!.abortAnimation()
        }
    }

    private fun isWebViewCanScroll(): Boolean {
        return getWebViewContentHeight() > height
    }

    /****** NestedScrollingChild BEGIN  */
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        getNestedScrollingHelper().setNestedScrollingEnabled(enabled)
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return getNestedScrollingHelper().isNestedScrollingEnabled()
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return getNestedScrollingHelper().startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        getNestedScrollingHelper().stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return getNestedScrollingHelper().hasNestedScrollingParent()
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, @Nullable consumed: IntArray?, @Nullable offsetInWindow: IntArray?): Boolean {
        return getNestedScrollingHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, @Nullable offsetInWindow: IntArray?): Boolean {
        return getNestedScrollingHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return getNestedScrollingHelper().dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return getNestedScrollingHelper().dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return getNestedScrollingHelper().startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        getNestedScrollingHelper().stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return getNestedScrollingHelper().hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, @Nullable offsetInWindow: IntArray?, type: Int): Boolean {
        return getNestedScrollingHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, @Nullable consumed: IntArray?, @Nullable offsetInWindow: IntArray?, type: Int): Boolean {
        return getNestedScrollingHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }
}