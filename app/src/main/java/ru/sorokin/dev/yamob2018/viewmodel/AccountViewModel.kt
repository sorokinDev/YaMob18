package ru.sorokin.dev.yamob2018.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import android.view.View
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.kotlin.addChangeListener
import ru.sorokin.dev.yamob2018.model.entity.AccountInfo
import ru.sorokin.dev.yamob2018.model.repository.AccountRepo
import ru.sorokin.dev.yamob2018.util.RealmLiveData
import ru.sorokin.dev.yamob2018.util.mutableLiveDataWithValue
import ru.sorokin.dev.yamob2018.viewmodel.base.BaseFragmentViewModel

class AccountViewModel: BaseFragmentViewModel() {

    var realm = Realm.getDefaultInstance()

    val accounts : RealmLiveData<AccountInfo>
        get() = RealmLiveData(realm.where(AccountInfo::class.java).findAllAsync())

    fun getCurrentUser() {
        var repo = AccountRepo()
        repo.getAccountInfo(false)
    }

    override var bottomBarVisibility = mutableLiveDataWithValue(View.VISIBLE)

}