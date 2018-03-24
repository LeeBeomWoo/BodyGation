package bodygate.bcns.bodygation.dummy

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by LeeBeomWoo on 2018-03-24.
 */
data class SearchData (val videoId:String, val title:String, val url:String,
                       val publishedAt:String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(videoId)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeString(publishedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchData> {
        override fun createFromParcel(parcel: Parcel): SearchData {
            return SearchData(parcel)
        }

        override fun newArray(size: Int): Array<SearchData?> {
            return arrayOfNulls(size)
        }
    }
}
