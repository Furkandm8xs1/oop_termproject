# Ekip İçin Docker & Docker Compose Başlangıç Rehberi

Bu rehber, FoodFlow projesinde Docker'ın ne amaçla kullanıldığını ve günlük geliştirmede ihtiyaç duyacağınız komutları basit ve anlaşılır bir dille açıklar.

---

## 1. Neden Projemizde Docker Kullanıyoruz?

Normalde bir Java projesini çalıştırmak için her ekip üyesinin bilgisayarına:
* Doğru Java sürümünü (Java 21 LTS),
* Maven derleyicisini,
* İşletim sistemine özel çevre değişkenlerini (`JAVA_HOME`, `PATH`, UTF-8 kodlama vb.)
tek tek kurması gerekir. İşletim sistemi farklılıkları (Mac M-serisi, Windows, Linux) yüzünden *"benim bilgisayarımda çalışıyordu, sende neden çalışmadı?"* sorunları çıkar.

**Docker bu sorunu çözer:**
Docker, uygulamanın çalışması için gereken Java 21 ortamını izole bir kutu (konteyner) içinde hazırlar. Sizin bilgisayarınızda Java veya Maven kurulu olmasa bile sadece Docker Desktop açıkken tek komutla proje derlenir, test edilir ve çalışır.

---

## 2. Temel Kavramlar (Kısa Sözlük)

* **Dockerfile:** Projenin nasıl derleneceğini ve çalıştırılacağını belirten "yemek tarifidir".
* **Image (Kalıp / İmaj):** `Dockerfile` kullanılarak üretilen, uygulamanın ve Java ortamının paketlenmiş dondurulmuş halidir.
* **Container (Konteyner):** Bir imajın bellekte canlı olarak çalışan örneğidir.
* **docker-compose.yml:** Projedeki servisleri (ana uygulama, test ortamı vb.) tek bir merkezden kolayca yönetmenizi sağlayan yapılandırma dosyasıdır.

---

## 3. Projemiz İçin Günlük Kullanılan 3 Ana Komut

FoodFlow projesinde çalışırken **yalnızca şu 3 komuta** ihtiyacınız olacak:

### 1. Projeyi Derlemek (Build):
```bash
docker compose build
```
* **Ne yapar?** Java kodlarınızı derler, **tüm JUnit testlerini çalıştırır** ve çalışmaya hazır yeni bir Docker imajı üretir.
* **Ne zaman kullanılır?** Kodu GitHub'dan ilk çektiğinizde (`git pull`) veya kodda herhangi bir değişiklik yaptığınızda.
* *Not:* Eğer testlerden biri bile başarısız olursa (fail), imaj üretilmez ve size hangi testin patladığını ekranda gösterir.

---

### 2. Uygulamayı Konsolda Başlatmak (Run):
```bash
docker compose run --rm foodflow
```
* **Ne yapar?** Terminalinizi doğrudan konteynerin içine bağlar ve FoodFlow konsol uygulamasını başlatır (`FoodFlow > ` komut satırı açılır).
* **`--rm` ne işe yarar?** Uygulamadan `quit` yazıp çıktığınızda geçici konteyneri otomatik temizler, bilgisayarınızda çöp dosya biriktirmez.
* **`data/restaurants.json` güncellemesi:** Bu dosya konteynere dışarıdan bağlı (`volume`) olduğu için, json dosyasında bir restoranı veya fiyatı değiştirdiğinizde tekrar build yapmanıza gerek kalmaz; bu komutla başlattığınız anda yeni veriler geçerli olur.

---

### 3. Sadece Testleri Koşturmak:
```bash
docker compose --profile test run --rm test
```
* **Ne yapar?** Uygulamayı açmadan sadece `src/test/java` altındaki birim testleri (JUnit 5) çalıştırır ve test raporunu (`BUILD SUCCESS` / `FAILURE`) ekrana basar.
* **Ne zaman kullanılır?** GitHub'a kod göndermeden (`git push`) hemen önce yaptığınız değişikliklerin mevcut kuralları bozmadığından emin olmak için.

---

### 4. Demo Senaryosunu Tek Seferde Çalıştırmak:
Hocaya sunum yaparken veya örnek akışı doğrulamak için komutları tek tek elle yazmak yerine hazır test senaryosunu içeri besleyebilirsiniz:
```bash
docker compose run --rm -T -e FOODFLOW_ECHO=true foodflow < docs/demo-input.txt
```

---

## 4. Sıkça Sorulan Sorular ve Hata Çözümleri

### Soru 1: Neden `docker compose up -d` kullanmıyoruz?
`up -d` komutu web sunucuları veya veritabanları gibi arkada sessizce çalışması gereken sistemler içindir. FoodFlow ise sizden klavye girdisi (`register`, `add`, `order` vb.) bekleyen bir konsol uygulamasıdır. `run` komutu terminalinizi uygulamanın klavye/ekranına doğrudan bağlar.

### Hata: "Cannot connect to the Docker daemon" veya "docker daemon is not running"
* **Çözüm:** Docker Desktop uygulamanız bilgisayarınızda açık değildir. Docker Desktop'ı açın ve simgesinin yeşil (running) olduğunu gördükten sonra terminal komutunuzu tekrar çalıştırın.

### Soru 2: Bilgisayarımda Docker çok yer kaplar mı? Nasıl temizlerim?
Zaman zaman eski veya kullanılmayan konteyner/imaj artıklarını temizlemek için şu komutu çalıştırabilirsiniz:
```bash
docker system prune -f
```

