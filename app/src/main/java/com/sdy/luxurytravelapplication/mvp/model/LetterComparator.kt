package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.mvp.model.bean.ContactBean
import java.util.*

class LetterComparator :
    Comparator<ContactBean> {
    override fun compare(contactModel: ContactBean, t1: ContactBean): Int {

        if (contactModel == null || t1 == null) {
            return 0
        }
        val lhsSortLetters: String = contactModel.index.substring(0, 1).toUpperCase(Locale.ROOT)
        val rhsSortLetters: String = t1.index.substring(0, 1).toUpperCase(Locale.ROOT)

        return if (lhsSortLetters == "#" && rhsSortLetters != "#") 1 else if (lhsSortLetters != "#" && rhsSortLetters == "#") -1 else lhsSortLetters.compareTo(
            rhsSortLetters
        )
    }

}
