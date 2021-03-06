package com.suxiunet.repair.businiss.order.orderlist.view

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.ViewGroup
import com.suxiunet.data.entity.order.OrderInfoEntity
import com.suxiunet.data.entity.order.OrderListEntity
import com.suxiunet.data.util.SpUtil
import com.suxiunet.repair.R
import com.suxiunet.repair.base.BasicHolder
import com.suxiunet.repair.base.RefreshProxy
import com.suxiunet.repair.base.adapter.BaseRecyclerViewAdapter
import com.suxiunet.repair.base.baseui.BasicRecyclerViewFragment
import com.suxiunet.repair.businiss.order.orderlist.contract.OrderListContract
import com.suxiunet.repair.businiss.order.orderlist.holder.OrderListHolder
import com.suxiunet.repair.businiss.order.orderlist.model.OrderListModel
import com.suxiunet.repair.businiss.order.orderlist.presenter.OrderListPresenter
import com.suxiunet.repair.businiss.order.orderlist.proxy.OrderListProxy
import com.suxiunet.repair.businiss.order.orderlist.request.OrderListRequest
import com.suxiunet.repair.evententity.OrderEventEntity
import com.suxiunet.repair.plugin.RxBus
import rx.android.schedulers.AndroidSchedulers

/**
 * author : chenzhi
 * time   : 2018/01/08
 * desc   : 订单列表
 */
class OrderListFragment() : BasicRecyclerViewFragment<OrderListRequest, OrderListPresenter, OrderListEntity, OrderInfoEntity>(), OrderListContract.View {

    /**
     * 记录当前的订单状态
     */
    var mCurStatus = "A"
    var mRequest = OrderListRequest()
    lateinit var mProxy: OrderListProxy

    constructor(status: String):this() {
        this.mCurStatus = status
    }
    

    override fun initView() {
        //初始化请求参数的值
        val loginId = SpUtil.getString(context, SpUtil.LOGIN_ID_KEY, "")
        mRequest.loginId = loginId
        mRequest.status = mCurStatus
        mProxy = OrderListProxy(context)
        initEvent()
    }
    /**
     * 注册RxBus监听
     */
    private fun initEvent() {
        RxBus.toObservable(OrderEventEntity::class.java)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    //刷新页面
                    if (activity != null) {
                        mProxy = OrderListProxy(activity)
                        initLoadData()
                    }
                }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            mCurStatus = arguments.getString(PAGE_TYPE_KEY)
        }
    }

    companion object {
        val TYPE_ITEM_ORDER_LIST = R.layout.item_order_list
        var PAGE_TYPE_KEY = "page_type_key"

        fun newInstance(status: String): OrderListFragment {
            val fragment = OrderListFragment()
            val bundle = Bundle()
            bundle.putString(PAGE_TYPE_KEY,status)
            fragment.arguments = bundle
            return OrderListFragment()
        }
    }

    /**
     * 禁用上拉加载功能
     */
    override fun pageEnable(): Boolean {
        return false
    }

    /**
     * 创建Presenter对象
     */
    override fun getPresenter(): OrderListPresenter? {
        return OrderListPresenter(activity, this, OrderListModel())
    }

    /**
     * 创建代理对象
     */
    override fun onCreateProxy(): RefreshProxy<OrderListRequest, OrderListEntity> {
        mProxy = OrderListProxy(context)
        return mProxy
    }
    
    /**
     * 返回条目类型
     */
    override fun onCreateViewTypeMapper(): BaseRecyclerViewAdapter.ViewTypeMapper<OrderInfoEntity>? {
        return BaseRecyclerViewAdapter.ViewTypeMapper<OrderInfoEntity> { d, position -> TYPE_ITEM_ORDER_LIST }
    }

    /**
     * 返回请求类对象
     */
    override fun getRequestData(): OrderListRequest {
        //初始化请求参数的值
        val loginId = SpUtil.getString(context, SpUtil.LOGIN_ID_KEY, "")
        mRequest.loginId = loginId
        return mRequest
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BasicHolder<out OrderInfoEntity, out ViewDataBinding> {
        return OrderListHolder(parent!!,viewType,mPresenter)
    }

    override fun converDataToList(data: OrderListEntity?): List<OrderInfoEntity>? {
        var list = ArrayList<OrderInfoEntity>()
        if (data != null && data.orderInfolist != null && data.orderInfolist.size > 0) {
            list.addAll(data.orderInfolist)
        }
        return list
    }
}