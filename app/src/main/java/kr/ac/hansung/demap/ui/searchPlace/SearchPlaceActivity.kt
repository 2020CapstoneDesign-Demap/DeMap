package kr.ac.hansung.demap.ui.searchPlace

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search_place.*
import kr.ac.hansung.demap.R
import kr.ac.hansung.demap.data.remote.source.NaverSearchRemoteDataSourceImpl
import kr.ac.hansung.demap.databinding.ActivitySearchPlaceBinding
import kr.ac.hansung.demap.model.SearchResponse
import kr.ac.hansung.demap.ui.main.MainActivity


class SearchPlaceActivity : AppCompatActivity(), SearchPlaceContract.View  {
    private val presenter: SearchPlaceContract.Presenter by lazy() {
        SearchPlacePresenter(this, NaverSearchRemoteDataSourceImpl())
    }

    private val searchAdapter: SearchPlaceAdapter by lazy {
        SearchPlaceAdapter()
    }

    lateinit var binding: ActivitySearchPlaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_place)

        searchAdapter.setOnItemClickListener(placeListener = { place ->
            Intent(this, MainActivity::class.java).also {
                // 일단 주소만 넘겨주기
                it.putExtra("place", place.address)
                setResult(Activity.RESULT_OK, it)
                finish()
            }
        })

        binding.presenter = presenter as SearchPlacePresenter
        binding.apply {
            rvSearchPlaceList.adapter = searchAdapter
            rvSearchPlaceList.layoutManager = LinearLayoutManager(this@SearchPlaceActivity)
        }

/*

        binding.btnSearch.setOnClickListener {
            presenter.getPlaces(et_search.text.toString())
        }
*/

    }

    override fun onDestroy() {
        presenter.clearDisposable()
        super.onDestroy()
    }

    override fun showToastMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun finishView() {
        finish()
    }

    override fun setItems(placeList: List<SearchResponse.Place>) {
        searchAdapter.setItems(placeList)
    }

    override fun setSizeText(size: Int) {
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    companion object {
        private const val CURRENT_LOCATION = "current location"
    }

}
