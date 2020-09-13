class Service {

    fun addPost(user:Int, title:String, text:String, pics:List<String>):Long {
        if (user <=0) throw Exception("用户必须登录")
        if(title.len>30) throw Exception("标题长度不得超过 30")
        //todo
    }

}