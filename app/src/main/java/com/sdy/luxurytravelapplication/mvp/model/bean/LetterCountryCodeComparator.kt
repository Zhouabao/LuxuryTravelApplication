package com.sdy.luxurytravelapplication.mvp.model.bean

class LetterCountryCodeComparator : Comparator<CountryCodeBean> {
    override fun compare(contactModel: CountryCodeBean?, t1: CountryCodeBean?): Int {

        if (contactModel == null || t1 == null) {
            return 0
        }
        val lhsSortLetters: String = contactModel.index.substring(0, 1).toUpperCase()
        val rhsSortLetters: String = t1.index.substring(0, 1).toUpperCase()

        return if (lhsSortLetters == "#" && rhsSortLetters != "#") 1 else if (lhsSortLetters != "#" && rhsSortLetters == "#") -1 else lhsSortLetters.compareTo(
            rhsSortLetters
        )
    }

}
