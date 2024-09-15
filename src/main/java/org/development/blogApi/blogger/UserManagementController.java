//package org.development.blogApi.blogger;
//
//import org.development.blogApi.core.blog.BlogService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/blogger/users")
//public class UserManagementController {
//
////    private BanService banService;
////    private BanQueryRepository banQueryRepository;
//    private BlogService blogService;
//
//    @Autowired
//    public UserManagementController(BlogService blogService) {
//        this.blogService = blogService;
//    }
//
//
//    @GetMapping("/blog/{blogId}")
//    public List<BanQueryRepository.BannedUserDto> getBannedForBlogUsers(
//            @PathVariable String blogId,
//            @RequestAttribute("tokenPayload") AuthTokenPayloadDto tokenPayload,
//            Map<String, String> query) {
//
//        return blogsService.findById(blogId).thenCompose(blog -> {
//            if (blog == null) {
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "blog is not found");
//            }
//            if (!blog.getUserId().equals(tokenPayload.getUserId())) {
//                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "can not get data of other user");
//            }
//            return banQueryRepository.findBannedUsersForBlog(blogId, query);
//        });
//    }
//
////    @PutMapping("/{userId}/ban")
////    public CompletableFuture<Void> banUser(
////            @PathVariable String userId,
////            @RequestAttribute("tokenPayload") AuthTokenPayloadDto tokenPayload,
////            @RequestBody CreateBanByBloggerDto createBanByBloggerDto) {
////
////        return commandBus.execute(new BanUserByBloggerCommand(tokenPayload.getUserId(), userId, createBanByBloggerDto))
////                .thenAccept(result -> {
////                    if (result instanceof CustomErrorDto) {
////                        throw new ResponseStatusException(HttpStatus.valueOf(((CustomErrorDto) result).getCode()), ((CustomErrorDto) result).getMessage());
////                    }
////                    if (!result) {
////                        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
////                    }
////                });
////    }
//
//}
