package com.jsennett.appshortcut.widget.configuration

import android.content.pm.ResolveInfo
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jsennett.appshortcut.R
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LauncherAppsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val apps = mutableListOf<AppItem>()
    private val listeners = mutableListOf<ResolveInfoClickListener>()

    fun setApps(data: List<AppItem>) {
        apps.clear()
        apps.addAll(data)
        notifyDataSetChanged()
    }

    fun addListener(listener: ResolveInfoClickListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ResolveInfoClickListener) {
        listeners.remove(listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        APP_ITEM_VIEW -> LauncherAppViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.launcher_app_item, parent, false))
        else -> LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.loading_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LauncherAppViewHolder -> {
                holder.imageLoadDisposable?.dispose()
                val appItem = apps[position]
                holder.label.text = appItem.label
                holder.itemView.setOnClickListener {
                    for (listener in listeners) {
                        listener.itemClicked(appItem)
                    }
                }

                holder.imageLoadDisposable = Single.fromCallable {
                    appItem.resolveInfo.loadIcon(holder.itemView.context.packageManager)
                }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { icon -> holder.image.setImageDrawable(icon) }
            }
        }

    }

    override fun getItemCount(): Int = if (apps.isEmpty()) 1 else apps.size
    override fun getItemViewType(position: Int): Int = if (apps.isEmpty()) LOADING_VIEW else APP_ITEM_VIEW

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is LauncherAppViewHolder) {
            holder.itemView.setOnClickListener(null)
            holder.imageLoadDisposable?.dispose()
            holder.imageLoadDisposable = null
            holder.image.setImageDrawable(null)
        }
    }

    companion object {
        private const val LOADING_VIEW = 1
        private const val APP_ITEM_VIEW = 2
    }
}

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class LauncherAppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image: ImageView by lazy { itemView.findViewById<ImageView>(R.id.app_icon) }
    val label: TextView by lazy { itemView.findViewById<TextView>(R.id.app_label) }
    var imageLoadDisposable: Disposable? = null
}

interface ResolveInfoClickListener {
    fun itemClicked(appItem: AppItem)
}

class AppItem(val resolveInfo: ResolveInfo, val label: CharSequence)