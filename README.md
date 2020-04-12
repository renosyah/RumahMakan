# Project Rumah Makan With HERE MAP

adalah aplikasi proyek android untuk menemukan informasi dan rute ke objek wisata kuliner di DIY

## Overview

* splash screen

![GitHub Logo](/img/splash.jpg) 


* explore

![GitHub Logo](/img/explore.jpg) 


* search

![GitHub Logo](/img/search_map.jpg)  ![GitHub Logo](/img/search_list.jpg) 


* detail

![GitHub Logo](/img/detail.jpg) 


* routing

![GitHub Logo](/img/routing.jpg) 




## Cara menautkan ke akun pengembang peta Anda di sini

- masuk ke akun dev peta Anda di sini : [Here Map Dev](https://developer.here.com/login)
- download pustaka android `.aar` dan `credentials.properties`
- letakan `credentials.properties` direktori proyek
- extract file zip yg mana file tersebut mengandung pustaka `.aar` lalu letakan di direktory `/app/libs`

atau download disini : [Google Drive](https://drive.google.com/open?id=1YonGysXJw8dUktOQL3KzdkNWwglpCX3u)

## MVP Architecture

Model-View-Presenter atau yang biasa disingkat menjadi MVP adalah sebuah konsep arsitektur pengembangan aplikasi yang memisahkan antara tampilan aplikasi dengan proses bisnis yang bekerja pada aplikasi. Arsitektur ini akan membuat pengembangan aplikasi kita menjadi lebih terstuktur, mudah di-test dan juga mudah di-maintain.

Berikut penjelasan masing-masing layer pada MVP.
- View, merupakan layer untuk menampilkan data dan interaksi ke user. View biasanya berupa Activity, Fragment atau Dialog di Android. View ini juga yang langsung berkomunikasi dengan user.
- Model, merupakan layer yang menunjuk kepada objek dan data yang ada pada aplikasi.
- Presenter, merupakan layer yang menghubungkan komunikasi antara Model dan View. Setiap interaksi yang dilakukan oleh user akan memanggil Presenter untuk memrosesnya dan mengakses Model lalu mengembalikan responnya kembali kepada View.


sumber : [medium MVP by Eminarti Sianturi
](https://medium.com/easyread/android-mvp-series-membangun-aplikasi-android-dengan-arsitektur-mvp-fbf1f77ecaec)

sumber : [github.com/MindorksOpenSource/android-mvp-architecture](https://github.com/MindorksOpenSource/android-mvp-architecture)


## Retrofit

Klien HTTP yang aman untuk Android dan Java oleh Square, Inc.
Untuk informasi lebih lanjut silakan lihat situs web.


sumber : [square.github.io/retrofit](https://square.github.io/retrofit)




## Gson

Gson adalah pustaka Java yang dapat digunakan untuk mengubah Java Objects menjadi representasi JSON mereka. Itu juga dapat digunakan untuk mengonversi string JSON ke objek Java yang setara. Gson dapat bekerja dengan objek Java yang arbitrer termasuk objek yang sudah ada sebelumnya yang tidak Anda miliki kode sumbernya.
Ada beberapa proyek open-source yang dapat mengkonversi objek Java ke JSON. Namun, kebanyakan dari mereka mengharuskan Anda menempatkan anotasi Java di kelas Anda; sesuatu yang tidak dapat Anda lakukan jika Anda tidak memiliki akses ke kode sumber. Sebagian besar juga tidak sepenuhnya mendukung penggunaan Java Generics. Gson menganggap kedua hal ini sebagai tujuan desain yang sangat penting.

Tujuan
* Memberikan metode toJson () dan fromJson () yang sederhana untuk mengonversi objek Java ke JSON dan sebaliknya
* Izinkan objek yang tidak dimodifikasi yang sudah ada sebelumnya dikonversi ke dan dari JSON
* Dukungan luas dari Java Generics
* Izinkan representasi khusus untuk objek
* Mendukung objek kompleks yang sewenang-wenang (dengan hierarki warisan yang dalam dan penggunaan tipe generik yang ekstensif)


sumber : [github.com/google/gson](https://github.com/google/gson)


## Pustaka Lainnya

* [SwipeStack by flschweiger](https://github.com/flschweiger/SwipeStack)
* [AnyChart-Android by AnyChart](https://github.com/AnyChart/AnyChart-Android)
* [picasso by square](https://square.github.io/picasso/)


