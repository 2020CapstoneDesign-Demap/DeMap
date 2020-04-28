package kr.ac.hansung.demap.ui.searchPlace


import kr.ac.hansung.demap.model.SearchResponse

interface SearchPlaceContract {

    interface View {
        fun setItems(placeList: List<SearchResponse.Place>)
        fun setSizeText(size: Int)
        fun showProgress()
        fun hideProgress()
        fun finishView()
        fun showToastMessage(message: String)
    }

    interface Presenter{
        fun getPlaces(query: String)
        fun clearDisposable()
    }
}