package com.dev.touyou.ffmultiplier.data

import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

/** ランキングへの書き込みが Play から入れた本物のアプリからだと Firebase が確かめられるようにする。 */
object AppCheckSetup {
    fun install() {
        Firebase.appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
    }
}
