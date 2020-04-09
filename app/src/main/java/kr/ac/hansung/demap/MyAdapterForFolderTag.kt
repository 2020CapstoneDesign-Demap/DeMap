package kr.ac.hansung.demap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.folder_tag_list.view.*

class MyAdapterForFolderTag(private var item_list: Array<String>) :
    RecyclerView.Adapter<MyAdapterForFolderTag.MyViewHolder>() {

    private var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflateView =
            LayoutInflater.from(parent.context).inflate(R.layout.folder_tag_list, parent, false)
        return MyViewHolder(inflateView)
    }

    fun getSelectedItem(): Int {
        return mSelectedItem
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

            itemView.checkedtextview1.setOnClickListener {
                mSelectedItem = getAdapterPosition()
                notifyDataSetChanged()
            }
        }
    }
}

/*
class MyAdapterForFolderTag(
    context: Context,
    resource: Int,
    item_folder_tag1: Array<String>,
    item_folder_tag2: Array<String>
) : BaseAdapter() {
    private val mContext = context
    private val mItem_folder_tag1 = item_folder_tag1 //왼쪽 태그
    private val mItem_folder_tag2 = item_folder_tag2 //오른쪽 태그

    var mSelectedCheckPosition : Int? = -1 // 선택된 버튼 위치
    var mSelectedCheckList : ArrayList<Int>? = null; //선택된 버튼 리스트


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

            if ((mSelectedCheckPosition == -1 && position == 0)) {
                viewHolder.checkedTextView1.isChecked = true
            }
        }
        viewHolder.checkedTextView1.text = mItem_folder_tag1[position]
        viewHolder.checkedTextView2.text = mItem_folder_tag2[position]

        // CheckedTextView 클릭 이벤트
        viewHolder.checkedTextView1.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                if (viewHolder.checkedTextView1.isChecked == false) {
                    viewHolder.checkedTextView1.isChecked = true
                    if (mSelectedCheckList!!.contains(position) == false) mSelectedCheckList?.add(position)
                };
                else {
                    viewHolder.checkedTextView1.isChecked = false
                    if (mSelectedCheckList!!.contains(position) == true) mSelectedCheckList?.remove(position)
                };
                mSelectedCheckPosition = position
                Toast.makeText(mContext, mSelectedCheckPosition.toString(), Toast.LENGTH_SHORT).show()
                notifyDataSetChanged(); // 변경사항 notify
            }
        })
        // CheckedTextView 클릭 이벤트
        viewHolder.checkedTextView2.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View?) {
                if (viewHolder.checkedTextView2.isChecked == false) {
                    viewHolder.checkedTextView2.isChecked = true
                    if (mSelectedCheckList!!.contains(position) == false) mSelectedCheckList?.add(position)
                };
                else {
                    viewHolder.checkedTextView2.isChecked = false
                    if (mSelectedCheckList!!.contains(position) == true) mSelectedCheckList?.remove(position)
                };
                mSelectedCheckPosition = position
                Toast.makeText(mContext, mSelectedCheckPosition.toString(), Toast.LENGTH_SHORT).show()
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
*/