# Spring Boot REST Service Application

## 📋 İçindekiler
- [Genel Bakış](#genel-bakış)
- [Teknolojiler](#teknolojiler)
- [Mimari](#mimari)
- [Kurulum](#kurulum)
- [Konfigürasyon](#konfigürasyon)
- [API Endpoints](#api-endpoints)
- [Güvenlik](#güvenlik)
- [Veritabanı](#veritabanı)
- [Kullanım Örnekleri](#kullanım-örnekleri)
- [Özellikler](#özellikler)

## 🎯 Genel Bakış

Bu proje, Spring Boot kullanılarak geliştirilmiş modern bir REST API uygulamasıdır. Uygulama, blog yazıları (posts) ve yorumlar (comments) yönetimi için kapsamlı bir sistem sunar. JWT tabanlı kimlik doğrulama, rol bazlı yetkilendirme ve OpenAPI/Swagger dokümantasyonu ile donatılmıştır.

### Temel İşlevler
- ✅ Kullanıcı kayıt ve giriş (Register/Login)
- ✅ JWT Token tabanlı kimlik doğrulama
- ✅ Blog yazıları (Posts) CRUD işlemleri
- ✅ Yorum (Comments) yönetimi
- ✅ Rol ve Scope bazlı yetkilendirme
- ✅ Global hata yönetimi
- ✅ Validation ve Data Transfer Objects (DTO)
- ✅ OpenAPI/Swagger dokümantasyonu

## 🚀 Teknolojiler

### Backend Framework & Kütüphaneler
- **Spring Boot 3.5.8** - Ana framework
- **Spring Security** - Güvenlik ve kimlik doğrulama
- **Spring Data JPA** - ORM ve veritabanı işlemleri
- **Spring Web** - RESTful web servisleri
- **Spring Validation** - Input validation

### Veritabanı
- **H2 Database** - Embedded veritabanı (Development)
- **Hibernate** - JPA implementasyonu

### Güvenlik
- **JJWT (0.11.5)** - JSON Web Token üretimi ve doğrulama
  - jjwt-api
  - jjwt-impl
  - jjwt-jackson

### Dokümantasyon
- **SpringDoc OpenAPI (2.7.0)** - API dokümantasyonu ve Swagger UI

### Yardımcı Kütüphaneler
- **Lombok** - Boilerplate kod azaltma
- **Spring Boot DevTools** - Geliştirme kolaylıkları

### Build Tool
- **Maven** - Bağımlılık yönetimi ve build

### Java Version
- **Java 17**

## 🏗️ Mimari

Proje, **Separation of Concerns** prensibi ile katmanlı mimari kullanmaktadır:

```
src/main/java/com/tcell_spring/restservice/
│
├── presentation/           # Sunum Katmanı
│   ├── controller/        # REST Controllers
│   ├── config/            # Security, OpenAPI yapılandırmaları
│   ├── filter/            # JWT Filter
│   └── errorhandling/     # Global Exception Handler
│
├── application/           # Uygulama Katmanı
│   ├── handler/           # Use-case handlers
│   ├── request/           # Request DTOs
│   └── response/          # Response DTOs
│
├── domain/                # Domain Katmanı
│   ├── entity/            # JPA Entities
│   ├── service/           # Business Logic
│   └── exception/         # Custom Exceptions
│
└── infra/                 # Altyapı Katmanı
    ├── repository/        # Data Access Layer
    ├── jwt/               # JWT Service
    └── mail/              # Email Service (Interface)
```

### Katman Sorumlulukları

#### 1. Presentation Layer (Sunum Katmanı)
- HTTP isteklerini karşılama
- Request/Response dönüşümleri
- Validation kontrolü
- HTTP status kodları yönetimi

#### 2. Application Layer (Uygulama Katmanı)
- Use-case'lerin koordinasyonu
- DTO dönüşümleri
- İş akışı yönetimi

#### 3. Domain Layer (Domain Katmanı)
- İş mantığı (Business Logic)
- Domain modelleri
- Business kuralları

#### 4. Infrastructure Layer (Altyapı Katmanı)
- Veritabanı erişimi
- Dış servis entegrasyonları
- Teknik servisler

## 📦 Kurulum

### Gereksinimler
- Java 17 veya üzeri
- Maven 3.6+

### Adımlar

1. **Projeyi klonlayın**
```bash
git clone <repository-url>
cd restservice
```

2. **Bağımlılıkları yükleyin**
```bash
mvn clean install
```

3. **Uygulamayı çalıştırın**
```bash
mvn spring-boot:run
```

veya

```bash
java -jar target/restservice-0.0.1-SNAPSHOT.jar
```

Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışacaktır.

## ⚙️ Konfigürasyon

### application.properties

```properties
# Uygulama Ayarları
spring.application.name=restservice
server.port=8080
spring.profiles.active=dev

# Logging Seviyeleri
logging.level.org.springframework=INFO
logging.level.com.tcell_spring=DEBUG
logging.level.org.springframework.security=DEBUG

# H2 Veritabanı Ayarları
spring.datasource.url=jdbc:h2:file:C:/data/tspring_db
spring.datasource.username=sa
spring.datasource.password=pass
spring.datasource.driver-class-name=org.h2.Driver

# JPA/Hibernate Ayarları
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Email Provider
defaultEmailProvider=sendGridEmailSender
```

### JWT Konfigürasyonu
- **Secret Key**: HS512 algoritması ile imzalama
- **Token Geçerlilik Süresi**: 15 dakika
- **Token İçeriği**: Username, Roles, Scopes

## 📡 API Endpoints

### 🔐 Authentication Endpoints

#### Kullanıcı Kaydı
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "kullanici_adi",
  "password": "sifre123"
}

Response: 200 OK
"Kayıt başarılı"
```

#### Kullanıcı Girişi
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "kullanici_adi",
  "password": "sifre123"
}

Response: 200 OK
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer"
}
```

### 📝 Posts Endpoints

#### Tüm Postları Listele
```http
GET /api/v1/posts
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "id": 1,
    "title": "İlk Blog Yazım",
    "content": "Blog içeriği...",
    "isReleased": true,
    "releaseDate": "2025-12-18T10:00:00"
  }
]
```

#### Post Detayı Getir
```http
GET /api/v1/posts/{id}
Authorization: Bearer {token}

Response: 200 OK
{
  "id": 1,
  "title": "İlk Blog Yazım",
  "content": "Blog içeriği...",
  "isReleased": true,
  "releaseDate": "2025-12-18T10:00:00"
}
```

#### Yeni Post Oluştur
```http
POST /api/v1/posts
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Yeni Blog Yazısı",
  "content": "Blog içeriği burada...",
  "isReleased": false
}

Response: 201 CREATED
{
  "id": 2,
  "title": "Yeni Blog Yazısı",
  "message": "Post başarıyla oluşturuldu"
}
```

#### Post Güncelle
```http
PUT /api/v1/posts/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Güncellenmiş Başlık",
  "content": "Güncellenmiş içerik..."
}

Response: 204 NO CONTENT
```

#### Post Yayın Durumu Değiştir
```http
PATCH /api/v1/posts/{id}/changeReleaseStatus
Authorization: Bearer {token}
Content-Type: application/json

{
  "isReleased": true
}

Response: 204 NO CONTENT
```

#### Post Sil
```http
DELETE /api/v1/posts/{id}
Authorization: Bearer {token}

Response: 204 NO CONTENT
```

### 💬 Comments Endpoints

#### Post Yorumlarını Listele
```http
GET /api/v1/posts/{postId}/comments
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "id": 1,
    "content": "Harika bir yazı!",
    "postId": 1
  }
]
```

#### Posta Yorum Ekle
```http
POST /api/v1/posts/{postId}/comments
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "Çok faydalı bir içerik, teşekkürler!"
}

Response: 201 CREATED
{
  "id": 2,
  "content": "Çok faydalı bir içerik, teşekkürler!",
  "postId": 1
}
```

## 🔒 Güvenlik

### JWT (JSON Web Token) Kimlik Doğrulama

Uygulama, stateless JWT tabanlı kimlik doğrulama kullanır.

#### Token Yapısı
```json
{
  "sub": "kullanici_adi",
  "role": "ROLE_USER,ROLE_ADMIN",
  "scope": "SCOPE_READ_POSTS,SCOPE_WRITE_POSTS",
  "iat": 1702900000,
  "exp": 1702900900
}
```

#### Token Kullanımı
Her istekte `Authorization` header'ında token gönderilmelidir:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

### Spring Security Yapılandırması

#### Güvenlik Filtreleri
1. **JwtFilter**: Token doğrulama ve kullanıcı authentication
2. **UsernamePasswordAuthenticationFilter**: Spring Security default filter

#### Public Endpoints (Authentication gerektirmez)
- `/h2-console/**` - H2 Database Console
- `/api/v1/auth/**` - Kayıt ve giriş endpoints
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - OpenAPI dokümantasyonu

#### Protected Endpoints
- `/api/v1/posts/**` - Tüm post işlemleri (Authentication gerekli)

### Yetkilendirme (Authorization)

#### Role Bazlı
- `ROLE_USER` - Normal kullanıcı
- `ROLE_ADMIN` - Yönetici kullanıcı

#### Scope Bazlı
- `SCOPE_READ_POSTS` - Post okuma yetkisi
- `SCOPE_WRITE_POSTS` - Post yazma yetkisi

### Şifreleme
- **BCryptPasswordEncoder** kullanılarak şifreler hash'lenir
- Veritabanında düz metin şifre saklanmaz

## 💾 Veritabanı

### H2 Database

#### Erişim Bilgileri
- **Console URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:file:C:/data/tspring_db`
- **Username**: `sa`
- **Password**: `pass`

### Veri Modeli

#### Entity İlişkileri

```
AppUser (1) ----< (N) AppUserAuthority
  |
  └─ UserDetails implementasyonu

Post (1) ----< (N) Comment
  |                  |
  └─ OneToMany       └─ ManyToOne
     CascadeType.ALL    FetchType.LAZY
     FetchType.LAZY
```

#### Tablolar

**users**
```sql
- id (UUID, PK)
- username (VARCHAR, UNIQUE)
- password (VARCHAR, BCrypt hash)
```

**user_authorities**
```sql
- user_id (UUID, FK)
- authority_id (UUID, FK)
```

**posts**
```sql
- id (INTEGER, PK, AUTO_INCREMENT)
- title (VARCHAR(50), UNIQUE, NOT NULL)
- content (VARCHAR(500), NOT NULL)
- is_released (BOOLEAN, DEFAULT FALSE)
- release_date (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
```

**comments**
```sql
- id (INTEGER, PK, AUTO_INCREMENT)
- content (VARCHAR(500), NOT NULL)
- post_id (INTEGER, FK, NOT NULL)
```

### JPA Özellikleri

#### Fetch Strategies
- **LAZY Loading**: İlişkili veriler sadece erişildiğinde yüklenir (performans)
- **EAGER Loading**: İlişkili veriler ana entity ile birlikte yüklenir

#### Cascade Types
- **CascadeType.ALL**: Tüm işlemler ilişkili entity'e yansır
- **CascadeType.PERSIST**: Kayıt işlemi yansır
- **CascadeType.MERGE**: Güncelleme işlemi yansır
- **CascadeType.REMOVE**: Silme işlemi yansır

#### Orphan Removal
Post silindiğinde, ona ait tüm yorumlar otomatik silinir.

## 🛠️ Kullanım Örnekleri

### 1. Yeni Kullanıcı Kaydı ve Login

```bash
# 1. Kayıt Ol
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123"
  }'

# 2. Giriş Yap ve Token Al
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123"
  }'

# Response:
# {
#   "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
#   "tokenType": "Bearer"
# }
```

### 2. Post Oluşturma ve Listeleme

```bash
# Token'ı değişkene kaydet
TOKEN="eyJhbGciOiJIUzUxMiJ9..."

# Yeni Post Oluştur
curl -X POST http://localhost:8080/api/v1/posts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring Boot ile REST API",
    "content": "Bu yazıda Spring Boot kullanarak nasıl REST API geliştirilebileceğini anlatacağım.",
    "isReleased": true
  }'

# Tüm Postları Listele
curl -X GET http://localhost:8080/api/v1/posts \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Yorum Ekleme

```bash
# Posta yorum ekle
curl -X POST http://localhost:8080/api/v1/posts/1/comments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Harika bir yazı olmuş, teşekkürler!"
  }'

# Post yorumlarını listele
curl -X GET http://localhost:8080/api/v1/posts/1/comments \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Post Güncelleme ve Silme

```bash
# Post Güncelle
curl -X PUT http://localhost:8080/api/v1/posts/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring Boot ile Modern REST API",
    "content": "Güncellenmiş içerik..."
  }'

# Yayın Durumunu Değiştir
curl -X PATCH http://localhost:8080/api/v1/posts/1/changeReleaseStatus \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "isReleased": false
  }'

# Post Sil
curl -X DELETE http://localhost:8080/api/v1/posts/1 \
  -H "Authorization: Bearer $TOKEN"
```

## ✨ Özellikler

### 1. Dependency Injection (DI)
- Spring IoC Container ile bağımlılık yönetimi
- Constructor-based injection kullanımı
- `@Service`, `@Component`, `@Repository` stereotypes

### 2. Global Exception Handling
```java
@RestControllerAdvice
public class GlobalErrorHandling {
    @ExceptionHandler(EntityNotFoundException.class)
    @ExceptionHandler(SameEntityExistException.class)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ExceptionHandler(DataIntegrityViolationException.class)
}
```

### 3. Request Validation
```java
public class PostCreateRequest {
    @NotBlank(message = "Başlık boş olamaz")
    @Size(min = 5, max = 50)
    private String title;
    
    @NotBlank(message = "İçerik boş olamaz")
    @Size(min = 10, max = 500)
    private String content;
}
```

### 4. DTO Pattern
- Request DTOs: API'ye gelen veriler
- Response DTOs: API'den dönen veriler
- Entity'lerden ayrı, güvenli veri transferi

### 5. Repository Pattern
```java
public interface IPostRepository extends JpaRepository<Post, Integer> {
    boolean existsByTitle(String title);
    List<Post> findByIsReleased(Boolean isReleased);
}
```

### 6. Method Security
```java
@EnableMethodSecurity
// Controller methodlarında kullanılabilir:
@PreAuthorize("hasAuthority('SCOPE_READ_POSTS')")
@PostAuthorize("hasRole('ADMIN')")
```

### 7. OpenAPI/Swagger Dokümantasyonu
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- İnteraktif API test arayüzü
- Otomatik şema üretimi

### 8. Stateless Authentication
- Her istek bağımsız
- Session kullanılmaz
- Scalability için ideal

### 9. CORS Desteği
- Cross-Origin Resource Sharing yapılandırması
- Frontend entegrasyonu için hazır

### 10. Profile Management
- Development profile: H2 Database
- Production profile: application.prod.yml

## 📚 Swagger Dokümantasyonu

API dokümantasyonuna erişim:
```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI spesifikasyonu:
```
http://localhost:8080/v3/api-docs
```

### Swagger'da Token Kullanımı
1. `/api/v1/auth/login` endpoint'ini kullanarak token alın
2. Sağ üstteki "Authorize" butonuna tıklayın
3. Token'ı `Bearer {token}` formatında girin
4. Artık protected endpoint'leri test edebilirsiniz

## 🐛 Hata Yönetimi

### HTTP Status Kodları
- **200 OK**: Başarılı GET istekleri
- **201 CREATED**: Başarılı POST istekleri
- **204 NO CONTENT**: Başarılı PUT/PATCH/DELETE istekleri
- **400 BAD REQUEST**: Validation hataları, bad request
- **401 UNAUTHORIZED**: Authentication hatası
- **403 FORBIDDEN**: Authorization hatası
- **404 NOT FOUND**: Kayıt bulunamadı
- **500 INTERNAL SERVER ERROR**: Sunucu hatası

### Hata Response Formatları

**Validation Error:**
```json
{
  "title": ["Başlık en az 5 karakter olmalıdır"],
  "content": ["İçerik boş olamaz"]
}
```

**Not Found Error:**
```json
"Kayıt bulunamadı: Post not found."
```

**Duplicate Error:**
```json
"Aynı kayıt zaten mevcut: Post with the same title already exists."
```

## 🔄 Bean Lifecycle

### Lifecycle Annotations
```java
@PostConstruct  // Bean oluşturulduktan sonra
public void init() {
    log.info("Bean initialized");
}

@PreDestroy     // Bean yok edilmeden önce
public void cleanup() {
    log.info("Bean destroyed");
}
```

## 📊 Loglama

### Log Seviyeleri
- **TRACE**: En detaylı
- **DEBUG**: Debug bilgileri
- **INFO**: Genel bilgilendirme
- **WARN**: Uyarılar
- **ERROR**: Hatalar

### Kullanım
```java
@Slf4j
public class MyService {
    public void myMethod() {
        log.debug("Debug mesajı");
        log.info("Info mesajı");
        log.error("Error mesajı", exception);
    }
}
```

## 🧪 Testing

### Test Çalıştırma
```bash
mvn test
```

### Test Sınıfları
```
src/test/java/
└── com/tcell_spring/restservice/
    └── RestserviceApplicationTests.java
```

## 📝 Best Practices

1. **Separation of Concerns**: Katmanlı mimari
2. **DRY Principle**: Kod tekrarından kaçınma
3. **SOLID Principles**: Özellikle Single Responsibility
4. **RESTful Conventions**: HTTP metodları doğru kullanım
5. **Security First**: Varsayılan olarak güvenli
6. **Exception Handling**: Merkezi hata yönetimi
7. **Validation**: Input validasyonu her zaman
8. **DTOs**: Entity'leri dışarıya expose etme
9. **Logging**: Yeterli ve anlamlı loglama
10. **Documentation**: API dokümantasyonu

## 🚨 Önemli Notlar

### Güvenlik
- **Secret Key**: Production'da environment variable kullanın
- **Database Password**: Hassas bilgileri şifrelenerek saklayın
- **CORS**: Production'da sadece güvenilir origin'lere izin verin

### Performans
- **Lazy Loading**: N+1 query problemine dikkat
- **Pagination**: Büyük veri setlerinde pagination kullanın
- **Caching**: Sık erişilen veriler için cache düşünün

### Veritabanı
- **H2**: Sadece development için
- **Production**: PostgreSQL, MySQL gibi production-ready DB kullanın

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.

## 📞 İletişim

Sorularınız için lütfen issue açın veya iletişime geçin.

---

**Not**: Bu uygulama Turkcell Spring Boot eğitimi kapsamında geliştirilmiştir ve eğitim materyallerini içermektedir.

