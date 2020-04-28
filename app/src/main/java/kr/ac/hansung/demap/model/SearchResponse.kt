package kr.ac.hansung.demap.model

import android.os.Parcel
import android.os.Parcelable

data class SearchResponse(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<Place>
) {
    data class Place(
        val title: String,
        val link: String,
        val category: String,
        val description: String,
        val telephone: String,
        val address: String,
        val roadAddress: String,
        val mapx: String,
        val mapy: String
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(address)
            parcel.writeString(category)
            parcel.writeString(description)
            parcel.writeString(link)
            parcel.writeString(mapx)
            parcel.writeString(mapy)
            parcel.writeString(roadAddress)
            parcel.writeString(telephone)
            parcel.writeString(title)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Place> {
            override fun createFromParcel(parcel: Parcel): Place {
                return Place(
                    parcel
                )
            }

            override fun newArray(size: Int): Array<Place?> {
                return arrayOfNulls(size)
            }
        }
    }
}