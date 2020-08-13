package ai.kitt.vnest.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes val resLayout: Int) : Fragment(resLayout) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView(view!!)
        initAction(view!!)
    }


    abstract fun initView(view: View)
    abstract fun initAction(view: View)
}