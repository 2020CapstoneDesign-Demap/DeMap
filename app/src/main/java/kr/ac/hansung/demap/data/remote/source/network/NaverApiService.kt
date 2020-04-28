package kr.ac.hansung.demap.data.remote.source.network

import io.reactivex.Single
import kr.ac.hansung.demap.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NaverApiService {

    @Headers(
        "X-Naver-Client-Id: ZoIPbkrQupJiQKLhUkUO",
        "X-Naver-Client-Secret: IklE1elxUv"
    )
    @GET("search/local.json")
    fun getPlaces(@Query("query") query: String): Single<SearchResponse>
}