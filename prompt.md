# Muldum 프로젝트 코드 컨벤션 및 규칙

> item 도메인 분석 기반

## 행동규칙
- 매 행동을 할 때마다 커밋을 작게 쪼개서 진행할 것
- 커밋 메시지는 명확하고 간결하게 작성할 것
- 커밋 컨벤션은 다음과 같이 작성할 것:  
  - feat: 새로운 기능 추가  
  - fix: 버그 수정  
  - docs: 문서 수정  
  - style: 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우  
  - refactor: 코드 리팩토링  
  - test: 테스트 코드 추가 및 수정  
  - chore: 빌드 업무 수정, 패키지 매니저 설정 등 기타 변경사항
  - 형식은 `<타입>: <한글설명>`으로 이루어져 있음

## 목차
0. [규칙](#규칙)
1. [프로젝트 구조](#프로젝트-구조)
2. [패키지 구조 규칙](#패키지-구조-규칙)
3. [네이밍 컨벤션](#네이밍-컨벤션)
4. [어노테이션 사용 규칙](#어노테이션-사용-규칙)
5. [엔티티 설계 규칙](#엔티티-설계-규칙)
6. [DTO 설계 규칙](#dto-설계-규칙)
7. [서비스 레이어 규칙](#서비스-레이어-규칙)
8. [컨트롤러 레이어 규칙](#컨트롤러-레이어-규칙)
9. [예외 처리 규칙](#예외-처리-규칙)
10. [로깅 규칙](#로깅-규칙)
11. [트랜잭션 관리](#트랜잭션-관리)
12. [검증(Validation) 규칙](#검증validation-규칙)

---

## 규칙
- 항상 위 규칙을 우선시할것
- SOLID 원칙 준수
- 함수의 depth는 3을 넘지 않도록 할것
- OOP 설계 원칙 준수
- 객체지향 생활체조원칙 준수

---

## 프로젝트 구조

### 기본 패키지 구조
```
co.kr.muldum/
├── application/          # 애플리케이션 서비스 (Facade 패턴)
├── domain/              # 도메인 로직
│   ├── item/
│   │   ├── dto/        # Data Transfer Objects
│   │   │   ├── req/   # Request DTO
│   │   │   └── res/   # Response DTO
│   │   ├── model/      # 엔티티 및 VO
│   │   │   └── enums/ # 열거형
│   │   ├── repository/ # JPA Repository
│   │   ├── service/    # 도메인 서비스
│   │   └── validator/  # 검증 로직
├── global/              # 공통 설정 및 유틸
│   ├── config/
│   ├── dto/
│   ├── exception/
│   ├── security/
│   └── util/
├── infrastructure/      # 외부 시스템 연동
└── presentation/        # 컨트롤러 레이어
```

---

## 패키지 구조 규칙

### 1. **도메인 중심 설계**
- 각 도메인(item, user, teamspace 등)은 독립적인 패키지로 분리
- 도메인 내부에서 model, dto, service, repository, validator를 포함

### 2. **레이어별 책임 분리**
- **presentation**: HTTP 요청/응답 처리, 권한 검증
- **application**: 여러 도메인 서비스 조합 (Facade)
- **domain/service**: 순수 비즈니스 로직
- **domain/repository**: 데이터 접근 추상화
- **infrastructure**: 외부 API, 파일 시스템 등

### 3. **DTO 패키지 세분화**
```
dto/
├── SomeDto.java              // 공통 DTO
├── req/                      // Request 전용
│   └── ItemOpenRequest.java
└── res/                      // Response 전용
    └── ItemGuideResponse.java
```

---

## 네이밍 컨벤션

### 1. **클래스명**
- **Entity**: 명사 단수형 (예: `ItemRequest`, `NthStatus`)
- **DTO**: 명확한 용도 표시 (예: `ItemResponseDto`, `TempItemRequestDto`)
- **Service**: 역할 명시 (예: `ItemRequestService`, `ItemRequestFinalizer`)
- **Controller**: `{역할}{도메인}Controller` (예: `StudentItemController`, `TeacherItemController`)
- **Repository**: `{Entity명}Repository` (예: `ItemRequestRepository`)
- **Validator**: `{대상}Validator` (예: `ProductLinkValidator`)

### 2. **메서드명**
- **생성**: `create`, `build` (예: `createTempItemRequest`)
- **조회**: `get`, `find`, `read` (예: `getTempItemRequests`, `findByTeamId`)
- **수정**: `update`, `modify` (예: `updateTempItemRequest`)
- **삭제**: `delete`, `remove` (예: `deleteTempItemRequest`)
- **검증**: `validate` (예: `validateProductLink`)
- **변환**: `convert`, `to` (예: `convertToTempListDto`)
- **판단**: `decide`, `is` (예: `decideStatus`, `isRejected`)

### 3. **변수명**
- **DTO 필드**: `snake_case` 사용 (JSON 매핑용)
  ```java
  private String product_name;
  private String product_link;
  ```
- **Java 변수**: `camelCase` 사용
  ```java
  private Integer teamId;
  private Long userId;
  ```
- **상수**: `UPPER_SNAKE_CASE`
  ```java
  private static final int MAX_RETRY_COUNT = 3;
  ```

### 4. **URL 패스**
- **snake_case** 사용 (예: `/std/items/temp`, `/tch/items/open-status`)
- **역할 기반 prefix**: `/std/` (학생), `/tch/` (선생님), `/ara/` (공통)

---

## 어노테이션 사용 규칙

### 1. **Lombok 어노테이션 순서**
```java
@Entity
@Table(name = "item_requests")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemRequest { }
```

### 2. **Spring 어노테이션 순서**
```java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemRequestService { }
```

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/std/items")
@Slf4j
@PreAuthorize("hasRole('STUDENT')")
public class StudentItemController { }
```

### 3. **필수 어노테이션**
- **Service**: `@Service`, `@RequiredArgsConstructor`, `@Slf4j`
- **Controller**: `@RestController`, `@RequiredArgsConstructor`, `@Slf4j`
- **Entity**: `@Entity`, `@Table`, `@Getter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **DTO**: `@Getter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **Record**: DTO의 간단한 경우 record 사용 가능
  ```java
  public record ItemOpenRequest(
      String projectType,
      List<ItemMinPriceRequest> guide,
      String deadlineDate
  ) {}
  ```

---

## 엔티티 설계 규칙

### 1. **기본 구조**
```java
@Entity
@Table(name = "item_requests")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemRequest {
    
    @Id
    private Long id;
    
    @Column(name = "team_id", nullable = false)
    private Integer teamId;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ItemStatus status = ItemStatus.PENDING;
    
    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
            this.id = generator.generateId(this.teamId);
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

### 2. **필수 규칙**
- **불변성**: Setter 금지, 업데이트는 명확한 메서드 제공
  ```java
  public void updateStatus(ItemStatus status) {
      this.status = status;
  }
  
  public void updateProductInfo(ProductInfo productInfo) {
      this.productInfo = productInfo;
  }
  ```

- **Enum은 STRING 저장**
  ```java
  @Enumerated(EnumType.STRING)
  private ItemStatus status;
  ```

- **JSONB 컬럼 매핑**
  ```java
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "product_info", columnDefinition = "jsonb", nullable = false)
  private ProductInfo productInfo;
  ```

- **컬럼명 명시**
  ```java
  @Column(name = "requester_user_id", nullable = false)
  private Integer requesterUserId;
  ```

### 3. **ID 생성 전략**
- **일반**: `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- **Snowflake**: `SnowflakeIdGenerator` 사용 (분산 환경 대응)

---

## DTO 설계 규칙

### 1. **Request DTO**
```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TempItemRequestDto {
    private String product_name;
    private Integer quantity;
    private String price;
    
    @JsonAlias("product_link")
    private String productLink;
    
    private String reason;
    
    @JsonAlias("delivery_price")
    private String deliveryPrice;
}
```

**규칙:**
- 필드명은 `snake_case` (프론트엔드 규격)
- Java 관례 필드는 `@JsonAlias` 사용
- 검증은 Validator 클래스에서 처리 (jakarta.validation 사용 최소화)

### 2. **Response DTO**
```java
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {
    private String status;
    private String message;
}
```

**규칙:**
- `@Builder` 패턴 사용
- 필드명은 `snake_case`
- null 가능성 고려한 설계

### 3. **Record 사용 케이스**
- 단순 데이터 전달용
- 불변성이 보장되어야 하는 경우
```java
public record ItemOpenRequest(
    String projectType,
    List<ItemMinPriceRequest> guide,
    String deadlineDate
) {}
```

---

## 서비스 레이어 규칙

### 1. **서비스 분리 전략**
- **{Domain}Service**: 기본 CRUD 및 조회
- **{Domain}Facade**: 여러 서비스 조합
- **{Domain}Executor**: 실제 실행 로직
- **{Domain}Finalizer**: 최종 처리 로직
- **{Domain}QueryService**: 복잡한 조회 전담

```java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemRequestService {
    
    private final ItemRequestFacade itemRequestFacade;
    private final ItemRequestRepository itemRequestRepository;
    private final UserReader userReader;
    
    public ItemResponseDto createTempItemRequest(TempItemRequestDto requestDto, Long userId) {
        log.info("임시 물품 생성 요청 - userId={}", userId);
        return itemRequestFacade.createTempItemRequest(requestDto, userId);
    }
}
```

### 2. **의존성 주입**
- **Constructor Injection** 사용 (`@RequiredArgsConstructor`)
- Field Injection 금지
```java
// ❌ 금지
@Autowired
private ItemRequestRepository repository;

// ✅ 권장
private final ItemRequestRepository repository;
```

### 3. **반환 타입**
- DTO 반환 (Entity 직접 반환 금지)
- 컬렉션은 `List<>` 사용 (구체적 구현체 노출 금지)

---

## 컨트롤러 레이어 규칙

### 1. **기본 구조**
```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/std/items")
@Slf4j
@PreAuthorize("hasRole('STUDENT')")
public class StudentItemController {
    
    private final ItemRequestService itemRequestService;
    private final UserReader userReader;
    
    @GetMapping
    public ResponseEntity<List<ItemListResponseDto>> getTeamItems(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserInfo userInfo = userReader.read(User.class, userDetails.getUserId());
        List<ItemListResponseDto> items = itemListService.getTeamItemRequests(userInfo);
        return ResponseEntity.ok(items);
    }
}
```

### 2. **역할 기반 분리**
- **StudentItemController**: 학생용 API (`/std/**`)
- **TeacherItemController**: 선생님용 API (`/tch/**`)
- **ItemController**: 공통 API

### 3. **권한 검증**
- 클래스 레벨: `@PreAuthorize("hasRole('STUDENT')")`
- 메서드 레벨: 필요시 추가 권한 검증

### 4. **PathVariable & RequestParam**
```java
@GetMapping("/{item_id}")
public ResponseEntity<ItemResponseDto> getItem(
        @PathVariable("item_id") Long itemId,
        @RequestParam(required = false) Integer nth,
        @AuthenticationPrincipal CustomUserDetails userDetails
) { }
```

### 5. **응답 처리**
```java
// 성공
return ResponseEntity.ok(response);

// 조건부 에러 처리
private ResponseEntity<ItemResponseDto> handleItemResponse(ItemResponseDto response) {
    if ("REJECTED".equals(response.getStatus())) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
}
```

---

## 예외 처리 규칙

### 1. **CustomException 사용**
```java
throw new CustomException(ErrorCode.ITEM_NOT_FOUND);
throw new CustomException(ErrorCode.FORBIDDEN_TEAM_ITEM);
```

### 2. **ErrorCode 정의**
```java
@Getter
public enum ErrorCode {
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "물품을 찾을 수 없습니다."),
    FORBIDDEN_TEAM_ITEM(HttpStatus.FORBIDDEN, "자신이 속한 팀의 물품만 취소할 수 있습니다."),
    INVALID_PRODUCT_LINK(HttpStatus.BAD_REQUEST, "상품 링크가 유효하지 않습니다.");
    
    private final HttpStatus status;
    private final String message;
    
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
```

### 3. **검증 실패 처리**
```java
// Validator 결과 처리
ValidationResult result = validator.validateProductLink(productLink, userId);
if (!result.isValid()) {
    return ItemResponseDto.builder()
            .status(ItemStatus.REJECTED.name())
            .message(result.getErrorMessage())
            .build();
}
```

---

## 로깅 규칙

### 1. **로그 레벨**
- **INFO**: 중요 비즈니스 로직 시작/종료
- **DEBUG**: 상세한 처리 과정
- **WARN**: 예상 가능한 이상 상황
- **ERROR**: 예외 발생 상황

### 2. **로깅 패턴**
```java
// 요청 시작
log.info("임시 물품 생성 요청 - userId={}", userId);

// 상세 정보
log.debug("임시 물품 수정 - 사용자 정보: userId={}, teamId={}, userType={}",
        userInfo.getUserId(), userInfo.getTeamId(), userInfo.getUserType());

// 경고
log.warn("물품 신청 실패 - productLink가 비었습니다. userId={}", userId);

// 에러
log.error("물품신청 n차 문제 해결 중 오류 발생: {}", e.getMessage(), e);
```

### 3. **필수 로깅 위치**
- Controller 진입점
- 중요 비즈니스 로직 실행 전
- 예외 발생 시
- 데이터 삭제/수정 시

---

## 트랜잭션 관리

### 1. **기본 원칙**
```java
@Service
@RequiredArgsConstructor
@Transactional  // 기본: readOnly = false
@Slf4j
public class ItemRequestService { }
```

### 2. **읽기 전용 최적화**
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 클래스 레벨
@Slf4j
public class TeacherItemService {
    
    @Transactional  // 쓰기 작업만 오버라이드
    public void updateItem(Long itemId) { }
}
```

### 3. **트랜잭션 범위**
- Service 레이어에서 관리
- Controller는 트랜잭션 경계 밖
- Repository는 트랜잭션 내부에서만 호출

---

## 검증(Validation) 규칙

### 1. **Validator 클래스 분리**
```java
@Component
@Slf4j
public class ProductLinkValidator {
    
    public ValidationResult validateProductLink(String productLink, Long userId) {
        if (productLink == null || productLink.isBlank()) {
            log.warn("물품 신청 실패 - productLink가 비었습니다. userId={}", userId);
            return ValidationResult.fail("상품 링크가 유효하지 않습니다.");
        }
        return ValidationResult.success();
    }
    
    @Getter
    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;
        
        private ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult fail(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }
    }
}
```

### 2. **도메인별 Validator**
- `ProductLinkValidator`: 상품 링크 검증
- `TeamValidator`: 팀 권한 검증
- `ItemSourceValidator`: 아이템 소스 검증

### 3. **검증 타이밍**
- Service 진입 시 (Facade 또는 Service 초반)
- 비즈니스 로직 실행 전
- DB 작업 전

---

## 추가 규칙

### 1. **Enum 활용**
```java
public enum ItemStatus {
    PENDING,
    INTEMP,
    APPROVED,
    REJECTED,
    DELETED;
    
    public boolean isRejected() {
        return this == REJECTED;
    }
}
```
- 상태값은 Enum으로 관리
- 비즈니스 로직 메서드 추가 가능

### 2. **Result 객체 패턴**
```java
@Getter
public static class FinalizeResult {
    private final ItemStatus status;
    private final String message;
    
    private FinalizeResult(ItemStatus status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public static FinalizeResult of(ItemStatus status, String message) {
        return new FinalizeResult(status, message);
    }
}
```

### 3. **Repository 쿼리 메서드**
```java
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByTeamIdAndStatus(Integer teamId, ItemStatus itemStatus);
    List<ItemRequest> findByStatusIn(List<ItemStatus> statuses);
    
    @Query("SELECT ir FROM ItemRequest ir WHERE ir.status = :itemStatus")
    List<ItemRequest> findByStatusAndNthAndStartAndEnd(
        ItemStatus itemStatus, 
        Integer nth, 
        LocalDateTime start, 
        LocalDateTime end
    );
}
```

### 4. **불필요한 주석 지양**
- 코드 자체로 의미 전달
- 복잡한 비즈니스 로직만 주석 추가
- 메서드명으로 의도 표현

---

## 체크리스트

새로운 기능 개발 시 확인사항:

- [ ] 패키지 구조가 도메인 중심으로 구성되어 있는가?
- [ ] Entity는 불변성을 지키고 있는가?
- [ ] DTO 필드명이 snake_case인가?
- [ ] Service에 `@Transactional`이 적절히 적용되었는가?
- [ ] Controller에 역할 기반 권한(`@PreAuthorize`)이 설정되었는가?
- [ ] 예외 처리는 `CustomException`과 `ErrorCode`를 사용하는가?
- [ ] 중요 로직에 적절한 로깅이 추가되었는가?
- [ ] Validator를 통한 검증이 이루어지는가?
- [ ] Repository 메서드명이 JPA 규칙을 따르는가?
- [ ] Entity를 직접 반환하지 않고 DTO로 변환하는가?

---

## 참고사항

- **프레임워크**: Spring Boot 3.x
- **Java 버전**: Java 17+
- **빌드 도구**: Gradle
- **ORM**: JPA (Hibernate)
- **데이터베이스**: PostgreSQL (JSONB 컬럼 사용)
- **인증/인가**: Spring Security + JWT
- **로깅**: SLF4J + Logback

---

**Last Updated**: 2025-11-23  
**Based on**: item 도메인 분석

