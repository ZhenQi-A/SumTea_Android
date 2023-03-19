package com.sum.main.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.sum.common.constant.KEY_ID
import com.sum.framework.decoration.StaggeredItemDecoration
import com.sum.framework.base.BaseMvvmFragment
import com.sum.framework.utils.dpToPx
import com.sum.main.R
import com.sum.main.databinding.FragmentHomeVideoBinding
import com.sum.main.ui.home.adapter.HomeTabItemAdapter
import com.sum.main.ui.home.viewmodel.HomeViewModel

/**
 * @author mingyan.su
 * @date   2023/3/5 20:11
 * @desc   首页资讯列表
 */
class HomeTabFragment : BaseMvvmFragment<FragmentHomeVideoBinding, HomeViewModel>(), OnRefreshListener,
    OnLoadMoreListener {
    private var mPage = 1
    private var mId: Int? = null
    private lateinit var mAdapter: HomeTabItemAdapter

    companion object {
        fun newInstance(id: Int): HomeTabFragment {
            val args = Bundle()
            args.putInt(KEY_ID, id)
            val fragment = HomeTabFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutResId(): Int = R.layout.fragment_home_video

    override fun initView(view: View, savedInstanceState: Bundle?) {
        mBinding?.refreshLayout?.apply {
            autoRefresh()
            setEnableRefresh(true)
            setEnableLoadMore(true)
            setOnRefreshListener(this@HomeTabFragment)
            setOnLoadMoreListener(this@HomeTabFragment)
        }

        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mAdapter = HomeTabItemAdapter()

        mBinding?.recyclerView?.apply {
            layoutManager = manager
            addItemDecoration(StaggeredItemDecoration(dpToPx(10)))
            adapter = mAdapter
        }
    }

    override fun initData() {
        mId = arguments?.getInt(KEY_ID, 0)
        mViewModel.projectItemLiveData.observe(this) {
            if (mPage == 1) {
                mAdapter.setData(it?.datas)
                mBinding?.refreshLayout?.finishRefresh()
                mBinding?.refreshLayout?.setEnableRefresh(false)
            } else {
                mAdapter.addAll(it?.datas)
                mBinding?.refreshLayout?.finishLoadMore()
            }
        }
    }

    /**
     * 获取项目列表数据
     */
    private fun getProjectItemData() {
        mViewModel.getProjectList(mPage, mId ?: 0)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        getProjectItemData()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPage++
        getProjectItemData()
    }
}