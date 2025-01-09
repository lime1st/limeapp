package lime1st.limeApp.blog.presentation;

import lime1st.limeApp.blog.application.BlogService;
import lime1st.limeApp.blog.application.dto.BlogDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {

    private static final Logger log = LoggerFactory.getLogger(BlogController.class);

    private final BlogService service;

    public BlogController(BlogService service) {
        this.service = service;
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> deleteBlog(@PathVariable("requestId") Long requestedId, Principal principal) {
        // ResponseEntity<Void>
        if (service.deleteByIdAndAuthor(requestedId, principal.getName())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<?> putBlog(@PathVariable("requestId") Long requestId,
                                     @RequestBody BlogDTO updateBlogRequest, Principal principal) {
        if (service.putByIdAndAuthor(requestId, updateBlogRequest, principal.getName())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createBlog(@RequestBody @Validated BlogDTO newBlogRequest,
                                        UriComponentsBuilder ucb, Principal principal) {
        var blogDTOWithAuthor = new BlogDTO(null, newBlogRequest.title(),
                newBlogRequest.content(), principal.getName(), null, null);

        var savedBlogId = service.create(blogDTOWithAuthor);

        var entityModel = EntityModel.of(blogDTOWithAuthor,
                linkTo(methodOn(BlogController.class).getBlogById(savedBlogId, principal)).withSelfRel(),
                linkTo(methodOn(BlogController.class).getAllBlog(null, principal)).withRel("blogs")
        );

        var locationOfNewBlog = ucb.path("/api/v1/blogs/{id}")
                .buildAndExpand(savedBlogId)
                .toUri();
        return ResponseEntity.created(locationOfNewBlog).body(entityModel);
    }

    @GetMapping
    public ResponseEntity<?> getAllBlog(Pageable pageable, Principal principal) {
        // ResponseEntity<CollectionModel<EntityModel<BlogDTO>>>
        var blogDTOList = service.findAllByAuthor(pageable, principal.getName());

        var blogEntities = blogDTOList.stream()
                .map(blogDTO -> EntityModel.of(blogDTO,
                        linkTo(methodOn(BlogController.class).getBlogById(blogDTO.id(), principal)).withSelfRel(),
                        linkTo(methodOn(BlogController.class).getAllBlog(pageable, principal)).withRel("blogs")
                )).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(blogEntities,
                linkTo(methodOn(BlogController.class).getAllBlog(pageable, principal)).withSelfRel()
        ));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<?> getBlogById(@PathVariable("requestId") Long requestId, Principal principal) {
        // ResponseEntity<EntityModel<BlogDTO>>
        var blogDTO = service.findByIdAndAuthor(requestId, principal.getName());

        if (blogDTO != null) {
            EntityModel<BlogDTO> entityModel = EntityModel.of(blogDTO,
                    linkTo(methodOn(BlogController.class).getBlogById(requestId, principal)).withSelfRel(),
                    linkTo(methodOn(BlogController.class).getAllBlog(Pageable.unpaged(), principal)).withRel("blogs"),
                    linkTo(methodOn(BlogController.class).deleteBlog(requestId, principal)).withRel("delete"),
                    linkTo(methodOn(BlogController.class).putBlog(requestId, blogDTO, principal)).withRel("update")
            );
            return ResponseEntity.ok(entityModel);
        }
        return ResponseEntity.notFound().build();
    }
}