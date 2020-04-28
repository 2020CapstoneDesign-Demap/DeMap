package kr.ac.hansung.demap.di

import kr.ac.hansung.demap.data.remote.source.FirebaseDataSource
import kr.ac.hansung.demap.data.remote.source.FirebaseDataSourceImpl
import org.koin.dsl.module


val datasourceModule = module {

    single<FirebaseDataSource> { FirebaseDataSourceImpl() }
}