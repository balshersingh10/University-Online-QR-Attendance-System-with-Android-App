from django.urls import path

from . import views

urlpatterns = [
    path('', views.index, name='index'),
    path("enrollc", views.enrollc, name="enrollc"),
    path("signup", views.signup, name="signup"),
    path("login_view" , views.login_view , name="login_view"),
    path("register", views.register, name = "register"),
    path("register_course", views.register_course, name = "register_course"),
    path("logout_view", views.logout_view, name = "logout_view"),
    path("<slug:course>", views.course_detail, name="course_detail"),
    path("<slug:course>/qr", views.qr, name="qr"),
    path("<slug:course>/mark_attendence",views.mark_attendence, name="mark_attendence"),
    path("<slug:course>/drop", views.drop, name="drop"),
    path("<slug:course>/dereg", views.dereg, name="dereg"),

]
