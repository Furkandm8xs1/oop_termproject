# Ekip İçin Git & GitHub Başlangıç Rehberi

Bu rehber, FoodFlow projesinde çalışırken Git ve GitHub kullanımını adım adım öğrenmek ve ekipçe sorunsuz kod geliştirmek için hazırlanmıştır.

---

## 1. Temel Kavramlar (Sözlük)

* **Repository (Repo):** Projenin tüm dosyalarını, geçmişini ve sürümlerini tutan klasördür.
* **Commit:** Dosyalarda yaptığınız değişikliklerin o anki "fotoğrafıdır" (checkpoint).
* **Branch (Dal):** Ana koddan (`main`) bağımsız olarak yeni bir özellik geliştirmek için açılan paralel çalışma alanıdır.
* **Merge:** Bir daldaki değişiklikleri başka bir dala (örneğin `main` dalına) birleştirmektir.
* **Pull Request (PR):** Kendi dalınızda yaptığınız işi `main` dalına eklemek için grup arkadaşlarınıza sunduğunuz inceleme ve onay talebidir.
* **Origin:** GitHub üzerindeki uzak sunucunun (remote) varsayılan takma adıdır.

---

## 2. İlk Kurulum ve Ayarlar (Sadece 1 Kere Yapılır)

Bilgisayarınızda terminali açıp adınızı ve e-postanızı tanıtın:
```bash
git config --global user.name "Adınız Soyadınız"
git config --global user.email "okul-veya-github-epostaniz@ornek.com"
```

Projeyi GitHub'dan kendi bilgisayarınıza ilk kez indirmek için:
```bash
git clone <github-repo-linki>
cd foodflow
```

---

## 3. Günlük Çalışma Akışı (Her Yeni Görev İçin)

Projede herkesin aynı anda `main` dalı üzerinde çalışması çakışmalara (conflict) yol açar. Bu yüzden şu standart akışı izleyin:

### Adım 1: Güncel Kodu Çekin
Çalışmaya başlamadan önce her zaman ana dalın en güncel halini alın:
```bash
git checkout main
git pull origin main
```

### Adım 2: Kendinize Yeni Bir Dal (Branch) Açın
Üzerinde çalışacağınız iş için anlamlı bir dal adı belirleyin:
```bash
# Format: git checkout -b feature/<ozellik-adi> veya fix/<hata-adi>
git checkout -b feature/cart-rule-update
```
*(Bu komut yeni dalı oluşturur ve ona geçiş yapar).*

### Adım 3: Kodunuzu Yazın ve Test Edin
Değişikliklerinizi yapın. Kodun çalıştığından ve testlerin geçtiğinden emin olun:
```bash
docker compose --profile test run --rm test
```

### Adım 4: Değişiklikleri Kaydedin (Commit)
Nelerin değiştiğini kontrol edin:
```bash
git status
```
Değişiklikleri commit kuyruğuna ekleyin:
```bash
# Tüm değişen dosyaları eklemek için:
git add .

# Veya belirli bir dosyayı eklemek için:
git add src/main/java/foodflow/domain/Cart.java
```
Açıklayıcı bir mesaj ile commit oluşturun:
```bash
git commit -m "BR-2: Cart tek restoran kuralı kontrolü güncellendi"
```

### Adım 5: Dalınızı GitHub'a Gönderin (Push)
Kendi açtığınız dalı uzak sunucuya yükleyin:
```bash
git push -u origin feature/cart-rule-update
```

### Adım 6: GitHub Üzerinden Pull Request (PR) Açın
1. GitHub deposuna gidin.
2. Üstte beliren **"Compare & pull request"** butonuna tıklayın.
3. Ne yaptığınızı kısaca yazın.
4. Grup arkadaşlarınızdan birini inceleyici (Reviewer) olarak atayın.
5. Arkadaşınız inceleyip onayladıktan sonra **"Merge pull request"** yaparak `main` ile birleştirin.

---

## 4. Sık Kullanılan Git Komutları ve Anlamları

| Komut | Ne İşe Yarar? |
|---|---|
| `git status` | Hangi dosyaları değiştirdiğinizi, hangilerinin eklenip eklenmediğini gösterir. (Günde 50 kere kullanabilirsiniz!) |
| `git diff` | Dosyalarda satır bazında hangi kodları ekleyip sildiğinizi gösterir. |
| `git branch` | Bilgisayarınızda hangi dalların olduğunu ve o an hangisinde olduğunuzu listeler. |
| `git checkout main` | `main` dalına geri dönmenizi sağlar. |
| `git log --oneline` | Projenin son commit geçmişini tek satırlık özetler halinde gösterir. |
| `git stash` | Henüz commit yapmadığınız yarım kalmış işleri geçici olarak kenara kaldırır (temiz bir alana dönmek için). |
| `git stash pop` | Kenara kaldırdığınız değişiklikleri geri getirir. |

---

## 5. Çakışma (Merge Conflict) Olursa Ne Yapmalı?

Eğer bir arkadaşınız sizin düzenlediğiniz satırları daha önce `main`'e gönderdiyse çakışma oluşabilir. Panik yapmayın:

1. Kendi dalınızdayken güncel `main`'i çekin:
   ```bash
   git merge main
   ```
2. Git size çakışan dosyaları söyler (örneğin `CONFLICT in Cart.java`).
3. VS Code'da o dosyayı açın. VS Code size:
   * *Accept Current Change* (Sizin yazdığınız kalsın)
   * *Accept Incoming Change* (Arkadaşınızın getirdiği kalsın)
   * *Accept Both Changes* (İkisi de kalsın)
   seçeneklerini sunar.
4. Kodu düzenleyip doğru hale getirin, kaydedin.
5. Ardından:
   ```bash
   git add .
   git commit -m "Merge conflict çözüldü"
   git push
   ```

