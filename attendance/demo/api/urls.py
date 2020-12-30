from demo.api import views
from django.urls import path

app_name = 'demo'
urlpatterns = [
    path('', views.checkuser,name="detail"),
    path('enroll/', views.enroll, name ="enroll"),
    path('courses/', views.getcourse, name="getcourse"),
    path('qrcode/',views.qrcode, name="qrcode"),
    path('signup/',views.signup,name="signup"),
    path('getallcourses/',views.getallcourses,name="getallcourses"),
    path('viewattendance/',views.viewattendance,name="viewattendance"),
    path('deregister/',views.deregister,name="deregister")
]
