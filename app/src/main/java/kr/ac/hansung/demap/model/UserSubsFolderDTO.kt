package kr.ac.hansung.demap.model

data class UserSubsFolderDTO(
    var subscribefolders: MutableMap<String, Boolean> = HashMap()
)