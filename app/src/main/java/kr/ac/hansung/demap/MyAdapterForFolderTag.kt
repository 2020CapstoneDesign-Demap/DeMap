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

class MyAdapterForFolderTag(
    context: Context,
    resource: Int,
    item_folder_tag1: Array<String>,
    item_folder_tag2: Array<String>
) : BaseAdapter() {
    private val mContext = context
    private val mItem_folder_tag1 = item_folder_tag1 //왼쪽 태그
    private val mItem_folder_tag2 = item_folder_tag2 //오른쪽 태그


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        lateinit var viewHolder: ViewHolder
        var view = convertView
        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.folder_tag_list, parent, false)
            viewHolder.checkedTextView1 = view.findViewById(R.id.checkedtextview1)
            viewHolder.checkedTextView2 = view.findViewById(R.id.checkedtextview2)
            view.tag = viewHolder
            viewHolder.checkedTextView1.text = mItem_folder_tag1[position]
            viewHolder.checkedTextView1.id = position
            viewHolder.checkedTextView2.text = mItem_folder_tag2[position]
            viewHolder.checkedTextView2.id = position

            return view
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.checkedTextView1.text = mItem_folder_tag1[position]
        viewHolder.checkedTextView2.text = mItem_folder_tag2[position]

        // CheckedTextView 클릭 이벤트
        viewHolder.checkedTextView1.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                if (viewHolder.checkedTextView1.isChecked == false) viewHolder.checkedTextView1.isChecked = true;
                else viewHolder.checkedTextView1.isChecked = false;
                notifyDataSetChanged(); // 변경사항 notify
            }
        })
        // CheckedTextView 클릭 이벤트
        viewHolder.checkedTextView2.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                if (viewHolder.checkedTextView2.isChecked == false) viewHolder.checkedTextView2.isChecked = true;
                else viewHolder.checkedTextView2.isChecked = false;
                notifyDataSetChanged(); // 변경사항 notify
            }
        })

        return view
    }

    override fun getItem(position: Int) = mItem_folder_tag1[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = mItem_folder_tag1.size

    inner class ViewHolder {
        lateinit var checkedTextView1: CheckedTextView
        lateinit var checkedTextView2: CheckedTextView
    }

}
