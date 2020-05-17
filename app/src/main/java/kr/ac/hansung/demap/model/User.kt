package kr.ac.hansung.demap.model

data class User(
    var nickName: String? = null
)

data class UserMyFolderDTO(var myfolders: MutableMap<String, Boolean> = HashMap())

data class UserSubsFolderDTO(var subscribefolders: MutableMap<String, Boolean> = HashMap())