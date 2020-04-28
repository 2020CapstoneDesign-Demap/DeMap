package kr.ac.hansung.demap.ui.searchPlace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_search_place.view.*
import kr.ac.hansung.demap.R
import kr.ac.hansung.demap.model.SearchResponse


class SearchPlaceAdapter : RecyclerView.Adapter<SearchPlaceAdapter.SearchViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    private val placeList = mutableListOf<SearchResponse.Place>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_place, parent, false)

        val holder = SearchViewHolder(view)
        holder.itemView.setOnClickListener {
            onItemClickListener!!.onPlaceClick(placeList[holder.adapterPosition])
        }
        return holder
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(placeList[position])
    }

    override fun getItemCount(): Int = placeList.size

    fun setItems(places: List<SearchResponse.Place>) {
        placeList.clear()
        placeList.addAll(places)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(
        placeListener: (SearchResponse.Place) -> Unit
    ) {
        onItemClickListener = object : OnItemClickListener {
            override fun onPlaceClick(place: SearchResponse.Place) {
                placeListener(place)
            }
        }
    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(place: SearchResponse.Place) {
            with(itemView) {
                tv_title.text = place.title
                tv_address.text = place.address
                tv_category.text = place.category
                tv_distance.text = "거리"
            }
        }
    }

    interface OnItemClickListener {
        fun onPlaceClick(place: SearchResponse.Place)
    }
}