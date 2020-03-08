package com.example.kotlinreport.adapter

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinreport.R
import com.example.kotlinreport.model.SexType
import com.example.kotlinreport.model.User
import java.io.File
import java.io.FileInputStream

abstract class UserAdapter(var mList: ArrayList<User?>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        @JvmStatic
        private val VIEW_ITEM: Int = 0
        @JvmStatic
        private val VIEW_LOADING: Int = 1
    }

    abstract fun onActionDelete(user: User, position: Int)
    abstract fun onActionUpdate(user: User, position: Int)

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
            val user: User = mList.get(position)!!
            holder.bindView(user)
            holder.mSwipeActionDelete.setOnClickListener { view ->
                this.onActionDelete(user, position)
            }
            holder.mSwipeActionUpdate.setOnClickListener { view ->
                this.onActionUpdate(user, position)
            }
        } else if (holder is LoadingViewHolder) {
            holder.bindView()
        }
    }

    fun clear(): Unit {
        mList.clear()
        this.notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        mList.removeAt(position)
        this.notifyItemRemoved(position)
    }

    fun addAll(listAdd: ArrayList<User>) {
        val start: Int = mList.size
        val count: Int = listAdd.size
        mList.addAll(listAdd)
        notifyItemRangeInserted(start, count)
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
        // swipe
        var mSwipeContainer: LinearLayout
        var mSwipeActionDelete: ImageView
        var mSwipeActionUpdate: ImageView
        // item
        var mViewContent: ConstraintLayout
        var mTextViewName: TextView
        var mImageViewAvatar: ImageView

        init {
            mSwipeContainer = itemView.findViewById(R.id.viewHolderUserSwipeContainer)
            mSwipeActionDelete = itemView.findViewById(R.id.viewHolderUserSwipeImageViewDelete)
            mSwipeActionUpdate = itemView.findViewById(R.id.viewHolderUserSwipeImageViewUpdate)

            mViewContent = itemView.findViewById(R.id.viewHolderUserContent)
            mTextViewName = itemView.findViewById(R.id.textViewViewHolderUserName)
            mImageViewAvatar = itemView.findViewById(R.id.imageViewViewHolderUserAvatar)
        }

        fun bindView(user: User?) {
            user?.let {
                mTextViewName.text = it.name
                if (it.avatar != null) {
                    var cw = ContextWrapper(itemView.context.applicationContext)
                    var directory = cw.getDir("avata", Context.MODE_PRIVATE)

                    var avataFile = File(directory, it.avatar)
                    if (avataFile.exists()) {
                        val bitmap = BitmapFactory.decodeStream(FileInputStream(avataFile))
                        mImageViewAvatar.setImageBitmap(bitmap)
                    }
                } else {
                    if (it.sex == SexType.MALE) {
                        mImageViewAvatar.setImageResource(R.drawable.ic_man)
                    } else {
                        mImageViewAvatar.setImageResource(R.drawable.ic_woman)
                    }
                }
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