package kr.ac.hansung.demap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.folder_public_list.view.*

class MyAdapterForPublic(private var item_list: Array<String>, private var item_desc: Array<String>, private var listOnclickInterface: List_onClick_interface) :
    RecyclerView.Adapter<MyAdapterForPublic.MyViewHolder>() {

    private var mSelectedItem = -1 //선택된 아이템 위치(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflateView = LayoutInflater.from(parent.context).inflate(R.layout.folder_public_list, parent, false)
        return MyViewHolder(inflateView)
    }

    fun getSelectedItem(): Int {
        return mSelectedItem
    }

    override fun getItemCount(): Int {
        return item_list.size
    }

    override fun onBindViewHolder(holder: MyAdapterForPublic.MyViewHolder, position: Int) {
        holder.bind(item_list[position], position, mSelectedItem)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: String, position: Int, selectedPosition: Int) {
            itemView.checkedtextview.text = item_list[position]
            itemView.textview_desc.text = item_desc[position]

            if ((selectedPosition == -1 && position == 0)) { //화면 생성시 첫번째 아이템은 체크상태로
                itemView.checkedtextview.setChecked(true)
                listOnclickInterface.onCheckbox(0, 0) //선택된 데이터 넘겨줌
            }
            else {
                if (selectedPosition == position)
                    itemView.checkedtextview.setChecked(true)
                else
                    itemView.checkedtextview.setChecked(false)
            }

            //클릭리스너
            itemView.checkedtextview.setOnClickListener {
                mSelectedItem = getAdapterPosition()
                listOnclickInterface.onCheckbox(0, adapterPosition) //선택된 데이터 넘겨줌
                notifyDataSetChanged()
            }
            itemView.textview_desc.setOnClickListener {
                mSelectedItem = getAdapterPosition()
                listOnclickInterface.onCheckbox(0, adapterPosition) //선택된 데이터 넘겨줌
                notifyDataSetChanged()
            }
        }
    }
}
