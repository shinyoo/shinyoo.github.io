class BadDomain {
    private var id: Int = 0
    private var name: String = ""
    private var createTime: Long = 0L
    private var updateTime: Long = 0L
    private var status: Byte = 0L
    /*...
            setters and getters
    ...*/
}

class GoodDomain {
    private var name:String
    fun setName(newName:String){
        if (newName.length > 30) throw BadRequestException("名称长度过长")
    }
}

/**************************************************************/
class User {
    private var id: Int =0
    private var name: String=""
    private var gender: Gender=FEMALE
    private var phone: String?=null
    private var mail: String?= null
    private var posts: Int = 0
    private var lastLoginTime: Long=0L,
    private var posts: Int=0
}

class Author {
    private var id: Int = 0
    private var gender: Gender=FEMALE
    private var posts: Int=0
    init {
        if(id <= 0) throw BadRequestException("用户 id 无效")
    }
}
/*.......*/
interface addPost(author:Author, post:Post)


/************************************************ */
class UserAccount {
    private var id: Int =0
    private var status:Status=OK
    private var followings: Set<UserAccount>
    
    fun addFollowing(other:UserAccount) {
        followings.add(other)
    }

    fun ban(){
        status = BANNED
    }
}


class UserAccount {
    private val id: Int =0
    private var status:Status=OK
    fun ban(){
        status = BANNED
    }
}
class SocialNetworkUser {
    private val id: Int = 0
    private var followings: Set<SocialNetworkUser>
    fun follow(other:SocialNetworkUser){ /*...*/ }
    fun unfollow(toUnfollow:SocialNetworkUser) { /*...*/ }
}

/************** */

class UserAccount {
    private val id: Int =0
    private var status: Status=OK
    private var banReason: String?=null
    fun ban(reason: OutSideReason){
        status = BANNED
        banReason = when (reason) {
            BAD_BEHAVIOR -> DomainEnum.BAD_BEHAVIOR.str
            ADMIN_BAN -> DomainEnum.ADMIN_BAN.str
            else->null
        }
    }
}

class UserAccount {
    private val id: Int =0
    private var status: Status=OK
    private var banReason: String?=null
    fun ban(reason: DomainEnum){
        status = BANNED
        banReason = reason.str
    }
}