package kr.ac.hansung.jsouptest

class ChartDTO {

    var title: String? = null
    var name: String? = null
    var rankNum: String? = null
    var imageUrl: String? = null

    init {
        title = title
        name = name
        rankNum = rankNum
        imageUrl = imageUrl
    }

    fun getRankNum(): Any? {
        return rankNum
    }

    fun getName(): Any? {
        return name
    }

    fun getTitle(): Any? {
        return title
    }

    fun getImageUrl(): Any? {
        return imageUrl
    }


}