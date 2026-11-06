# FoodFlow

FoodFlow, CSE3063 dönem projesi kapsamında geliştirilen Java 21 tabanlı bir yemek siparişi konsol uygulamasıdır. Kullanıcı restoranları ve menüleri görüntüleyebilir, restoran veya semt mutfağına göre filtreleme yapabilir, sepetini yönetebilir ve kapıda nakit ödeme ile sipariş verebilir.

Restoran ve menü bilgileri UTF-8 biçimindeki [`data/restaurants.json`](data/restaurants.json) dosyasından okunur. Uygulama kapsamındaki müşteri ve sipariş bilgileri çalışma sırasında bellekte tutulur; uygulama kapandığında silinir.

## Gereksinimler

- Java 21
- Maven 3.9 veya üzeri (yerel derleme ve test için)
- Docker Desktop veya Docker Engine ve Docker Compose v2 (Docker ile çalıştırmak için)

## Testleri çalıştırma

Proje kök dizininde terminal açıp tüm JUnit testlerini çalıştırın:

```bash
mvn test
```

Başarılı çalışmada Maven `BUILD SUCCESS` mesajını gösterir. Test kaynak kodları `src/test/java` altındadır; test verileri `src/test/resources` içindedir.

## Uygulamayı Maven ile çalıştırma

Önce paketleyip testleri de çalıştırın:

```bash
mvn package
```

Ardından çalıştırılabilir JAR dosyasını başlatın:

```bash
java -jar target/foodflow.jar
```

Varsayılan restoran verisi `data/restaurants.json` dosyasından yüklenir. Başka bir JSON dosyası kullanmak için dosya yolunu uygulamaya argüman olarak verebilirsiniz:

```bash
java -jar target/foodflow.jar /dosya/yolu/restaurants.json
```

## Docker ile derleme ve çalıştırma

Docker Desktop/Engine çalışır durumdayken proje kökünde aşağıdaki komutları kullanın:

```bash
docker compose build
docker compose run --rm foodflow
```

İlk komut Docker imajını oluşturur. Dockerfile, Maven ve Java 21 içeren derleme aşamasında kaynakları derler, testleri çalıştırır ve uygulama JAR'ını üretir. Testlerden biri başarısız olursa imaj derlemesi de başarısız olur.

İkinci komut konsol uygulamasını terminalde etkileşimli olarak başlatır. Çıkmak için `exit` veya `quit` yazın. `--rm`, uygulama kapandığında geçici konteyneri siler. `data` dizini konteynere salt okunur bağlandığı için JSON verisini değiştirmek için imajı yeniden oluşturmanız gerekmez.

Testleri yalnızca Docker ortamında çalıştırmak için:

```bash
docker compose --profile test run --rm test
```

FoodFlow klavyeden komut bekleyen bir konsol programıdır; arka planda servis olarak çalıştırılmaz. Bu nedenle `docker compose up -d` yerine `docker compose run --rm foodflow` kullanın.

## Konsol komutları

Uygulama açıldığında `help` yazarak komut listesini görebilirsiniz. Örnek akış:

```text
register --name Ali Yılmaz --email ali@example.com --district Kadıköy --street Moda Caddesi 5
restaurants --cuisine Turkish --district Kadıköy
menu R01
add R01-01 2
cart
order
orders
exit
```

Desteklenen komutlar:

| Komut | Açıklama |
| --- | --- |
| `restaurants [--cuisine MUTFAK] [--district SEMT]` | Restoranları listeler veya filtreler. |
| `menu RESTORAN_ID` | Restoranın menüsünü gösterir. |
| `add ÜRÜN_ID ADET` | Sepete ürün ekler. |
| `cart` | Sepet içeriğini ve tutarları gösterir. |
| `qty ÜRÜN_ID ADET` | Sepetteki ürün miktarını değiştirir. |
| `remove ÜRÜN_ID` | Ürünü sepetten kaldırır. |
| `clear` | Sepeti boşaltır. |
| `register --name AD --email EPOSTA --district SEMT --street SOKAK` | Müşteri kaydı oluşturur ve giriş yapar. |
| `login EPOSTA` | Mevcut müşteriyle giriş yapar. |
| `order` | Sepetteki ürünler için kapıda nakit sipariş oluşturur ve fişi gösterir. |
| `orders` | Giriş yapan müşterinin bu oturumdaki siparişlerini listeler. |
| `help`, `exit` | Yardımı gösterir veya uygulamadan çıkar. |

## Proje yapısı

```text
src/main/java/foodflow/
├── App.java                    # Nesneleri oluşturur, veriyi yükler ve arayüzü başlatır
├── application/                # Kullanım senaryoları ve OrderingService
├── domain/                     # Restoran, menü, müşteri, sepet ve sipariş kuralları
├── infrastructure/             # JSON dosyasını domain nesnelerine yükler
└── ui/                         # Konsol komutlarını okur ve çıktıları biçimlendirir

src/test/java/                  # JUnit testleri
src/test/resources/             # Testlerde kullanılan JSON verileri
data/restaurants.json           # Uygulamanın restoran ve menü verileri
Dockerfile                      # Çok aşamalı Maven derlemesi ve Java çalışma imajı
docker-compose.yml              # Uygulama ve test servisleri
pom.xml                         # Maven bağımlılıkları ve derleme ayarları
```

## İş kurallarının özeti

- Sepette tek bir restorana ait ürünler bulunabilir.
- Ürün miktarı 1–20 arasında olmalıdır; aynı ürün yeniden eklenirse miktarı artırılır.
- Sipariş vermek için müşteri girişi ve boş olmayan bir sepet gerekir.
- Toplam tutar ara toplam ile restoranın teslimat ücretinin toplamıdır.
- Sipariş verildiğinde sipariş numarası oluşturulur ve sepet boşaltılır.
