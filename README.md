# Project Rumah Makan With HERE MAP

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


