package kr.ac.hansung.demap.model

data class UserMyFolderDTO(
    var myfolders: MutableMap<String, Boolean> = HashMap()
)