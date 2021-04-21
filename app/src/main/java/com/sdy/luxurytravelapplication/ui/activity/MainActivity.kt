package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityMainBinding
import com.sdy.luxurytravelapplication.event.AccountDangerEvent
import com.sdy.luxurytravelapplication.event.DatingStopPlayEvent
import com.sdy.luxurytravelapplication.event.GetNewMsgEvent
import com.sdy.luxurytravelapplication.event.ReVerifyEvent
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.MainContract
import com.sdy.luxurytravelapplication.mvp.model.bean.AllMsgCount
import com.sdy.luxurytravelapplication.mvp.presenter.MainPresenter
import com.sdy.luxurytravelapplication.ui.adapter.MainPager2Adapter
import com.sdy.luxurytravelapplication.ui.dialog.AccountDangerDialog
import com.sdy.luxurytravelapplication.ui.dialog.ChangeAvatarRealManDialog
import com.sdy.luxurytravelapplication.ui.dialog.GotoVerifyDialog
import com.sdy.luxurytravelapplication.ui.fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class MainActivity :
    BaseMvpActivity<MainContract.View, MainContract.Presenter, ActivityMainBinding>(),
    MainContract.View, View.OnClickListener {
    companion object {
        fun startToMain(context: Context, clearTop: Boolean = true) {
            if (clearTop)
                context.startActivity(context.intentFor<MainActivity>().clearTask().newTask())
            else
                context.startActivity(context.intentFor<MainActivity>())
        }
    }

    override fun createPresenter(): MainContract.Presenter = MainPresenter()


    private val iconUnselectedIds by lazy {
        arrayOf(
            R.drawable.icon_tab_index,
            R.drawable.icon_tab_find,
            R.drawable.icon_tab_travel,
            R.drawable.icon_tab_message
        )
    }
    private val iconSelectedIds by lazy {
        arrayOf(
            R.drawable.icon_tab_index_checked,
            R.drawable.icon_tab_find_checked,
            R.drawable.icon_tab_travel_checked,
            R.drawable.icon_tab_message_checked
        )
    }
    private val titles by lazy {
        arrayOf(
            getString(R.string.tab_index),
            getString(R.string.tab_find),
            getString(R.string.tab_travel),
            getString(R.string.tab_message),
            getString(R.string.tab_mine)
        )
    }

    private val fragments by lazy {
        ArrayList<Fragment>().apply {
            add(IndexFragment())
            add(FindFragment())
            add(TravelFragment())
            add(MessageFragment())
            add(UserCenterFragment())
        }
    }

    override fun initData() {
        GlideUtil.loadAvatorImg(this, UserManager.avatar, binding.tabMine)
        if (UserManager.getAccountDanger() || UserManager.getAccountDangerAvatorNotPass()) {
            //0未认证/认证不成功     1认证通过     2认证中
            if (UserManager.isverify == 2) {
                onAccountDangerEvent(AccountDangerEvent(AccountDangerDialog.VERIFY_ING))
            }
        }
    }


    override fun initView() {
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(indexBtn, findBtn, messageBtn, travelBtn, mineBtn),
                this@MainActivity
            )
            vpMain.apply {
                offscreenPageLimit = fragments.size
                isUserInputEnabled = false
                adapter = MainPager2Adapter(this@MainActivity, fragments)
                currentItem = 0
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        updateTabChecked(position)
                        if (position != 3 || position != 4) {
                            EventBus.getDefault().post(DatingStopPlayEvent())
                        }
                    }
                })

            }
        }

    }

    private var checkedPosition = -1
    private fun updateTabChecked(position: Int, fromVp: Boolean = false) {
        if (checkedPosition == position)
            return
        if (position == 4) {
            (fragments[position] as UserCenterFragment).userCenterVisible = true
            if (!(fragments[position] as UserCenterFragment).appbarTop)
                BarUtils.setStatusBarColor(this, resources.getColor(R.color.colorAccent))
        } else {
            BarUtils.setStatusBarColor(this, resources.getColor(R.color.colorWhite))
        }

        if (!fromVp) {
            binding.vpMain.setCurrentItem(position, false)
        }
        when (position) {
            0 -> {
                binding.tabIndexTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabIndex.setImageResource(iconSelectedIds[0])


                binding.tabFindTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabFind.setImageResource(iconUnselectedIds[1])
                binding.tabTravelTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabTravel.setImageResource(iconUnselectedIds[2])
                binding.tabMessageTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabMessage.setImageResource(iconUnselectedIds[3])
                binding.tabMineCheckView.isInvisible = true
                binding.tabMineTv.setTextColor(Color.parseColor("#FFCBD0D7"))


            }
            1 -> {
                binding.tabFindTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabFind.setImageResource(iconSelectedIds[1])


                binding.tabIndexTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabIndex.setImageResource(iconUnselectedIds[0])
                binding.tabTravelTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabTravel.setImageResource(iconUnselectedIds[2])
                binding.tabMessageTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabMessage.setImageResource(iconUnselectedIds[3])
                binding.tabMineCheckView.isInvisible = true
                binding.tabMineTv.setTextColor(Color.parseColor("#FFCBD0D7"))
            }
            2 -> {
                binding.tabTravelTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabTravel.setImageResource(iconSelectedIds[2])

                binding.tabIndexTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabIndex.setImageResource(iconUnselectedIds[0])
                binding.tabFindTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabFind.setImageResource(iconUnselectedIds[1])
                binding.tabMessageTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabMessage.setImageResource(iconUnselectedIds[3])
                binding.tabMineCheckView.isInvisible = true
                binding.tabMineTv.setTextColor(Color.parseColor("#FFCBD0D7"))
            }
            3 -> {
                binding.tabMessageTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabMessage.setImageResource(iconSelectedIds[3])


                binding.tabIndexTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabIndex.setImageResource(iconUnselectedIds[0])
                binding.tabFindTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabFind.setImageResource(iconUnselectedIds[1])
                binding.tabTravelTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabTravel.setImageResource(iconUnselectedIds[2])
                binding.tabMineCheckView.isInvisible = true
                binding.tabMineTv.setTextColor(Color.parseColor("#FFCBD0D7"))
            }
            else -> {
                binding.tabMineTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabMineCheckView.isInvisible = false


                binding.tabIndexTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabIndex.setImageResource(iconUnselectedIds[0])
                binding.tabFindTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabFind.setImageResource(iconUnselectedIds[1])
                binding.tabTravelTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabTravel.setImageResource(iconUnselectedIds[2])
                binding.tabMessageTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabMessage.setImageResource(iconUnselectedIds[3])

            }
        }

        checkedPosition = position


    }


    override fun start() {
        mPresenter?.msgList()
    }


    override fun onClick(v: View) {
        when (v) {
            binding.indexBtn -> {
                updateTabChecked(0)
            }
            binding.findBtn -> {
                updateTabChecked(1)

            }
            binding.travelBtn -> {
                updateTabChecked(2)

            }
            binding.messageBtn -> {
                updateTabChecked(3)

            }
            binding.mineBtn -> {
                updateTabChecked(4)
            }
        }
    }


    /**
     * 重新认证事件总线
     */
