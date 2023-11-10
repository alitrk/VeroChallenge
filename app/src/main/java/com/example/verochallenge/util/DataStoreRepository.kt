package com.example.verochallenge.util

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

const val PREFERENCE_NAME = "my_preference"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCE_NAME)
class DataStoreRepository @Inject constructor(private val context: Context) {

    private object PreferenceKeys {
        val accessToken : Preferences.Key<String> = stringPreferencesKey("accessToken")
    }

    suspend fun saveToDataStore(accessToken: String){
        context.dataStore.edit { preference ->
            preference[PreferenceKeys.accessToken] = accessToken
        }
    }

    val readFromDataStore: Flow<String> = context.dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val myName = preference[PreferenceKeys.accessToken] ?: "none"
            myName
        }

}