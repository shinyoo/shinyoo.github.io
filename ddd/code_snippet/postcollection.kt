class PostCollection private constructor(
        val compilationID: CompilationID,
        private var holdType: HoldType,
        private var regular: Set<PostID>,
        private var tops: List<PostID>
) : SmartEntity<PostCollection>() {

    fun holdType() = holdType

    @SmartEntity.Companion.NotProp
    val size: Int
        get() = regular.size + tops.size

    @SmartEntity.Companion.NotProp
    val postSet: Set<PostID>
        get() = regular + tops

    fun regular() = regular
    fun tops() = tops


    fun add(inputs: Set<InputPost>) {
        val posts = inputs.filter { it.postID !in postSet }
        Validator.require(posts.isNotEmpty()) { "帖子已经在合集里啦" }
        Validator.require(posts.size + size <= 500) { "合集下最多添加 500 个帖子" }
        //合集为空时, 使用第一个传入的帖子来决定类型
        if (size == 0) {
            if (holdType != HoldType.SERIAL_TYPE) {
                holdType = HoldType.getValidHoldType(posts.first().typeInfo)
            }
        }
        checkType(posts)
        regular = regular.plus(posts.map { it.postID })
    }


    fun remove(posts: Set<PostID>) {
        tops = tops.minus(posts)
        regular = regular.minus(posts)
        //合集为空时, 使用第一个传入的帖子来决定类型
        if (holdType != HoldType.SERIAL_TYPE && size == 0) {
            holdType = HoldType.NO_TYPE
        }
    }

    fun setOnTop(posts: Set<InputPost>) {
        Validator.require(posts.isNotEmpty()) { "置顶帖子不能为空" }
        //只允许置顶已有的帖子
        Validator.require(posts.all { it.postID in regular || it.postID in tops }) { "合集中没有该帖子" }
        //同时置顶的按照帖子创建时间倒序, 非同时置顶的按照置顶时间倒序
        val newTops = (posts.sortedByDescending { it.createTime }
                .map { it.postID } + tops
                ).distinct().take(2)        //最多允许 2 个置顶帖子
        //被挤出置顶的帖子
        val outOfTops = tops.minus(newTops)
        tops = newTops
        //非置顶池需要去掉被置顶的帖子, 并且加上被挤出置顶的帖子
        regular = regular.minus(newTops).plus(outOfTops)
    }

    fun unsetOnTop(posts: Set<PostID>) {
        val toRem = tops.filter { it in posts }
        tops = tops.minus(toRem)
        regular = regular.plus(toRem)
    }

    private fun checkType(posts: Collection<InputPost>) {
        posts.forEach { post ->
            Validator.require(this.holdType.canHold(post.typeInfo)) { "帖子和合集类型不兼容" }
        }
    }

    override fun sameIdentityAs(other: PostCollection): Boolean {
        return this == other
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PostCollection

        if (compilationID != other.compilationID) return false

        return true
    }

    override fun hashCode(): Int {
        return compilationID.hashCode()
    }

    companion object {

        fun build(
                cid: CompilationID,
                holdType: HoldType,
                regular: Set<PostID> = emptySet(),
                tops: List<PostID> = emptyList()
        ): PostCollection {
            return new {
                PostCollection(
                        compilationID = cid,
                        holdType = holdType,
                        regular = regular,
                        tops = tops
                )
            }
        }
    }
}