//    const val TYPE_VERIFY = 4//认证失败去认证
//    const val TYPE_CHANGE_AVATOR_NOT_PASS = 7//头像违规替换
//    const val TYPE_CHANGE_ABLUM = 3//完善相册
    private var gotoVerifyDialog: GotoVerifyDialog? = null

    private fun showGotoVerifyDialog(type: Int, avator: String = UserManager.avatar) {
        var content = ""
        var title = ""
        var confirmText = ""
        when (type) {
            GotoVerifyDialog.TYPE_VERIFY -> { //认证不通过
                content = getString(R.string.avatar_compare_fail)
                title = getString(R.string.avata_verify_fail)
            }
            GotoVerifyDialog.TYPE_CHANGE_AVATOR_NOT_PASS -> {//7头像违规
                content = getString(R.string.avatar_not_pass_content)
                title = getString(R.string.avatar_change)
                confirmText = getString(R.string.avator_change_text)
            }
//            GotoVerifyDialog.TYPE_CHANGE_ABLUM -> {//完善相册
//                content = "完善相册会使你的信息更多在匹配页展示\n现在就去完善你的相册吧！"
//                title = "完善相册"
//                confirmText = "完善相册"
//            }

        }
        if (gotoVerifyDialog != null) {
            gotoVerifyDialog!!.dismiss()
            gotoVerifyDialog = null
        }

        gotoVerifyDialog = GotoVerifyDialog.Builder(ActivityUtils.getTopActivity())
            .setTitle(title)
            .setContent(content)
            .setConfirmText(confirmText)
            .setIcon(avator)
            .setIconVisible(true)
            .setType(type)
            .setCancelIconIsVisibility(type != GotoVerifyDialog.TYPE_CHANGE_AVATOR_NOT_PASS)
            .setOnCancelable(type != GotoVerifyDialog.TYPE_CHANGE_AVATOR_NOT_PASS)
            .create()
        gotoVerifyDialog?.show()
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onReVerifyEvent(event: ReVerifyEvent) {
        if (accountDangerDialog != null) {
            accountDangerDialog!!.dismiss()
            accountDangerDialog = null
        }

        if (event.type == GotoVerifyDialog.TYPE_CHANGE_AVATOR_REAL_NOT_VALID) {//11
            UserManager.saveNeedChangeAvator(true)//需要换头像
            UserManager.saveForceChangeAvator(false)//是否强制替换过头像
            UserManager.saveChangeAvatorType(2)//真人不合规
            ChangeAvatarRealManDialog(ChangeAvatarRealManDialog.VERIFY_NEED_VALID_REAL_MAN).show()
        } else if (event.type == GotoVerifyDialog.TYPE_CHANGE_AVATOR_NOT_PASS) { //7
            UserManager.saveNeedChangeAvator(true)//需要换头像
            UserManager.saveForceChangeAvator(false)//是否强制替换过头像
            UserManager.saveChangeAvatorType(1)//头像不合规
            showGotoVerifyDialog(event.type, event.avator)
        }
        if (EventBus.getDefault().getStickyEvent(ReVerifyEvent::class.java) != null) {
            // 若粘性事件存在，将其删除
            EventBus.getDefault()
                .removeStickyEvent(EventBus.getDefault().getStickyEvent(ReVerifyEvent::class.java))
        }
    }


    private var accountDangerDialog: AccountDangerDialog? = null

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAccountDangerEvent(event: AccountDangerEvent) {
        if (accountDangerDialog != null) {
            accountDangerDialog!!.dismiss()
            accountDangerDialog = null
        }

        if (UserManager.getAccountDanger() || UserManager.getAccountDangerAvatorNotPass()) {
            accountDangerDialog = AccountDangerDialog()
            accountDangerDialog!!.show()
            accountDangerDialog!!.changeVerifyStatus(event.type)
        }
        if (EventBus.getDefault().getStickyEvent(AccountDangerEvent::class.java) != null) {
            // 若粘性事件存在，将其删除
            EventBus.getDefault()
                .removeStickyEvent(
                    EventBus.getDefault().getStickyEvent(AccountDangerEvent::class.java)
                )
        }
    }

    override fun onResume() {
        super.onResume()
        if (KeyboardUtils.isSoftInputVisible(this))
            KeyboardUtils.hideSoftInput(this)
        Log.d("OKhttp", "${UserManager.isNeedChangeAvator()},${UserManager.isForceChangeAvator()}}")
        if (UserManager.isNeedChangeAvator())
            if (!UserManager.isForceChangeAvator()) {
                if (UserManager.getChangeAvatorType() == 1)
                    showGotoVerifyDialog(
                        GotoVerifyDialog.TYPE_CHANGE_AVATOR_NOT_PASS,
                        UserManager.getChangeAvator()
                    )
                else
                    ChangeAvatarRealManDialog(ChangeAvatarRealManDialog.VERIFY_NEED_VALID_REAL_MAN).show()
            } else {
                UserManager.saveNeedChangeAvator(false)
                UserManager.saveForceChangeAvator(true)
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onGetMSGEvent(event: GetNewMsgEvent) {
        mPresenter?.startupRecord()
        mPresenter?.msgList()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (gotoVerifyDialog != null) {
            gotoVerifyDialog!!.dismiss()
            gotoVerifyDialog = null
        }
        EventBus.getDefault().unregister(this)
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeReceiveMessage(incomingMessageObserver, false)

    }


    /**
     * 消息接收观察者
     */
    private var incomingMessageObserver: Observer<List<IMMessage>> = Observer {
        mPresenter?.msgList()
    }

    override fun onMsgListResult(allMsgCount: AllMsgCount) {
        if (allMsgCount != null) {
            //未读消息个数
            val msgCount = NIMClient.getService(MsgService::class.java).totalUnreadCount
            Log.d(
                "msgcount", "msgcount = ${msgCount},likecount = ${allMsgCount.likecount}" +
                        ",square_count = ${allMsgCount.square_count}"
            )

            showMsgDot(allMsgCount.square_count > 0 || msgCount > 0)
        }
    }

    /**
     * 显示未读消息红点
     */
    private fun showMsgDot(show: Boolean) {
        binding.msgNewCnt.isVisible = show
    }


}