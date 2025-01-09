package lime1st.limeApp.member.presentation;

import jakarta.validation.Valid;
import lime1st.limeApp.member.application.MemberService;
import lime1st.limeApp.member.event.MemberRegistrationEvent;
import lime1st.limeApp.member.presentation.dto.MemberCreateDTO;
import lime1st.limeApp.member.presentation.dto.MemberPutDTO;
import lime1st.limeApp.member.presentation.dto.MemberResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    public static final Logger log = LoggerFactory.getLogger(MemberController.class);

    private final MemberService service;
    private final ApplicationEventPublisher eventPublisher;

    public MemberController(MemberService service, ApplicationEventPublisher eventPublisher) {
        this.service = service;
        this.eventPublisher = eventPublisher;
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable("memberId") String memberId, Principal principal) {
        if (service.delete(memberId, principal.getName())) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //    put 기존에 있던 자원을 요청 바디에 있는 내용으로 변경
    //    patch: put 처럼 기존 자원을 변경하지만 해당 자원의 전체를 변경하는 것이 아니라 일부만 변경한다.
    //    -> 사용자가 자신의 데이터를 변경하는 데 사용하자! 여기서는 password 만 변경 가능하다.
    @PutMapping
    public ResponseEntity<?> putMember(@RequestBody MemberPutDTO putRequest, Principal principal) {
        if (service.update(putRequest.toService(), principal.getName()) != null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    //  post 새로운 자원 생성을 요청하는 메서드
    @PostMapping
    public ResponseEntity<Void> postMember(@Valid @RequestBody MemberCreateDTO newMemberRequest,
                                             UriComponentsBuilder ucb) {
        var serviceDTO = service.join(newMemberRequest.toService());

        URI locationOfNewMember = ucb.path("api/v1/members/{id}")
                .buildAndExpand(serviceDTO.memberId())
                .toUri();

        //  저장된 값을 리턴하기 위해 controller layer dto 로 변경 ->
        //  리턴값 변경으로 사용 안 함. 아래에서 이메일, username 값 불러옴 근데 serviceDTO 를 그냥 써도 된다.
        var registeredMember = MemberResponseDTO.fromService(serviceDTO);

        //  등록 이벤트를 발생시킨다. 이벤트는 외부 사항이라 생각해서 presentation 계층에 넣었다.
        var email = registeredMember.email();
        var username = registeredMember.username();
        if (email.equals(newMemberRequest.email())) {
            eventPublisher.publishEvent(new MemberRegistrationEvent(username, email));
        }

        return ResponseEntity.created(locationOfNewMember).build();
    }

    @GetMapping
//    @PreAuthorize("ADMIN") TODO: 관리자만 전체 회원 목록을 볼 수 있어야 한다.
    public ResponseEntity<?> getAllMembers(Pageable pageable, Principal principal) {
    //  return: CollectionModel<EntityModel<MemberResponseDTO>>

        log.info("pageable: {}", pageable);

        //  TODO: principal 등으로 관리자 계정 확인..

        // List<MemberServiceDTO>
        var memberList = service.findAll(pageable);

        // hateoas, List<EntityModel<MemberResponseDTO>>
        var memberEntities = memberList.stream()
                .map(dto -> EntityModel.of(MemberResponseDTO.fromService(dto),
                        linkTo(methodOn(MemberController.class)
                                .getMember(dto.memberId(), principal)).withSelfRel(),
                        linkTo(methodOn(MemberController.class)
                                .getAllMembers(pageable, principal)).withRel("members")
                )).collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(memberEntities,
                linkTo(methodOn(MemberController.class)
                        .getAllMembers(pageable, principal)).withSelfRel()
        ));
    }

    //    스프링부트 3.2 이후부터 @PathVariable 에 매개변수의 이름을 기입하지 않으면 에러가 발생한다.
    @GetMapping("/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getMember(@PathVariable("memberId") String memberId,
                                       Principal principal) {
        var dto = service.findById(memberId);

        if (dto != null) {
            var responseDTO = new MemberResponseDTO(dto.memberId(), dto.email(), dto.username(), dto.password(),
                    dto.createdAt(), dto.updatedAt());
            var entityModel =  EntityModel.of(responseDTO,
                    linkTo(methodOn(MemberController.class)
                            .getMember(memberId, principal)).withSelfRel(),
                    linkTo(methodOn(MemberController.class)
                            .getAllMembers(Pageable.unpaged(), principal)).withRel("members"),
                    linkTo(methodOn(MemberController.class)
                            .deleteMember(memberId, principal)).withRel("delete"),
                    linkTo(methodOn(MemberController.class)
                            .putMember(new MemberPutDTO(dto.memberId(), dto.email(), dto.username(), dto.password(),
                                            dto.enabled(), dto.role()),
                                    principal)).withRel("update")
            );
            return ResponseEntity.ok(entityModel);
        }
        return ResponseEntity.notFound().build();
    }
}
