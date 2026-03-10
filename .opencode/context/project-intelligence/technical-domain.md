<!-- Context: project-intelligence/technical | Priority: critical | Version: 1.0 | Updated: 2026-03-07 -->

# Technical Domain

**Purpose**: Tech stack, architecture, development patterns for oda-history-service.  
**Last Updated**: 2026-03-07

## Quick Reference
**Update Triggers**: Tech stack changes | New patterns | Architecture decisions  
**Audience**: Developers, AI agents

## Primary Stack
| Layer | Technology | Version | Rationale |
|-------|-----------|---------|-----------|
| Framework | Micronaut | Latest | Cloud-native Java framework with native compilation support |
| Language | Java | 17+ | Modern Java with records and sealed classes |
| Database | PostgreSQL | Latest | Primary data store with Flyway migrations |
| Messaging | RabbitMQ | Latest | Event-driven architecture for async operations |
| ORM | Micronaut Data | Latest | Type-safe repository pattern with JDBC support |

## Code Patterns
### API Endpoint
```java
@Controller
@Secured(SecurityRule.IS_AUTHENTICATED)
public CompletableFuture<Void> addHistoryItem(Authentication auth, @Body AddHistoryItemCommand command) {
  var recipientId = getOwnerId(auth);
  if (recipientId.isEmpty()) {
    return CompletableFuture.completedFuture(null);
  }
  var created = new HistoryItemData(...);
  return CompletableFuture.runAsync(() -> repository.create(created));
}

@Serdeable
public static record AddHistoryItemCommand(
  @Nullable String paymentId,
  String nickname,
  Amount amount,
  // ... other fields
) {}
```

**Key patterns:**
- Use `@Controller` with `@Post` for REST endpoints
- Protect with `@Secured(SecurityRule.IS_AUTHENTICATED)`
- Return `CompletableFuture` for async operations
- Use records for command DTOs with `@Serdeable`
- Validate and handle optional fields gracefully

### Component / Domain Model
```java
@Serdeable
@MappedEntity("history")
@Wither
public record HistoryItemData(
  @Id String id,
  @MappedProperty("event_type") String type,
  @Nullable Amount amount,
  @MappedProperty(type = DataType.JSON) List<Attachment> attachments,
  // ... other fields
) {
  @Serdeable
  public record Attachment(String id, String url, String title, String thumbnail) {}
}

@Serdeable
public class HistoryItem {
  private HistoryItemData data;
  
  public void addGoal(TargetGoal goal) {
    var updatedGoals = new ArrayList<>(data.goals());
    updatedGoals.add(goal);
    data = data.withGoals(updatedGoals);
    save();
  }
}
```

**Key patterns:**
- Use `@Serdeable` for all domain models (serialization support)
- Records with `@MappedProperty` annotations for DB mapping
- Wither pattern for immutable updates (`data.withGoals()`)
- Domain objects receive data-records and required beans via constructor
- Nested records for complex types (Attachment, ReelResult, etc.)

### Repository Pattern
```java
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface HistoryItemDataRepository extends CrudRepository<HistoryItemData, String> {
  Page<HistoryItemData> findByRecipientIdOrderByTimestampDesc(String recipientId, Pageable pageable);
  Optional<HistoryItemData> findByOriginId(String paymentId);
}

@Singleton
public class HistoryItemRepository {
  private final HistoryItemDataRepository repository;
  private final HistoryFacade facade;
  
  public CompletableFuture<HistoryItem> create(HistoryItemData data) {
    repository.save(data);
    return facade.sendEvent(new HistoryItemEvent(...))
      .thenApply(it -> convert(data));
  }
}
```

**Key patterns:**
- Micronaut Data repository with `@JdbcRepository` annotation
- Custom query methods (findByRecipientIdOrderByTimestampDesc)
- Repository layer separates data access from business logic
- Event publishing via facade pattern after persistence

## Naming Conventions
| Type | Convention | Example |
|------|-----------|---------|
| Files | PascalCase | `AddHistoryItem.java`, `HistoryItemData.java` |
| Classes/Records | PascalCase | `HistoryItemRepository`, `AddHistoryItemCommand` |
| Methods/Variables | camelCase | `findByRecipientIdOrderByTimestampDesc`, `recipientId` |
| Database Tables | snake_case | `history` |
| DB Columns | snake_case with underscores | `event_type`, `recipient_id`, `authorization_timestamp` |

## Code Standards
- **Domain objects**: Use constructor to pass data-records and required beans
- **Serialization**: Apply `@Serdeable` annotation to all domain models
- **Data mapping**: Use records with `@MappedProperty` for PostgreSQL entities
- **Async operations**: Return `CompletableFuture` for non-blocking I/O
- **Dependency injection**: Constructor injection via `@Inject` annotations
- **Singletons**: Mark service classes with `@Singleton`

## Security Requirements
- All API endpoints protected with `@Secured(SecurityRule.IS_AUTHENTICATED)`
- Use environment variables through YAML configuration files (no hardcoded secrets/tokens)
- Authentication context retrieved from `Authentication` parameter in controller methods
- Owner identification via `getOwnerId(auth)` helper method

## 📂 Codebase References
**Implementation**: 
- Controllers: `src/main/java/io/github/opendonationassistant/history/command/`
- Data models: `src/main/java/io/github/opendonationassistant/history/repository/`
- Domain objects: `src/main/java/io/github/opendonationassistant/history/model/`
- **Config**: 
  - `application.yml` (shared settings)
  - `application-allinone.yml` (test-like environment)
  - `application-standalone.yml` (production-like environment)

## Related Files
- Business Domain (example: business-domain.md)
- Decisions Log (example: decisions-log.md)
