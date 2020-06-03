package kr.ac.hansung.demap.ui.createfolder

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.folder_public_list.view.*
import kr.ac.hansung.demap.R
import okhttp3.internal.notify
import okhttp3.internal.notifyAll

class MyAdapterForPublic(private var flag: Int, private var item_list: Array<String>, private var item_desc: Array<String>, private var old_item : String?, private var listOnclickInterface: List_onClick_interface) :
    RecyclerView.Adapter<MyAdapterForPublic.MyViewHolder>() {

    private var mSelectedItem = -1 //선택된 아이템 위치(position)

    private var enabled = 1; // 비활성화 변수

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

    public fun setEnabled(enabled: Int) {
        this.enabled = enabled
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(item_list[position], position, mSelectedItem)
        if (flag == 1 && enabled == 1 && position == 2) {
            holder.checkedTextview.isEnabled = false
            holder.checkedTextview.setTextColor(Color.GRAY)
        }
        else {
            //클릭리스너
            holder.checkedTextview.isEnabled = true
            holder.checkedTextview.setTextColor(Color.DKGRAY)
            holder.checkedTextview.setOnClickListener {
                mSelectedItem = position
                listOnclickInterface.onCheckbox(flag, position) //선택된 데이터 넘겨줌
                notifyDataSetChanged()
            }
            holder.Textview_desc.setOnClickListener {
                mSelectedItem = position
                listOnclickInterface.onCheckbox(flag, position) //선택된 데이터 넘겨줌
                notifyDataSetChanged()
            }
        }

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var checkedTextview: CheckedTextView = itemView.checkedtextview
        var Textview_desc: TextView = itemView.textview_desc

        fun bind(item: String, position: Int, selectedPosition: Int) {

            checkedTextview.text = item_list[position]
            Textview_desc.text = item_desc[position]

            if ((selectedPosition == -1 && position == 0)) { //화면 생성시 첫번째 아이템은 체크상태로
                checkedTextview.setChecked(true)
                listOnclickInterface.onCheckbox(flag, 0) //선택된 데이터 넘겨줌
                if (flag == 0) {
                    enabled = 1;
                }
            }
            else {
                if (selectedPosition == position) {
                    checkedTextview.setChecked(true)
                }
                else {
                    checkedTextview.setChecked(false)
                }
            }

        }
    }
}
