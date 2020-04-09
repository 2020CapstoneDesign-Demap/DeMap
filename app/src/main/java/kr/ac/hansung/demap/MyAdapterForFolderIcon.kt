package kr.ac.hansung.demap

import android.os.Parcel
import android.os.Parcelable
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.folder_icon_list.view.*
import java.text.FieldPosition

/*
class MyAdapterForFolderIcon(
    context: Context,
    resource: Int,
    item_folder_icon: Array<Int>
) : BaseAdapter() {
    private val mContext = context
    private val item_folder_icon = item_folder_icon

    var mSelectedRadioPosition : Int? = null // 선택된 라디오버튼 위치
    var mLastSelectedButton : CheckedTextView? = null // 마지막으로 선택되어 있던 버튼 뷰

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var viewHolder: ViewHolder
        var view = convertView
        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.folder_icon_list, parent, false)
            viewHolder.folder_icon = view.findViewById(R.id.folder_icon)
            viewHolder.folder_icon_btn = view.findViewById(R.id.folder_icon_btn)
            view.tag = viewHolder
            viewHolder.folder_icon.id = position
            viewHolder.folder_icon.setBackgroundResource(item_folder_icon[position])
            viewHolder.folder_icon_btn.id = position

            return view
        } else {
            viewHolder = view.tag as ViewHolder

            // 선택된 버튼 위치가 현재 위치이면 라디오 체크 표시를, 아니라면 라디오 체크 표시 해제
            viewHolder.folder_icon_btn.isChecked = mSelectedRadioPosition == position
        }

        // RadioButton 클릭 이벤트
        viewHolder.folder_icon_btn.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {

                if (mSelectedRadioPosition == position) { // 선택한 라디오 위치가 현재 위치이면
                    return; // 변동사항 없이 리턴
                }
                // 선택된 라디오 위치가 변동 되었다면
                mSelectedRadioPosition = position; // 변동된 위치로 갱신

                mLastSelectedButton?.isChecked = false // 이전에 선택 되었던 버튼이 null이 아니라면 라디오체크 표시 비활성화

                mLastSelectedButton = v as CheckedTextView; // mLastSelectedButton을 현재 선택된 뷰로 갱신

                notifyDataSetChanged(); // 변경사항 notify
            }
        })
        // CheckedTextView 클릭 이벤트
//        viewHolder.folder_icon.setOnClickListener(object : View.OnClickListener {
//
//            override fun onClick(v: View?) {
//                if (mSelectedRadioPosition == position) { // 선택한 라디오 위치가 현재 위치이면
//                    return; // 변동사항 없이 리턴
//                }
//                // 선택된 라디오 위치가 변동 되었다면
//                mSelectedRadioPosition = position; // 변동된 위치로 갱신
//
//                mLastSelectedButton?.isChecked = false // 이전에 선택 되었던 버튼이 null이 아니라면 라디오체크 표시 비활성화
//
//                mLastSelectedButton = v as CheckedTextView; // mLastSelectedButton을 현재 선택된 뷰로 갱신
//                notifyDataSetChanged(); // 변경사항 notify
//            }
//        })

        return view
    }

    override fun getItem(position: Int) = item_folder_icon[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = item_folder_icon.size

    inner class ViewHolder {
        lateinit var folder_icon: ImageView
        lateinit var folder_icon_btn: RadioButton
    }

}
*/
class MyAdapterForFolderIcon(private var item_folder_icon: Array<Int>) :
    RecyclerView.Adapter<MyAdapterForFolderIcon.MyViewHolder>() {

    private var mSelectedItem = -1

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

    override fun onBindViewHolder(holder: MyAdapterForFolderIcon.MyViewHolder, position: Int) {
        holder.bind(item_folder_icon[position], position, mSelectedItem)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Int, position: Int, selectedPosition: Int) {
            itemView.folder_icon.setBackgroundResource(item_folder_icon[position])

            if ((selectedPosition == -1 && position == 0))
                itemView.folder_icon_btn.setChecked(true)
            else
                if (selectedPosition == position)
                    itemView.folder_icon_btn.setChecked(true)
                else
                    itemView.folder_icon_btn.setChecked(false)

            itemView.folder_icon.setOnClickListener {
                mSelectedItem = getAdapterPosition()
                notifyDataSetChanged()
            }
            itemView.folder_icon_btn.setOnClickListener {
                mSelectedItem = getAdapterPosition()
                notifyDataSetChanged()
            }
        }
    }
}
