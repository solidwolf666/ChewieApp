package com.dango.commands

import CommandsApiInterface
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import models.CommandsApiModelItem
import models.CommandsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommandsViewModel : ViewModel() {
    private val filteredCommandsData = MutableLiveData<ArrayList<CommandsApiModelItem>>()
    val permissionType = mapOf<Int, String>(1 to "Viewer", 2 to "Subscriber", 3 to "Moderator", 4 to "Bot", 5 to "Admin", 6 to "Broadcaster")
    val commandType = mapOf<Int, String>(0 to "Text", 1 to "Alias", 2 to "System")

    companion object {
        private const val BASE_URL = "https://chewiemelodies.com/"
    }

    private val _text = MutableLiveData<String>().apply {
        value = "No Data Was Found"
    }
    val text: LiveData<String> = _text

    fun getCommandsList() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(CommandsApiInterface::class.java)
        val retrofitData = retrofitBuilder.getCommandsList()
        retrofitData.enqueue(object : Callback<List<CommandsApiModelItem>?> {
            override fun onResponse(
                call: Call<List<CommandsApiModelItem>?>,
                response: Response<List<CommandsApiModelItem>?>
            ) {
                filteredCommandsData.postValue(response.body()!! as ArrayList<CommandsApiModelItem>)
//                commandsList = response.body()!! as ArrayList<CommandsApiModelItem>
//                renderDataToRecyclerView(commandsList!!)
            }

            override fun onFailure(call: Call<List<CommandsApiModelItem>?>, t: Throwable) {
                Log.d("API FAILED", t.message!!)
//                Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun filterData(
        searchedText:String,
        data: ArrayList<CommandsApiModelItem>?
    ): ArrayList<CommandsApiModelItem> {
        var filteredList: ArrayList<CommandsApiModelItem> = ArrayList()
        data?.forEach{
            if(it.commandName.lowercase().contains(searchedText.lowercase()) ||
                permissionType[it.minUserLevel]?.lowercase()?.contains(searchedText.lowercase()) == true ||
                    commandType[it.type]?.lowercase()?.contains(searchedText.lowercase()) == true ||
                    it.content.lowercase().contains(searchedText.lowercase())){
                filteredList.add(it)
            }
        }
        return filteredList
    }

    fun setFilterList(
        filteredList: ArrayList<CommandsApiModelItem>,
        data: ArrayList<CommandsApiModelItem>?,
        s: Editable
    ) {
        if(s.isNotEmpty()){
            filteredCommandsData.postValue(filteredList)
        } else {
            filteredCommandsData.postValue(data)
        }
    }

    fun getFilteredData(): MutableLiveData<ArrayList<CommandsApiModelItem>>{
        return filteredCommandsData
    }

    fun postFilteredCommandsList(data: ArrayList<CommandsApiModelItem>?) {
        filteredCommandsData.postValue(data)
    }
}