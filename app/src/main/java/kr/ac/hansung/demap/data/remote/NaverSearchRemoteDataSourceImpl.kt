package kr.ac.hansung.demap.data.remote.source

import io.reactivex.Single
import kr.ac.hansung.demap.data.remote.source.network.NaverApiClient
import kr.ac.hansung.demap.model.SearchResponse

class NaverSearchRemoteDataSourceImpl:
    NaverSearchRemoteDataSource {

    override fun getPlaces(query: String): Single<SearchResponse> {
        return NaverApiClient.retrofit.getPlaces(query)
    }
}