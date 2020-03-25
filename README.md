# Project Rumah Makan With HERE MAP

is an android project application to find information and routes to culinary attractions in DIY

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




## How to linked to your here map developer account

- login to your here map dev account here : [Here Map Dev](https://developer.here.com/login)
- download android library `.aar` and `credentials.properties`
- put `credentials.properties` into the root directory of project
- extract zip file which contain the `.aar` file and put into the `/app/libs` directory


## MVP Architecture

Model-View-Presenter or commonly abbreviated as MVP is an application development architecture concept that separates the appearance of applications with business processes that work on the application. This architecture will make our application development more structured, easy to test and also easy to maintain.

Following is an explanation of each layer in MVP.
- View, is a layer for displaying data and interaction to users. View is usually in the form of Activity, Fragment or Dialog on Android. This view also directly communicates with the user.
- Model, which is a layer that points to objects and data in an application.
- Presenter, is the layer that connects communication between Model and View. Every interaction conducted by the user will call the Presenter to process it and access the Model and then return the response back to the View.


sumber : [medium MVP by Eminarti Sianturi
](https://medium.com/easyread/android-mvp-series-membangun-aplikasi-android-dengan-arsitektur-mvp-fbf1f77ecaec)

sumber : [github.com/MindorksOpenSource/android-mvp-architecture](https://github.com/MindorksOpenSource/android-mvp-architecture)



## Retrofit

Type-safe HTTP client for Android and Java by Square, Inc.
For more information please see the website.


sumber : [square.github.io/retrofit](https://square.github.io/retrofit)




## Gson

Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object. Gson can work with arbitrary Java objects including pre-existing objects that you do not have source-code of.

There are a few open-source projects that can convert Java objects to JSON. However, most of them require that you place Java annotations in your classes; something that you can not do if you do not have access to the source-code. Most also do not fully support the use of Java Generics. Gson considers both of these as very important design goals.

Goals
* Provide simple toJson() and fromJson() methods to convert Java objects to JSON and vice-versa
* Allow pre-existing unmodifiable objects to be converted to and from JSON
* Extensive support of Java Generics
* Allow custom representations for objects
* Support arbitrarily complex objects (with deep inheritance hierarchies and extensive use of generic types)


sumber : [github.com/google/gson](https://github.com/google/gson)


## Other Library

* [SwipeStack by flschweiger](https://github.com/flschweiger/SwipeStack)
* [AnyChart-Android by AnyChart](https://github.com/AnyChart/AnyChart-Android)
* [picasso by square](https://square.github.io/picasso/)


