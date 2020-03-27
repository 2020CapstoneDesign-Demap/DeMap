package kr.ac.hansung.demap.di

import kr.ac.hansung.demap.data.remote.FirebaseRepository
import kr.ac.hansung.demap.data.remote.FirebaseRepositoryImpl
import org.koin.dsl.module


val repositoryModule = module {

    single<FirebaseRepository> { FirebaseRepositoryImpl(get())}
}