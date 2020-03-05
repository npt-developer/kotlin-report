package com.example.kotlinreport.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinreport.R
import com.example.kotlinreport.model.User

class UserAdapter(var mList: ArrayList<User?>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        @JvmStatic
        private val VIEW_ITEM: Int = 0
        @JvmStatic
        private val VIEW_LOADING: Int = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            var itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_user, parent, false)
            viewHolder = UserViewHolder(itemView)
        } else {
            var itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_progress_bar, parent, false)
            viewHolder = LoadingViewHolder(itemView)
        }

        return viewHolder
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (mList.get(position) == null) {
            return VIEW_LOADING
        }
        return VIEW_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserViewHolder) {
            val new = mList.get(position)
            holder.bindView(new)
        } else if (holder is LoadingViewHolder) {
            holder.bindView()
        }
    }

    fun clear(): Unit {
        mList.clear()
        this.notifyDataSetChanged()
    }

    fun addAll(listAdd: ArrayList<User>) {
        val start: Int = mList.size
        val count: Int = listAdd.size
        mList.addAll(listAdd)
        notifyItemRangeChanged(start, count)
    }

    fun openLoading() {
        mList.add(null)
        notifyItemInserted(mList.size - 1)
    }

    fun closeLoading() {
        mList.removeAt(mList.size -1)
        notifyItemRemoved(mList.size -1)
    }



    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var mTextViewName: TextView

        init {
            mTextViewName = itemView.findViewById(R.id.textViewViewHolderUserName)
        }

        fun bindView(user: User?) {
            user?.let {
                mTextViewName.text = it.name
            }
        }

    }

    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var mProgressBar: ProgressBar

        init {
            mProgressBar = itemView.findViewById(R.id.progressBarViewHolder)
        }

        fun bindView() {
            mProgressBar.isIndeterminate = true
        }
    }
}