fun create(cmd: CreateCommand) {
    val permissions = getUserPermissionsFromExternalService(cmd.uid)
    if (!permissions.canPublishCompilation) throw Exception("无权限")
    if (!valid(cmd)) throw Exception("参数错误")
    val censorResult = checkContentByCensorship(cmd)
    if (!censorResult.pass) throw Exception("")
}