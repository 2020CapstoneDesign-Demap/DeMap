package kr.ac.hansung.demap

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import android.widget.RadioButton
import android.widget.TextView
import java.util.*

class MyAdapterForPublic(context: Context, resource: Int, item_pub: Array<String>, item_desc: Array<String>) : BaseAdapter() {
    private val mContext = context
    private val mItem_pub = item_pub // 공개, 비공개
    private val mItem_desc = item_desc // 공개/비공개 설명

    var mSelectedRadioPosition : Int? = null // 선택된 라디오버튼 위치
    var mLastSelectedButton : CheckedTextView? = null // 마지막으로 선택되어 있던 버튼 뷰


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var viewHolder : ViewHolder
        var view = convertView
        if (view == null){
            viewHolder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.folder_public_list,parent,false)
            viewHolder.checkedTextView = view.findViewById(R.id.checkedtextview)
            viewHolder.textView = view.findViewById(R.id.textview_desc)
            view.tag = viewHolder
            viewHolder.checkedTextView.text = mItem_pub[position]
            viewHolder.textView.text = mItem_desc[position]
            viewHolder.checkedTextView.id = position

            return view
        }else{
            viewHolder = view.tag as ViewHolder

            // 선택된 버튼 위치가 현재 위치이면 라디오 체크 표시를, 아니라면 라디오 체크 표시 해제
            viewHolder.checkedTextView.isChecked = mSelectedRadioPosition == position

        }
        viewHolder.checkedTextView.text = mItem_pub[position]
        viewHolder.textView.text = mItem_desc[position]

        // CheckedTextView 클릭 이벤트
        viewHolder.checkedTextView.setOnClickListener( object : View.OnClickListener {

            override fun onClick(v : View?) {
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

        return  view
    }

    override fun getItem(position: Int) = mItem_pub[position]

    fun getItemDesc(position: Int) = mItem_desc[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mItem_pub.size

    inner class ViewHolder{
        lateinit var checkedTextView : CheckedTextView
        lateinit var textView : TextView
    }

}
