package com.mobileprogramming.seoultechquiz.community

import android.os.Parcel
import android.os.Parcelable

data class CommunitySet(
    val quizSetName: String,
    val word: String,
    val definition: String

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(quizSetName)
        parcel.writeString(word)
        parcel.writeString(definition)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommunitySet> {
        override fun createFromParcel(parcel: Parcel): CommunitySet {
            return CommunitySet(parcel)
        }

        override fun newArray(size: Int): Array<CommunitySet?> {
            return arrayOfNulls(size)
        }
    }
}
