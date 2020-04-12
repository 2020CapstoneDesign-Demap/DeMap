package kr.ac.hansung.demap.ui.createfolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.folder_icon_list.view.*
import kr.ac.hansung.demap.R

class MyAdapterForFolderIcon(private var item_folder_icon: Array<Int>, private var listOnclickInterface: List_onClick_interface) :
    RecyclerView.Adapter<MyAdapterForFolderIcon.MyViewHolder>() {

    private var mSelectedItem = -1 //선택된 아이템 위치(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflateView =
            LayoutInflater.from(parent.context).inflate(R.layout.folder_icon_list, parent, false)
        return MyViewHolder(inflateView)
    }

    fun getSelectedItem(): Int {
        return mSelectedItem
    }

    override fun getItemCount(): Int {
        return item_folder_icon.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(item_folder_icon[position], position, mSelectedItem)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Int, position: Int, selectedPosition: Int) {
            itemView.folder_icon.setBackgroundResource(item_folder_icon[position])

            if ((selectedPosition == -1 && position == 0)) { //화면 생성시 첫번째 아이템은 체크상태로
                itemView.folder_icon_btn.setChecked(true)
                listOnclickInterface.onCheckbox(3, 0) //선택된 데이터 넘겨줌
            }
            else
                if (selectedPosition == position)
                    itemView.folder_icon_btn.setChecked(true)
                else
                    itemView.folder_icon_btn.setChecked(false)

            //클릭리스너
            itemView.folder_icon.setOnClickListener {
                mSelectedItem = getAdapterPosition()
                listOnclickInterface.onCheckbox(3, adapterPosition) //선택된 데이터 넘겨줌
                notifyDataSetChanged()
            }
            itemView.folder_icon_btn.setOnClickListener {
                mSelectedItem = getAdapterPosition()
                listOnclickInterface.onCheckbox(3, adapterPosition) //선택된 데이터 넘겨줌
                notifyDataSetChanged()
            }
        }
    }
}
