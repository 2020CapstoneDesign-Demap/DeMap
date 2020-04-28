package kr.ac.hansung.demap.ui.searchPlace

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import kr.ac.hansung.demap.data.remote.source.NaverSearchRemoteDataSource

class SearchPlacePresenter(
    val view: SearchPlaceContract.View,
    private val naverSearchRemoteDataSource: NaverSearchRemoteDataSource
) : SearchPlaceContract.Presenter {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    var query: String? = null

    override fun getPlaces(query: String) {
        compositeDisposable.add(
            naverSearchRemoteDataSource.getPlaces(query)
                .subscribeOn(
                    Schedulers.io()
                )
                .observeOn(
                    AndroidSchedulers.mainThread()
                )
                .doOnSubscribe {
                    view.showProgress()
                }
                .doAfterTerminate {
                    view.hideProgress()
                }
                .subscribe({
                    if (it.items.isNotEmpty()) {
                        view.setItems(it.items)
                        view.setSizeText(it.total)
                    }
                }, {

                })
        )
    }


    override fun clearDisposable() {
        compositeDisposable.clear()
    }
}