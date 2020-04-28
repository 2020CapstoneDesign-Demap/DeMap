package kr.ac.hansung.demap.data.remote.source

import io.reactivex.Single
import kr.ac.hansung.demap.model.SearchResponse

interface NaverSearchRemoteDataSource {

    fun getPlaces(query: String): Single<SearchResponse>
}