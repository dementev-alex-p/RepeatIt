SET search_path TO repeat_it;

INSERT INTO card_collection (name, author_id, is_public)
VALUES ('Spring. Основные понятия', 1, true);

INSERT INTO card (user_id, card_collection_id, front_side, back_side)
SELECT 1,
       (SELECT card_collection_id FROM card_collection WHERE name = 'Spring. Основные понятия'),
       front_side,
       back_side
FROM (VALUES ('Что такое Spring Framework?',
              'Spring Framework - это комплексный фреймворк для разработки Java приложений. Предоставляет инфраструктуру для dependency injection, транзакций, веб-разработки, безопасности и работы с данными. Упрощает разработку enterprise приложений через модульную архитектуру и уменьшает связность кода.'),

             ('Как работает Dependency Injection в Spring?',
              'DI - паттерн, при котором зависимости передаются объектам извне, а не создаются внутри. Spring использует IoC контейнер для управления бинами и их зависимостями. Реализации: через конструктор (рекомендуется), сеттеры или поля. Аннотации: @Autowired, @Inject, @Resource.'),

             ('В чем разница между @Component, @Service, @Repository?',
              '@Component - общая стереотипная аннотация. @Service - для бизнес-логики. @Repository - для DAO слоя, автоматически перехватывает исключения JDBC. Все являются производными от @Component, но имеют семантические различия для лучшей читаемости кода.'),

             ('Как работает Spring Boot Autoconfiguration?',
              'Автоконфигурация анализирует classpath и настройки приложения, затем автоматически конфигурирует бины. Использует spring.factories файлы для регистрации конфигурационных классов. Условная конфигурация через @Conditional аннотации. Можно переопределить через явную конфигурацию.'),

             ('Что такое Spring Bean Scope?',
              'Scope определяет жизненный цикл бина. Основные: singleton (один экземпляр на контейнер), prototype (новый экземпляр при каждом запросе). Веб-скопы: request, session, application. Настраивается через @Scope аннотацию.'),

             ('Как настроить Spring Security?',
              'Создать класс конфигурации с @EnableWebSecurity. Переопределить configure(HttpSecurity) для настройки доступа. Реализовать UserDetailsService для аутентификации. Использовать PasswordEncoder для хеширования паролей. Настроить CORS и CSRF защиту.'),

             ('Что делает @Transactional?',
              '@Transactional управляет транзакциями на уровне метода или класса. Автоматически открывает и закрывает транзакции. Настраивает propagation (REQUIRED, REQUIRES_NEW), isolation level, timeout, readOnly флаг. Работает через AOP прокси.'),

             ('Как работает Spring MVC?',
              'Запрос обрабатывается DispatcherServlet, который ищет подходящий Controller через HandlerMapping. Controller обрабатывает запрос, возвращает ModelAndView. ViewResolver определяет представление. Модель передается в представление для рендеринга.'),

             ('Что такое Spring Data JPA?',
              'Spring Data JPA упрощает реализацию JPA репозиториев. Создает реализации репозиториев автоматически. Поддерживает производные методы по именам. Предоставляет JpaRepository с CRUD операциями. Интегрируется с Hibernate, EclipseLink.'),

             ('Для чего нужен Spring Boot Actuator?',
              'Actuator предоставляет production-ready фичи для мониторинга и управления приложением. Endpoints: /health, /metrics, /info, /beans. Позволяет мониторить состояние приложения, метрики, логи, конфигурацию. Интегрируется с Micrometer для метрик.'),

             ('Как тестировать Spring приложения?',
              '@SpringBootTest для интеграционных тестов. @WebMvcTest для тестирования MVC слоя. @DataJpaTest для тестирования JPA. @MockBean для мокинга бинов. TestRestTemplate для тестирования REST API. @TestPropertySource для тестовых настроек.'),

             ('Что такое Spring Profiles?',
              'Profiles позволяют условную активацию бинов и конфигураций. Используется для разделения сред (dev, test, prod). Активируется через spring.profiles.active. @Profile("dev") над классом конфигурации или бином.'),

             ('Как работает Spring AOP?',
              'AOP разделяет сквозную функциональность через аспекты. Advice: @Before, @After, @Around, @AfterThrowing. Pointcut определяет где применяется advice. Использует динамические прокси (JDK или CGLIB). Применяется для логирования, транзакций, безопасности.'),

             ('Что такое Spring Boot Starter?',
              'Starter - это набор предварительно сконфигурированных зависимостей. Упрощает сборку приложения. Примеры: spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-security. Содержит автоматические конфигурации и зависимости.'),

             ('Как настроить многомодульность в Spring Boot?',
              'Создать parent pom с spring-boot-starter-parent. Модули объявлять как <module>. В дочерних модулях добавить зависимость на родителя. Использовать @SpringBootApplication в основном модуле. @ComponentScan для сканирования пакетов.'),

             ('Что такое Spring Cloud?',
              'Spring Cloud предоставляет инструменты для микросервисной архитектуры. Config Server - централизованная конфигурация. Eureka - service discovery. Zuul - API gateway. Hystrix - circuit breaker. Feign - declarative REST client.'),

             ('Как работает кэширование в Spring?',
              '@EnableCaching включает кэширование. @Cacheable кэширует результат метода. @CacheEvict удаляет из кэша. @CachePut обновляет кэш. Поддерживается Redis, Ehcache, Caffeine, Hazelcast. Настраивается через CacheManager.'),

             ('Что такое Spring Expression Language (SpEL)?',
              'SpEL - мощный язык выражений для работы с графом объектов во время выполнения. Используется в аннотациях @Value, Spring Security, XML конфигурациях. Синтаксис: #{expression}. Поддерживает операции, методы, свойства, тернарный оператор.'),

             ('Как настроить REST API в Spring?',
              '@RestController вместо @Controller. @RequestMapping, @GetMapping, @PostMapping для маппинга. @RequestBody для приема JSON/XML. @ResponseBody для возврата данных. ResponseEntity для контроля статуса и заголовков. Использовать DTO вместо сущностей.'),

             ('Что такое Spring Boot DevTools?',
              'DevTools ускоряет разработку. Автоматический перезапуск при изменении классов. LiveReload для обновления браузера. Отключает кэширование шаблонов. Настройки по умолчанию для разработки. Работает только в dev среде.'),

             ('Как мигрировать с XML на Java конфигурацию?',
              'Заменить <bean> на @Configuration классы. @Bean методы вместо bean определений. @Import для импорта конфигураций. @PropertySource для свойств. Использовать Spring Boot для автоматической миграции. Постепенная миграция через @ImportResource.'),

             ('Что такое Spring Batch?',
              'Spring Batch - фреймворк для пакетной обработки данных. Состоит из Job (работа) и Step (шаги). ItemReader, ItemProcessor, ItemWriter для обработки. Поддерживает повторные попытки, пропуск ошибок, управление транзакциями. Используется для ETL процессов.'),

             ('Как настроить мониторинг в Spring Boot?',
              'Использовать Spring Boot Actuator. Добавить micrometer-registry-prometheus для Prometheus. Настроить Grafana для визуализации. Использовать @Timed, @Counted для кастомных метрик. HealthIndicator для кастомных проверок здоровья.'),

             ('Что такое Reactive Spring?',
              'Reactive Spring - неблокирующая, асинхронная обработка запросов. Использует WebFlux вместо MVC. Работает через Project Reactor (Mono, Flux). Поддерживает backpressure. Увеличивает производительность при I/O bound операциях. Требует reactive драйверы БД.'),

             ('Как работает Spring Security OAuth2?',
              'OAuth2 авторизация через сторонние провайдеры. Реализует Authorization Server, Resource Server. @EnableAuthorizationServer, @EnableResourceServer. Хранит токены в БД или JWT. Интегрируется с Keycloak, Google, GitHub.'),

             ('Что такое Spring Boot Test Slice?',
              'Test Slice - тестирование только определенного слоя. @WebMvcTest - только web слой. @DataJpaTest - только JPA. @JsonTest - только JSON сериализация. @RestClientTest - только REST клиенты. Ускоряет тестирование, загружая только нужные бины.'),

             ('Как настроить многопоточность в Spring?',
              '@EnableAsync включает асинхронное выполнение. @Async на методах. TaskExecutor для управления пулами потоков. @Scheduled для периодических задач. CompletableFuture для цепочек асинхронных операций. Настройка через ThreadPoolTaskExecutor.'),

             ('Что такое Spring Boot Configuration Processor?',
              'Configuration Processor генерирует метаданные для кастомных свойств. Позволяет IDE подсказывать доступные свойства в application.yml/properties. Добавляется как optional зависимость. Создает spring-configuration-metadata.json.'),

             ('Как работает Spring Boot Externalized Configuration?',
              'Внешняя конфигурация загружает свойства из разных источников. Порядок: командная строка, переменные окружения, application-{profile}.properties, application.properties, @PropertySource, default значения. Конфигурируется через @ConfigurationProperties.'),

             ('Что такое Spring Integration?',
              'Spring Integration - фреймворк для Enterprise Integration Patterns. Обрабатывает сообщения через каналы. Поддерживает трансформаторы, фильтры, маршрутизаторы. Интегрируется с JMS, FTP, HTTP, Web Services. Упрощает интеграцию разнородных систем.'),

             ('Как настроить CORS в Spring?',
              '@CrossOrigin на контроллере или методе. WebMvcConfigurer.addCorsMappings() для глобальной настройки. CorsConfigurationSource для кастомной конфигурации. В Spring Security: HttpSecurity.cors(). Указывать origins, methods, allowedHeaders.'),

             ('Что такое Spring Boot Docker?',
              'Docker-образ Spring Boot приложения. Использовать multi-stage build для уменьшения размера. Jib plugin для создания образов без Docker. Spring Boot 2.3+ поддерживает Cloud Native Buildpacks. Настройка health checks, ресурсов, переменных окружения.'),

             ('Как работает Retry в Spring?',
              '@Retryable для повторных попыток при исключениях. @Recover для fallback метода. Настройка: maxAttempts, backoff, retryOn. Использует Spring AOP. Подходит для временных сбоев (сеть, БД). Интегрируется с Circuit Breaker.'),

             ('Что такое Spring Boot Admin?',
              'Spring Boot Admin - веб-интерфейс для мониторинга приложений. Агрегирует данные с Actuator endpoints. Показывает health, metrics, logs, configuration. Управляет JMX бинами. Отправляет уведомления при изменении статуса.'),

             ('Как настроить Swagger в Spring Boot?',
              'SpringDoc OpenAPI 3.0 для документации API. @Operation, @ApiResponse для аннотирования методов. Генерирует JSON спецификацию и UI. Настройка через application.properties. Группировка API по тегам. Интеграция с Spring Security.'),

             ('Что такое Spring Boot Gradle Plugin?',
              'Gradle Plugin для сборки Spring Boot приложений. spring-boot-gradle-plugin. Задачи: bootRun (запуск), bootJar (создание jar), bootBuildImage (Docker образ). Управляет зависимостями через BOM. Поддерживает layered jar для Docker.'),

             ('Как работает Validation в Spring?',
              '@Valid для валидации входных данных. @NotNull, @Size, @Pattern аннотации. Custom validators через ConstraintValidator. BindingResult для обработки ошибок. @Validated для валидации на уровне сервиса. Группы валидации.'),

             ('Что такое Spring Boot Logging?',
              'Логирование через SLF4J с реализацией Logback. Настройка уровня логирования через application.properties. Profile-specific конфигурации. Custom logback-spring.xml. Structured logging для JSON формата. Интеграция с ELK стеком.'),

             ('Как настроить Flyway/Liquibase в Spring Boot?',
              'Миграции БД через Flyway или Liquibase. Автоматическое применение при старте. Flyway: SQL скрипты в db/migration. Liquibase: changelog в XML/YAML. @ConditionalOnProperty для контроля в разных средах. Интеграция с Spring Boot Actuator.'),

             ('Что такое Spring Boot Conditionals?',
              '@Conditional аннотации для условного создания бинов. @ConditionalOnClass, @ConditionalOnProperty, @ConditionalOnBean. Используется в autoconfiguration. Позволяет создавать бины только при выполнении условий. Кастомные условия через Condition интерфейс.'),

             ('JWT в Spring Security',
              'JSON Web Tokens для stateless аутентификации. JwtAuthenticationFilter в цепи фильтров проверяет токен. Claims содержат информацию о пользователе. Подпись гарантирует целостность. Используется для безопасного обмена данными между сторонами.'),

             ('Spring Circular Dependency',
              'Циклическая зависимость когда A зависит от B, а B от A. Spring решает через конструктор инъекцию (выбрасывает исключение) или через setter/field инъекцию (создает прокси). @Lazy для ленивого разрешения. Рекомендуется пересмотреть архитектуру.'),

             ('Spring Data Redis',
              'Шаблон для работы с Redis. RedisTemplate, StringRedisTemplate. Поддержка Redis repositories. Pub/Sub messaging. Подключение через Lettuce или Jedis клиенты. Используется для кэширования, сессий, очередей.'),

             ('Spring Events',
              'Механизм событий: ApplicationEventPublisher, @EventListener. Синхронные и асинхронные события (@Async). ApplicationEvent для кастомных событий. Observer паттерн. Для loose coupling между компонентами.'),

             ('Spring Session',
              'Абстракция для управления сессиями. Поддержка Redis, JDBC, Hazelcast для хранения. HttpSession заменяется на собственные реализации. Для кластеризации и stateless приложений. Обеспечивает масштабируемость.'),

             ('Spring Boot Actuator Endpoints',
              '/health (статус), /metrics (метрики), /info (информация), /beans (бины), /configprops (конфигурация), /env (переменные), /mappings (маппинги), /loggers (логирование). Кастомные endpoints через @Endpoint.'),

             ('Spring @Primary и @Qualifier',
              '@Primary определяет основной бин при неоднозначности. @Qualifier уточняет имя бина для инъекции. Решают проблемы multiple bean candidates. Приоритет у @Qualifier над @Primary. Для точного управления зависимостями.'),

             ('Spring Boot Error Handling',
              '@ControllerAdvice для глобальной обработки исключений. ErrorAttributes для кастомных ошибок. WhiteLabel Error Page. BasicErrorController. Handling через Problem Details RFC 7807. Единообразная обработка ошибок.'),

             ('Spring Data Projections',
              'Способы получения частичных данных из БД. Interface-based projections (DTO). Class-based projections. Dynamic projections. Для оптимизации запросов и уменьшения передаваемых данных. Улучшает производительность.'),

             ('Spring Boot и Kubernetes',
              'Интеграция с K8s: ConfigMaps, Secrets, Health Checks. Spring Cloud Kubernetes для service discovery. Container-friendly конфигурация. Readiness/Liveness пробы через Actuator. Для cloud-native приложений.'),

             ('@SpringBootApplication состав',
              'Объединяет три аннотации: @Configuration (класс с бинами), @ComponentScan (сканирование компонентов), @EnableAutoConfiguration (включение автоконфигурации). Точка входа Spring Boot приложения. Упрощает начальную настройку.'),

             ('Spring Security Architecture',
              'FilterChain с AuthenticationManager (аутентификация) и AccessDecisionManager (авторизация). UserDetailsService для загрузки пользователей. SecurityContext хранит данные аутентификации. ProviderManager делегирует аутентификацию.'),

             ('Spring WebClient',
              'Реактивный HTTP клиент. Non-blocking, функциональный API. Интеграция с WebFlux. Поддержка SSE (Server-Sent Events). Лучшая альтернатива RestTemplate. Для асинхронных HTTP запросов.'),

             ('Spring Boot Metrics',
              'Micrometer - фасад для метрик. Интеграция с Prometheus, Grafana, InfluxDB. @Timed, @Counted для кастомных метрик. Health indicators для мониторинга здоровья приложения. Унифицированный сбор метрик.')) AS cards(front_side, back_side);