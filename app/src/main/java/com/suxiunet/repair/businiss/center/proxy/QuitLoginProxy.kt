package com.suxiunet.repair.businiss.center.proxy

import android.content.Context
import com.suxiunet.data.api.UserApiImpl
import com.suxiunet.data.entity.base.ApiResponse
import com.suxiunet.repair.base.BasicRequest
import com.suxiunet.repair.base.RefreshProxy
import com.suxiunet.repair.businiss.center.request.LoginRequest
import rx.Observable
import rx.functions.Func1

/**
 * author : chenzhi
 * time   : 2018/01/14
 * desc   : 退出登录
 */
class QuitLoginProxy: RefreshProxy<LoginRequest, Any>{
    private lateinit var mUserApiImpl: UserApiImpl
    constructor(context: Context): super(context){
        mUserApiImpl = UserApiImpl(context)
    }
    
    override fun getObservable(request: LoginRequest): Observable<Any> {
        return mUserApiImpl.quitLogin(request.getLoginName(),"C")
                .map { t -> t?.retData?:Any() }
    }
}