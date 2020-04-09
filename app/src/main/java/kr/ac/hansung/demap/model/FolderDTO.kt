package kr.ac.hansung.demap.model

data class FolderDTO(
    var name: String? = null,
    var imageUrl: String? = null,
    var uid: String? = null,
    var timestamp: Long? = null,
    var subscribeCount: Int = 0,
    var subscribers: MutableMap<String, Boolean> = HashMap()
)