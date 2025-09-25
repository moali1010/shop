# 🛍️ فروشـگاه

مستند API فروشگاه شامل بخش‌های احراز هویت، محصولات، سفارشات و پرداخت است.

---

## 🛡️ احراز هویت (Authentication)

### 📌 ثبت‌نام کاربر جدید

* **روش:** `POST`
* **آدرس:** `http://localhost:8080/api/auth/register`
* **بدنه:**

```json
{
  "username": "newuser",
  "password": "123456"
}
```

* **پاسخ:** اطلاعات کاربر (بدون رمز عبور) + وضعیت `201 Created`

### 🔑 ورود کاربر (دریافت توکن JWT)

* **روش:** `POST`
* **آدرس:** `http://localhost:8080/api/auth/login`
* **بدنه:**

```json
{
  "username": "admin",
  "password": "admin"
}
```

* **پاسخ:** توکن JWT برای استفاده در سایر درخواست‌ها

  * **هدر مورد نیاز در سایر بخش‌ها:**

```text
Authorization: Bearer <your_token>
```

---

## 🧺 محصولات (Products)

### 👀 مشاهده لیست محصولات (برای مشتریان)

* **روش:** `GET`
* **آدرس:** `http://localhost:8080/api/products?page=0&size=10`
* **پاسخ:** لیستی از محصولات فعال

---

## 📦 سفارشات (Orders)

### 📝 ثبت سفارش جدید

* **روش:** `POST`
* **آدرس:** `http://localhost:8080/api/orders`
* **هدر:**

```text
Authorization: Bearer <your_token>
```

* **بدنه:**

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "discountCode": "AXFMO"
}
```

* **پاسخ:** شماره سفارش (Order ID)

---

## 💳 پرداخت (Payments)

### 💰 پرداخت سفارش

* **روش:** `POST`
* **آدرس:** `http://localhost:8080/api/payments/order/1?method=WALLET`
* **هدر:**

```text
Authorization: Bearer <your_token>
```

* **پاسخ:** جزئیات تراکنش پرداخت موفق