package kr.ac.hansung.jsouptest

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class AppCrawlingRecyclerAdapter : RecyclerView.Adapter<AppCrawlingRecyclerAdapter.ItemViewHolder>() {
    private val listData: ArrayList<ChartDTO> = ArrayList()
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ItemViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.crawlingitem_chart, viewGroup, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(
        itemViewHolder: ItemViewHolder,
        i: Int
    ) {
        itemViewHolder.onBind(listData[i])
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun addItem(data: ChartDTO) { // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data)
    }

    inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val txt_ranktNum: TextView
        private val txt_chartName: TextView
        private val txt_chartTitle: TextView
        private val img_chart: ImageView
        fun onBind(data: ChartDTO) {
            txt_ranktNum.setText(data.getRankNum())
            txt_chartName.setText(data.getName())
            txt_chartTitle.setText(data.getTitle())
            Glide.with(itemView.context).load(data.getImageUrl()).into(img_chart)
        }

        init {
            txt_chartTitle = itemView.findViewById(R.id.txt_chartTitle)
            txt_chartName = itemView.findViewById(R.id.txt_chartName)
            txt_ranktNum = itemView.findViewById(R.id.txt_ranktNum)
            img_chart = itemView.findViewById(R.id.img_chart)
        }
    }
}

