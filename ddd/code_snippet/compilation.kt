fun create(cmd: CreateCommand) {
    val permissions = getUserPermissionsFromExternalService(cmd.uid)
    if (!permissions.canPublishCompilation) throw Exception("无权限")
    validate(cmd)
    val censorResult = checkContentByCensorship(cmd)
    if (!censorResult.pass) throw Exception("未通过初审")
    val status = if (censorResult.setPublic) Status.PUBLIC else Status.PRIVATE
    
    dao.insertCompilation(cmd.uid, cmd.title, cmd.description, cmd.cover, status)

}

fun validate(cmd: CreateCommand) {
    if (cmd.title.length >30) throw Exception("标题过长")
    if (cmd.description.length >80) throw Exception("描述过长")
}