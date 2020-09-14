class Service {

    fun addWeibo(user:Int, title:String, text:String, pics:List<String>):Long {
        val authorities = getUserAuthorityFromExternalService(uid)
        if (!authorities.canPublishWeibo) throw Exception("用户无权限")
        if (user <=0) throw Exception("用户必须登录")
        if(title.len>30) throw Exception("标题长度不得超过 30")
        if (text.len>140) throw Exception("正文不得超过 140")
        val newPics = if (pics.isNotEmpty()) {
            generateWatermarks(pics)
        } else emptyList()
        val id = generatePostId()

        executors.submit {
            retry{
                storeContents(id,title,text,pics)
            }
            writeToDB(user,id, title, text,pics)
        }
        return id
    }
}

class Title(val text:String) {
    init {
        if (text.len>30) throw Exception()
    }
}

class Text(val text:String) {
    init {
        if (text.len>140) throw Exception()
    }
}

class Author(userId:Int, authorities:Authority) {
    init{
        if (usrId<=0) throw Exception()
        if(!authorities.canPublishWeibo) throw Exception()
    }
}

class Weibo(
    val author:Int,
    val id:Long, 
    var title:Title, 
    var text:Text, 
    var waterMarkedPics:List<URL>
)
class Editor(userId:Int, authorities:Authority) {
    init{
        if (usrId<=0) throw Exception()
        if(!authorities.canEditWeibo) throw Exception()
    }
}
class WeiboService {
    private val repo:Repository
    fun addWeibo(author: Author, title:Title, text:Text, waterMarkedPics:List<URL>) {
        val weibo = Weibo(author.userId, repo.nextId(), title, text, waterMarkedPics)
        repo.store(weibo)
    }

    fun modifyWeibo(author: Editor, id:Long, title:Title, text:Text, waterMarkedPics:List<URL>){
        val weibo = repo.find(id)
        weibo.title = title
        weibo.text = text
        weibo.waterMarkedPics = waterMarkedPics
        repo.store(weibo)
    }
}