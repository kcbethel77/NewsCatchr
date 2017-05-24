/*
 * NewsCatchr
 * Copyright © 2017 Jan-Lukas Else
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jlelse.newscatchr.ui.recycleritems

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import jlelse.newscatchr.backend.Feed
import jlelse.newscatchr.backend.helpers.Database
import jlelse.newscatchr.extensions.getPrimaryTextColor
import jlelse.newscatchr.extensions.hideView
import jlelse.newscatchr.extensions.resDrw
import jlelse.newscatchr.extensions.showView
import jlelse.newscatchr.ui.fragments.FeedView
import jlelse.readit.R
import jlelse.viewmanager.ViewManagerView
import org.jetbrains.anko.find

class FeedRecyclerItem(val feed: Feed? = null, val isLast: Boolean = false, val fragment: ViewManagerView? = null, val adapter: FastItemAdapter<FeedRecyclerItem>? = null) : AbstractItem<FeedRecyclerItem, FeedRecyclerItem.ViewHolder>() {

	override fun getType(): Int {
		return R.id.feed_item_id
	}

	override fun getLayoutRes(): Int {
		return R.layout.feedlistrecycleritem
	}

	override fun bindView(viewHolder: ViewHolder, payloads: MutableList<Any?>?) {
		super.bindView(viewHolder, payloads)
		val context = viewHolder.itemView.context
		if (feed != null) {
			setTitleText(feed.title, viewHolder.title)
			viewHolder.website.text = Uri.parse(feed.website ?: feed.url()).host
			viewHolder.itemView.setOnClickListener {
				fragment?.openView(FeedView(feed = feed).withTitle(feed.title))
			}
			viewHolder.favorite.setImageDrawable((if (Database.isSavedFavorite(feed.url())) R.drawable.ic_favorite_universal else R.drawable.ic_favorite_border_universal).resDrw(context, context.getPrimaryTextColor()))
			viewHolder.favorite.setOnClickListener {
				if (Database.isSavedFavorite(feed.url())) {
					Database.deleteFavorite(feed.url())
					viewHolder.favorite.setImageDrawable(R.drawable.ic_favorite_border_universal.resDrw(context, context.getPrimaryTextColor()))
				} else {
					Database.addFavorites(feed)
					viewHolder.favorite.setImageDrawable(R.drawable.ic_favorite_universal.resDrw(context, context.getPrimaryTextColor()))
				}
				context.sendBroadcast(Intent("favorites_updated"))
			}
		}
		if (isLast) viewHolder.divider.hideView() else viewHolder.divider.showView()
	}

	private fun setTitleText(title: String?, textView: TextView) {
		textView.text = "$title"
	}

	override fun getViewHolder(p0: View) = ViewHolder(p0)

	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		var title: TextView = view.find<TextView>(R.id.title)
		var website: TextView = view.find<TextView>(R.id.website)
		var favorite: ImageView = view.find<ImageView>(R.id.favorite)
		var divider: View = view.find<View>(R.id.divider)
	}
}