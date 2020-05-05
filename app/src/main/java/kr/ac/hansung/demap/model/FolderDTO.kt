package kr.ac.hansung.demap.model

data class FolderDTO(
    var name: String? = null,
    var imageUrl: String? = null,
    var timestamp: Long? = null,
    var subscribeCount: Int = 0,
    var places: MutableMap<String, Boolean> = HashMap()
)

data class FolderSubsDTO(var subscribers: MutableMap<String, Boolean> = HashMap())

data class FolderPlacesDTO(var places: MutableMap<String, Boolean> = HashMap())