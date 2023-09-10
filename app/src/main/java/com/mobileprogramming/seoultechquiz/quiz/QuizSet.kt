package com.mobileprogramming.seoultechquiz.quiz

import android.os.Parcel
import android.os.Parcelable

data class QuizSet(
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

    companion object CREATOR : Parcelable.Creator<QuizSet> {
        override fun createFromParcel(parcel: Parcel): QuizSet {
            return QuizSet(parcel)
        }

        override fun newArray(size: Int): Array<QuizSet?> {
            return arrayOfNulls(size)
        }
    }
}
