package kr.ac.hansung.demap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.folder_tag_list.view.*

class MyAdapterForFolderTag(private var item_list: Array<String>, private var listOnclickInterface: List_onClick_interface) :
    RecyclerView.Adapter<MyAdapterForFolderTag.MyViewHolder>() {

    private var mSelectedItem = -1 //선택된 아이템 위치(position)
//    private var mSelectedItem : ArrayList<Int>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflateView =
            LayoutInflater.from(parent.context).inflate(R.layout.folder_tag_list, parent, false)
        return MyViewHolder(inflateView)
    }

    override fun getItemCount(): Int {
        return item_list.size
    }

    override fun onBindViewHolder(holder: MyAdapterForFolderTag.MyViewHolder, position: Int) {
        holder.bind(item_list[position], position, mSelectedItem)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: String, position: Int, selectedPosition: Int) {
            itemView.checkedtextview1.text = item_list[position]

            if (selectedPosition == position)
                itemView.checkedtextview1.setChecked(true)
            else
                itemView.checkedtextview1.setChecked(false)

//            if (selectedPosition?.contains(position) == true)
//                itemView.checkedtextview1.setChecked(true)
//            else
//                itemView.checkedtextview1.setChecked(false)

            //클릭리스너
            itemView.checkedtextview1.setOnClickListener {
//                if (mSelectedItem?.contains(adapterPosition) == false)
//                    mSelectedItem?.add(adapterPosition)
//                else
//                    mSelectedItem?.remove(adapterPosition)
                mSelectedItem = getAdapterPosition()
                listOnclickInterface.onCheckbox(2, adapterPosition) //선택된 데이터 넘겨줌
                notifyDataSetChanged()
            }
        }
    }
}