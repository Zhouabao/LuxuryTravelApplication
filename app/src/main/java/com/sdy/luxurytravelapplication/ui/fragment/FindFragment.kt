package com.sdy.luxurytravelapplication.ui.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPUtils
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.FragmentFindBinding
import com.sdy.luxurytravelapplication.event.AnnounceEvent
import com.sdy.luxurytravelapplication.event.RePublishEvent
import com.sdy.luxurytravelapplication.event.UploadEvent
import com.sdy.luxurytravelapplication.mvp.contract.PublishContract
import com.sdy.luxurytravelapplication.mvp.presenter.PublishPresenter
import com.sdy.luxurytravelapplication.ui.activity.PublishActivity
import com.sdy.luxurytravelapplication.ui.adapter.MainPager2Adapter
import com.sdy.luxurytravelapplication.utils.RandomUtils
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 發現
 */
class FindFragment :
    BaseMvpFragment<PublishContract.View, PublishContract.Presenter, FragmentFindBinding>(),
    PublishContract.View {
    override fun createPresenter(): PublishContract.Presenter = PublishPresenter()

    private val fragments by lazy {
        arrayListOf<Fragment>(
            FindContentFragment(FindContentFragment.TYPE_RECOMMEND),
            FindContentFragment(FindContentFragment.TYPE_NEARBY),
            FindContentFragment(FindContentFragment.TYPE_NEWEST)
        )
    }
    private val titles by lazy {
        arrayOf(
            getString(R.string.tab_recommend),
            getString(R.string.near),
            getString(R.string.newest)
        )
    }


    override fun lazyLoad() {
        binding.apply {
            content.isUserInputEnabled = false
            content.adapter = MainPager2Adapter(activity!!, fragments)
            tabFind.setTabData(titles)
//            tabFind.setTabData(titles, activity!!, R.id.content, fragments)
            tabFind.setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    content.currentItem = position
                }

                override fun onTabReselect(position: Int) {
                }

            })
            tabFind.currentTab = 0
            content.currentItem = 0
            ClickUtils.applySingleDebouncing(publishBtn) {
                mPresenter?.checkBlock()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onProgressEvent(event: UploadEvent) {
        if (event.qnSuccess) {
            binding.uploadCl.isVisible = true
            binding.publishStatusIcon.setImageResource(R.drawable.icon_publish_ing)
            binding.publishStateContent.text = getString(R.string.publish_ing)
            binding.publishProgress.progress =
                (((event.currentFileIndex - 1) * 1.0F / event.totalFileCount + (1.0F / event.totalFileCount * event.progress)) * 100).toInt()
        } else {
            UserManager.cancelUpload = true
            UserManager.publishState = -2
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAnnounceEvent(event: AnnounceEvent) {
        if (event.serverSuccess) {
            UserManager.clearPublishParams()
            binding.publishStateContent.text = getString(R.string.publish_success)
            binding.publishStatusIcon.setImageResource(R.drawable.icon_publish_success)
            binding.uploadCl.postDelayed({
                binding.uploadCl.isVisible = false
            }, 500L)

            SPUtils.getInstance(Constants.SPNAME).remove("draft", true)
        } else {
            UserManager.cancelUpload = true
            binding.uploadCl.isVisible = true
            binding.publishProgress.progress = 0
            binding.publishStatusIcon.setImageResource(R.drawable.icon_publish_fail)
            binding.republishBtn.isVisible = true
            when (event.code) {
                402 -> { //内容违规重新去编辑
                    UserManager.publishState = -1
                    binding.publishStateContent.text = getString(R.string.unilegal_content)
                    binding.republishBtn.text = getString(R.string.edit)
                    ClickUtils.applySingleDebouncing(binding.republishBtn) {
                        SPUtils.getInstance(Constants.SPNAME)
                            .put("draft", UserManager.publishParams["descr"] as String)
                        UserManager.clearPublishParams()
                        PublishActivity.startToPublish(activity!!)
                        UserManager.publishState = 0
                        binding.uploadCl.isVisible = false
                    }
                }
                else -> {//发布失败重新发布
                    UserManager.publishState = -2
                    binding.publishStateContent.text = getString(R.string.publish_fail)
                    binding.republishBtn.text = getString(R.string.republish)
                    ClickUtils.applySingleDebouncing(binding.republishBtn) {
                        retryPublish()
                    }
                }
            }
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRePublishEvent(event: RePublishEvent) {
        if (UserManager.publishState == 1) {//正在发布中
            ToastUtil.toast(getString(R.string.publish_ing))
            return
        } else if (UserManager.publishState == -2) {//发布失败
            MessageDialog.show(
                activity!! as AppCompatActivity,
                getString(R.string.publish_tip),
                getString(R.string.re_publish_for_fail_content),
                getString(R.string.retry_upload),
                getString(R.string.publish_new_content)
            )
                .setOnOkButtonClickListener { baseDialog, v ->
                    retryPublish()
                    false
                }
                .setOnCancelButtonClickListener { baseDialog, v ->
                    binding.uploadCl.isVisible = false
                    UserManager.clearPublishParams()
                    PublishActivity.startToPublish(activity!!)
                    false
                }
        } else if (UserManager.publishState == -1) { //400
            SPUtils.getInstance(Constants.SPNAME)
                .put("draft", UserManager.publishParams["descr"] as String)
            UserManager.clearPublishParams()
            PublishActivity.startToPublish(activity!!)
            binding.uploadCl.isVisible = false
        } else if (UserManager.publishState == 0) {
            PublishActivity.startToPublish(activity!!)
        }
    }


    /*-------------------------------------- 重新上传-----------------------------*/
    private var uploadCount = 0

    private fun retryPublish() {
        if (!NetworkUtils.isAvailable()) {
            binding.publishStateContent.text = getString(R.string.network_is_not_available)
            binding.publishStatusIcon.setImageResource(R.drawable.icon_publish_fail)
            return
        } else {
            binding.publishStateContent.text = getString(R.string.publish_ing)
            binding.publishStatusIcon.setImageResource(R.drawable.icon_publish_ing)
        }
        uploadCount = 0
        binding.republishBtn.isVisible = false
        //发布消息的类型0,纯文本的 1，照片 2，视频 3，声音
        UserManager.publishState = 1
        LogUtils.e(UserManager.publishParams["comment"])
        val tempComment =
            Gson().fromJson<ArrayList<String>>(UserManager.publishParams["comment"].toString(),
                object : TypeToken<ArrayList<String>>() {}.type
            )
        when {
            UserManager.publishParams["type"] == 0 -> publish()
            UserManager.publishParams["type"] == 1 -> {
                UserManager.cancelUpload = false
                if (tempComment.isNullOrEmpty())
                    uploadPictures()
                else
                    publish()
            }
            UserManager.publishParams["type"] == 2 -> {
                UserManager.cancelUpload = false

                if (tempComment.isNullOrEmpty()) {
                    //TODO上传视频
                    val videoQnPath =
                        "${Constants.FILE_NAME_INDEX}${Constants.PUBLISH}${SPUtils.getInstance(
                            Constants.SPNAME
                        )
                            .getString(
                                "accid"
                            )}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                            16
                        )}"
                    mPresenter?.uploadFile(1, 1, UserManager.mediaBeans[0].url, videoQnPath, 2)
                } else {
                    publish()
                }
            }
            UserManager.publishParams["type"] == 3 -> {
                UserManager.cancelUpload = false
                if (tempComment.isNullOrEmpty()) {
                    //TODO上传音频
                    val audioQnPath =
                        "${Constants.FILE_NAME_INDEX}${Constants.PUBLISH}${SPUtils.getInstance(
                            Constants.SPNAME
                        )
                            .getString(
                                "accid"
                            )}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                            16
                        )}"
                    mPresenter?.uploadFile(1, 1, UserManager.mediaBeans[0].url, audioQnPath, 3)
                } else {
                    publish()
                }
            }

        }
    }


    //发布消息的类型0,纯文本的 1，照片 2，视频 3，声音
    override fun onQnUploadResult(success: Boolean, type: Int, key: String) {
        if (success) {
            when (type) {
                0 -> {
                    publish()
                }
                1 -> {
                    UserManager.mediaBeans[uploadCount].url = key ?: ""
                    UserManager.keyList.add(Gson().toJson(UserManager.mediaBeans[uploadCount]))
                    uploadCount++
                    if (uploadCount == UserManager.mediaBeans.size) {
                        publish()
                    } else {
                        uploadPictures()
                    }
                }
                2 -> {
                    UserManager.mediaBeans[uploadCount].url = key ?: ""
                    UserManager.keyList.add(Gson().toJson(UserManager.mediaBeans[0]))
                    publish()
                }
                3 -> {
                    UserManager.mediaBeans[uploadCount].url = key ?: ""
                    UserManager.keyList.add(Gson().toJson(UserManager.mediaBeans[0]))
                    publish()
                }
            }
        } else {
            onProgressEvent(UploadEvent(qnSuccess = false))
        }
    }

    override fun onSquareAnnounceResult(type: Int, success: Boolean, code: Int) {
        onAnnounceEvent(AnnounceEvent(success, code))
    }


    //验证用户是否被封禁结果
    override fun onCheckBlockResult(result: Boolean) {
        if (result) {
            onRePublishEvent(RePublishEvent(true, FindFragment::class.java.simpleName))
        }
    }

    private fun uploadPictures() {
        //上传图片
        val imagePath =
            "${Constants.FILE_NAME_INDEX}${Constants.PUBLISH}${SPUtils.getInstance(Constants.SPNAME)
                .getString(
                    "accid"
                )}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                16
            )}"
        mPresenter?.uploadFile(
            UserManager.mediaBeans.size,
            uploadCount + 1,
            UserManager.mediaBeans[uploadCount].url,
            imagePath,
            1
        )
    }

    private fun publish() {
        mPresenter?.publishContent(
            UserManager.publishParams["type"] as Int,
            UserManager.publishParams,
            UserManager.keyList
        )
    }

}