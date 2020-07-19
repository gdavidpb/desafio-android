package com.gdavidpb.github.ui.viewholders

import android.view.View
import com.gdavidpb.github.R
import com.gdavidpb.github.presentation.model.PullItem
import com.gdavidpb.github.ui.adapters.PullAdapter
import com.gdavidpb.github.utils.onClickOnce
import kotlinx.android.synthetic.main.item_pull.view.*

open class PullViewHolder(
    itemView: View,
    private val manager: PullAdapter.AdapterManager
) : BaseViewHolder<PullItem>(itemView) {
    override fun bindView(item: PullItem) {
        with(itemView) {
            tViewPullName.text = item.title
            tViewPullDescription.text = item.body
            tViewPullUserName.text = item.userLogin

            tViewPullInfo.text = context.getString(R.string.label_info_pulls, item.number, item.message)

            manager.loadImage(item.userAvatarUrl, iViewPullUserAvatar)

            onClickOnce { manager.onPullClicked(item) }

            iViewPullUserAvatar.onClickOnce { manager.onUserClicked(item) }
        }
    }
}