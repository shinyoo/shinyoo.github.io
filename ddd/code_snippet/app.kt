
class CompilationServiceImpl(
         private val compilationRepo: CompilationRepository,
        private val thirdPartyCensorService: ThirdPartyCensorService,
        private val accessControlService: AccessControlService,
        private val applicationEvents: ApplicationEvents,
        private val transactionTemplate: TransactionTemplate,
        private val authorWorkService: AuthorWorkService,
        private val propertyService: CompilationPropertyService
): CompilationService {

    override fun create(author: Author, title: Title, description: Description?, cover: String?): CompilationID {
        //权限检查
        accessControlService.ensureAddBy(author)
        val compilation: Compilation = Compilation.build(
                compilationID = compilationRepo.nextID(),
                author = author,
                title = title,
                description = description,
                cover = cover,
                status = CompilationStatus.PRIVATE
        )
        authorWorkService.ensureDeduplicateTitle(compilation)
        val censorResult: CensorshipCallback = thirdPartyCensorService.beforePublishCheck(compilation)
        Validator.require(censorResult !is CensorshipCallback.CensorshipRejected) { "标题或描述含敏感词,请修改!" }
        if (censorResult is CensorshipCallback.CensorshipApproved) compilation.updateStatus(CompilationStatus.PUBLIC)
        compilationRepo.store(compilation)
        applicationEvents.compilationAdded(compilation)
        return compilation.compilationID
    }
}