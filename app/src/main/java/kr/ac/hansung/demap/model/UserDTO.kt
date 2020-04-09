package kr.ac.hansung.demap.model

data class UserDTO(
    var myfolders: MutableMap<String, Boolean> = HashMap(),
    var subscribefolders: MutableMap<String, Boolean> = HashMap()
)