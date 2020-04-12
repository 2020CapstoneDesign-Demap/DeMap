package kr.ac.hansung.demap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.folder_list.view.*
import kotlinx.android.synthetic.main.folder_public_list.view.*

class MyAdapterForFolderList() : RecyclerView.Adapter<MyAdapterForFolderList.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflateView = LayoutInflater.from(parent.context).inflate(R.layout.folder_public_list, parent, false)
        return MyViewHolder(inflateView)
    }

    fun getSelectedItem(): Int {
        return 0
    }

    override fun getItemCount(): Int {
        return 0
    }

    override fun onBindViewHolder(holder: MyAdapterForFolderList.MyViewHolder, position: Int) {

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: String, position: Int, selectedPosition: Int) {
            itemView.folder_subscribe_btn.setOnClickListener {

            }
        }
    }
}
