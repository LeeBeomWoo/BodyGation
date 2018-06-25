package bodygate.bcns.bodygation.support

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class PlayModel(val url:String): ViewModel(){
}
class PlayModelFactory(val url: String):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(String::class.java).newInstance(url)
    }
}

class FollowModel(val cate:String, var sect:String?, var plac:String?): ViewModel(){
}
class FollowModelFactory(val cate:String, var sect:String?, var plac:String?): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(String::class.java).newInstance(cate, sect, plac)
    }
}


class GoalModel(val cate:String, var sect:String?, var plac:String?): ViewModel(){
}
class GoalModelFactory(val cate:String, var sect:String?, var plac:String?): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(String::class.java).newInstance(cate, sect, plac)
    }
}